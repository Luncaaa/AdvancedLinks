package me.lucaaa.advancedlinks.velocity.tasks;

import com.velocitypowered.api.scheduler.ScheduledTask;
import me.lucaaa.advancedlinks.common.tasks.ITask;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;
import me.lucaaa.advancedlinks.velocity.IVelocityAdvancedLinks;
import net.kyori.adventure.util.Ticks;

public class VelocityTasksManager implements ITasksManager {
    private final IVelocityAdvancedLinks plugin;

    public VelocityTasksManager(IVelocityAdvancedLinks plugin) {
        this.plugin = plugin;
    }

    @Override
    public ITask runTaskAsynchronously(Runnable task) {
        ScheduledTask bukkitTask = plugin.getServer().getScheduler().buildTask(plugin, task).schedule();
        return new VelocityTask(bukkitTask);
    }

    @Override
    public ITask runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        ScheduledTask bukkitTask = plugin.getServer().getScheduler().buildTask(plugin, task)
                .delay(Ticks.duration(delay))
                .repeat(Ticks.duration(period))
                .schedule();
        return new VelocityTask(bukkitTask);
    }
}