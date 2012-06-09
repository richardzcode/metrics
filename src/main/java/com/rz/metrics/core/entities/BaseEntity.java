package com.rz.metrics.core.entities;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

/**
 * Author: richardz
 * Date: 6/7/12 Time: 2:51 PM
 */
public class BaseEntity {
    public static String ASPECT_NAME_COUNTER    = "counter";
    public static String ASPECT_NAME_GAUGER     = "gauger";
    public static String ASPECT_NAME_LOGGER     = "logger";

    public static String JSON_KEY_ASPECT    = "aspect";
    public static String JSON_KEY_KEY       = "key";
    public static String JSON_KEY_TIMESTAMP = "ts";
    public static String JSON_KEY_TIME_UNIT = "timeUnit";
    public static String JSON_KEY_DATA      = "data";

    protected static ObjectMapper mapper;

    private String aspect;
    private String key;

    private long ts;
    private long timeUnit;

    static {
        mapper = new ObjectMapper();
    }

    public BaseEntity(String aspectName, long ts, long timeUnit) {
        this.aspect = aspectName;
        this.ts = ts;
        this.timeUnit = timeUnit;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JsonNode toJson() {
        JsonNode root = mapper.createObjectNode();

        ObjectNode obj = (ObjectNode) root;
        obj.put(JSON_KEY_ASPECT,    this.aspect);
        obj.put(JSON_KEY_KEY,       this.key);
        obj.put(JSON_KEY_TIMESTAMP, this.ts);
        obj.put(JSON_KEY_TIME_UNIT, this.timeUnit);

        JsonNode data = mapper.createObjectNode();
        obj.put(JSON_KEY_DATA, data);

        return root;
    }
}
