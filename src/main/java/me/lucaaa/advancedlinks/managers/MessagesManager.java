package me.lucaaa.advancedlinks.managers;

import me.lucaaa.advancedlinks.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;

import java.util.Objects;
import java.util.logging.Level;

public class MessagesManager {
    private final String prefix;

    public MessagesManager(ConfigManager mainConfigManager) {
        this.prefix = mainConfigManager.getConfig().getString("prefix");
    }

    public String getColoredMessage(String message, boolean addPrefix) {
        String messageToSend = message;
        if (addPrefix) messageToSend =  prefix + " " + messageToSend;

        return ChatColor.translateAlternateColorCodes('&', messageToSend);
    }

    public String getColoredMessageMM(String key, String message) {
        MiniMessage mm = MiniMessage.miniMessage();
        Component component = mm.deserialize(message);

        if (!(component instanceof TextComponent)) {
            Logger.log(Level.WARNING,"The link \"" + key + "\" has an invalid text component in the \"displayName\" field!");
            return "Invalid text component!";
        }

        return componentToString((TextComponent) MiniMessage.miniMessage().deserialize(message), "");
    }

    private String componentToString(TextComponent component, String initial) {
        ChatColor color = (component.color() == null) ? ChatColor.WHITE : ChatColor.of(Objects.requireNonNull(component.color()).asHexString());
        String componentString = initial + color + component.content();

        if (component.children().isEmpty()) return componentString;

        for (Component child : component.children()) {
            componentString = componentToString((TextComponent) child, componentString);
        }

        return ChatColor.translateAlternateColorCodes('&', componentString);
    }
}