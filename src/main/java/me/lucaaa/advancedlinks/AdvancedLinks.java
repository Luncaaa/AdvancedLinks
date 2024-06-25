package me.lucaaa.advancedlinks;

import me.lucaaa.advancedlinks.commands.MainCommand;
import me.lucaaa.advancedlinks.managers.ConfigManager;
import me.lucaaa.advancedlinks.managers.LinksManager;
import me.lucaaa.advancedlinks.managers.MessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class AdvancedLinks extends JavaPlugin {
    // Config file.
    private ConfigManager mainConfig;

    // Managers.
    private MessagesManager messagesManager;
    private LinksManager linksManager;

    // Reload the config files.
    public void reloadConfigs() {
        // Creates the config file.
        if (!new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml").exists())
            saveResource("config.yml", false);

        mainConfig = new ConfigManager(this, "config.yml");

        // Managers
        messagesManager = new MessagesManager(this.mainConfig);
        if (linksManager != null) linksManager.removeLinks();
        linksManager = new LinksManager(this, this.mainConfig, linksManager != null);
    }

    @Override
    public void onEnable() {
        // Set up files and managers.
        reloadConfigs();

        // Registers the main command and adds tab completions.
        MainCommand commandHandler = new MainCommand(this);
        Objects.requireNonNull(this.getCommand("al")).setExecutor(commandHandler);
        Objects.requireNonNull(this.getCommand("al")).setTabCompleter(commandHandler);

        Bukkit.getConsoleSender().sendMessage(messagesManager.getColoredMessage("&aThe plugin has been successfully enabled! &7Version: " + this.getDescription().getVersion(), true));
    }

    public ConfigManager getMainConfig() {
        return this.mainConfig;
    }

    public MessagesManager getMessagesManager() {
        return this.messagesManager;
    }

    public LinksManager getLinksManager() {
        return this.linksManager;
    }
}