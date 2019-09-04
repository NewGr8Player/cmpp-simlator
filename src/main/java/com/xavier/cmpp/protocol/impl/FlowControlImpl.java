package com.xavier.cmpp.protocol.impl;

import com.xavier.cmpp.config.SimulatorConfig;
import com.xavier.cmpp.protocol.FlowControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FlowControlImpl implements FlowControl {
    private int limitNum;/* 流量限额 */
    private AtomicInteger actNum;/* 实际计数器 */

    @Autowired
    private SimulatorConfig simulatorConfig;

    @PostConstruct
    public void init() {
        int limitNum = simulatorConfig.getFlowLimit();
        this.limitNum = limitNum;
        actNum = new AtomicInteger(limitNum);
    }

    @Override
    public boolean isOverFlow() {
        return actNum.decrementAndGet() < 0;
    }

    @Override
    public boolean isOverFlow(int checkNum) {
        return actNum.addAndGet(-checkNum) < 0;
    }

    @Override
    @Scheduled(fixedRate = 1000L)
    public void resetFlow() {
        actNum.set(limitNum);
    }

    @Override
    public void changeSpeed(int speed) {
        limitNum = speed;
    }

    @Override
    public Integer getSpeed() {
        return limitNum;
    }
}
