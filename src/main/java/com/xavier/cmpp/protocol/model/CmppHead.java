package com.xavier.cmpp.protocol.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * 通用头
 *
 * @author NewGr8Player
 */
public abstract class CmppHead implements Serializable, Cloneable {
    @Getter
    @Setter
    protected int totalLength;/* 不允许自行设置，doEncode()的时候会自动计算 */
    @Getter
    @Setter
    protected int commandId; /* 不允许自行设置，对象创建时根据构造函数设置 */
    @Getter
    @Setter
    protected int secquenceId;/* 需要自行赋值，请求与响应的相对应，无论上下行 */

    protected int protocolType;
    protected byte[] msgBytes;

    /**
     * 子类字节获取，要负责父类中三属性数据生成
     *
     * @param bb
     * @return
     */
    protected abstract void doSubEncode(ByteBuffer bb);

    /**
     * 子类解码，被父类回调
     *
     * @param bb
     */
    protected abstract void doSubDecode(ByteBuffer bb);

    /**
     * 对象编码为字节数组
     *
     * @return
     */
    public byte[] doEncode() {
        processHead();
        ByteBuffer bb = ByteBuffer.allocate(totalLength);
        bb.putInt(totalLength);
        bb.putInt(commandId);
        bb.putInt(secquenceId);
        doSubEncode(bb);
        this.msgBytes = bb.array();
        return bb.array();
    }

    protected abstract void processHead();

    /**
     * 字节数组解码为对象
     *
     * @param bytes
     * @return
     */
    public void doDecode(byte[] bytes) {
        this.msgBytes = bytes;
        doDecode();
    }

    public void doDecode() {
        if (msgBytes == null) {
            throw new RuntimeException("Object Bytes is Null");
        }
        ByteBuffer bb = ByteBuffer.wrap(msgBytes);
        totalLength = bb.getInt();
        commandId = bb.getInt();
        secquenceId = bb.getInt();
        doSubDecode(bb);
    }

    protected byte[] getHead() {
        byte[] head = new byte[12];
        ByteBuffer byteBuffer = ByteBuffer.wrap(head);
        byteBuffer.putInt(totalLength);
        byteBuffer.putInt(commandId);
        byteBuffer.putInt(secquenceId);
        return byteBuffer.array();
    }

    protected void setHead(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        totalLength = byteBuffer.getInt();
        commandId = byteBuffer.getInt();
        secquenceId = byteBuffer.getInt();
    }

    public byte[] getMsgBytes() {
        return msgBytes;
    }

    public void setMsgBytes(byte[] msgBytes) {
        this.msgBytes = msgBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CmppHead cmppHead = (CmppHead) o;

        return new EqualsBuilder()
                .append(totalLength, cmppHead.totalLength)
                .append(commandId, cmppHead.commandId)
                .append(secquenceId, cmppHead.secquenceId)
                .append(protocolType, cmppHead.protocolType)
                .append(msgBytes, cmppHead.msgBytes)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(totalLength)
                .append(commandId)
                .append(secquenceId)
                .append(protocolType)
                .append(msgBytes)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("totalLength", totalLength)
                .append("commandId", commandId)
                .append("secquenceId", secquenceId)
                .append("protocolType", protocolType)
                .append("msgBytes", msgBytes)
                .toString();
    }
}
