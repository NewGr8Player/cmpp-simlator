package com.xavier.cmpp.protocol.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 统计输出，调用 {@link #logOutSpeed()}方法可实现周期速度统计
 */
@Component
public class StatisticUtil {
    private final static Logger logger = LoggerFactory.getLogger(StatisticUtil.class);

    private static AtomicInteger submitCount = new AtomicInteger(0);
    private static AtomicInteger deliverCount = new AtomicInteger(0);
    private int lastSubmitCount = 0;
    private int lastDeliverCount = 0;

    public static void addSubmit(Integer num) {
        submitCount.addAndGet(num);
    }

    public static void addSubmit() {
        addSubmit(1);
    }

    public static void addDeliver(Integer num) {
        deliverCount.addAndGet(num);
    }

    public static void addDeliver() {
        addDeliver(1);
    }

    @Scheduled(fixedRate = 1000L)
    public void logOutSpeed() {
        int temp = submitCount.get();
        logger.info("Submit total:【{}】,Speed:【{}/s】", temp, temp - lastSubmitCount);
        lastSubmitCount = temp;
        temp = deliverCount.get();
        logger.info("Deliver total:【{}】,Speed:【{}/s】", temp, temp - lastDeliverCount);
        lastDeliverCount = temp;
    }
}