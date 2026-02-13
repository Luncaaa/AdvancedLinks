package me.lucaaa.advancedlinks;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
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
import java.nio.charset.StandardCharsets;
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
    private final MinecraftChannelIdentifier channelId = MinecraftChannelIdentifier.from(AdvancedLinks.CHANNEL_ID);

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

        // Listen to the plugin messaging channel (to print a warning if it's present on both backend and proxy server)
        getServer().getChannelRegistrar().register(channelId);

        messagesManager.sendColoredMessage(getMessageReceiver(server.getConsoleCommandSource()), "&aThe plugin has been successfully enabled! &7Version: " + getVersion(), true);
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        if (linksManager != null) linksManager.shutdown();

        getServer().getChannelRegistrar().unregister(channelId);
    }

    @Subscribe
    public void onPlayerPostConnect(ServerPostConnectEvent event) {
        event.getPlayer().getCurrentServer().ifPresent(server -> {
            boolean couldSend = server.sendPluginMessage(channelId, AdvancedLinks.INSTALLED_MSG.getBytes(StandardCharsets.UTF_8));
            if (!couldSend) {
                log(Level.WARNING, "Couldn't send a plugin message notifying the plugin is installed.");
                log(Level.WARNING, "Player: " + event.getPlayer().getUsername() + " - Server: " + server.getServerInfo().getName());
            }
        });

        linksManager.sendLinks(new VelocityLinkReceiver(event.getPlayer()));
    }

    @Override
    public boolean supportsPapi() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return !getServer().isShuttingDown();
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

    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event) {
        if (!channelId.equals(event.getIdentifier())) return;

        event.setResult(PluginMessageEvent.ForwardResult.handled());

        if (!(event.getSource() instanceof ServerConnection backend)) return;

        String message = new String(event.getData(), StandardCharsets.UTF_8);
        if (message.equals(AdvancedLinks.DISABLED_MSG)) {
            log(Level.SEVERE, "AdvancedLinks is installed in both a backend and the proxy server, which may cause unwanted problems.");
            log(Level.SEVERE, "Please remove it from either side. Keeping it on the proxy server is suggested for simplicity.");
            log(Level.SEVERE, "AdvancedLinks has been disabled on server: " + backend.getServerInfo().getName());

            // Reload links manager in case disabling the plugin in the backend server somehow broke the links.
            if (linksManager != null) linksManager.shutdown();
            linksManager = new VelocityLinksManager(this, true);
        }
    }

    private String getVersion() {
        Optional<PluginContainer> plugin = server.getPluginManager().getPlugin(AdvancedLinks.ID);
        return plugin.map(pluginContainer -> pluginContainer.getDescription().getVersion().orElse("Unknown")).orElse("Unknown");
    }
}