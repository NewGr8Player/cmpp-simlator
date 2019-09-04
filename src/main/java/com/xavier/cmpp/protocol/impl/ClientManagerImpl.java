package com.xavier.cmpp.protocol.impl;

import com.xavier.cmpp.protocol.ClientManager;
import com.xavier.cmpp.protocol.constant.Constants;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ClientManagerImpl implements ClientManager {
    Logger logger = LoggerFactory.getLogger(ClientManagerImpl.class);
    ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRegister(Channel channel) {
        channels.add(channel);
        logger.debug("groub size:{}", channels.size());
        closeMatchChannels(new PropertiesChannelMatcher<>(Constants.PROTOCOL_TYPE_VERSION,
                Constants.PROTOCOL_TYPE_VERSION_CMPP2));
    }

    @Override
    public Integer activeChannels() {
        return channels.size();
    }

    @Override
    public void sendMessageToMatchChannels(Object message, ChannelMatcher channelMatcher) {
        channels.writeAndFlush(message, channelMatcher);
    }

    @Override
    public void closeMatchChannels(ChannelMatcher channelMatcher) {
        channels.close(channelMatcher);
    }
}
