package me.lucaaa.advancedlinks.managers;

import me.lucaaa.advancedlinks.AdvancedLinks;
import me.lucaaa.advancedlinks.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ServerLinks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

@SuppressWarnings("UnstableApiUsage")
public class LinksManager {
    private final AdvancedLinks plugin;
    private final ConfigManager configManager;
    private final Map<String, ServerLinks.ServerLink> links = new HashMap<>();

    public LinksManager(AdvancedLinks plugin, ConfigManager configManager, boolean reload) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.loadLinks();
        if (reload) sendLinks();
    }

    private void loadLinks() {
        YamlConfiguration config = configManager.getConfig();

        if (!config.contains("links")) {
            Logger.log(Level.WARNING, "The config file does not have any \"links\" section! The server will not have any links.");
            return;
        }

        ConfigurationSection links = config.getConfigurationSection("links");
        for (String key : Objects.requireNonNull(links).getKeys(false)) {
            if (!links.isConfigurationSection(key)) continue;

            ConfigurationSection link = links.getConfigurationSection(key);
            assert link != null;

            if ((!link.contains("displayName") && !link.contains("type")) || !link.contains("url")) {
                Logger.log(Level.WARNING, "Error in link \"" + key + "\" - It must have the properties \"displayName\" or \"type\" and \"url\"! This link will be ignored.");
                continue;
            }

            ServerLinks.Type type = null;
            if (link.contains("type")) {
                try {
                    type = ServerLinks.Type.valueOf(Objects.requireNonNull(link.getString("type")).toUpperCase());
                } catch (IllegalArgumentException e) {
                    Logger.log(Level.WARNING, "Error in link \"" + key + "\" - The type of the link is not valid! This link will be ignored.");
                    return;
                }
            }

            URI url;
            try {
                String configUrl = Objects.requireNonNull(link.getString("url"));
                if (!configUrl.startsWith("https://") && !configUrl.startsWith("http://")) throw new URISyntaxException(configUrl, "The url must start with \"https://\" or \"http://\"");
                url = new URI(configUrl);
            } catch (URISyntaxException e) {
                Logger.log(Level.WARNING, "Error in link \"" + key + "\" - The URL in the \"url\" field is invalid! The url must start with \"https://\" or \"http://\". This link will be ignored.");
                return;
            }

            ServerLinks.ServerLink serverLink;
            if (type == null) {
                serverLink = Bukkit.getServerLinks().addLink(plugin.getMessagesManager().parseMessage(Objects.requireNonNull(link.getString("displayName"))), url);
            } else {
                serverLink = Bukkit.getServerLinks().addLink(type, url);
            }

            this.links.put(key, serverLink);
        }
    }

    public boolean addLink(String key, String displayName, URI url) {
        if (this.links.containsKey(key)) return false;

        YamlConfiguration config = configManager.getConfig();
        ConfigurationSection link = Objects.requireNonNull(config.getConfigurationSection("links")).createSection(key);
        link.set("displayName", displayName);
        link.set("url", url.toASCIIString());
        configManager.save();

        ServerLinks.ServerLink serverLink = Bukkit.getServerLinks().addLink(plugin.getMessagesManager().parseMessage(displayName), url);
        this.links.put(key, serverLink);
        sendLinks();

        return true;
    }

    public boolean addLink(String key, ServerLinks.Type type, URI url) {
        if (this.links.containsKey(key)) return false;

        YamlConfiguration config = configManager.getConfig();
        ConfigurationSection link = Objects.requireNonNull(config.getConfigurationSection("links")).createSection(key);
        link.set("type", type.name());
        link.set("url", url.toASCIIString());
        configManager.save();

        ServerLinks.ServerLink serverLink = Bukkit.getServerLinks().addLink(type, url);
        this.links.put(key, serverLink);
        sendLinks();

        return true;
    }

    public boolean removeLink(String key) {
        if (!this.links.containsKey(key)) return false;

        YamlConfiguration config = configManager.getConfig();
        Objects.requireNonNull(config.getConfigurationSection("links")).set(key, null);
        configManager.save();

        ServerLinks.ServerLink serverLink = this.links.remove(key);
        Bukkit.getServerLinks().removeLink(serverLink);
        sendLinks();

        return true;
    }

    public void removeLinks() {
        for (ServerLinks.ServerLink link : links.values()) {
            Bukkit.getServerLinks().removeLink(link);
        }

        // Not necessary for now because a new instance of this class is created when the plugin is reloaded.
        // links.clear();
    }

    public List<String> getKeys() {
        return this.links.keySet().stream().toList();
    }

    private void sendLinks() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendLinks(Bukkit.getServerLinks());
        }
    }
}