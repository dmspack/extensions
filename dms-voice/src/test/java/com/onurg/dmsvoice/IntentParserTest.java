package com.onurg.dmsvoice;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntentParserTest {

    private final IntentParser parser = new IntentParser();

    @Test
    void testEnglishSendMessageToContact() {
        VoiceCommand command = parser.parse("send message to John saying hello");
        assertEquals(VoiceIntent.SEND_MESSAGE_TO_CONTACT, command.getIntent());
        assertEquals("john", command.getTarget());
        assertEquals("hello", command.getPayload());
    }

    @Test
    void testTurkishSendMessageToContact() {
        VoiceCommand command = parser.parse("ahmete de merhaba");
        assertEquals(VoiceIntent.SEND_MESSAGE_TO_CONTACT, command.getIntent());
        assertEquals("ahmete", command.getTarget());
        assertEquals("merhaba", command.getPayload());
    }

    @Test
    void testEnglishSearch() {
        VoiceCommand command = parser.parse("search for meeting notes");
        assertEquals(VoiceIntent.SEARCH_MESSAGES, command.getIntent());
        assertEquals("meeting notes", command.getPayload());
    }

    @Test
    void testTurkishSearch() {
        VoiceCommand command = parser.parse("toplantı notlarını ara");
        assertEquals(VoiceIntent.SEARCH_MESSAGES, command.getIntent());
        assertEquals("toplantı notlarını", command.getPayload());
    }

    @Test
    void testEnglishSearchArchive() {
        VoiceCommand command = parser.parse("search archive for project files");
        assertEquals(VoiceIntent.SEARCH_ARCHIVE, command.getIntent());
        assertEquals("project files", command.getPayload());
    }

    @Test
    void testTurkishSearchArchive() {
        VoiceCommand command = parser.parse("arşivde proje dosyalarını ara");
        assertEquals(VoiceIntent.SEARCH_ARCHIVE, command.getIntent());
        assertEquals("proje dosyalarını", command.getPayload());
    }

    @Test
    void testEnglishClearConversation() {
        VoiceCommand command = parser.parse("clear conversation");
        assertEquals(VoiceIntent.CLEAR_CONVERSATION, command.getIntent());
    }

    @Test
    void testTurkishClearConversation() {
        VoiceCommand command = parser.parse("sohbeti temizle");
        assertEquals(VoiceIntent.CLEAR_CONVERSATION, command.getIntent());
    }

    @Test
    void testEnglishLogout() {
        VoiceCommand command = parser.parse("logout");
        assertEquals(VoiceIntent.LOGOUT, command.getIntent());
    }

    @Test
    void testTurkishLogout() {
        VoiceCommand command = parser.parse("çıkış");
        assertEquals(VoiceIntent.LOGOUT, command.getIntent());
    }

    @Test
    void testEnglishSwitchAudioOn() {
        VoiceCommand command = parser.parse("turn audio on");
        assertEquals(VoiceIntent.SWITCH_AUDIO, command.getIntent());
        assertEquals("on", command.getTarget());
    }

    @Test
    void testTurkishSwitchAudioOn() {
        VoiceCommand command = parser.parse("sesi aç");
        assertEquals(VoiceIntent.SWITCH_AUDIO, command.getIntent());
        assertEquals("on", command.getTarget());
    }

    @Test
    void testEnglishArchiveMessages() {
        VoiceCommand command = parser.parse("archive messages 1,2,3");
        assertEquals(VoiceIntent.ARCHIVE_MESSAGES, command.getIntent());
        assertEquals("1,2,3", command.getPayload());
    }

    @Test
    void testTurkishArchiveMessages() {
        VoiceCommand command = parser.parse("mesajları arşivle 1,2,3");
        assertEquals(VoiceIntent.ARCHIVE_MESSAGES, command.getIntent());
        assertEquals("1,2,3", command.getPayload());
    }

    @Test
    void testEnglishDeleteMessages() {
        VoiceCommand command = parser.parse("delete messages 4,5");
        assertEquals(VoiceIntent.DELETE_MESSAGES, command.getIntent());
        assertEquals("4,5", command.getPayload());
    }

    @Test
    void testTurkishDeleteMessages() {
        VoiceCommand command = parser.parse("mesajları sil 4,5");
        assertEquals(VoiceIntent.DELETE_MESSAGES, command.getIntent());
        assertEquals("4,5", command.getPayload());
    }

    @Test
    void testUnknownCommand() {
        VoiceCommand command = parser.parse("some unknown command");
        assertEquals(VoiceIntent.UNKNOWN, command.getIntent());
    }
}