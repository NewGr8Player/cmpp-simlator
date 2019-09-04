package com.xavier.cmpp.protocol.constant;

/**
 * 消息类型
 *
 * @author NewGr8Player
 */
public class CMPPConstant {

    public final static int CMPP_CONNECT = 0x00000001;
    public final static int CMPP_CONNECT_RESP = 0x80000001;
    public final static int APP_SUBMIT = 0x00000004;
    public final static int APP_SUBMIT_RESP = 0x80000004;
    public final static int APP_DELIVER = 0x00000005;
    public final static int APP_DELIVER_RESP = 0x80000005;
    public final static int APP_ACTIVE_TEST = 0x00000008;
    public final static int APP_ACTIVE_TEST_RESP = 0x80000008;
}