package me.lucaaa.advancedlinks.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucaaa.advancedlinks.AdvancedLinks;
import me.lucaaa.advancedlinks.data.Link;
import me.lucaaa.advancedlinks.data.Ticking;
import org.bukkit.ServerLinks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;

@SuppressWarnings("UnstableApiUsage")
public class LinksManager extends Ticking {
    private final AdvancedLinks plugin;
    private final ConfigManager configManager;
    private final boolean isPapiInstalled;
    private final Map<String, Link> individualLinks = new HashMap<>();
    private final Map<String, ServerLinks.ServerLink> globalLinks = new HashMap<>();

    public LinksManager(AdvancedLinks plugin, ConfigManager configManager, boolean isPapiInstalled, boolean reload) {
        super(plugin);
        this.plugin = plugin;
        this.configManager = configManager;
        this.isPapiInstalled = isPapiInstalled;
        loadLinks();
        if (reload) sendLinks();
        startTicking();
    }

    private void loadLinks() {
        YamlConfiguration config = configManager.getConfig();

        if (!config.contains("links")) {
            plugin.log(Level.WARNING, "The config file does not have any \"links\" section! The server will not have any links.");
            return;
        }

        ConfigurationSection links = config.getConfigurationSection("links");
        linkMap: for (String key : Objects.requireNonNull(links).getKeys(false)) {
            if (!links.isConfigurationSection(key)) continue;

            String errorPrefix = "Error in link \"" + key + "\" - ";
            ConfigurationSection link = links.getConfigurationSection(key);
            assert link != null;

            if ((!link.contains("displayName") && !link.contains("type")) || !link.contains("url")) {
                plugin.log(Level.WARNING, errorPrefix + "It must have the properties \"displayName\" or \"type\" and \"url\"! This link will be ignored.");
                continue;
            }

            String displayName = link.getString("displayName", "No display name provided");

            ServerLinks.Type type = null;
            if (link.contains("type")) {
                try {
                    type = ServerLinks.Type.valueOf(Objects.requireNonNull(link.getString("type")).toUpperCase());
                } catch (IllegalArgumentException e) {
                    plugin.log(Level.WARNING, errorPrefix + "The type of the link is not valid! This link will be ignored.");
                    continue;
                }
            }

            String url = Objects.requireNonNull(link.getString("url"));
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                plugin.log(Level.WARNING, errorPrefix + "The URL in the \"url\" field is invalid! Make sure the URL starts with \"https://\" or \"http://\". This link will be ignored.");
                continue;
            }

            boolean isIndividual = link.getBoolean("individual", false);

            List<Link.Placeholder> placeholders = new ArrayList<>();
            for (Map<?, ?> entry : link.getMapList("placeholders")) {
                String match = (String) entry.get("match");
                if (match == null) {
                    plugin.log(Level.WARNING, errorPrefix + "Missing \"match\" field in some placeholder(s). This link will be ignored.");
                    continue linkMap;
                }

                String replacement = (String) entry.get("replacement");
                if (replacement == null) {
                    plugin.log(Level.WARNING, errorPrefix + "Missing \"replacement\" field in some placeholder(s). This link will be ignored.");
                    continue linkMap;
                }

                boolean usePapi = (boolean) Objects.requireNonNullElse(entry.get("parsePapi"), true);
                placeholders.add(new Link.Placeholder(match, replacement, usePapi));
            }

            Link configLink = new Link(key, displayName, type, url, isIndividual, placeholders);

            if (!isIndividual) {
                // Adds the link to the server's links.
                ServerLinks.ServerLink serverLink = parseLink(plugin.getServer().getServerLinks(), configLink, null);
                globalLinks.put(key, serverLink);
            } else {
                individualLinks.put(key, configLink);
            }
        }
    }

    public boolean addLink(String key, String value, String url) {
        if (individualLinks.containsKey(key) || globalLinks.containsKey(key)) return false;

        YamlConfiguration config = configManager.getConfig();
        ConfigurationSection link = Objects.requireNonNull(config.getConfigurationSection("links")).createSection(key);

        ServerLinks.Type type = null;
        String displayName = "";
        try {
            type = ServerLinks.Type.valueOf(value);
            link.set("type", value);

        } catch (IllegalArgumentException e) {
            displayName = value;
            link.set("displayName", value);
        }

        link.set("url", url);
        configManager.save();

        Link newLink = new Link(key, displayName, type, url, false, List.of());
        // Adds the link to the server's links.
        ServerLinks.ServerLink serverLink = parseLink(plugin.getServer().getServerLinks(), newLink, null);
        globalLinks.put(key, serverLink);
        sendLinks();

        return true;
    }

    public boolean removeLink(String key) {
        if (!individualLinks.containsKey(key) && !globalLinks.containsKey(key)) return false;

        YamlConfiguration config = configManager.getConfig();
        Objects.requireNonNull(config.getConfigurationSection("links")).set(key, null);
        configManager.save();

        if (globalLinks.containsKey(key)) {
            plugin.getServer().getServerLinks().removeLink(globalLinks.remove(key));
        } else {
            individualLinks.remove(key);
        }

        sendLinks();

        return true;
    }

    public void removeLinks() {
        for (ServerLinks.ServerLink link : globalLinks.values()) {
            plugin.getServer().getServerLinks().removeLink(link);
        }

        // This method is called when the plugin is reloaded. After this method, a new instance of this
        // class is initialized and links are sent to every player again, removing the old links.
        // Therefore, the individual links don't need to be removed.
    }

    public List<String> getKeys() {
        List<String> list = new ArrayList<>(globalLinks.keySet().stream().toList());
        list.addAll(individualLinks.keySet().stream().toList());
        return list;
    }

    private void sendLinks() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            sendLinks(player);
        }
    }

    public void sendLinks(Player player) {
        ServerLinks serverLinks = plugin.getServer().getServerLinks().copy();
        for (Link link : individualLinks.values()) {
            parseLink(serverLinks, link, player);
        }
        player.sendLinks(serverLinks);
    }

    private ServerLinks.ServerLink parseLink(ServerLinks links, Link link, Player player) {
        URI url = parseUrl(replacePlaceholders(link.url(), link.placeholders(), player), "Error in link \"" + link.name() + "\" - ");

        if (url == null) return null;

        if (link.type() == null) {
            String displayName = replacePlaceholders(link.displayName(), link.placeholders(), player);
            if (isPapiInstalled) displayName = PlaceholderAPI.setPlaceholders(player, displayName);
            return links.addLink(plugin.getMessagesManager().parseMessage(displayName), url);
        } else {
            return links.addLink(link.type(), url);
        }
    }

    private URI parseUrl(String url, String errorPrefix) {
        try {
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                plugin.log(Level.WARNING, errorPrefix + "The URL in the \"url\" field is invalid! Make sure the URL starts with \"https://\" or \"http://\". This link will be ignored.");
                return null;
            }
            return new URI(url);

        } catch (URISyntaxException e) {
            plugin.log(Level.WARNING, errorPrefix + "The URL in the \"url\" field is invalid! Make sure the URL starts with \"https://\" or \"http://\". This link will be ignored.");
            return null;
        }
    }

    private String replacePlaceholders(String text, List<Link.Placeholder> placeholders, Player player) {
        for (Link.Placeholder placeholder : placeholders) {
            String replacement = placeholder.replacement();

            if (placeholder.replacePapi() && isPapiInstalled) {
                replacement = PlaceholderAPI.setPlaceholders(player, replacement);
            }

            text = text.replace(placeholder.match(), replacement);
        }

        return text;
    }

    @Override
    public void tick() {
        sendLinks();
    }
}