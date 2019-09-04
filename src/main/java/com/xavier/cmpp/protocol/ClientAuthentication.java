package com.xavier.cmpp.protocol;

/**
 * 客户端鉴权接口
 *
 * @author NewGr8Player
 */
public interface ClientAuthentication {
    /**
     * 客户端鉴权
     *
     * @param authenticatorSource connect请求加密数据
     * @param timeStamp           时间戳
     * @param remoteAddr          客户端地址，便于扩展IP鉴权
     * @return 是否通过鉴权
     */
    boolean authenticateClient(byte[] authenticatorSource, byte[] timeStamp, String remoteAddr);
}
