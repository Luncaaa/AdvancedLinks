package me.lucaaa.advancedlinks.velocity.tasks;

import com.velocitypowered.api.scheduler.ScheduledTask;
import me.lucaaa.advancedlinks.common.tasks.ITask;

public class VelocityTask implements ITask {
    private final ScheduledTask task;

    public VelocityTask(ScheduledTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}