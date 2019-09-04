package com.xavier.cmpp.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 模拟器配置
 *
 * @author NewGr8Player
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@ConfigurationProperties(prefix = "simulator.config")
public class SimulatorConfig {
    private int listenPort = 7890;
    private int idleSeconds = 1000;
    private int flowLimit = 20000;
    private String username;
    private String password;
    private boolean needValidate = true;
}
