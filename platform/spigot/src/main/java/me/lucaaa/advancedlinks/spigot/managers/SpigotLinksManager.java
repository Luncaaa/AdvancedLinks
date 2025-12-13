package me.lucaaa.advancedlinks.spigot.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.LinkReceiver;
import me.lucaaa.advancedlinks.common.data.ParsedLink;
import me.lucaaa.advancedlinks.common.data.Parsers;
import me.lucaaa.advancedlinks.common.managers.LinksManager;
import me.lucaaa.advancedlinks.spigot.ISpigotAdvancedLinks;
import org.bukkit.ServerLinks;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public class SpigotLinksManager extends LinksManager<ServerLinks.ServerLink, ServerLinks.Type> {
    public SpigotLinksManager(AdvancedLinks plugin, Class<ServerLinks.Type> enumTypeClass, boolean reload) {
        super(plugin, enumTypeClass, reload);
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
        ((ISpigotAdvancedLinks) plugin).getServer().getServerLinks().removeLink(link);
    }

    @Override
    protected void sendLinks() {
        for (Player player : ((ISpigotAdvancedLinks) plugin).getServer().getOnlinePlayers()) {
            sendLinks(((ISpigotAdvancedLinks) plugin).getPlatformManager().getLinkReceiver(player));
        }
    }

    @Override
    protected String replacePapiPlaceholders(String text, LinkReceiver<ServerLinks.Type> receiver) {
        if (receiver == null) {
            if (plugin.supportsPapi()) {
                return PlaceholderAPI.setPlaceholders(null, text);
            } else {
                return text;
            }
        } else {
            return receiver.replacePapiPlaceholders(text);
        }
    }
}