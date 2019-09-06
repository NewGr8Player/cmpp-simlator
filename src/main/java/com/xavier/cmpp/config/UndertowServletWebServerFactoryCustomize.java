package com.xavier.cmpp.config;

import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

/**
 * 使用自定义 Undertow pool
 *
 * @author NewGr8Player
 */
@Component
public class UndertowServletWebServerFactoryCustomize implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private static final boolean DEFAULT_DIRECT = false;

    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        factory.addDeploymentInfoCustomizers(
                deploymentInfo ->
                        deploymentInfo.addServletContextAttribute(
                                WebSocketDeploymentInfo.ATTRIBUTE_NAME
                                , new WebSocketDeploymentInfo().setBuffers(
                                        new DefaultByteBufferPool(DEFAULT_DIRECT, DEFAULT_BUFFER_SIZE)
                                )
                        )
        );
    }
}
