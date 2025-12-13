package me.lucaaa.advancedlinks;

import me.lucaaa.advancedlinks.common.managers.*;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;
import me.lucaaa.advancedlinks.spigot.ISpigotAdvancedLinks;
import me.lucaaa.advancedlinks.spigot.managers.PlatformManager;
import me.lucaaa.advancedlinks.spigot.managers.SpigotConfigManager;
import me.lucaaa.advancedlinks.spigot.managers.SpigotLinksManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class SpigotAdvancedLinks extends JavaPlugin implements ISpigotAdvancedLinks, Listener {
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
            new UpdateManager(this).getVersion(v -> UpdateManager.sendStatus(this, platformManager.getMessageReceiver(getServer().getConsoleSender()), v, getDescription().getVersion()));
        }

        // Register events.
        getServer().getPluginManager().registerEvents(this, this);

        // Registers the main command and adds tab completions.
        platformManager.registerMainCommand();

        messagesManager.sendColoredMessage(platformManager.getMessageReceiver(getServer().getConsoleSender()), "&aThe plugin has been successfully enabled! &7Version: " + this.getDescription().getVersion(), true);
    }

    @Override
    public void onDisable() {
        if (linksManager != null) linksManager.shutdown();
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
    @SuppressWarnings("unchecked")
    public <T, S extends Enum<S>> LinksManager<T, S> getLinksManager() {
        return (LinksManager<T, S>) linksManager;
    }

    @Override
    public void log(Level level, String message) {
        getLogger().log(level, message);
    }

    @Override
    public void logError(Level level, String message, Throwable error) {
        getLogger().log(level, message, error);
    }
}