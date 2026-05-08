package com.onurg.dmsvoice;

public interface DmsControlAdapter {
    boolean sendMessageToContact(String message, String contactName);
    boolean sendMessageToGroup(String message, String groupName);
    boolean search(String query);
    boolean searchArchive(String query);
    void clearConversation();
    void logout();
    boolean switchAudio(boolean enabled);
    boolean archiveMessages(String messageIds);
    boolean deleteMessages(String messageIds);
    boolean isConnected();
}
