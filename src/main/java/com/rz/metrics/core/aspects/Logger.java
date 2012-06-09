package com.rz.metrics.core.aspects;

import com.rz.metrics.core.entities.BaseEntity;
import com.rz.metrics.core.entities.LoggerEntity;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Author: rizhang
 * Date: 6/7/12 Time: 5:23 PM
 */
public class Logger extends Aspect {
    private ConcurrentLinkedQueue<String> values;
    private ConcurrentLinkedQueue<String> _values;

    public Logger(String key, long timeUnit) {
        super(key, timeUnit);
    }

    public void reset() {
        super.reset();

        this.values = new ConcurrentLinkedQueue<String>();
    }

    public void log(String val) {
        this.checkBound();
        this.values.add(val);
    }

    public void stackData() {
        super.stackData();

        this._values = values;
    }

    public BaseEntity getStackedData() {
        LoggerEntity entity = new LoggerEntity(this.getStackedTs(), this.getTimeUnit());
        entity.setKey(this.getKey());
        entity.setValues(this._values);

        this._values = null;

        return entity;
    }
}
