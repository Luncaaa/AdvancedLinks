package me.lucaaa.advancedlinks.forge;

import me.lucaaa.advancedlinks.common.data.Parsers;
import me.lucaaa.advancedlinks.mod_common.ModAdvancedLinks;
import me.lucaaa.advancedlinks.mod_common.data.ModLinkReceiver;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(ModAdvancedLinks.MOD_ID)
public class ForgeAdvancedLinks extends ModAdvancedLinks {
    private final String version;

    public ForgeAdvancedLinks() {
        MinecraftForge.EVENT_BUS.register(this);

        this.version = ModList.get().getModContainerById(MOD_ID)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("Unknown");
    }

    @net.minecraftforge.eventbus.api.listener.SubscribeEvent
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
        return "Forge";
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