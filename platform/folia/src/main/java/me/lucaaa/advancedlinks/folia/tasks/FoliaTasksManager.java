package me.lucaaa.advancedlinks.folia.tasks;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.lucaaa.advancedlinks.common.tasks.ITask;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;
import org.bukkit.plugin.Plugin;

public class FoliaTasksManager implements ITasksManager {
    private final Plugin plugin;

    public FoliaTasksManager(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ITask runTaskAsynchronously(Runnable task) {
        ScheduledTask foliaTask = plugin.getServer().getGlobalRegionScheduler().run(plugin, t -> task.run());
        return new FoliaTask(foliaTask);
    }

    @Override
    public ITask runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        long parsedDelay = (delay == 0) ? 1L : delay;
        ScheduledTask foliaTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, t -> task.run(), parsedDelay, period);
        return new FoliaTask(foliaTask);
    }
}