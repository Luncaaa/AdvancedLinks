package me.lucaaa.advancedlinks.spigot.managers;

import me.lucaaa.advancedlinks.common.managers.ConfigManager;
import me.lucaaa.advancedlinks.spigot.ISpigotAdvancedLinks;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

public class SpigotConfigManager extends ConfigManager {
    private final YamlConfiguration config;

    public SpigotConfigManager(ISpigotAdvancedLinks plugin, File file) {
        super(plugin, file);

        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
        }

        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void save() {
        try {
            config.save(this.file);
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
        return Objects.requireNonNull(config.getConfigurationSection(path)).getKeys(false);
    }

    @Override
    public void createSection(String path) {
        config.createSection(path);
    }

    @Override
    public boolean isConfigurationSection(String path) {
        return config.isConfigurationSection(path);
    }

    @Override
    public boolean isString(String path) {
        return config.isString(path);
    }
}