package com.xavier.cmpp.protocol;

/**
 * 流量管理
 *
 * @author NewGr8Player
 */
public interface FlowControl {
    /**
     * 检测是否超出流量
     *
     * @return 是否超出流量
     */
    boolean isOverFlow();

    /**
     * 检测是否超出流量,流量限额<=0时返回false，否则返回true
     *
     * @return 是否超出流量
     */
    boolean isOverFlow(int checkNum);

    /**
     * 修改流量
     *
     * @param speed 流量
     */
    void changeSpeed(int speed);


    /**
     * 获取当前速度
     */
    Integer getSpeed();

    /**
     * 重置流量
     */
    void resetFlow();
}
