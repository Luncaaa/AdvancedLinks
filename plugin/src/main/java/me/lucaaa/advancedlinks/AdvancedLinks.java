package me.lucaaa.advancedlinks;

import me.lucaaa.advancedlinks.commands.MainCommand;
import me.lucaaa.advancedlinks.common.TasksManager;
import me.lucaaa.advancedlinks.listeners.PlayerListener;
import me.lucaaa.advancedlinks.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class AdvancedLinks extends JavaPlugin {
    // Config files.
    private ConfigManager mainConfig;

    // Managers.
    private me.lucaaa.advancedlinks.managers.TasksManager tasksManager;
    private MessagesManager messagesManager;
    private LinksManager linksManager;

    // Reload the config files.
    public void reloadConfigs() {
        // Other
        boolean isPapiInstalled = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        // Config file.
        mainConfig = new ConfigManager(this, "config.yml", true);

        // Managers
        if (linksManager != null) linksManager.shutdown();

        tasksManager = new me.lucaaa.advancedlinks.managers.TasksManager(this);
        messagesManager = new MessagesManager(mainConfig);
        linksManager = new LinksManager(this, mainConfig, isPapiInstalled, linksManager != null);
    }

    @Override
    public void onEnable() {
        // Set up files and managers.
        reloadConfigs();

        // Look for updates.
        if (mainConfig.getConfig().getBoolean("updateChecker", true)) {
            new UpdateManager(this).getVersion(v -> UpdateManager.sendStatus(this, v, getDescription().getVersion()));
        }

        // Register events.
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // Registers the main command and adds tab completions.
        MainCommand commandHandler = new MainCommand(this);
        Objects.requireNonNull(getCommand("al")).setExecutor(commandHandler);

        getServer().getConsoleSender().sendMessage(messagesManager.getColoredMessage("&aThe plugin has been successfully enabled! &7Version: " + this.getDescription().getVersion(), true));
    }

    @Override
    public void onDisable() {
        if (linksManager != null) linksManager.shutdown();
    }

    public ConfigManager getMainConfig() {
        return mainConfig;
    }

    public TasksManager getTasksManager() {
        return tasksManager.getTasksManager();
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public LinksManager getLinksManager() {
        return linksManager;
    }

    public void log(Level level, String message) {
        getLogger().log(level, message);
    }
}