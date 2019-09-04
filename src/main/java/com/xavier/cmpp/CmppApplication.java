package com.xavier.cmpp;

import com.xavier.cmpp.netty.ServerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Cmpp服务端模拟器
 *
 * @author NewGr8Player
 */
@SpringBootApplication
public class CmppApplication implements CommandLineRunner {

    @Autowired
    private ServerListener serverListener;

    public static void main(String[] args) {
        SpringApplication.run(CmppApplication.class, args);
    }

    @Override
    public void run(String... args) {
        serverListener.start();
    }
}
