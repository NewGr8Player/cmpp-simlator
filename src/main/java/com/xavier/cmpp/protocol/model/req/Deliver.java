package com.xavier.cmpp.protocol.model.req;

import com.google.common.primitives.UnsignedBytes;
import com.xavier.cmpp.protocol.constant.CMPPConstant;
import com.xavier.cmpp.protocol.constant.Constants;
import com.xavier.cmpp.protocol.model.CmppHead;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.ByteBuffer;

import static java.lang.System.arraycopy;

/**
 * 送交消息
 *
 * @author NewGr8Player
 */
@Getter
@Setter
public class Deliver extends CmppHead {
    private byte[] msgId = new byte[8];
    private byte[] destId = new byte[21];
    private byte[] serviceId = new byte[10];
    private byte tpPid;
    private byte tpUdhi;
    private byte msgFmt;

    private byte[] srcTerminalId;
    private byte srcTerminalType;
    @Setter(AccessLevel.NONE)
    private byte registeredDelivery;
    private int msgLength;//由于符号位关系，这里用int替代byte
    private byte[] msgContent;

    private byte[] msg_Id = new byte[8];//状态报告中msgid与响应中对应
    private byte[] stat = new byte[7];
    private byte[] submitTime = new byte[10];
    private byte[] doneTime = new byte[10];
    private byte[] destTerminalId;
    private byte[] smscSequence = new byte[4];
    private byte[] reservedOrLinkId;

    public Deliver(int protocolType) {
        super.protocolType = protocolType;
        super.commandId = CMPPConstant.APP_DELIVER;
        srcTerminalId = new byte[protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2 ? 21 : 32];
        reservedOrLinkId = new byte[protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2 ? 8 : 20];
    }

    public void setRegisteredDelivery(byte registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
        if (1 == registeredDelivery) {
            destTerminalId = new byte[protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2 ? 21 : 32];
        }
    }

    @Override
    protected void doSubEncode(ByteBuffer bb) {
        boolean isCmpp2 = protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2;
        bb.put(msgId);
        bb.put(destId);
        bb.put(serviceId);
        bb.put(tpPid);
        bb.put(tpUdhi);
        bb.put(msgFmt);
        bb.put(srcTerminalId);
        if (!isCmpp2) {
            bb.put(srcTerminalType);
        }
        bb.put(registeredDelivery);
        bb.put((byte) msgLength);
        if (msgContent != null) {
            bb.put(msgContent);
        } else {
            bb.put(msg_Id);
            bb.put(stat);
            bb.put(submitTime);
            bb.put(doneTime);
            bb.put(destTerminalId);
            bb.put(smscSequence);
        }
        bb.put(reservedOrLinkId);
    }

    @Override
    protected void processHead() {
        boolean isCmpp2 = protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2;
        if (null == msgContent) {
            totalLength = isCmpp2 ? 145 : 180;
        } else {
            processTotalLength(isCmpp2);
        }
    }

    private void processTotalLength(boolean isCmpp2) {
        if (isCmpp2) {
            msgLength = registeredDelivery == 1 ? 60 : msgContent.length;
            totalLength = 85 + msgLength;
        } else {
            msgLength = registeredDelivery == 1 ? 71 : msgContent.length;
            totalLength = 109 + msgLength;
        }
    }

