package me.lucaaa.advancedlinks.common.data;

import java.util.List;

public record Link<T extends Enum<T>>(
        String name,
        String displayName,
        T type, // ServerLink class is different for each platform so the generic class is used instead.
        String url,
        boolean individual,
        List<Placeholder> placeholders) {

    public record Placeholder(String match, String replacement, boolean replacePapi) {}
}