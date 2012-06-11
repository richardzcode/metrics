package com.rz.metrics.core.entities;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

/**
 * Author: rizhang
 * Date: 6/7/12 Time: 2:51 PM
 */
public class CounterEntity extends BaseEntity{
    public static String JSON_KEY_COUNT     = "count";

    private long count;

    public CounterEntity(long ts, long timeUnit) {
        super(ASPECT_NAME_COUNTER, ts, timeUnit);
    }

    public void setCount(long count) {
        this.count = count;
    }

    public JsonNode toJson() {
        JsonNode root = super.toJson();

        JsonNode data = root.get(JSON_KEY_DATA);
        ObjectNode obj = (ObjectNode) data;
        obj.put(JSON_KEY_COUNT, this.count);

        return root;
    }
}
