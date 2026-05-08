package com.onurg.dmsvoice;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dms.core.control.DmsControl;
import com.dms.core.intf.handles.ContactHandle;
import com.dms.core.intf.handles.GroupHandle;

public class DmsControlAdapterImpl implements DmsControlAdapter {

    private static final Logger LOGGER = Logger.getLogger(DmsControlAdapterImpl.class.getName());
    private final DmsControl control;

    public DmsControlAdapterImpl(DmsControl control) {
        this.control = Objects.requireNonNull(control, "control");
    }

    @Override
    public boolean sendMessageToContact(String message, String contactName) {
        return findContact(contactName).map(contact -> {
            try {
                control.sendGuiMessageToContact(message, contact.getId());
                return true;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to send message to contact: " + contactName, e);
                return false;
            }
        }).orElseGet(() -> {
            LOGGER.warning("Contact not found: " + contactName);
            return false;
        });
    }

    @Override
    public boolean sendMessageToGroup(String message, String groupName) {
        return findGroup(groupName).map(group -> {
            try {
                control.sendGuiMessageToGroup(message, group.getGroupId());
                return true;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to send message to group: " + groupName, e);
                return false;
            }
        }).orElseGet(() -> {
            LOGGER.warning("Group not found: " + groupName);
            return false;
        });
    }

    @Override
    public boolean search(String query) {
        if (query == null || query.isBlank()) {
            LOGGER.warning("Search query is empty");
            return false;
        }
        control.searchRequested(query);
        return true;
    }

    @Override
    public boolean searchArchive(String query) {
        if (query == null || query.isBlank()) {
            LOGGER.warning("Archive search query is empty");
            return false;
        }
        control.archiveSearchRequested(query);
        return true;
    }

    @Override
    public void clearConversation() {
        control.clearConversationRequested();
    }

    @Override
    public void logout() {
        control.logout();
    }

    @Override
    public boolean switchAudio(boolean enabled) {
        try {
            control.switchAudio(enabled);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to switch audio: " + enabled, e);
            return false;
        }
    }

    @Override
    public boolean archiveMessages(String messageIds) {
        if (messageIds == null || messageIds.isBlank()) {
            LOGGER.warning("Message IDs for archiving are empty");
            return false;
        }
        try {
            // Assuming messageIds is a comma-separated list
            String[] ids = messageIds.split(",");
            Long[] longIds = new Long[ids.length];
            for (int i = 0; i < ids.length; i++) {
                longIds[i] = Long.parseLong(ids[i].trim());
            }
            control.archiveMessagesRequested(longIds);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to archive messages: " + messageIds, e);
            return false;
        }
    }

    @Override
    public boolean deleteMessages(String messageIds) {
        if (messageIds == null || messageIds.isBlank()) {
            LOGGER.warning("Message IDs for deletion are empty");
            return false;
        }
        try {
            // Assuming messageIds is a comma-separated list
            String[] ids = messageIds.split(",");
            Long[] longIds = new Long[ids.length];
            for (int i = 0; i < ids.length; i++) {
                longIds[i] = Long.parseLong(ids[i].trim());
            }
            control.deleteMessagesRequested(longIds);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to delete messages: " + messageIds, e);
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        return control.isServerConnected();
    }

    private Optional<ContactHandle> findContact(String contactName) {
        if (contactName == null || contactName.isBlank()) {
            return Optional.empty();
        }
        String normalized = contactName.trim().toLowerCase();
        return getAllContacts().stream()
                .filter(contact -> contact.getName() != null && contact.getName().toLowerCase().contains(normalized))
                .findFirst();
    }

    private Optional<GroupHandle> findGroup(String groupName) {
        if (groupName == null || groupName.isBlank()) {
            return Optional.empty();
        }
        String normalized = groupName.trim().toLowerCase();
        return getAllGroups().stream()
                .filter(group -> group.getName() != null && group.getName().toLowerCase().contains(normalized))
                .findFirst();
    }

    private List<ContactHandle> getAllContacts() {
        return control.getAllContactHandles();
    }

    private List<GroupHandle> getAllGroups() {
        return control.getAllGroupHandles();
    }
}
