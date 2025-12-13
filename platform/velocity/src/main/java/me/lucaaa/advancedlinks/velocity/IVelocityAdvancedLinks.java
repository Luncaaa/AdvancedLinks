package me.lucaaa.advancedlinks.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import me.lucaaa.advancedlinks.common.AdvancedLinks;
import me.lucaaa.advancedlinks.common.data.MessageReceiver;

public interface IVelocityAdvancedLinks extends AdvancedLinks {
    ProxyServer getServer();

    MessageReceiver getMessageReceiver(CommandSource source);

    String getVersion();
}