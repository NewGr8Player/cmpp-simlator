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

/**
 * 提交消息
 *
 * @author NewGr8Player
 */
@Getter
@Setter
public class Submit extends CmppHead {
    private byte[] msgId = new byte[8];
    private byte pkTotal;
    private byte pkNumber;
    private byte registeredDelivery;
    private byte msgLevel;
    private byte[] serviceId = new byte[10];
    private byte feeUserType;
    private byte[] feeTerminalId;
    private byte feeTerminalType;
    private byte tppId;
    private byte tpUdhi;
    private byte msgFmt;
    private byte[] msgSrc = new byte[6];
    private byte[] feeType = new byte[2];
    private byte[] feeCode = new byte[6];
    private byte[] validTime = new byte[17];
    private byte[] atTime = new byte[17];
    private byte[] srcId = new byte[21];
    @Setter(AccessLevel.NONE)
    private byte destUsrTl;
    private byte[] destTerminalIds;
    private byte destTerminalType;
    private int msgLength;
    @Setter(AccessLevel.NONE)
    private byte[] msgContent;
    private byte[] reserveOrLinkId;

    public Submit(int protocolType) {
        super.protocolType = protocolType;
        super.commandId = CMPPConstant.APP_SUBMIT;
        feeTerminalId = new byte[protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2 ? 21 : 32];
        reserveOrLinkId = new byte[protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2 ? 8 : 20];
    }

    public void setDestUsrTl(byte destUsrTl) {
        this.destUsrTl = destUsrTl;
        destTerminalIds = new byte[destUsrTl * feeTerminalId.length];
    }

    public void setMsgContent(byte[] msgContent) {
        this.msgContent = msgContent;
        msgLength = msgContent.length;
    }

    @Override
    protected void processHead() {
        boolean isCmpp2 = protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2;
        if (isCmpp2) {
            totalLength = 138 + 21 * destUsrTl + msgLength;
        } else {
            totalLength = 163 + 32 * destUsrTl + msgLength;
        }
    }

    @Override
    protected void doSubEncode(ByteBuffer bb) {
        boolean isCmpp2 = protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2;

        bb.put(msgId);
        bb.put(pkTotal);
        bb.put(pkNumber);
        bb.put(registeredDelivery);
        bb.put(msgLevel);
        bb.put(serviceId);
        bb.put(feeUserType);
        bb.put(feeTerminalId);
        if (!isCmpp2) {
            bb.put(feeTerminalType);
        }
        bb.put(tppId);
        bb.put(tpUdhi);
        bb.put(msgFmt);
        bb.put(msgSrc);
        bb.put(feeType);
        bb.put(feeCode);
        bb.put(validTime);
        bb.put(atTime);
        bb.put(srcId);
        bb.put(destUsrTl);
        bb.put(destTerminalIds);
        if (!isCmpp2) {
            bb.put(destTerminalType);
        }

        bb.put((byte) msgLength);
        bb.put(msgContent);
        bb.put(reserveOrLinkId);
    }

    @Override
    protected void doSubDecode(ByteBuffer bb) {
        boolean isCmpp2 = protocolType == Constants.PROTOCOL_TYPE_VERSION_CMPP2;
        bb.get(msgId);
        pkTotal = bb.get();
        pkNumber = bb.get();
        registeredDelivery = bb.get();
        msgLevel = bb.get();
        bb.get(serviceId);
        feeUserType = bb.get();
        bb.get(feeTerminalId);
        if (!isCmpp2) {
            feeTerminalType = bb.get();
        }
        tppId = bb.get();
        tpUdhi = bb.get();
        msgFmt = bb.get();
        bb.get(msgSrc);
        bb.get(feeType);
        bb.get(feeCode);
        bb.get(validTime);
        bb.get(atTime);
        bb.get(srcId);
        setDestUsrTl(bb.get());
        bb.get(destTerminalIds);
        if (!isCmpp2) {
            destTerminalType = bb.get();
        }
        msgLength = UnsignedBytes.toInt(bb.get());
        msgContent = new byte[msgLength];
        bb.get(msgContent);
        bb.get(reserveOrLinkId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Submit submit = (Submit) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(pkTotal, submit.pkTotal)
                .append(pkNumber, submit.pkNumber)
                .append(registeredDelivery, submit.registeredDelivery)
                .append(msgLevel, submit.msgLevel)
                .append(feeUserType, submit.feeUserType)
                .append(feeTerminalType, submit.feeTerminalType)
                .append(tppId, submit.tppId)
                .append(tpUdhi, submit.tpUdhi)
                .append(msgFmt, submit.msgFmt)
                .append(destUsrTl, submit.destUsrTl)
                .append(destTerminalType, submit.destTerminalType)
                .append(msgLength, submit.msgLength)
                .append(msgId, submit.msgId)
                .append(serviceId, submit.serviceId)
                .append(feeTerminalId, submit.feeTerminalId)
                .append(msgSrc, submit.msgSrc)
                .append(feeType, submit.feeType)
                .append(feeCode, submit.feeCode)
                .append(validTime, submit.validTime)
                .append(atTime, submit.atTime)
                .append(srcId, submit.srcId)
                .append(destTerminalIds, submit.destTerminalIds)
                .append(msgContent, submit.msgContent)
                .append(reserveOrLinkId, submit.reserveOrLinkId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(msgId)
                .append(pkTotal)
                .append(pkNumber)
                .append(registeredDelivery)
                .append(msgLevel)
                .append(serviceId)
                .append(feeUserType)
                .append(feeTerminalId)
                .append(feeTerminalType)
                .append(tppId)
                .append(tpUdhi)
                .append(msgFmt)
                .append(msgSrc)
                .append(feeType)
                .append(feeCode)
                .append(validTime)
                .append(atTime)
                .append(srcId)
                .append(destUsrTl)
                .append(destTerminalIds)
                .append(destTerminalType)
                .append(msgLength)
                .append(msgContent)
                .append(reserveOrLinkId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("msgId", msgId)
                .append("pkTotal", pkTotal)
                .append("pkNumber", pkNumber)
                .append("registeredDelivery", registeredDelivery)
                .append("msgLevel", msgLevel)
                .append("serviceId", serviceId)
                .append("feeUserType", feeUserType)
                .append("feeTerminalId", feeTerminalId)
                .append("feeTerminalType", feeTerminalType)
                .append("tppId", tppId)
                .append("tpUdhi", tpUdhi)
                .append("msgFmt", msgFmt)
                .append("msgSrc", msgSrc)
                .append("feeType", feeType)
                .append("feeCode", feeCode)
                .append("validTime", validTime)
                .append("atTime", atTime)
                .append("srcId", srcId)
                .append("destUsrTl", destUsrTl)
                .append("destTerminalIds", destTerminalIds)
                .append("destTerminalType", destTerminalType)
                .append("msgLength", msgLength)
                .append("msgContent", msgContent)
                .append("reserveOrLinkId", reserveOrLinkId)
                .append("totalLength", totalLength)
                .append("commandId", commandId)
                .append("secquenceId", secquenceId)
                .append("protocolType", protocolType)
                .append("msgBytes", msgBytes)
                .toString();
    }
}
