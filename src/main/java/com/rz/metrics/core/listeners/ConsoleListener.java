package com.rz.metrics.core.listeners;

/**
 * Author: richardz
 * Date: 6/7/12 Time: 4:21 PM
 */
public class ConsoleListener implements IListener {
    public void onTimeUnit(String data) {
        System.out.println(data);
    }
}
