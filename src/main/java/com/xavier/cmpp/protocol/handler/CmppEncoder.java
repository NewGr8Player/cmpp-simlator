package com.xavier.cmpp.protocol.handler;

import com.xavier.cmpp.protocol.model.CmppHead;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 *
 * @author NewGr8Player
 */
public class CmppEncoder extends MessageToByteEncoder<CmppHead> {

    @Override
    protected void encode(ChannelHandlerContext ctx, CmppHead msg, ByteBuf out) throws Exception {
        msg.doEncode();
        out.writeBytes(msg.getMsgBytes());
    }
}
