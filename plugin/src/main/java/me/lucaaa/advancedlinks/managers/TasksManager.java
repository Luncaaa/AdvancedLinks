package me.lucaaa.advancedlinks.managers;

import me.lucaaa.advancedlinks.AdvancedLinks;
import me.lucaaa.advancedlinks.folia.FoliaTasksManager;
import me.lucaaa.advancedlinks.spigot.SpigotTasksManager;

import java.util.logging.Level;

public class TasksManager {
    private final me.lucaaa.advancedlinks.common.TasksManager tasksManager;

    public TasksManager(AdvancedLinks plugin) {
        if (isFolia()) {
            plugin.log(Level.INFO, "Using the Folia tasks manager.");
            tasksManager = new FoliaTasksManager();
        } else {
            plugin.log(Level.INFO, "Using the Paper/Spigot tasks manager.");
            tasksManager = new SpigotTasksManager();
        }
    }

    public me.lucaaa.advancedlinks.common.TasksManager getTasksManager() {
        return tasksManager;
    }

    private boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}