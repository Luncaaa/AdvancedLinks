package me.lucaaa.advancedlinks.velocity.data;

import com.velocitypowered.api.command.CommandSource;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import me.lucaaa.advancedlinks.common.data.Parsers;

public class VelocityMessageReceiver implements MessageReceiver {
    private final CommandSource sender;

    public VelocityMessageReceiver(CommandSource sender) {
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