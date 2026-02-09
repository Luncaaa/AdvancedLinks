package me.lucaaa.advancedlinks.spigot.data;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucaaa.advancedlinks.common.data.LinkReceiver;
import me.lucaaa.advancedlinks.common.data.ParsedLink;
import me.lucaaa.advancedlinks.common.data.Parsers;
import me.lucaaa.advancedlinks.spigot.ISpigotAdvancedLinks;
import org.bukkit.ServerLinks;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SpigotLinkReceiver implements LinkReceiver<ServerLinks.ServerLink, ServerLinks.Type> {
    private final ISpigotAdvancedLinks plugin;
    private final Player player;

    public SpigotLinkReceiver(ISpigotAdvancedLinks plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void sendLinks(Collection<ServerLinks.ServerLink> globalLinks, List<ParsedLink<ServerLinks.Type>> parsedLinks) {
        // Global links are automatically sent by the server.
        ServerLinks serverLinks = plugin.getServer().getServerLinks().copy();
        for (ParsedLink<ServerLinks.Type> parsedLink : parsedLinks) {
            if (parsedLink.type() != null) {
                serverLinks.addLink(parsedLink.type(), parsedLink.url());
            } else {
                serverLinks.addLink(Parsers.legacySectionSerializer.serialize(parsedLink.displayName()), parsedLink.url());
            }
        }
        player.sendLinks(serverLinks);
    }

    @Override
    public String replacePapiPlaceholders(String text) {
        if (plugin.supportsPapi()) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        } else {
            text = text.replace("%player_name%", player.getName());
        }
        return text;
    }
}