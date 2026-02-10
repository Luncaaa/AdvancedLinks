package me.lucaaa.advancedlinks;

import me.lucaaa.advancedlinks.bungeecord.IBungeeAdvancedLinks;
import me.lucaaa.advancedlinks.bungeecord.commands.BungeeMainCommand;
import me.lucaaa.advancedlinks.bungeecord.data.BungeeLinkReceiver;
import me.lucaaa.advancedlinks.bungeecord.data.BungeeMessageReceiver;
import me.lucaaa.advancedlinks.bungeecord.managers.BungeeConfigManager;
import me.lucaaa.advancedlinks.bungeecord.managers.BungeeLinksManager;
import me.lucaaa.advancedlinks.bungeecord.tasks.BungeeTasksManager;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import me.lucaaa.advancedlinks.common.managers.ConfigManager;
import me.lucaaa.advancedlinks.common.managers.LinksManager;
import me.lucaaa.advancedlinks.common.managers.MessagesManager;
import me.lucaaa.advancedlinks.common.managers.UpdateManager;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.util.logging.Level;

public class BungeeAdvancedLinks extends Plugin implements IBungeeAdvancedLinks, Listener {
    // Config files.
    private ConfigManager mainConfig;

    // Managers.
    private BungeeTasksManager tasksManager;
    private MessagesManager messagesManager;
    private BungeeLinksManager linksManager;

    @Override
    public void reloadConfigs() {
        getLogger().log(Level.INFO, "Detected platform: Velocity. Enabling support...");
        getLogger().log(Level.WARNING, "The proxy version of the plugin does not support PlaceholderAPI!");

        // Config file.
        mainConfig = new BungeeConfigManager(this, new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml"));

        // Managers
        if (linksManager != null) linksManager.shutdown();

        tasksManager = new BungeeTasksManager(this);
        messagesManager = new MessagesManager(mainConfig.getOrDefault("prefix", "&7[&6AL&7]"));
        linksManager = new BungeeLinksManager(this, linksManager != null);
    }

    @Override
    public void onEnable() {
        // Set up files and managers.
        reloadConfigs();

        // Look for updates.
        if (mainConfig.getOrDefault("updateChecker", true)) {
            new UpdateManager(this).getVersion(v -> UpdateManager.sendStatus(this, getMessageReceiver(getProxy().getConsole()), v, getDescription().getVersion()));
        }

        // Register events. Not needed because the main class is automatically registered.
        getProxy().getPluginManager().registerListener(this, this);

        // Registers the main command and adds tab completions.
        new BungeeMainCommand(this);

        messagesManager.sendColoredMessage(getMessageReceiver(getProxy().getConsole()), "&aThe plugin has been successfully enabled! &7Version: " + getDescription().getVersion(), true);
    }

    @Override
    public void onDisable() {
        if (linksManager != null) linksManager.shutdown();
    }

    @EventHandler
    public void onPlayerConnect(ServerConnectedEvent event) {
        linksManager.sendLinks(new BungeeLinkReceiver(event.getPlayer()));
    }

    @Override
    public boolean supportsPapi() {
        return false;
    }

    @Override
    public ConfigManager getConfigManager() {
        return mainConfig;
    }

    @Override
    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    @Override
    public ITasksManager getTasksManager() {
        return tasksManager;
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

    @Override
    public String replacePapiPlaceholders(String text) {
        return text;
    }

    @Override
    public ProxyServer getServer() {
        return super.getProxy();
    }

    @Override
    public MessageReceiver getMessageReceiver(CommandSender source) {
        return new BungeeMessageReceiver(source);
    }
}