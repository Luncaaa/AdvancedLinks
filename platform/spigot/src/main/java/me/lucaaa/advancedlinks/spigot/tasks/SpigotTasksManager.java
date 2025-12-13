package me.lucaaa.advancedlinks.spigot.tasks;

import me.lucaaa.advancedlinks.common.tasks.ITask;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;
import me.lucaaa.advancedlinks.spigot.ISpigotAdvancedLinks;
import org.bukkit.scheduler.BukkitTask;

public class SpigotTasksManager implements ITasksManager {
    private final ISpigotAdvancedLinks plugin;

    public SpigotTasksManager(ISpigotAdvancedLinks plugin) {
        this.plugin = plugin;
    }

    @Override
    public ITask runTaskAsynchronously(Runnable task) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
        return new SpigotTask(bukkitTask);
    }

    @Override
    public ITask runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
        return new SpigotTask(bukkitTask);
    }
}