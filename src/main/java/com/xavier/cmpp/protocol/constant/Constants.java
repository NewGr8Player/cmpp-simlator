package com.xavier.cmpp.protocol.constant;

import io.netty.util.AttributeKey;

/**
 * 版本
 *
 * @author NewGr8Player
 */
public class Constants {
    public static int PROTOCOL_TYPE_VERSION_CMPP2 = 32;
    public static int PROTOCOL_TYPE_VERSION_CMPP3 = 48;
    public static AttributeKey PROTOCOL_TYPE_VERSION = AttributeKey.newInstance("protocolType");
}
