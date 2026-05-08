package com.onurg.dmsvoice;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.dms.core.control.DmsControl;

public class VoiceAssistantApp {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: java -jar dms-voice.jar <username> <password> <vosk-model-path>");
            System.exit(1);
        }

        String username = args[0];
        String password = args[1];
        Path modelPath = Paths.get(args[2]);

        DmsControl dmsControl = new DmsControl(username, password, () -> System.out.println("Logout requested by voice assistant"));
        DmsControlAdapter controlAdapter = new DmsControlAdapterImpl(dmsControl);

        try (VoskSpeechRecognizer recognizer = new VoskSpeechRecognizer(modelPath)) {
            DmsVoiceAssistant assistant = new DmsVoiceAssistant(controlAdapter, new IntentParser(), recognizer);
            System.out.println("Voice assistant is listening for a single command...");
            assistant.listenOnce(15);
            Thread.sleep(20_000);
            assistant.shutdown();
        }
    }
}
