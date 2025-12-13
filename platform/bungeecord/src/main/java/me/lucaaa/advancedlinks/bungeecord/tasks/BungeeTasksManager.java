package me.lucaaa.advancedlinks.bungeecord.tasks;

import me.lucaaa.advancedlinks.bungeecord.IBungeeAdvancedLinks;
import me.lucaaa.advancedlinks.common.tasks.ITask;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class BungeeTasksManager implements ITasksManager {
    private final IBungeeAdvancedLinks plugin;

    public BungeeTasksManager(IBungeeAdvancedLinks plugin) {
        this.plugin = plugin;
    }

    @Override
    public ITask runTaskAsynchronously(Runnable task) {
        ScheduledTask bukkitTask = plugin.getServer().getScheduler().runAsync((Plugin) plugin, task);
        return new BungeeTask(bukkitTask);
    }

    @Override
    public ITask runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        ScheduledTask bukkitTask = plugin.getServer().getScheduler().schedule((Plugin) plugin, task, 50 * delay, 50 * period, TimeUnit.MILLISECONDS);
        return new BungeeTask(bukkitTask);
    }
}