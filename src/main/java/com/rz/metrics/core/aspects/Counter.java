package com.rz.metrics.core.aspects;

import com.rz.metrics.core.entities.BaseEntity;
import com.rz.metrics.core.entities.CounterEntity;

/**
 * Author: richardz
 * Date: 6/6/12 Time: 12:19 PM
 */
public class Counter extends Aspect{
    private volatile long count;

    // Stacked data
    private long _count;

    public Counter(String key, long timeUnit) {
        super(key, timeUnit);
    }

    public void resetValues() {
        super.resetValues();

        this.count = 0;
    }

    public void count(int num) {
        this.checkBound();
        this.count += num;
    }

    public void stackData() {
        super.stackData();
        this._count = this.count;
    }

    public BaseEntity getStackedData() {
        CounterEntity entity = new CounterEntity(this.getStackedTs(), this.getTimeUnit());
        entity.setKey(this.getKey());
        entity.setCount(this._count);
        return entity;
    }
}
