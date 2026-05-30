package me.lucaaa.advancedlinks.mod_common.tasks;

import me.lucaaa.advancedlinks.common.tasks.ITask;

import java.util.concurrent.ScheduledFuture;

public class ModTask implements ITask {
    private final ScheduledFuture<?> future;

    public ModTask(ScheduledFuture<?> future) {
        this.future = future;
    }

    @Override
    public void cancel() {
        future.cancel(false);
    }
}