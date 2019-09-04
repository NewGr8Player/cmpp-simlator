package com.xavier.cmpp.protocol.util;

import com.google.common.base.Charsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 协议相关工具类
 *
 * @author NewGr8Player
 */
public class ProtocolUtil {

    /**
     * MD5算法
     */
    public static final String ALGORITHM_MD5 = "MD5";

    /**
     * 用户鉴权数据生成
     *
     * @param user      协议中 Source_Addr
     * @param password  协议中 shared secret
     * @param timeStamp MMDDHHMMSS 格式时间字符串，不足十位左则补0，例如『0708101023』
     * @return byte数组
     */
    public static byte[] getAuthString(String user, String password, String timeStamp) {
        byte[] bytes = new byte[user.length() + 9 + password.length() + timeStamp.length()];
        System.arraycopy(user.getBytes(Charsets.US_ASCII), 0, bytes, 0, user.length());
        System.arraycopy(password.getBytes(Charsets.US_ASCII), 0, bytes, user.length() + 9, password.length());
        System.arraycopy(timeStamp.getBytes(Charsets.US_ASCII), 0, bytes, user.length() + 9 + password.length(), 10);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(ALGORITHM_MD5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return md.digest(bytes);
    }

    /**
     * 用户鉴权数据生成
     *
     * @param user      协议中 Source_Addr
     * @param passwd    协议中 shared secret
     * @param timeStamp 整型时间，例如『708101023』
     * @return byte数组
     */
    public static byte[] getAuthString(String user, String passwd, int timeStamp) {
        String timeStr = String.valueOf(timeStamp);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 10 - timeStr.length(); i++) {
            sb.append("0");
        }
        sb.append(timeStr);
        return getAuthString(user, passwd, sb.toString());
    }
}