    @Override
    protected void doSubDecode(ByteBuffer bb) {
        boolean isCmpp2 = protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2;
        bb.get(msgId);
        bb.get(destId);
        bb.get(serviceId);
        tpPid = bb.get();
        tpUdhi = bb.get();
        msgFmt = bb.get();
        bb.get(srcTerminalId);
        if (!isCmpp2) {
            srcTerminalType = bb.get();
        }
        setRegisteredDelivery(bb.get());
        msgLength = UnsignedBytes.toInt(bb.get());
        if (registeredDelivery == 1) {
            bb.get(msg_Id);
            bb.get(stat);
            bb.get(submitTime);
            bb.get(doneTime);
            bb.get(destTerminalId);
            bb.get(smscSequence);
        } else {
            msgContent = new byte[msgLength];
            bb.get(msgContent);
        }
        bb.get(reservedOrLinkId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Deliver deliver = (Deliver) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(tpPid, deliver.tpPid)
                .append(tpUdhi, deliver.tpUdhi)
                .append(msgFmt, deliver.msgFmt)
                .append(srcTerminalType, deliver.srcTerminalType)
                .append(registeredDelivery, deliver.registeredDelivery)
                .append(msgLength, deliver.msgLength)
                .append(msgId, deliver.msgId)
                .append(destId, deliver.destId)
                .append(serviceId, deliver.serviceId)
                .append(srcTerminalId, deliver.srcTerminalId)
                .append(msgContent, deliver.msgContent)
                .append(msg_Id, deliver.msg_Id)
                .append(stat, deliver.stat)
                .append(submitTime, deliver.submitTime)
                .append(doneTime, deliver.doneTime)
                .append(destTerminalId, deliver.destTerminalId)
                .append(smscSequence, deliver.smscSequence)
                .append(reservedOrLinkId, deliver.reservedOrLinkId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(msgId)
                .append(destId)
                .append(serviceId)
                .append(tpPid)
                .append(tpUdhi)
                .append(msgFmt)
                .append(srcTerminalId)
                .append(srcTerminalType)
                .append(registeredDelivery)
                .append(msgLength)
                .append(msgContent)
                .append(msg_Id)
                .append(stat)
                .append(submitTime)
                .append(doneTime)
                .append(destTerminalId)
                .append(smscSequence)
                .append(reservedOrLinkId)
                .toHashCode();
    }

    @Override
    public Deliver clone() {
        Deliver deliver = new Deliver(this.protocolType);
        deliver.totalLength = this.totalLength;
        deliver.commandId = this.commandId;
        deliver.secquenceId = this.secquenceId;
        arraycopy(this.msgId, 0, deliver.msgId, 0, 8);
        arraycopy(this.destId, 0, deliver.destId, 0, 21);
        arraycopy(this.serviceId, 0, deliver.serviceId, 0, 10);
        deliver.setTpPid(this.tpPid);
        deliver.setTpUdhi(this.tpUdhi);
        deliver.setMsgFmt(this.msgFmt);
        if (this.srcTerminalId != null) {
            deliver.srcTerminalId = new byte[this.srcTerminalId.length];
            arraycopy(this.srcTerminalId, 0, deliver.srcTerminalId, 0, this.srcTerminalId.length);
        }
        deliver.srcTerminalType = this.srcTerminalType;
        deliver.registeredDelivery = this.registeredDelivery;
        deliver.msgLength = this.msgLength;
        if (this.msgContent != null) {
            deliver.msgContent = new byte[this.msgContent.length];
            arraycopy(this.msgContent, 0, deliver.msgContent, 0, this.msgContent.length);
        }
        arraycopy(this.msg_Id, 0, deliver.msg_Id, 0, 8);
        arraycopy(this.stat, 0, deliver.stat, 0, 7);
        arraycopy(this.submitTime, 0, deliver.submitTime, 0, 10);
        arraycopy(this.doneTime, 0, deliver.doneTime, 0, 10);
        if (this.destTerminalId != null) {
            deliver.destTerminalId = new byte[this.destTerminalId.length];
            arraycopy(this.destTerminalId, 0, deliver.destTerminalId, 0, this.destTerminalId.length);
        }
        arraycopy(this.smscSequence, 0, deliver.smscSequence, 0, 4);
        if (this.reservedOrLinkId != null) {
            deliver.reservedOrLinkId = new byte[this.reservedOrLinkId.length];
            arraycopy(this.reservedOrLinkId, 0, deliver.reservedOrLinkId, 0, this.reservedOrLinkId.length);
        }
        return deliver;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("msgId", msgId)
                .append("destId", destId)
                .append("serviceId", serviceId)
                .append("tpPid", tpPid)
                .append("tpUdhi", tpUdhi)
                .append("msgFmt", msgFmt)
                .append("srcTerminalId", srcTerminalId)
                .append("srcTerminalType", srcTerminalType)
                .append("registeredDelivery", registeredDelivery)
                .append("msgLength", msgLength)
                .append("msgContent", msgContent)
                .append("msg_Id", msg_Id)
                .append("stat", stat)
                .append("submitTime", submitTime)
                .append("doneTime", doneTime)
                .append("destTerminalId", destTerminalId)
                .append("smscSequence", smscSequence)
                .append("reservedOrLinkId", reservedOrLinkId)
                .append("totalLength", totalLength)
                .append("commandId", commandId)
                .append("secquenceId", secquenceId)
                .append("protocolType", protocolType)
                .append("msgBytes", msgBytes)
                .toString();
    }
}
