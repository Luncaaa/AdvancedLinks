package me.lucaaa.advancedlinks.listeners;

import me.lucaaa.advancedlinks.AdvancedLinks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {
    private final AdvancedLinks plugin;

    public PlayerListener(AdvancedLinks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getLinksManager().sendLinks(event.getPlayer());
    }
}