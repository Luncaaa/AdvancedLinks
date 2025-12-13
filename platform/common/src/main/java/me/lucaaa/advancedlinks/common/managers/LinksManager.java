package me.lucaaa.advancedlinks.common.managers;

import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.Link;
import me.lucaaa.advancedlinks.common.data.LinkReceiver;
import me.lucaaa.advancedlinks.common.data.ParsedLink;
import me.lucaaa.advancedlinks.common.data.Parsers;
import me.lucaaa.advancedlinks.common.tasks.ITask;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;

/**
 * The base LinksManager class.
 * @param <T> The ServerLink class for the platform.
 * @param <S> The ServerLink.Type class for the platform.
 */
public abstract class LinksManager<T, S extends Enum<S>> {
    protected final AdvancedLinks plugin;
    private final ConfigManager config;
    private final Class<S> enumTypeClass;

    private final Map<String, Link<S>> individualLinks = new HashMap<>();
    private final Map<String, T> globalLinks = new HashMap<>();

    private final ITask tickingTask;
    // Because this class will update every x ticks, this method prevents the same error from occurring
    // every single time. To fix it, the player will have to reload the plugin, which will create a new
    // instance of this class and, therefore, resetting the list.
    private final List<Link<S>> disabledLinks = new ArrayList<>();

    public LinksManager(AdvancedLinks plugin, Class<S> enumTypeClass, boolean reload) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.enumTypeClass = enumTypeClass;
        loadLinks();

        if (reload) sendLinks();

