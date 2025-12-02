package me.lucaaa.advancedlinks.spigot;

import me.lucaaa.advancedlinks.common.ITask;
import me.lucaaa.advancedlinks.common.TasksManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class SpigotTasksManager implements TasksManager {
    @Override
    public ITask runTaskAsynchronously(Plugin plugin, Runnable task) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
        return new SpigotTask(bukkitTask);
    }

    @Override
    public ITask runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) {
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delay, period);
        return new SpigotTask(bukkitTask);
    }
}