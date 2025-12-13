package me.lucaaa.advancedlinks.common.data;

public interface MessageReceiver {
    void sendMessage(String message);

    boolean hasPermission(String permission);
}