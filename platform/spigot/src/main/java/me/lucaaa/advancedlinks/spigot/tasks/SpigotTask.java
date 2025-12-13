package me.lucaaa.advancedlinks.spigot.tasks;

import me.lucaaa.advancedlinks.common.tasks.ITask;
import org.bukkit.scheduler.BukkitTask;

public class SpigotTask implements ITask {
    private final BukkitTask task;

    public SpigotTask(BukkitTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        task.cancel();
    }
}