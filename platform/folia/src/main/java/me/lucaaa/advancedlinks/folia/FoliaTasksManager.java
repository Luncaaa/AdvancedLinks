package me.lucaaa.advancedlinks.folia;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.lucaaa.advancedlinks.common.ITask;
import me.lucaaa.advancedlinks.common.TasksManager;
import org.bukkit.plugin.Plugin;

public class FoliaTasksManager implements TasksManager {
    @Override
    public ITask runTaskAsynchronously(Plugin plugin, Runnable task) {
        ScheduledTask foliaTask = plugin.getServer().getGlobalRegionScheduler().run(plugin, t -> task.run());
        return new FoliaTask(foliaTask);
    }

    @Override
    public ITask runTaskTimerAsynchronously(Plugin plugin, Runnable task, long delay, long period) {
        long parsedDelay = (delay == 0) ? 1L : delay;
        ScheduledTask foliaTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, t -> task.run(), parsedDelay, period);
        return new FoliaTask(foliaTask);
    }
}