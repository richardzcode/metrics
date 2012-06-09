package com.rz.metrics.core.entities;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Author: richardz
 * Date: 6/7/12 Time: 5:58 PM
 */
public class LoggerEntity extends BaseEntity {
    public static String JSON_KEY_COUNT = "count";
    public static String JSON_KEY_LOGS  = "logs";

    private ConcurrentLinkedQueue<String> values;

    public LoggerEntity(long ts, long timeUnit) {
        super(ASPECT_NAME_LOGGER, ts, timeUnit);
    }

    public void setValues(ConcurrentLinkedQueue<String> values) {
        this.values = values;
    }

    public JsonNode toJson() {
        JsonNode root = super.toJson();

        JsonNode data = root.get(JSON_KEY_DATA);
        ObjectNode obj = (ObjectNode) data;
        obj.put(JSON_KEY_COUNT, this.values.size());

        ArrayNode logs = mapper.createArrayNode();
        for (String log : this.values) {
            logs.add(log);
        }
        obj.put(JSON_KEY_LOGS, logs);

        return root;
    }
}
