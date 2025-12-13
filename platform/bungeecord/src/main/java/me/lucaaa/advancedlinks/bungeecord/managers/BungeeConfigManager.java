package me.lucaaa.advancedlinks.bungeecord.managers;

import me.lucaaa.advancedlinks.bungeecord.IBungeeAdvancedLinks;
import me.lucaaa.advancedlinks.common.managers.ConfigManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

public class BungeeConfigManager extends ConfigManager {
    private final Configuration config;

    public BungeeConfigManager(IBungeeAdvancedLinks plugin, File file) {
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

        try {
            this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            plugin.logError(Level.SEVERE, "Failed to load configuration file: " + file.getName(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void set(String path, T value) {
        config.set(path, value);
    }

    @Override
    public <T> T getOrDefault(String path, T def, boolean save) {
        if (!config.contains(path)) {
            if (save) {
                plugin.log(Level.WARNING, "Missing setting \"" + path + "\" in \"" + file.getName() + "\" file! Setting to default value: " + def);
                config.set(path, def);
                save();
            }

            return def;
        }

        Object data = config.get(path);
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) def.getClass();

        if (!clazz.isInstance(data)) {
            plugin.log(Level.WARNING, "Setting \"" + path + "\" is not a \"" + clazz.getSimpleName() + "\" value in \"" + file.getName() + "\" file! Setting to default value: " + def);
            // Config value won't be set in case the user just forgot the quotes (so he doesn't lose data).
            // config.set(setting, def);
            return def;
        }

        return clazz.cast(data);
    }

    @Override
    public Set<String> getKeys(String path) {
        return (Set<String>) Objects.requireNonNull(config.getSection(path)).getKeys();
    }

    @Override
    public void createSection(String path) {
        config.set(path, new HashMap<>());
    }

    @Override
    public boolean isConfigurationSection(String path) {
        return config.getSection(path) != null;
    }

    @Override
    public boolean isString(String path) {
        return config.get(path) instanceof String;
    }
}