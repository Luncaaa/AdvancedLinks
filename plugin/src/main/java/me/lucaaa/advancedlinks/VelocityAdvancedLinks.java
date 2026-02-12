package me.lucaaa.advancedlinks;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.ServerLink;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.managers.ConfigManager;
import me.lucaaa.advancedlinks.common.managers.LinksManager;
import me.lucaaa.advancedlinks.common.managers.MessagesManager;
import me.lucaaa.advancedlinks.common.managers.UpdateManager;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;
import me.lucaaa.advancedlinks.velocity.IVelocityAdvancedLinks;
import me.lucaaa.advancedlinks.velocity.commands.VelocityMainCommand;
import me.lucaaa.advancedlinks.velocity.data.VelocityLinkReceiver;
import me.lucaaa.advancedlinks.velocity.managers.VelocityConfigManager;
import me.lucaaa.advancedlinks.velocity.managers.VelocityLinksManager;
import me.lucaaa.advancedlinks.velocity.tasks.VelocityTasksManager;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Plugin(id = AdvancedLinks.ID, name = AdvancedLinks.NAME, version = AdvancedLinks.VERSION,
        url = "https://github.com/Luncaaa/AdvancedLinks/", description = "Create server links in the links menu!", authors = {"Lucaaa"})
public class VelocityAdvancedLinks implements IVelocityAdvancedLinks {
    // Config files.
    private ConfigManager mainConfig;

    // Managers.
    private VelocityTasksManager tasksManager;
    private MessagesManager messagesManager;
    private VelocityLinksManager linksManager;

    // Others.
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public VelocityAdvancedLinks(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Override
    public void reloadConfigs() {
        logger.log(Level.INFO, "Detected platform: Velocity. Enabling support...");
        logger.log(Level.WARNING, "The proxy version of the plugin does not support PlaceholderAPI!");

        // Config file.
        mainConfig = new VelocityConfigManager(this, new File(dataDirectory.toAbsolutePath() + File.separator + "config.yml"));

        // Managers
        if (linksManager != null) linksManager.shutdown();

        tasksManager = new VelocityTasksManager(this);
        messagesManager = new MessagesManager(mainConfig.getOrDefault("prefix", "&7[&6AL&7]"));
        linksManager = new VelocityLinksManager(this, linksManager != null);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Set up files and managers.
        reloadConfigs();

        // Look for updates.
        if (mainConfig.getOrDefault("updateChecker", true)) {
            UpdateManager updateManager = new UpdateManager(this);
            updateManager.getVersion(v -> updateManager.sendStatus(getMessageReceiver(server.getConsoleCommandSource()), v, getVersion()));
        }

        // Register events. Not needed because the main class is automatically registered.
        // server.getEventManager().register(this, this);

        // Registers the main command and adds tab completions.
        new VelocityMainCommand(this);

        messagesManager.sendColoredMessage(getMessageReceiver(server.getConsoleCommandSource()), "&aThe plugin has been successfully enabled! &7Version: " + getVersion(), true);
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        if (linksManager != null) linksManager.shutdown();
    }

    @Subscribe
    public void onPlayerConnect(ServerPostConnectEvent event) {
        linksManager.sendLinks(new VelocityLinkReceiver(event.getPlayer()));
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
    public LinksManager<ServerLink, ServerLink.Type> getLinksManager() {
        return linksManager;
    }

    @Override
    public void log(Level level, String message) {
        logger.log(level, message);
    }

    @Override
    public void logError(Level level, String message, Throwable error) {
        logger.log(level, message, error);
    }

    @Override
    public String replacePapiPlaceholders(String text) {
        return text;
    }

    @Override
    public ProxyServer getServer() {
        return server;
    }

    private String getVersion() {
        Optional<PluginContainer> plugin = server.getPluginManager().getPlugin(AdvancedLinks.ID);
        return plugin.map(pluginContainer -> pluginContainer.getDescription().getVersion().orElse("Unknown")).orElse("Unknown");
    }
}