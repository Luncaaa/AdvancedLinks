package me.lucaaa.advancedlinks.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.ServerLink;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;

public interface IVelocityAdvancedLinks extends AdvancedLinks<ServerLink, ServerLink.Type> {
    ProxyServer getServer();

    MessageReceiver getMessageReceiver(CommandSource source);

    String getVersion();
}