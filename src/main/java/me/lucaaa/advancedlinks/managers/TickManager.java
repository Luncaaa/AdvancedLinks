package me.lucaaa.advancedlinks.managers;

import me.lucaaa.advancedlinks.AdvancedLinks;
import me.lucaaa.advancedlinks.data.Ticking;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class TickManager {
    private final AdvancedLinks plugin;
    private final List<Ticking> ticking = new ArrayList<>();
    private final List<Ticking> toAdd = new ArrayList<>();
    private final List<Ticking> toRemove = new ArrayList<>();
    private BukkitTask task;
    private boolean isTicking = false;

    public TickManager(AdvancedLinks plugin, long period) {
        this.plugin = plugin;
        start(period);
    }

    private synchronized void start(long period) {
        if (period <= 0) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimerAsynchronously(plugin, 0L, period);
    }

    public synchronized void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        ticking.clear();
        toAdd.clear();
        toRemove.clear();
    }

    public void addTicking(Ticking ticked) {
        synchronized (toAdd) {
            toAdd.add(ticked);
        }
    }

    public void removeTicking(Ticking ticked) {
        synchronized (toRemove) {
            toRemove.add(ticked);
        }
    }

    private void tick() {
        if (isTicking) return;

        isTicking = true;

        synchronized (ticking) {
            for (Ticking ticked : ticking) {
                ticked.tick();
            }
        }

        synchronized (toAdd) {
            ticking.addAll(toAdd);
            toAdd.clear();
        }

        synchronized (toRemove) {
            ticking.removeAll(toRemove);
            toRemove.clear();
        }

        isTicking = false;
    }
}