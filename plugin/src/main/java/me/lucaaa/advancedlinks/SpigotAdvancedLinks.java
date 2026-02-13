package me.lucaaa.advancedlinks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.managers.*;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;
import me.lucaaa.advancedlinks.spigot.ISpigotAdvancedLinks;
import me.lucaaa.advancedlinks.spigot.managers.PlatformManager;
import me.lucaaa.advancedlinks.spigot.managers.SpigotConfigManager;
import me.lucaaa.advancedlinks.spigot.managers.SpigotLinksManager;
import org.bukkit.ServerLinks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

@SuppressWarnings("UnstableApiUsage")
public class SpigotAdvancedLinks extends ISpigotAdvancedLinks implements Listener, PluginMessageListener {
    // Config files.
    private ConfigManager mainConfig;

    // Managers.
    private PlatformManager platformManager;
    private MessagesManager messagesManager;
    private SpigotLinksManager linksManager;

    // Others.
    private boolean isPapiInstalled;

    @Override
    public void reloadConfigs() {
        // Other
        isPapiInstalled = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        // Config file.
        mainConfig = new SpigotConfigManager(this, new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml"));

        // Managers
        if (linksManager != null) linksManager.shutdown();

        platformManager = new PlatformManager(this);
        messagesManager = new MessagesManager(mainConfig.getOrDefault("prefix", "&7[&6AL&7]"));
        linksManager = new SpigotLinksManager(this, linksManager != null);
    }

    @Override
    public void onEnable() {
        // Set up files and managers.
        reloadConfigs();

        // Look for updates.
        if (mainConfig.getOrDefault("updateChecker", true)) {
            UpdateManager updateManager = new UpdateManager(this);
            updateManager.getVersion(v -> updateManager.sendStatus(platformManager.getMessageReceiver(getServer().getConsoleSender()), v, getDescription().getVersion()));
        }

        // Register events.
        getServer().getPluginManager().registerEvents(this, this);

        // Registers the main command and adds tab completions.
        platformManager.registerMainCommand();

        // Listen to the plugin messaging channel (to print a warning if it's present on both backend and proxy server)
        getServer().getMessenger().registerOutgoingPluginChannel(this, AdvancedLinks.CHANNEL_ID);
        getServer().getMessenger().registerIncomingPluginChannel(this, AdvancedLinks.CHANNEL_ID, this);

        messagesManager.sendColoredMessage(platformManager.getMessageReceiver(getServer().getConsoleSender()), "&aThe plugin has been successfully enabled! &7Version: " + this.getDescription().getVersion(), true);
    }

    @Override
    public void onDisable() {
        if (linksManager != null) linksManager.shutdown();

        getServer().getMessenger().unregisterOutgoingPluginChannel(this, AdvancedLinks.CHANNEL_ID);
        getServer().getMessenger().unregisterIncomingPluginChannel(this, AdvancedLinks.CHANNEL_ID);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        linksManager.sendLinks(platformManager.getLinkReceiver(event.getPlayer()));
    }

    @Override
    public boolean supportsPapi() {
        return isPapiInstalled;
    }

    @Override
    public ConfigManager getConfigManager() {
        return mainConfig;
    }

    @Override
    public PlatformManager getPlatformManager() {
        return platformManager;
    }

    @Override
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    @Override
    public ITasksManager getTasksManager() {
        return platformManager.getTasksManager();
    }

    @Override
    public LinksManager<ServerLinks.ServerLink, ServerLinks.Type> getLinksManager() {
        return linksManager;
    }

    @Override
    public void log(Level level, String message) {
        getLogger().log(level, message);
    }

    @Override
    public void logError(Level level, String message, Throwable error) {
        getLogger().log(level, message, error);
    }

    @Override
    public String replacePapiPlaceholders(String text) {
        if (isPapiInstalled) {
            return PlaceholderAPI.setPlaceholders(null, text);
        } else {
            return text;
        }
    }

    @Override
    public void onPluginMessageReceived(@NonNull String channel, @NonNull Player player, byte @NonNull [] message) {
        if (!channel.equals(AdvancedLinks.CHANNEL_ID)) return;

        String msg = new String(message, StandardCharsets.UTF_8);
        if (msg.equals(AdvancedLinks.INSTALLED_MSG)) {
            log(Level.SEVERE, "AdvancedLinks is installed in both a backend and the proxy server, which may cause unwanted problems.");
            log(Level.SEVERE, "Please remove it from either side. Keeping it on the proxy server is suggested for simplicity.");
            log(Level.SEVERE, "To prevent errors and duplicate or missing links, the plugin will disable for this server (proxy's will still work)");

            getServer().sendPluginMessage(this, AdvancedLinks.CHANNEL_ID, AdvancedLinks.DISABLED_MSG.getBytes(StandardCharsets.UTF_8));

            getServer().getPluginManager().disablePlugin(this);
        }
    }
}