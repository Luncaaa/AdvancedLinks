package me.lucaaa.advancedlinks.data;

import me.lucaaa.advancedlinks.AdvancedLinks;

public abstract class Ticking {
    private final AdvancedLinks plugin;

    public Ticking(AdvancedLinks plugin) {
        this.plugin = plugin;
    }

    public abstract void tick();

    public void startTicking() {
        plugin.getTickManager().addTicking(this);
    }

    public void stopTicking() {
        plugin.getTickManager().removeTicking(this);
    }
}