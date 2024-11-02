package me.lucaaa.advancedlinks.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

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

    public String parseMessage(String message) {
        // From legacy and minimessage format to a component
        Component legacy = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        // From component to Minimessage String. Replacing the "\" with nothing makes the minimessage formats work.
        String minimessage = MiniMessage.miniMessage().serialize(legacy).replace("\\", "");
        // From Minimessage String to Minimessage component
        Component component = MiniMessage.miniMessage().deserialize(minimessage);
        // From Minimessage component to legacy string.
        return LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build().serialize(component);
        // return TextComponent.toLegacyText(BungeeComponentSerializer.get().serialize(component));
    }
}