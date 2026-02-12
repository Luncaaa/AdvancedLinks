package me.lucaaa.advancedlinks.neoforge;

import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.Parsers;
import me.lucaaa.advancedlinks.mod_common.ModAdvancedLinks;
import me.lucaaa.advancedlinks.mod_common.data.ModLinkReceiver;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod(value = AdvancedLinks.ID, dist = Dist.DEDICATED_SERVER)
public class NeoForgeAdvancedLinks extends ModAdvancedLinks {
    private final String version;

    public NeoForgeAdvancedLinks(ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);

        this.version = modContainer.getModInfo().getVersion().toString();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        super.onInitialize(event.getServer(), FMLPaths.CONFIGDIR.get(), version);
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        super.registerCommands(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event) {
        super.onPlayerJoin((ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        super.onStop();
    }

    @Override
    public ModLinkReceiver getLinkReceiver(ServerPlayer player) {
        return new ModLinkReceiver(this, player) {
            @Override
            public String replacePapiPlaceholders(String text) {
                return text.replace("%player_name%", Parsers.legacySectionSerializer.serialize(asAdventure(player.getName())));
            }
        };
    }

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean supportsPapi() {
        return false;
    }

    @Override
    public String replacePapiPlaceholders(String text) {
        return text;
    }
}