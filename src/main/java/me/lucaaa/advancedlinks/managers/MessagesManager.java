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
        return LegacyComponentSerializer.legacySection().serialize(component);
        // return TextComponent.toLegacyText(BungeeComponentSerializer.get().serialize(component));
    }

    /* Code for old method - "manually" converts a component to a string.
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
    }*/
}