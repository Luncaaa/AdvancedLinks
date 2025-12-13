package me.lucaaa.advancedlinks.bungeecord.tasks;

import me.lucaaa.advancedlinks.common.tasks.ITask;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class BungeeTask implements ITask {
    private final ScheduledTask task;

    public BungeeTask(ScheduledTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}