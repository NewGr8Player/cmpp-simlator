package com.xavier.cmpp.protocol.model.resp;

import com.xavier.cmpp.protocol.constant.CMPPConstant;
import com.xavier.cmpp.protocol.constant.Constants;
import com.xavier.cmpp.protocol.model.CmppHead;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.ByteBuffer;

/**
 * 连接响应
 *
 * @author NewGr8player
 */
@Getter
@Setter
public class ConnectResp extends CmppHead {
    private int status;
    private byte[] authenticatorIsmg = new byte[16];
    private byte version;


    public ConnectResp(int protocolType) {
        super.protocolType = protocolType;
        super.commandId = CMPPConstant.CMPP_CONNECT_RESP;
    }

    @Override
    protected void doSubEncode(ByteBuffer bb) {

        if (protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2) {
            bb.put((byte) status);
        } else {
            bb.putInt(status);
        }
        bb.put(authenticatorIsmg);
        bb.put(version);
    }

    @Override
    protected void doSubDecode(ByteBuffer bb) {

        status = totalLength == 30 ? bb.get() : bb.getInt();
        bb.get(authenticatorIsmg);
        version = bb.get();
    }

    @Override
    protected void processHead() {
        totalLength = protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2 ? 30 : 33;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("status", status)
                .append("authenticatorIsmg", authenticatorIsmg)
                .append("version", version)
                .append("totalLength", totalLength)
                .append("commandId", commandId)
                .append("secquenceId", secquenceId)
                .append("protocolType", protocolType)
                .append("msgBytes", msgBytes)
                .toString();
    }
}
