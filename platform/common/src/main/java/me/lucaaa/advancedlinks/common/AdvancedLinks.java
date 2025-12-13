package me.lucaaa.advancedlinks.common;

import me.lucaaa.advancedlinks.common.managers.ConfigManager;
import me.lucaaa.advancedlinks.common.managers.LinksManager;
import me.lucaaa.advancedlinks.common.managers.MessagesManager;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;

import java.util.logging.Level;

public interface AdvancedLinks {
    void reloadConfigs();

    boolean supportsPapi();

    ConfigManager getConfigManager();

    MessagesManager getMessagesManager();

    ITasksManager getTasksManager();

    <T, S extends Enum<S>> LinksManager<T, S> getLinksManager();

    void log(Level level, String message);
}