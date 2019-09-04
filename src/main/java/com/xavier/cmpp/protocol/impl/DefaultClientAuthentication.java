package com.xavier.cmpp.protocol.impl;

import com.google.common.primitives.Ints;
import com.xavier.cmpp.config.SimulatorConfig;
import com.xavier.cmpp.protocol.ClientAuthentication;
import com.xavier.cmpp.protocol.util.ProtocolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DefaultClientAuthentication implements ClientAuthentication {
    @Autowired
    private SimulatorConfig simulatorConfig;

    @Override
    public boolean authenticateClient(byte[] authenticatorSource, byte[] timeStamp, String remoteAddr) {
        return !simulatorConfig.isNeedValidate()
                || Arrays.equals(ProtocolUtil.getAuthString(
                simulatorConfig.getUsername()
                , simulatorConfig.getPassword()
                , Ints.fromByteArray(timeStamp))
                , authenticatorSource
        );
    }
}
