package com.rz.metrics.core.entities;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Author: richardz
 * Date: 6/7/12 Time: 5:58 PM
 */
public class GaugerEntity extends BaseEntity {
    public static String JSON_KEY_COUNT         = "count";
    public static String JSON_KEY_TOTAL         = "total";
    public static String JSON_KEY_MIN           = "min";
    public static String JSON_KEY_MEAN          = "mean";
    public static String JSON_KEY_PERCENTILE_90 = "percentile90";
    public static String JSON_KEY_PERCENTILE_95 = "percentile95";
    public static String JSON_KEY_PERCENTILE_99 = "percentile99";
    public static String JSON_KEY_MAX           = "max";

    private PriorityBlockingQueue<Long> values;

    public GaugerEntity(long ts, long timeUnit) {
        super(ASPECT_NAME_GAUGER, ts, timeUnit);
    }

    public void setValues(PriorityBlockingQueue<Long> values) {
        this.values = values;
    }

    public JsonNode toJson() {
        JsonNode root = super.toJson();

        Calc calc = new Calc();
        calc.calc(this.values);

        JsonNode data = root.get(JSON_KEY_DATA);
        ObjectNode obj = (ObjectNode) data;
        obj.put(JSON_KEY_COUNT,         calc.getTotalCount());
        obj.put(JSON_KEY_TOTAL,         calc.getTotalValue());
        obj.put(JSON_KEY_MIN,           calc.getMin());
        obj.put(JSON_KEY_MEAN,          calc.getMean());
        obj.put(JSON_KEY_PERCENTILE_90, calc.getPercentile90());
        obj.put(JSON_KEY_PERCENTILE_95, calc.getPercentile95());
        obj.put(JSON_KEY_PERCENTILE_99, calc.getPercentile99());
        obj.put(JSON_KEY_MAX,           calc.getMax());

        return root;
    }

    private static class Calc {
        private int totalCount;
        private long totalValue;

        private long min;
        private long mean;
        private long percentile90;
        private long percentile95;
        private long percentile99;
        private long max;

        public int getTotalCount() {
            return this.totalCount;
        }

        public long getTotalValue() {
            return this.totalValue;
        }

        public long getMin() {
            return this.min;
        }

        public long getMean() {
            return this.mean;
        }

        public long getPercentile90() {
            return this.percentile90;
        }

        public long getPercentile95() {
            return this.percentile95;
        }

        public long getPercentile99() {
            return this.percentile99;
        }

        public long getMax() {
            return this.max;
        }

        public void calc(PriorityBlockingQueue<Long> values) {
            this.totalValue
                    = this.min
                    = this.mean
                    = this.percentile90
                    = this.percentile95
                    = this.percentile99
                    = this.max
                    = 0;

            this.totalCount = values.size();
            if (this.totalCount == 0) {
                return;
            }

            int index90 = (int) Math.ceil(((float) this.totalCount) * 0.9);
            int index95 = (int) Math.ceil(((float) this.totalCount) * 0.95);
            int index99 = (int) Math.ceil(((float) this.totalCount) * 0.99);

            int i = 1;
            Long value = values.poll();
            this.min = value;
            while(value != null) {
                this.totalValue += value;

                if (i == index90) { this.percentile90 = value; }
                if (i == index95) { this.percentile95 = value; }
                if (i == index99) { this.percentile99 = value; }

                if (i == this.totalCount) { this.max = value; }

                i ++;
                value = values.poll();
            }

            this.mean = this.totalValue / this.totalCount;
        }
    }
}
