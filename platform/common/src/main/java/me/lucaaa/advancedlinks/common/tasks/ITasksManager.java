package me.lucaaa.advancedlinks.common.tasks;

@SuppressWarnings("UnusedReturnValue")
public interface ITasksManager {
    ITask runTaskAsynchronously(Runnable task);
    ITask runTaskTimerAsynchronously(Runnable task, long delay, long period);
}