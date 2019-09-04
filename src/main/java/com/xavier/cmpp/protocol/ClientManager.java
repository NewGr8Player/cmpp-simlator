package com.xavier.cmpp.protocol;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelMatcher;

/**
 * 客户端管理
 *
 * @author NewGr8Player
 */
public interface ClientManager {
    /**
     * 注册Channel （不需要注销，断开连接channel自动注销）
     *
     * @param channel 通道
     */
    void channelRegister(Channel channel);

    /**
     * 活动通道数量
     *
     * @return 数量
     */
    Integer activeChannels();

    /**
     * 发送消息给所有匹配Channels
     *
     * @param message        消息
     * @param channelMatcher 匹配器
     */
    void sendMessageToMatchChannels(Object message, ChannelMatcher channelMatcher);

    /**
     * 关闭匹配Channels
     *
     * @param channelMatcher 匹配器
     */
    void closeMatchChannels(ChannelMatcher channelMatcher);
}
