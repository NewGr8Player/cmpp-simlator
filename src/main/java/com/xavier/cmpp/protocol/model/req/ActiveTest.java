package com.xavier.cmpp.protocol.model.req;

import com.xavier.cmpp.protocol.constant.CMPPConstant;
import com.xavier.cmpp.protocol.model.CmppHead;

import java.nio.ByteBuffer;

/**
 * 心跳消息
 *
 * @author NewGr8Player
 */
public class ActiveTest extends CmppHead {
    public ActiveTest() {
        totalLength = 12;
        commandId = CMPPConstant.APP_ACTIVE_TEST;
    }

    @Override
    protected void doSubEncode(ByteBuffer bb) {

    }

    @Override
    protected void doSubDecode(ByteBuffer bb) {

    }

    @Override
    protected void processHead() {

    }
}
