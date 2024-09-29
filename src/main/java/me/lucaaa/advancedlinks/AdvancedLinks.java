package me.lucaaa.advancedlinks;

import me.lucaaa.advancedlinks.commands.MainCommand;
import me.lucaaa.advancedlinks.managers.ConfigManager;
import me.lucaaa.advancedlinks.managers.LinksManager;
import me.lucaaa.advancedlinks.managers.MessagesManager;
import me.lucaaa.advancedlinks.managers.UpdateManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.logging.Level;

public class AdvancedLinks extends JavaPlugin {
    // Managers.
    private MessagesManager messagesManager;
    private LinksManager linksManager;

    // Reload the config files.
    public void reloadConfigs() {
        // Creates the config file.
        if (!new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml").exists())
            saveResource("config.yml", false);

        // Config file.
        ConfigManager mainConfig = new ConfigManager(this, "config.yml");

        // Managers
        messagesManager = new MessagesManager(mainConfig);
        if (linksManager != null) linksManager.removeLinks();
        linksManager = new LinksManager(this, mainConfig, linksManager != null);
    }

    @Override
    public void onEnable() {
        // Set up files and managers.
        reloadConfigs();

        // Look for updates.
        new UpdateManager(this).getVersion(v -> UpdateManager.sendStatus(this, v, getDescription().getVersion()));

        // Registers the main command and adds tab completions.
        MainCommand commandHandler = new MainCommand(this);
        Objects.requireNonNull(getCommand("al")).setExecutor(commandHandler);

        getServer().getConsoleSender().sendMessage(messagesManager.getColoredMessage("&aThe plugin has been successfully enabled! &7Version: " + this.getDescription().getVersion(), true));
    }

    public void log(Level level, String message) {
        getLogger().log(level, message);
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public LinksManager getLinksManager() {
        return linksManager;
    }
}