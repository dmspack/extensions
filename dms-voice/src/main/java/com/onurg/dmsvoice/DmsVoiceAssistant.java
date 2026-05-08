package com.onurg.dmsvoice;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DmsVoiceAssistant {

    private static final Logger LOGGER = Logger.getLogger(DmsVoiceAssistant.class.getName());

    private final DmsControlAdapter controlAdapter;
    private final IntentParser parser;
    private final VoskSpeechRecognizer recognizer;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DmsVoiceAssistant(DmsControlAdapter controlAdapter, IntentParser parser, VoskSpeechRecognizer recognizer) {
        this.controlAdapter = Objects.requireNonNull(controlAdapter, "controlAdapter");
        this.parser = Objects.requireNonNull(parser, "parser");
        this.recognizer = Objects.requireNonNull(recognizer, "recognizer");
    }

    public void listenOnce(int timeoutSeconds) {
        executor.submit(() -> {
            try {
                Optional<String> spokenText = recognizer.recognizeFromMicrophone(timeoutSeconds);
                if (spokenText.isPresent()) {
                    handleText(spokenText.get());
                } else {
                    LOGGER.info("No speech recognized in " + timeoutSeconds + " seconds.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Voice recognition failed", e);
            }
        });
    }

    public void handleText(String spokenText) {
        if (spokenText == null || spokenText.isBlank()) {
            LOGGER.warning("Received empty transcript.");
            return;
        }

        VoiceCommand command = parser.parse(spokenText);
        LOGGER.info("Recognized command: " + command.getIntent() + " payload=" + command.getPayload() + " target=" + command.getTarget());
        executeCommand(command);
    }

    private void executeCommand(VoiceCommand command) {
        switch (command.getIntent()) {
            case SEND_MESSAGE_TO_CONTACT -> executeSendMessageToContact(command);
            case SEND_MESSAGE_TO_GROUP -> executeSendMessageToGroup(command);
            case SEARCH_MESSAGES -> executeSearch(command);
            case SEARCH_ARCHIVE -> executeSearchArchive(command);
            case CLEAR_CONVERSATION -> controlAdapter.clearConversation();
            case LOGOUT -> controlAdapter.logout();
            case SWITCH_AUDIO -> executeSwitchAudio(command);
            case ARCHIVE_MESSAGES -> executeArchiveMessages(command);
            case DELETE_MESSAGES -> executeDeleteMessages(command);
            case UNKNOWN -> LOGGER.warning("Unable to understand voice command: " + command.getPayload());
        }
    }

    private void executeSendMessageToContact(VoiceCommand command) {
        if (!command.hasPayload() || !command.hasTarget()) {
            LOGGER.warning("Send message to contact command is missing a target or payload.");
            return;
        }
        boolean ok = controlAdapter.sendMessageToContact(command.getPayload(), command.getTarget());
        if (!ok) {
            LOGGER.warning("Failed to send voice message to contact: " + command.getTarget());
        }
    }

    private void executeSendMessageToGroup(VoiceCommand command) {
        if (!command.hasPayload() || !command.hasTarget()) {
            LOGGER.warning("Send message to group command is missing a target or payload.");
            return;
        }
        boolean ok = controlAdapter.sendMessageToGroup(command.getPayload(), command.getTarget());
        if (!ok) {
            LOGGER.warning("Failed to send voice message to group: " + command.getTarget());
        }
    }

    private void executeSearch(VoiceCommand command) {
        if (!command.hasPayload()) {
            LOGGER.warning("Search command is missing query text.");
            return;
        }
        controlAdapter.search(command.getPayload());
    }

    private void executeSearchArchive(VoiceCommand command) {
        if (!command.hasPayload()) {
            LOGGER.warning("Search archive command is missing query text.");
            return;
        }
        controlAdapter.searchArchive(command.getPayload());
    }

    private void executeSwitchAudio(VoiceCommand command) {
        String target = command.getTarget();
        if (target == null) {
            LOGGER.warning("Switch audio command did not include on/off information.");
            return;
        }
        boolean enabled = target.contains("on");
        controlAdapter.switchAudio(enabled);
    }

    private void executeArchiveMessages(VoiceCommand command) {
        if (!command.hasPayload()) {
            LOGGER.warning("Archive messages command is missing message IDs.");
            return;
        }
        boolean ok = controlAdapter.archiveMessages(command.getPayload());
        if (!ok) {
            LOGGER.warning("Failed to archive messages: " + command.getPayload());
        }
    }

    private void executeDeleteMessages(VoiceCommand command) {
        if (!command.hasPayload()) {
            LOGGER.warning("Delete messages command is missing message IDs.");
            return;
        }
        boolean ok = controlAdapter.deleteMessages(command.getPayload());
        if (!ok) {
            LOGGER.warning("Failed to delete messages: " + command.getPayload());
        }
    }

    public void shutdown() {
        executor.shutdownNow();
        recognizer.close();
    }
}
