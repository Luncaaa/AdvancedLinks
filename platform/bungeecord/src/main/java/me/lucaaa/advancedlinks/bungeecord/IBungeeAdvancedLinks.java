package me.lucaaa.advancedlinks.bungeecord;

import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerLink;

public interface IBungeeAdvancedLinks extends AdvancedLinks<ServerLink, ServerLink.LinkType> {
    ProxyServer getServer();

    MessageReceiver getMessageReceiver(CommandSender source);
}