package me.lucaaa.advancedlinks.v26_x;

import me.lucaaa.advancedlinks.common.AdvancedLinks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

public class FabricAdvancedLinks extends ModAdvancedLinks implements ModInitializer {
    private boolean supportsPapi = false;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            supportsPapi = FabricLoader.getInstance().isModLoaded("placeholder-api");

            String version = FabricLoader.getInstance()
                    .getModContainer(AdvancedLinks.ID)
                    .map(container -> container.getMetadata().getVersion().getFriendlyString())
                    .orElse("Unknown");

            super.onInitialize(server, FabricLoader.getInstance().getConfigDir(), version);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) ->
                super.registerCommands(dispatcher)
        );

        ServerPlayConnectionEvents.JOIN.register((listener, sender, server) -> super.onPlayerJoin(listener.player));

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> super.onStop());
    }

    @Override
    public boolean supportsPapi() {
        return supportsPapi;
    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }
}