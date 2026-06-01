package me.lucaaa.advancedlinks.v26_1;

import me.lucaaa.advancedlinks.common.AdvancedLinks;
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
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean supportsPapi() {
        return false;
    }
}