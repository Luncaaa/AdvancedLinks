package me.lucaaa.advancedlinks.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.ServerLink;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;
import me.lucaaa.advancedlinks.velocity.data.VelocityMessageReceiver;

public interface IVelocityAdvancedLinks extends AdvancedLinks<ServerLink, ServerLink.Type> {
    ProxyServer getServer();

    default MessageReceiver getMessageReceiver(CommandSource source) {
        return new VelocityMessageReceiver(source);
    }
}