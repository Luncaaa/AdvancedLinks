package me.lucaaa.advancedlinks;

import me.lucaaa.advancedlinks.bungeecord.IBungeeAdvancedLinks;
import me.lucaaa.advancedlinks.bungeecord.commands.BungeeMainCommand;
import me.lucaaa.advancedlinks.bungeecord.data.BungeeLinkReceiver;
import me.lucaaa.advancedlinks.bungeecord.managers.BungeeConfigManager;
import me.lucaaa.advancedlinks.bungeecord.managers.BungeeLinksManager;
import me.lucaaa.advancedlinks.bungeecord.tasks.BungeeTasksManager;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.managers.ConfigManager;
import me.lucaaa.advancedlinks.common.managers.LinksManager;
import me.lucaaa.advancedlinks.common.managers.MessagesManager;
import me.lucaaa.advancedlinks.common.managers.UpdateManager;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerLink;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class BungeeAdvancedLinks extends Plugin implements IBungeeAdvancedLinks, Listener {
    // Config files.
    private ConfigManager mainConfig;

    // Managers.
    private BungeeTasksManager tasksManager;
    private MessagesManager messagesManager;
    private BungeeLinksManager linksManager;

    // Others.
    private boolean isEnabled;

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
            UpdateManager updateManager = new UpdateManager(this);
            updateManager.getVersion(v -> updateManager.sendStatus(getMessageReceiver(getProxy().getConsole()), v, getDescription().getVersion()));
        }

        // Register events. Not needed because the main class is automatically registered.
        getProxy().getPluginManager().registerListener(this, this);

        // Registers the main command and adds tab completions.
        new BungeeMainCommand(this);

        // Listen to the plugin messaging channel (to print a warning if it's present on both backend and proxy server)
        getServer().registerChannel(AdvancedLinks.CHANNEL_ID);

        isEnabled = true;

        messagesManager.sendColoredMessage(getMessageReceiver(getProxy().getConsole()), "&aThe plugin has been successfully enabled! &7Version: " + getDescription().getVersion(), true);
    }

    @Override
    public void onDisable() {
        if (linksManager != null) linksManager.shutdown();

        getProxy().unregisterChannel(AdvancedLinks.CHANNEL_ID);
    }

    @EventHandler
    public void onPlayerConnect(ServerConnectedEvent event) {
        isEnabled = false;

        event.getPlayer().getServer().sendData(AdvancedLinks.CHANNEL_ID, AdvancedLinks.INSTALLED_MSG.getBytes(StandardCharsets.UTF_8));

        linksManager.sendLinks(new BungeeLinkReceiver(event.getPlayer()));
    }

    @Override
    public boolean supportsPapi() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
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
    public LinksManager<ServerLink, ServerLink.LinkType> getLinksManager() {
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
        return text;
    }

    @Override
    public ProxyServer getServer() {
        return super.getProxy();
    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (!AdvancedLinks.CHANNEL_ID.equals(event.getTag())) return;

        if (!(event.getSender() instanceof Server backend)) return;

        String message = new String(event.getData(), StandardCharsets.UTF_8);
        if (message.equals(AdvancedLinks.DISABLED_MSG)) {
            log(Level.SEVERE, "AdvancedLinks is installed in both a backend and the proxy server, which may cause unwanted problems.");
            log(Level.SEVERE, "Please remove it from either side. Keeping it on the proxy server is suggested for simplicity.");
            log(Level.SEVERE, "AdvancedLinks has been disabled on server: " + backend.getInfo().getName());

            // Reload links manager in case disabling the plugin in the backend server somehow broke the links.
            if (linksManager != null) linksManager.shutdown();
            linksManager = new BungeeLinksManager(this, true);
        }
    }
}