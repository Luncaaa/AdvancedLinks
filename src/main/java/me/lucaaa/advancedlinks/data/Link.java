package me.lucaaa.advancedlinks.data;

import org.bukkit.ServerLinks;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public record Link(
        String name,
        String displayName,
        @Nullable ServerLinks.Type type,
        String url,
        boolean individual,
        List<Placeholder> placeholders) {

    public record Placeholder(String match, String replacement, boolean replacePapi) {}
}
