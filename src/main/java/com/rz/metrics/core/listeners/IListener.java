package com.rz.metrics.core.listeners;

import org.codehaus.jackson.JsonNode;

/**
 * Author: richardz
 * Date: 6/7/12 Time: 4:01 PM
 */
public interface IListener {
    public void onTimeUnit(JsonNode data);
}