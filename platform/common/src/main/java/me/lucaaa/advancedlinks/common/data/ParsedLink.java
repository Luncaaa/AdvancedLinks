package me.lucaaa.advancedlinks.common.data;

import net.kyori.adventure.text.Component;

import java.net.URI;

public record ParsedLink<T extends Enum<T>>(
        Component displayName,
        T type, // ServerLink class is different for each platform so the generic class is used instead.
        URI url
) {}