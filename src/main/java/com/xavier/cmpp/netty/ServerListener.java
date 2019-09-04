package com.xavier.cmpp.netty;

import com.xavier.cmpp.config.SimulatorConfig;
import com.xavier.cmpp.protocol.handler.CmppDecoder;
import com.xavier.cmpp.protocol.handler.CmppEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监听
 *
 * @author NewGr8Player
 */
@Slf4j
@Component
public class ServerListener {

    @Autowired
    private SimulatorConfig simulatorConfig;
    @Autowired
    private ChannelHandler channelHandler;

    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                                  @Override
                                  protected void initChannel(SocketChannel channel) throws Exception {
                                      channel.pipeline().addLast(new IdleStateHandler(0, 0, simulatorConfig.getIdleSeconds()));
                                      channel.pipeline().addLast(new CmppDecoder());
                                      channel.pipeline().addLast(new CmppEncoder());
                                      channel.pipeline().addLast(channelHandler);
                                  }
                              }
                );

        try {
            int listenPort = simulatorConfig.getListenPort();
            serverBootstrap.bind(listenPort).sync();
            log.info("服务启动成功，监听端口:【{}】", listenPort);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("初始化失败!", e);
        }
    }
}