        long updateTime = plugin.getConfigManager().getOrDefault("updateTime", 0);
        if (updateTime > 0) {
            this.tickingTask = plugin.getTasksManager().runTaskTimerAsynchronously(
                    this::sendLinks,
                    0L,
                    updateTime
            );
        } else {
            this.tickingTask = null;
        }
    }

    private void loadLinks() {
        if (!config.isConfigurationSection("links")) {
            plugin.log(Level.WARNING, "The config file does not have any \"links\" section! The server will not have any links.");
            return;
        }

        linkMap: for (String key : config.getKeys("links")) {
            String path = "links." + key;
            if (!config.isConfigurationSection(path)) continue;

            String errorPrefix = "Error in link \"" + key + "\" - ";

            if ((!config.isString(path + ".displayName") && !config.isString(path + ".type")) || !config.isString(path + ".url")) {
                plugin.log(Level.WARNING, errorPrefix + "It must have the properties \"displayName\" or \"type\" and \"url\"! This link will be ignored.");
                continue;
            }

            S type = null;
            if (config.isString(path + ".type")) {
                try {
                    type = S.valueOf(enumTypeClass, Objects.requireNonNull(config.getOrDefault(path + ".type", "")).toUpperCase());
                } catch (IllegalArgumentException e) {
                    plugin.log(Level.WARNING, errorPrefix + "The type of the link is not valid! This link will be ignored.");
                    continue;
                }
            }

            String url = Objects.requireNonNull(config.getOrDefault(path + ".url", "", false));
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                plugin.log(Level.WARNING, errorPrefix + "The URL in the \"url\" field is invalid! Make sure the URL starts with \"https://\" or \"http://\". This link will be ignored.");
                continue;
            }

            boolean isIndividual = config.getOrDefault(path + ".individual", false, false);

            List<Link.Placeholder> placeholders = new ArrayList<>();
            for (Map<?, ?> entry : config.getOrDefault(path + ".placeholders", (List<Map<?, ?>>) new ArrayList<Map<?, ?>>())) {
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

            String displayName = config.getOrDefault(path + ".displayName", "No display name provided");
            Link<S> configLink = new Link<>(key, displayName, type, url, isIndividual, placeholders);

            if (!isIndividual) {
                if (!isUrlValid(configLink, null)) return;

                // Adds the link to the server's links.
                T serverLink = loadGlobalLink(configLink);
                if (serverLink != null) globalLinks.put(key, serverLink);
            } else {
                individualLinks.put(key, configLink);
            }
        }
    }

    public boolean addLink(String key, String value, String url) {
        if (individualLinks.containsKey(key) || globalLinks.containsKey(key)) return false;

        String path = "links." + key;
        config.createSection(path);

        S type = null;
        String displayName = "";
        try {
            type = S.valueOf(enumTypeClass, value);
            config.set(path + ".type", value);

        } catch (IllegalArgumentException e) {
            displayName = value;
            config.set(path + ".displayName", value);
        }

        config.set(path + ".url", url);
        config.save();

        Link<S> newLink = new Link<>(key, displayName, type, url, false, List.of());
        // Adds the link to the server's links.
        T serverLink = loadGlobalLink(newLink);
        if (serverLink != null) globalLinks.put(key, serverLink);
        sendLinks();

        return true;
    }

    public boolean removeLink(String key) {
        if (!individualLinks.containsKey(key) && !globalLinks.containsKey(key)) return false;

        config.set("links." + key, null);
        config.save();

        if (globalLinks.containsKey(key)) {
            removeGlobalLink(globalLinks.remove(key));
        } else {
            individualLinks.remove(key);
        }

        sendLinks();

        return true;
    }

    public void shutdown() {
        for (T link : globalLinks.values()) {
            removeGlobalLink(link);
        }

        if (tickingTask != null) {
            tickingTask.cancel();
        }

        // This method is called when the plugin is reloaded. After this method, a new instance of this
        // class is initialized and links are sent to every player again, removing the old links.
        // Therefore, the individual links don't need to be removed.
    }

    public T loadGlobalLink(Link<S> link) {
        return createGlobalLink(new ParsedLink<>(
                Parsers.parseMessage(replacePlaceholders(link.displayName(), link.placeholders(), null)),
                link.type(),
                parseUrl(replacePlaceholders(link.url(), link.placeholders(), null), link.name())
        ));
    }

    public abstract T createGlobalLink(ParsedLink<S> link);
    public abstract void removeGlobalLink(T link);

    public List<String> getKeys() {
        List<String> list = new ArrayList<>(globalLinks.keySet().stream().toList());
        list.addAll(individualLinks.keySet().stream().toList());
        return list;
    }

    protected abstract void sendLinks();

    public void sendLinks(LinkReceiver<S> receiver) {
        receiver.sendLinks(
                individualLinks.values().stream()
                        .filter(link -> isUrlValid(link, receiver))
                        .map(link ->
                            new ParsedLink<>(
                                    Parsers.parseMessage(receiver.replacePapiPlaceholders(replacePlaceholders(link.displayName(), link.placeholders(), receiver))),
                                    link.type(),
                                    parseUrl(replacePlaceholders(link.url(), link.placeholders(), receiver), link.name())
                            )
                        ).toList()
        );
    }

    private boolean isUrlValid(Link<S> link, LinkReceiver<S> receiver) {
        if (disabledLinks.contains(link)) return false;

        String url = replacePlaceholders(link.url(), link.placeholders(), receiver);

        if (parseUrl(url, link.name()) == null) {
            disabledLinks.add(link);
            return false;
        }

        return true;
    }

    private URI parseUrl(String url, String name) {
        try {
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                plugin.log(Level.WARNING, "Error in link \"" + name + "\" - The URL in the \"url\" field is invalid! Make sure the URL starts with \"https://\" or \"http://\". This link will be ignored.");
                plugin.log(Level.WARNING, "Invalid link: " + url);
                return null;
            }

            return new URI(url);

        } catch (URISyntaxException e) {
            plugin.log(Level.WARNING, "Error in link \"" + name + "\" - The URL in the \"url\" field is invalid! Make sure the URL starts with \"https://\" or \"http://\". This link will be ignored.");
            plugin.log(Level.WARNING, "Invalid link: " + url);
            return null;
        }
    }

    private String replacePlaceholders(String text, List<Link.Placeholder> placeholders, LinkReceiver<S> receiver) {
        for (Link.Placeholder placeholder : placeholders) {
            String replacement = placeholder.replacement();

            if (placeholder.replacePapi()) {
                replacement = replacePapiPlaceholders(replacement, receiver);
            }

            text = text.replace(placeholder.match(), replacement);
        }
        return text;
    }

    protected abstract String replacePapiPlaceholders(String text, LinkReceiver<S> receiver);

    public Collection<T> getGlobalLinksCopy() {
        return Collections.unmodifiableCollection(globalLinks.values());
    }
}