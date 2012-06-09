package com.rz.metrics.core.listeners;

import org.codehaus.jackson.JsonNode;

/**
 * Author: richardz
 * Date: 6/7/12 Time: 4:21 PM
 */
public class ConsoleListener implements IListener {
    public void onTimeUnit(JsonNode data) {
        System.out.println(data.toString());
    }
}
