package me.lucaaa.advancedlinks.velocity.managers;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.ServerLink;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.LinkReceiver;
import me.lucaaa.advancedlinks.common.data.ParsedLink;
import me.lucaaa.advancedlinks.common.managers.LinksManager;
import me.lucaaa.advancedlinks.velocity.IVelocityAdvancedLinks;
import me.lucaaa.advancedlinks.velocity.data.VelocityLinkReceiver;

public class VelocityLinksManager extends LinksManager<ServerLink, ServerLink.Type> {
    public VelocityLinksManager(AdvancedLinks plugin, boolean reload) {
        super(plugin, ServerLink.Type.class, reload);
    }

    @Override
    public ServerLink createGlobalLink(ParsedLink<ServerLink.Type> link) {
        ServerLink serverLink;
        if (link.type() != null) {
            serverLink = ServerLink.serverLink(link.type(), link.url().toString());
        } else {
            serverLink = ServerLink.serverLink(link.displayName(), link.url().toString());
        }
        return serverLink;
    }

    @Override
    public void removeGlobalLink(ServerLink link) {}

    @Override
    protected void sendLinks() {
        for (Player player : ((IVelocityAdvancedLinks) plugin).getServer().getAllPlayers()) {
            sendLinks(new VelocityLinkReceiver((IVelocityAdvancedLinks) plugin, player));
        }
    }

    @Override
    protected String replacePapiPlaceholders(String text, LinkReceiver<ServerLink.Type> receiver) {
        return (receiver == null) ? text : receiver.replacePapiPlaceholders(text);
    }
}