package me.lucaaa.advancedlinks.bungeecord.data;

import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import me.lucaaa.advancedlinks.common.data.Parsers;
import net.md_5.bungee.api.CommandSender;

public class BungeeMessageReceiver implements MessageReceiver {
    private final CommandSender sender;

    public BungeeMessageReceiver(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(Parsers.bungeeSerializer.serialize(Parsers.parseMessage(message)));
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }
}