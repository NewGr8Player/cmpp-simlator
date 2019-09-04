package com.xavier.cmpp.protocol.handler;

import com.xavier.cmpp.protocol.constant.CMPPConstant;
import com.xavier.cmpp.protocol.constant.Constants;
import com.xavier.cmpp.protocol.model.CmppHead;
import com.xavier.cmpp.protocol.model.req.ActiveTest;
import com.xavier.cmpp.protocol.model.req.Connect;
import com.xavier.cmpp.protocol.model.req.Submit;
import com.xavier.cmpp.protocol.model.resp.DeliverResp;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 解码器
 *
 * @author NewGr8Player
 */
@Slf4j
public class CmppDecoder extends ReplayingDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int totalLength = in.readInt();
        int commandId = in.readInt();
        if (!validateClient(ctx, commandId)) {
            log.info("客户端未登录:【{}】,关闭!", ctx.channel().remoteAddress());
            ctx.close();
            return;
        }
        log.debug("总长度:{}", totalLength);
        byte[] bytes = new byte[totalLength];
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.putInt(totalLength);
        bb.putInt(commandId);
        bb.putInt(in.readInt());
        in.readBytes(bytes, 12, totalLength - 12);
        CmppHead head = null;
        switch (commandId) {
            case CMPPConstant.APP_SUBMIT:
                head = new Submit((Integer) ctx.channel().attr(Constants.PROTOCOL_TYPE_VERSION).get());
                break;
            case CMPPConstant.APP_ACTIVE_TEST:
                head = new ActiveTest();
                break;
            case CMPPConstant.CMPP_CONNECT:
                head = new Connect();
                break;
            case CMPPConstant.APP_DELIVER_RESP:
                head = new DeliverResp((Integer) ctx.channel().attr(Constants.PROTOCOL_TYPE_VERSION).get());
                break;
            default:
                log.warn("无法解析的数据，commandId：{},Connection Closed!",
                        "0x" + Integer.toHexString(commandId));
                ctx.close();
        }
        if (null != head) {
            log.debug("【字节流读取完毕:{}】", in.readerIndex() == in.writerIndex());
            head.setMsgBytes(bytes);
            out.add(head);
        }
    }

    /**
     * check client is login
     *
     * @param ctx
     * @param commandId
     * @return
     */
    private boolean validateClient(ChannelHandlerContext ctx, int commandId) {
        return null != ctx.channel().attr(Constants.PROTOCOL_TYPE_VERSION).get() ||
                commandId == CMPPConstant.CMPP_CONNECT;
    }
}
