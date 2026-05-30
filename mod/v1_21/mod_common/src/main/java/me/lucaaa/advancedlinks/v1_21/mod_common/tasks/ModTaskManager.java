package me.lucaaa.advancedlinks.mod_common.tasks;

import me.lucaaa.advancedlinks.common.tasks.ITask;
import me.lucaaa.advancedlinks.common.tasks.ITasksManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ModTaskManager implements ITasksManager {
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);

    @Override
    public ITask runTaskAsynchronously(Runnable task) {
        ScheduledFuture<?> future = (ScheduledFuture<?>) executor.submit(task);
        return new ModTask(future);
    }

    @Override
    public ITask runTaskTimerAsynchronously(Runnable task, long delay, long period) {
        long delayMs = delay * 50;
        long periodMs = period * 50;

        ScheduledFuture<?> future = executor.scheduleAtFixedRate(
                task,
                delayMs,
                periodMs,
                TimeUnit.MILLISECONDS
        );
        return new ModTask(future);
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}