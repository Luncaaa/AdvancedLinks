package me.lucaaa.advancedlinks.fabric;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.Parsers;
import me.lucaaa.advancedlinks.mod_common.ModAdvancedLinks;
import me.lucaaa.advancedlinks.mod_common.data.ModLinkReceiver;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

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
    public String replacePapiPlaceholders(String text) {
        if (supportsPapi()) {
            return Parsers.mm.serialize(asAdventure(Placeholders.parseText(TextNode.of(text), PlaceholderContext.of(getServer()))));
        } else {
            return text;
        }
    }

    @Override
    public ModLinkReceiver getLinkReceiver(ServerPlayer player) {
        return new ModLinkReceiver(this, player) {
            @Override
            public String replacePapiPlaceholders(String text) {
                if (supportsPapi) {
                    return Parsers.mm.serialize(asAdventure(Placeholders.parseText(TextNode.of(text), PlaceholderContext.of(player))));
                } else {
                    return text.replace("%player_name%", Parsers.legacySectionSerializer.serialize(asAdventure(player.getName())));
                }
            }
        };
    }

    @Override
    public String getPlatformName() {
        return "Fabric";
    }
}