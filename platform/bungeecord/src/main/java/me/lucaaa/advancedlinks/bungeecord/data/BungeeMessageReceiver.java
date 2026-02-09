package me.lucaaa.advancedlinks.bungeecord.data;

import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import me.lucaaa.advancedlinks.common.data.Parsers;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.CommandSender;

public class BungeeMessageReceiver implements MessageReceiver {
    private final CommandSender sender;
    private static final BungeeComponentSerializer bungeeSerializer = BungeeComponentSerializer.get();

    public BungeeMessageReceiver(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        sender.sendMessage(bungeeSerializer.serialize(Parsers.parseMessage(message)));
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }
}