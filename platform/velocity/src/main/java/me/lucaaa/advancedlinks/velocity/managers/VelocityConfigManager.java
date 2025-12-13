package me.lucaaa.advancedlinks.velocity.managers;

import me.lucaaa.advancedlinks.common.managers.ConfigManager;
import me.lucaaa.advancedlinks.velocity.IVelocityAdvancedLinks;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class VelocityConfigManager extends ConfigManager {
    private final YamlConfigurationLoader loader;
    private CommentedConfigurationNode root;

    public VelocityConfigManager(IVelocityAdvancedLinks plugin, File file) {
        super(plugin, file);

        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                FileOutputStream outputStream = new FileOutputStream(file);
                if (in != null) {
                    in.transferTo(outputStream);
                } else {
                    plugin.log(Level.SEVERE, "Couldn't find config.yml file in the JAR!");
                }
            } catch (IOException e) {
                plugin.logError(Level.SEVERE, "Failed to create configuration file: " + file.getName(), e);
            }
        }

        this.loader = YamlConfigurationLoader.builder().file(file).build();

        try {
            this.root = loader.load();
        } catch (ConfigurateException e) {
            plugin.logError(Level.SEVERE, "Failed to load configuration file: " + file.getName(), e);
            this.root = loader.createNode();
        }
    }

    private Object[] splitPath(String path) {
        return path.split("\\.");
    }

    @Override
    public void save() {
        try {
            loader.save(root);
        } catch (ConfigurateException e) {
            plugin.logError(Level.SEVERE, "Failed to save the config file!", e);
        }
    }

    @Override
    public <T> void set(String path, T value) {
        try {
            root.node(splitPath(path)).set(value);
        } catch (SerializationException e) {
            plugin.logError(Level.SEVERE, "Could not set config value \"" + path + "\" to \"" + value + "\"", e);
        }
    }

    @Override
    public <T> T getOrDefault(String path, T def, boolean save) {
        Object[] nodePath = splitPath(path);
        CommentedConfigurationNode node = root.node(nodePath);

        if (node.virtual()) {
            if (save) {
                plugin.log(Level.WARNING, "Missing setting \"" + path + "\" in \"" + file.getName() + "\" file! Setting to default value: " + def);
                set(path, def);
                save();
            }

            return def;
        }

        try {
            Object value;

            if (def instanceof List) {
                value = (node.isList()) ? node.childrenList().stream().map(CommentedConfigurationNode::raw).toList() : null;
            } else {
                @SuppressWarnings("unchecked")
                Class<T> clazz = (Class<T>) def.getClass();
                value = node.get(clazz);
            }

            if (value == null) {
                plugin.log(Level.WARNING, "Setting \"" + path + "\" is not a \"" + def.getClass().getSimpleName() + "\" value in \"" + file.getName() + "\" file! Setting to default value: " + def);
                return def;
            }

            //noinspection unchecked
            return (T) value;

        } catch (SerializationException e) {
            plugin.logError(Level.WARNING, "Couldn't get value \"" + path + "\". Setting to default value: " + def, e);
            return def;
        }
    }

    @Override
    public Set<String> getKeys(String path) {
        CommentedConfigurationNode node = root.node(splitPath(path));

        if (node.virtual() || !node.isMap()) {
            return Collections.emptySet();
        }

        return node.childrenMap().keySet().stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    @Override
    public void createSection(String path) {
        try {
            root.node(splitPath(path)).set(new HashMap<>());
        } catch (SerializationException e) {
            plugin.logError(Level.SEVERE, "Couldn't create config section \"" + path + "\"", e);
        }
    }

    @Override
    public boolean isConfigurationSection(String path) {
        CommentedConfigurationNode node = root.node(splitPath(path));
        return !node.virtual() && node.isMap();
    }

    @Override
    public boolean isString(String path) {
        CommentedConfigurationNode node = root.node(splitPath(path));
        return !node.virtual() && node.raw() instanceof String;
    }
}