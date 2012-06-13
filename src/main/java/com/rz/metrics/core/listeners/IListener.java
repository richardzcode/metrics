package com.rz.metrics.core.listeners;

/**
 * Author: richardz
 * Date: 6/7/12 Time: 4:01 PM
 */
public interface IListener {
    /**
     * @param data
     *         {
     *             aspect: "counter"|"gauger"|"logger"
     *             , key: "..."
     *             , ts: "nnn" // Timestamp
     *             , timeUnit: "60000"
     *             , data: {...}
     *         }
     */
    public void onTimeUnit(String data);
}