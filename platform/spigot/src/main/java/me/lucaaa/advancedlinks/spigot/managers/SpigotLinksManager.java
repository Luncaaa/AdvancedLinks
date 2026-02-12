package me.lucaaa.advancedlinks.spigot.managers;

import me.lucaaa.advancedlinks.common.data.ParsedLink;
import me.lucaaa.advancedlinks.common.data.Parsers;
import me.lucaaa.advancedlinks.common.managers.LinksManager;
import me.lucaaa.advancedlinks.spigot.ISpigotAdvancedLinks;
import org.bukkit.ServerLinks;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public class SpigotLinksManager extends LinksManager<ServerLinks.ServerLink, ServerLinks.Type> {
    public SpigotLinksManager(ISpigotAdvancedLinks plugin, boolean reload) {
        super(plugin, ServerLinks.Type.class, reload);
    }

    @Override
    public ServerLinks.ServerLink createGlobalLink(ParsedLink<ServerLinks.Type> link) {
        ServerLinks serverLinks = ((JavaPlugin) plugin).getServer().getServerLinks();
        if (link.type() != null) {
            return serverLinks.addLink(link.type(), link.url());
        } else {
            return serverLinks.addLink(Parsers.legacySectionSerializer.serialize(link.displayName()), link.url());
        }
    }

    @Override
    public void removeGlobalLink(ServerLinks.ServerLink link) {
        ((JavaPlugin) plugin).getServer().getServerLinks().removeLink(link);
    }

    @Override
    protected void sendLinks() {
        for (Player player : ((JavaPlugin) plugin).getServer().getOnlinePlayers()) {
            sendLinks(((ISpigotAdvancedLinks) plugin).getPlatformManager().getLinkReceiver(player));
        }
    }
}