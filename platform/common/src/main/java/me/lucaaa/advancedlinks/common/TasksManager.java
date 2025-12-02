package me.lucaaa.advancedlinks.common;

import org.bukkit.plugin.Plugin;

@SuppressWarnings("UnusedReturnValue")
public interface TasksManager {
    ITask runTaskAsynchronously(Plugin plugin, Runnable task);
    ITask runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period);
}