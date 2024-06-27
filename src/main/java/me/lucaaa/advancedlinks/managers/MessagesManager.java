package me.lucaaa.advancedlinks.managers;

import me.lucaaa.advancedlinks.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

        return componentToString((TextComponent) MiniMessage.miniMessage().deserialize(message), null);
    }

    private String componentToString(TextComponent component, TextColor parentColor) {
        StringBuilder componentString = new StringBuilder(component.content());

        if (component.hasDecoration(TextDecoration.BOLD)) componentString.insert(0, ChatColor.BOLD);
        if (component.hasDecoration(TextDecoration.UNDERLINED)) componentString.insert(0, ChatColor.UNDERLINE);
        if (component.hasDecoration(TextDecoration.STRIKETHROUGH)) componentString.insert(0, ChatColor.STRIKETHROUGH);
        if (component.hasDecoration(TextDecoration.OBFUSCATED)) componentString.insert(0, ChatColor.MAGIC);

        ChatColor color;
        if (component.color() == null && parentColor == null) {
            color = ChatColor.WHITE;
        } else if (component.color() != null) {
            color = ChatColor.of(Objects.requireNonNull(component.color()).asHexString());
        } else {
            color = ChatColor.of(parentColor.asHexString());
        }

        componentString.insert(0, color).insert(0, "");

        if (component.children().isEmpty()) return ChatColor.translateAlternateColorCodes('&', componentString.toString());

        for (Component child : component.children()) {
            componentString.append(componentToString((TextComponent) child, component.color()));
        }

        return ChatColor.translateAlternateColorCodes('&', componentString.toString());
    }
}