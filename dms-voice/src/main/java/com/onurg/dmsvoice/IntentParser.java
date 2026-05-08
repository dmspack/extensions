package com.onurg.dmsvoice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntentParser {

    // English patterns
    private static final Pattern SEND_PATTERN_EN = Pattern.compile("^(?:send|tell|write)(?: a)?(?: message)?(?: to)? (?<target>[^:]+?)(?: saying| that|:|,)? (?<payload>.+)$");
    private static final Pattern DIRECT_TO_PATTERN_EN = Pattern.compile("^to (?<target>[^:]+?) (?:say|saying|that|:) (?<payload>.+)$");
    private static final Pattern SEARCH_PATTERN_EN = Pattern.compile("^(?:search|find|look for|search for) (?<payload>.+)$");
    private static final Pattern SEARCH_ARCHIVE_PATTERN_EN = Pattern.compile("^(?:search|find) (?:in )?archive (?:for )?(?<payload>.+)$");
    private static final Pattern CLEAR_PATTERN_EN = Pattern.compile(".*\\b(?:clear|erase|remove|delete) (?:conversation|chat|messages)\\b.*");
    private static final Pattern LOGOUT_PATTERN_EN = Pattern.compile(".*\\b(?:logout|log out|sign out|exit|quit)\\b.*");
    private static final Pattern SWITCH_AUDIO_PATTERN_EN = Pattern.compile(".*\\b(?:turn|switch) (?:audio|sound) (on|off)\\b.*");
    private static final Pattern ARCHIVE_PATTERN_EN = Pattern.compile("^(?:archive|store) (?:messages? )?(?<payload>.+)$");
    private static final Pattern DELETE_PATTERN_EN = Pattern.compile("^(?:delete|remove) (?:messages? )?(?<payload>.+)$");

    // Turkish patterns
    private static final Pattern SEND_PATTERN_TR = Pattern.compile("^(?:mesaj gönder|gönder mesaj|gönder)(?: (?<target>[^\\s]+?))?(?: (?:diyerek|söyleyerek|şöyle|de|:) )?(?<payload>.+)$");
    private static final Pattern DIRECT_TO_PATTERN_TR = Pattern.compile("^(?<target>[^\\s]+?) (?:diye söyle|şöyle söyle|diye|de) (?<payload>.+)$");
    private static final Pattern SEARCH_PATTERN_TR = Pattern.compile("^(?:ara|bul|araştır) (?<payload>.+)$");
    private static final Pattern SEARCH_ARCHIVE_PATTERN_TR = Pattern.compile(".*\\b(?:arşivde|arşiv).*\\bara\\b\\s*(?<payload>.+)?$");
    private static final Pattern CLEAR_PATTERN_TR = Pattern.compile(".*\\b(?:temizle|sil|kaldır) (?:sohbet|konuşma|mesajlar)\\b.*");
    private static final Pattern LOGOUT_PATTERN_TR = Pattern.compile(".*\\b(?:çıkış|çık|çıkış yap)\\b.*");
    private static final Pattern SWITCH_AUDIO_PATTERN_TR = Pattern.compile(".*\\b(?:sesi?|ses) (aç|kapat)\\b.*");
    private static final Pattern ARCHIVE_PATTERN_TR = Pattern.compile("^(?:arşivle|sakla) (?:mesajları? )?(?<payload>.+)$");
    private static final Pattern DELETE_PATTERN_TR = Pattern.compile("^(?:sil|kaldır) (?:mesajları? )?(?<payload>.+)$");

    public VoiceCommand parse(String spokenText) {
        if (spokenText == null) {
            return VoiceCommand.unknown(null);
        }

        String normalized = normalize(spokenText);

        // Try English patterns first
        VoiceCommand command = parseEnglish(normalized);
        if (command.getIntent() != VoiceIntent.UNKNOWN) {
            return command;
        }

        // Try Turkish patterns
        command = parseTurkish(normalized);
        if (command.getIntent() != VoiceIntent.UNKNOWN) {
            return command;
        }

        // Fallback to unknown
        return VoiceCommand.unknown(spokenText.trim());
    }

    private VoiceCommand parseEnglish(String normalized) {
        Matcher matcher = SEND_PATTERN_EN.matcher(normalized);
        if (matcher.matches()) {
            return new VoiceCommand(VoiceIntent.SEND_MESSAGE_TO_CONTACT,
                    matcher.group("target").trim(),
                    matcher.group("payload").trim());
        }

        matcher = DIRECT_TO_PATTERN_EN.matcher(normalized);
        if (matcher.matches()) {
            return new VoiceCommand(VoiceIntent.SEND_MESSAGE_TO_CONTACT,
                    matcher.group("target").trim(),
                    matcher.group("payload").trim());
        }

        matcher = SEARCH_PATTERN_EN.matcher(normalized);
        if (matcher.matches()) {
            return new VoiceCommand(VoiceIntent.SEARCH_MESSAGES, null, matcher.group("payload").trim());
        }

        matcher = SEARCH_ARCHIVE_PATTERN_EN.matcher(normalized);
        if (matcher.matches()) {
            return new VoiceCommand(VoiceIntent.SEARCH_ARCHIVE, null, matcher.group("payload").trim());
        }

        if (CLEAR_PATTERN_EN.matcher(normalized).matches()) {
            return new VoiceCommand(VoiceIntent.CLEAR_CONVERSATION, null, null);
        }

        if (LOGOUT_PATTERN_EN.matcher(normalized).matches()) {
            return new VoiceCommand(VoiceIntent.LOGOUT, null, null);
        }

        matcher = SWITCH_AUDIO_PATTERN_EN.matcher(normalized);
        if (matcher.matches()) {
            return new VoiceCommand(VoiceIntent.SWITCH_AUDIO, matcher.group(1), null);
        }

        matcher = ARCHIVE_PATTERN_EN.matcher(normalized);
        if (matcher.matches()) {
            return new VoiceCommand(VoiceIntent.ARCHIVE_MESSAGES, null, matcher.group("payload").trim());
        }

        matcher = DELETE_PATTERN_EN.matcher(normalized);
        if (matcher.matches()) {
            return new VoiceCommand(VoiceIntent.DELETE_MESSAGES, null, matcher.group("payload").trim());
        }

        if (normalized.startsWith("send ") && normalized.contains(" to ")) {
            int toIndex = normalized.indexOf(" to ");
            String messagePart = normalized.substring(0, toIndex).replaceFirst("^send ", "").trim();
            String targetPart = normalized.substring(toIndex + 4).trim();
            if (!targetPart.isBlank() && !messagePart.isBlank()) {
                return new VoiceCommand(VoiceIntent.SEND_MESSAGE_TO_CONTACT, targetPart, messagePart);
            }
        }

        return VoiceCommand.unknown(null);
    }

    private VoiceCommand parseTurkish(String normalized) {
        Matcher matcher = SEND_PATTERN_TR.matcher(normalized);
        if (matcher.matches()) {
            String target = matcher.group("target");
            String payload = matcher.group("payload");
            if (target != null && payload != null) {
                return new VoiceCommand(VoiceIntent.SEND_MESSAGE_TO_CONTACT, target.trim(), payload.trim());
            }
        }

        matcher = DIRECT_TO_PATTERN_TR.matcher(normalized);
        if (matcher.matches()) {
            return new VoiceCommand(VoiceIntent.SEND_MESSAGE_TO_CONTACT,
                    matcher.group("target").trim(),
                    matcher.group("payload").trim());
        }

        matcher = SEARCH_PATTERN_TR.matcher(normalized);
        if (matcher.matches()) {
            return new VoiceCommand(VoiceIntent.SEARCH_MESSAGES, null, matcher.group("payload").trim());
        }

        if (SEARCH_ARCHIVE_PATTERN_TR.matcher(normalized).matches()) {
            // Extract payload as everything between "arşiv" and "ara"
            String payload = extractSearchPayload(normalized, "arşivde", "arşiv", "ara");
            return new VoiceCommand(VoiceIntent.SEARCH_ARCHIVE, null, payload);
        }

        if (CLEAR_PATTERN_TR.matcher(normalized).matches()) {
            return new VoiceCommand(VoiceIntent.CLEAR_CONVERSATION, null, null);
        }

        if (LOGOUT_PATTERN_TR.matcher(normalized).matches()) {
            return new VoiceCommand(VoiceIntent.LOGOUT, null, null);
        }

        matcher = SWITCH_AUDIO_PATTERN_TR.matcher(normalized);
        if (matcher.matches()) {
            String action = matcher.group(1);
            return new VoiceCommand(VoiceIntent.SWITCH_AUDIO, action.equals("aç") ? "on" : "off", null);
        }

        matcher = ARCHIVE_PATTERN_TR.matcher(normalized);
        if (matcher.matches()) {
            return new VoiceCommand(VoiceIntent.ARCHIVE_MESSAGES, null, matcher.group("payload").trim());
        }

        matcher = DELETE_PATTERN_TR.matcher(normalized);
        if (matcher.matches()) {
            return new VoiceCommand(VoiceIntent.DELETE_MESSAGES, null, matcher.group("payload").trim());
        }

        // Fallback for Turkish send patterns
        String[] connectors = {" de", " diye", " söyle"};
        for (String connector : connectors) {
            int index = normalized.indexOf(connector);
            if (index > 0) {
                String targetPart = normalized.substring(0, index).trim();
                String payloadPart = normalized.substring(index + connector.length()).trim();
                if (!targetPart.isBlank() && !payloadPart.isBlank()) {
                    return new VoiceCommand(VoiceIntent.SEND_MESSAGE_TO_CONTACT, targetPart, payloadPart);
                }
            }
        }

        return VoiceCommand.unknown(null);
    }

    private String normalize(String text) {
        return text.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private String extractSearchPayload(String text, String startWord1, String startWord2, String endWord) {
        int startIndex = text.indexOf(startWord1);
        if (startIndex == -1) {
            startIndex = text.indexOf(startWord2);
        }
        if (startIndex == -1) return text;
        startIndex += (text.startsWith(startWord1) ? startWord1.length() : startWord2.length());
        
        int endIndex = text.indexOf(endWord, startIndex);
        if (endIndex == -1) return text.substring(startIndex).trim();
        
        return text.substring(startIndex, endIndex).trim();
    }
}
