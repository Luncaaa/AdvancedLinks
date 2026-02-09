package me.lucaaa.advancedlinks.common.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public interface Parsers {
    MiniMessage mm = MiniMessage.miniMessage();
    LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().character('&').build();
    LegacyComponentSerializer legacySectionSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().character('§').build();

    static Component parseMessage(String message) {
        message = message.replace("\\n", "\n").replace('§', '&');
        // From legacy and minimessage format to a component
        Component legacy = legacySerializer.deserialize(message);
        // From component to Minimessage String. Replacing the "\" with nothing makes the minimessage formats work.
        String minimessage = mm.serialize(legacy).replace("\\", "");
        // From Minimessage String to Minimessage component
        return mm.deserialize(minimessage);
    }
}