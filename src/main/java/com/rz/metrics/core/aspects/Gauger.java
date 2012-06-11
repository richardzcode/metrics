package com.rz.metrics.core.aspects;

import com.rz.metrics.core.entities.BaseEntity;
import com.rz.metrics.core.entities.GaugerEntity;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * Author: richardz
 * Date: 6/7/12 Time: 5:23 PM
 */
public class Gauger extends Aspect {
    private PriorityBlockingQueue<Long> values;

    // Stacked data
    private PriorityBlockingQueue<Long> _values;

    public Gauger(String key, long timeUnit) {
        super(key, timeUnit);
    }

    public void resetValues() {
        super.resetValues();

        this.values = new PriorityBlockingQueue<Long>(1000);
    }

    public void gauge(long val) {
        this.checkBound();
        this.values.offer(val);
    }

    public void stackData() {
        super.stackData();

        this._values = values;
    }

    public BaseEntity getStackedData() {
        GaugerEntity entity = new GaugerEntity(this.getStackedTs(), this.getTimeUnit());
        entity.setKey(this.getKey());
        entity.setValues(this._values);

        this._values = null;

        return entity;
    }
}
