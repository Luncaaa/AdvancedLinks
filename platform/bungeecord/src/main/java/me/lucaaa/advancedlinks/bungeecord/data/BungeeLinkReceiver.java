package me.lucaaa.advancedlinks.bungeecord.data;

import me.lucaaa.advancedlinks.bungeecord.IBungeeAdvancedLinks;
import me.lucaaa.advancedlinks.bungeecord.managers.BungeeLinksManager;
import me.lucaaa.advancedlinks.common.data.LinkReceiver;
import me.lucaaa.advancedlinks.common.data.ParsedLink;
import me.lucaaa.advancedlinks.common.data.Parsers;
import net.md_5.bungee.api.ServerLink;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class BungeeLinkReceiver implements LinkReceiver<ServerLink.LinkType> {
    private final IBungeeAdvancedLinks plugin;
    private final ProxiedPlayer player;

    public BungeeLinkReceiver(IBungeeAdvancedLinks plugin, ProxiedPlayer player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void sendLinks(List<ParsedLink<ServerLink.LinkType>> parsedLinks) {
        List<ServerLink> serverLinks = new ArrayList<>(((BungeeLinksManager) (Object) plugin.getLinksManager()).getGlobalLinksCopy());
        for (ParsedLink<ServerLink.LinkType> parsedLink : parsedLinks) {
            ServerLink serverLink;
            if (parsedLink.type() != null) {
                serverLink = new ServerLink(parsedLink.type(), parsedLink.url().toString());
            } else {
                serverLink = new ServerLink(TextComponent.fromLegacy(Parsers.legacySectionSerializer.serialize(parsedLink.displayName())), parsedLink.url().toString());
            }
            serverLinks.add(serverLink);
        }

        try {
            player.sendServerLinks(serverLinks);
        } catch (IllegalArgumentException ignored) {
            // This means that the player is not on 1.21. Because it's not a plugin error, nothing is logged.
        }
    }

    @Override
    public String replacePapiPlaceholders(String text) {
        return text.replace("%player_name%", player.getName());
    }
}