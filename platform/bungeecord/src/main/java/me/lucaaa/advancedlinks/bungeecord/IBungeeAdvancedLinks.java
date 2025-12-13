package me.lucaaa.advancedlinks.bungeecord;

import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

public interface IBungeeAdvancedLinks extends AdvancedLinks {
    ProxyServer getServer();

    MessageReceiver getMessageReceiver(CommandSender source);
}