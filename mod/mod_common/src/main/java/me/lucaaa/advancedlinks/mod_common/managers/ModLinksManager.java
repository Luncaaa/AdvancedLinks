package me.lucaaa.advancedlinks.mod_common.managers;

import com.mojang.datafixers.util.Either;
import me.lucaaa.advancedlinks.common.data.ParsedLink;
import me.lucaaa.advancedlinks.common.managers.LinksManager;
import me.lucaaa.advancedlinks.mod_common.ModAdvancedLinks;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.level.ServerPlayer;

public class ModLinksManager extends LinksManager<ServerLinks.UntrustedEntry, ServerLinks.KnownLinkType> {
    public ModLinksManager(ModAdvancedLinks plugin, boolean reload) {
        super(plugin, ServerLinks.KnownLinkType.class, reload);
    }

    @Override
    public ServerLinks.UntrustedEntry createGlobalLink(ParsedLink<ServerLinks.KnownLinkType> link) {
        ServerLinks.UntrustedEntry serverLink;
        if (link.type() != null) {
            serverLink = new ServerLinks.UntrustedEntry(Either.left(link.type()), link.url().toString());
        } else {
            serverLink = new ServerLinks.UntrustedEntry(Either.right(((ModAdvancedLinks) plugin).asNative(link.displayName())), link.url().toString());
        }
        return serverLink;
    }

    @Override
    public void removeGlobalLink(ServerLinks.UntrustedEntry link) {}

    @Override
    protected void sendLinks() {
        for (ServerPlayer player : ((ModAdvancedLinks) plugin).getServer().getPlayerList().getPlayers()) {
            sendLinks(((ModAdvancedLinks) plugin).getLinkReceiver(player));
        }
    }
}