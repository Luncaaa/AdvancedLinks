package me.lucaaa.advancedlinks.spigot.listeners;

import me.lucaaa.advancedlinks.spigot.ISpigotAdvancedLinks;
import me.lucaaa.advancedlinks.spigot.managers.SpigotLinksManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final ISpigotAdvancedLinks plugin;

    public PlayerListener(ISpigotAdvancedLinks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ((SpigotLinksManager) (Object) plugin.getLinksManager()).sendLinks(
                plugin.getPlatformManager().getLinkReceiver(event.getPlayer())
        );
    }
}