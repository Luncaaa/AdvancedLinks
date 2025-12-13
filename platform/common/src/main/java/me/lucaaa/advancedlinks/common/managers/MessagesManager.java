package me.lucaaa.advancedlinks.common.managers;

import me.lucaaa.advancedlinks.common.data.MessageReceiver;

public class MessagesManager {
    private final String prefix;

    public MessagesManager(String prefix) {
        this.prefix = prefix;
    }

    public void sendColoredMessage(MessageReceiver receiver, String message, boolean addPrefix) {
        if (addPrefix) message = prefix + " " + message;
        receiver.sendMessage(message);
    }
}