package me.lucaaa.advancedlinks.spigot.data;

import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import me.lucaaa.advancedlinks.common.data.Parsers;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class SpigotMessageReceiver implements MessageReceiver {
    private final CommandSender sender;

    public SpigotMessageReceiver(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String message) {
        // From legacy and minimessage format to a component
        Component component = Parsers.parseMessage(message);
        // Send a legacy String
        sender.sendMessage(Parsers.legacySectionSerializer.serialize(component));
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }
}