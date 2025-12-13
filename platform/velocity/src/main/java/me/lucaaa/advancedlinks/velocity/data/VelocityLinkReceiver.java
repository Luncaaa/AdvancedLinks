package me.lucaaa.advancedlinks.velocity.data;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.ServerLink;
import me.lucaaa.advancedlinks.common.data.LinkReceiver;
import me.lucaaa.advancedlinks.common.data.ParsedLink;
import me.lucaaa.advancedlinks.velocity.IVelocityAdvancedLinks;
import me.lucaaa.advancedlinks.velocity.managers.VelocityLinksManager;

import java.util.ArrayList;
import java.util.List;

public class VelocityLinkReceiver implements LinkReceiver<ServerLink.Type> {
    private final IVelocityAdvancedLinks plugin;
    private final Player player;

    public VelocityLinkReceiver(IVelocityAdvancedLinks plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void sendLinks(List<ParsedLink<ServerLink.Type>> parsedLinks) {
        List<ServerLink> serverLinks = new ArrayList<>(((VelocityLinksManager) (Object) plugin.getLinksManager()).getGlobalLinksCopy());
        for (ParsedLink<ServerLink.Type> parsedLink : parsedLinks) {
            ServerLink serverLink;
            if (parsedLink.type() != null) {
                serverLink = ServerLink.serverLink(parsedLink.type(), parsedLink.url().toString());
            } else {
                serverLink = ServerLink.serverLink(parsedLink.displayName(), parsedLink.url().toString());
            }
            serverLinks.add(serverLink);
        }

        try {
            player.setServerLinks(serverLinks);
        } catch (IllegalArgumentException ignored) {
            // This means that the player is not on 1.21. Because it's not a plugin error, nothing is logged.
        }
    }

    @Override
    public String replacePapiPlaceholders(String text) {
        return text.replace("%player_name%", player.getUsername());
    }
}