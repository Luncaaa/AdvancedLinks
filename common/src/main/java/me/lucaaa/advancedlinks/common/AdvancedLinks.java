package me.lucaaa.advancedlinks.common;

import me.lucaaa.advancedlinks.common.managers.ConfigManager;
import me.lucaaa.advancedlinks.common.managers.LinksManager;
import me.lucaaa.advancedlinks.common.managers.MessagesManager;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;

import java.util.logging.Level;

public interface AdvancedLinks<T, S extends Enum<S>> {
    String ID = "advancedlinks";
    String NAME = "AdvancedLinks";
    String VERSION = "2.1";
    String CHANNEL_ID = ID + ":main";
    String INSTALLED_MSG = "al-installed"; // Used for backend-server plugin messaging.
    String DISABLED_MSG = "al-disabled"; // Used for backend-server plugin messaging.

    void reloadConfigs();

    boolean supportsPapi();

    boolean isEnabled();

    ConfigManager getConfigManager();

    MessagesManager getMessagesManager();

    ITasksManager getTasksManager();

    LinksManager<T, S> getLinksManager();

    void log(Level level, String message);

    void logError(Level level, String message, Throwable error);

    String replacePapiPlaceholders(String text);
}