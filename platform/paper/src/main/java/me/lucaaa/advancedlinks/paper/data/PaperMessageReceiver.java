package me.lucaaa.advancedlinks.paper.data;

import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import me.lucaaa.advancedlinks.common.data.Parsers;
import org.bukkit.command.CommandSender;

public class PaperMessageReceiver implements MessageReceiver {
    private final CommandSender sender;

    public PaperMessageReceiver(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(Parsers.parseMessage(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }
}