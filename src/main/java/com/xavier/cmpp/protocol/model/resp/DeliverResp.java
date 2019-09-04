package com.xavier.cmpp.protocol.model.resp;

import com.xavier.cmpp.protocol.constant.CMPPConstant;
import com.xavier.cmpp.protocol.constant.Constants;
import com.xavier.cmpp.protocol.model.CmppHead;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.ByteBuffer;

/**
 * 递交响应
 *
 * @author NewGr8Player
 */
@Getter
@Setter
public class DeliverResp extends CmppHead {
    private byte[] msgId = new byte[8];
    private int result;

    public DeliverResp(int protocolType) {
        super.protocolType = protocolType;
        super.commandId = CMPPConstant.APP_DELIVER_RESP;
    }


    @Override
    protected void doSubEncode(ByteBuffer bb) {
        boolean isCmpp2 = protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2;
        bb.put(msgId);
        if (isCmpp2) {
            bb.put((byte) result);
        } else {
            bb.putInt(result);
        }
    }

    @Override
    protected void doSubDecode(ByteBuffer bb) {
        bb.get(msgId);
        if (totalLength == 21) {
            result = bb.get();
        } else {
            result = bb.getInt();
        }
    }

    @Override
    protected void processHead() {
        boolean isCmpp2 = protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2;
        totalLength = isCmpp2 ? 21 : 24;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DeliverResp that = (DeliverResp) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(result, that.result)
                .append(msgId, that.msgId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(msgId)
                .append(result)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("msgId", msgId)
                .append("result", result)
                .append("totalLength", totalLength)
                .append("commandId", commandId)
                .append("secquenceId", secquenceId)
                .append("protocolType", protocolType)
                .append("msgBytes", msgBytes)
                .toString();
    }
}
