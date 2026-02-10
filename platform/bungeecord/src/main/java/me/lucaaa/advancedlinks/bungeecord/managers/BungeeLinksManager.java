package me.lucaaa.advancedlinks.bungeecord.managers;

import me.lucaaa.advancedlinks.bungeecord.IBungeeAdvancedLinks;
import me.lucaaa.advancedlinks.bungeecord.data.BungeeLinkReceiver;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.ParsedLink;
import me.lucaaa.advancedlinks.common.data.Parsers;
import me.lucaaa.advancedlinks.common.managers.LinksManager;
import net.md_5.bungee.api.ServerLink;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeLinksManager extends LinksManager<ServerLink, ServerLink.LinkType> {
    public BungeeLinksManager(AdvancedLinks plugin, boolean reload) {
        super(plugin, ServerLink.LinkType.class, reload);
    }

    @Override
    public ServerLink createGlobalLink(ParsedLink<ServerLink.LinkType> link) {
        ServerLink serverLink;
        if (link.type() != null) {
            serverLink = new ServerLink(link.type(), link.url().toString());
        } else {
            serverLink = new ServerLink(TextComponent.fromLegacy(Parsers.legacySectionSerializer.serialize(link.displayName())), link.url().toString());
        }
        return serverLink;
    }

    @Override
    public void removeGlobalLink(ServerLink link) {}

    @Override
    protected void sendLinks() {
        for (ProxiedPlayer player : ((IBungeeAdvancedLinks) plugin).getServer().getPlayers()) {
            sendLinks(new BungeeLinkReceiver(player));
        }
    }
}