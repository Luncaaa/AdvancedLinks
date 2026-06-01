package me.lucaaa.advancedlinks.v26_1.data;

import com.mojang.datafixers.util.Either;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import me.lucaaa.advancedlinks.common.data.LinkReceiver;
import me.lucaaa.advancedlinks.common.data.ParsedLink;
import me.lucaaa.advancedlinks.common.data.Parsers;
import me.lucaaa.advancedlinks.v26_1.ModAdvancedLinks;
import net.minecraft.network.protocol.common.ClientboundServerLinksPacket;
import net.minecraft.server.ServerLinks;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModLinkReceiver implements LinkReceiver<ServerLinks.UntrustedEntry, ServerLinks.KnownLinkType> {
    private final ModAdvancedLinks plugin;
    private final ServerPlayer player;

    public ModLinkReceiver(ModAdvancedLinks plugin, ServerPlayer player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void sendLinks(Collection<ServerLinks.UntrustedEntry> globalLinks, List<ParsedLink<ServerLinks.KnownLinkType>> parsedLinks) {
        List<ServerLinks.UntrustedEntry> links = new ArrayList<>(globalLinks);

        for (ParsedLink<ServerLinks.KnownLinkType> parsedLink : parsedLinks) {
            ServerLinks.UntrustedEntry serverLink;
            if (parsedLink.type() != null) {
                serverLink = new ServerLinks.UntrustedEntry(Either.left(parsedLink.type()), parsedLink.url().toString());
            } else {
                serverLink = new ServerLinks.UntrustedEntry(Either.right(plugin.asNative(parsedLink.displayName())), parsedLink.url().toString());
            }
            links.add(serverLink);
        }

        player.connection.send(new ClientboundServerLinksPacket(links));
    }

    @Override
    public String replacePapiPlaceholders(String text) {
        if (plugin.supportsPapi()) {
            return Parsers.mm.serialize(plugin.asAdventure(Placeholders.SERVER_PLACEHOLDER_PARSER.parseComponent(TextNode.of(text), ServerPlaceholderContext.of(player).asParserContext())));
        } else {
            return text.replace("%player_name%", Parsers.legacySectionSerializer.serialize(plugin.asAdventure(player.getName())));
        }
    }
}