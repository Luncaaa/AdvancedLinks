package me.lucaaa.advancedlinks.common.managers;

import me.lucaaa.advancedlinks.common.AdvancedLinks;

import java.io.File;
import java.util.Set;

public abstract class ConfigManager {
    protected final AdvancedLinks<?, ?> plugin;
    protected final File file;

    public ConfigManager(AdvancedLinks<?, ?> plugin, File file) {
        this.plugin = plugin;
        this.file = file;
    }

    public abstract void save();

    public abstract <T> void set(String path, T value);

    public <T> T getOrDefault(String path, T def) {
        return getOrDefault(path, def, true);
    }

    public abstract <T> T getOrDefault(String path, T def, boolean save);

    public abstract Set<String> getKeys(String path);

    public abstract void createSection(String path);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean isConfigurationSection(String path);

    public abstract boolean isString(String path);
}