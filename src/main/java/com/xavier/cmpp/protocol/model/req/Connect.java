package com.xavier.cmpp.protocol.model.req;

import com.xavier.cmpp.protocol.constant.CMPPConstant;
import com.xavier.cmpp.protocol.model.CmppHead;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.ByteBuffer;

/**
 * 连接消息
 *
 * @author NewGr8Player
 */
@Getter
@Setter
public class Connect extends CmppHead {
    private byte[] sourceAddr = new byte[6];
    private byte[] authenticatorSource = new byte[16];
    private byte version;
    private byte[] timeStamp = new byte[4];

    public Connect() {
        commandId = CMPPConstant.CMPP_CONNECT;
    }

    @Override
    protected void doSubEncode(ByteBuffer bb) {
        bb.put(sourceAddr);
        bb.put(authenticatorSource);
        bb.put(version);
        bb.put(timeStamp);
    }

    @Override
    protected void doSubDecode(ByteBuffer bb) {
        bb.get(sourceAddr);
        bb.get(authenticatorSource);
        version = bb.get();
        bb.get(timeStamp);
    }

    @Override
    protected void processHead() {
        totalLength = 39;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Connect connect = (Connect) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(version, connect.version)
                .append(sourceAddr, connect.sourceAddr)
                .append(authenticatorSource, connect.authenticatorSource)
                .append(timeStamp, connect.timeStamp)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(sourceAddr)
                .append(authenticatorSource)
                .append(version)
                .append(timeStamp)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sourceAddr", sourceAddr)
                .append("authenticatorSource", authenticatorSource)
                .append("version", version)
                .append("timeStamp", timeStamp)
                .append("totalLength", totalLength)
                .append("commandId", commandId)
                .append("secquenceId", secquenceId)
                .append("protocolType", protocolType)
                .append("msgBytes", msgBytes)
                .toString();
    }
}
