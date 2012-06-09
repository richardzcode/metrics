package com.rz.metrics.core.aspects;

import com.rz.metrics.core.entities.BaseEntity;
import com.rz.metrics.core.listeners.IListener;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.Date;

/**
 * Author: richardz
 * Date: 6/7/12 Time: 2:22 PM
 */
public abstract class Aspect {
    private String key;
    private long timeUnit;

    private long start;
    private long end_bound;

    // Stack data
    private long _ts;

    private ArrayList<IListener> listeners;

    private Object lockOnTimeUnit = new Object();

    public Aspect(String key, long timeUnit) {
        this.key = key;
        this.timeUnit = timeUnit;
        this.listeners = new ArrayList<IListener>();

        this.reset();
    }

    public String getKey() {
        return this.key;
    }

    public long getTimeUnit() {
        return this.timeUnit;
    }

    public long getStackedTs() {
        return this._ts;
    }

    public void reset() {
        this.start = (new Date()).getTime();

        long end = this.start + this.timeUnit;
        this.end_bound = end - end % this.timeUnit;
    }

    public boolean isOverBound(long ts) {
        return ts > this.end_bound;
    }

    public void checkBound() {
        long ts = (new Date()).getTime();
        if (this.isOverBound(ts)) {
            synchronized (this.lockOnTimeUnit) {
                if (this.isOverBound(ts)) {
                    this.stackData();
                    this.reset();
                    this.onTimeUnit();
                }
            }
        }
    }

    public void addListener(IListener listener) {
        this.listeners.add(listener);
    }

    public void addListeners(ArrayList<IListener> listeners) {
        this.listeners.addAll(listeners);
    }

    public void onTimeUnit() {
        EventEmitter eventEmitter = new EventEmitter(this.getStackedData(), this.listeners);
        eventEmitter.start();
    }

    // Stack data to be processed later.
    // Event will be emitted from stacked data.
    public void stackData() {
        this._ts = this.end_bound - this.timeUnit;
    }

    public BaseEntity getStackedData() {
        return null;
    }

    private static class EventEmitter extends Thread {
        private BaseEntity entity;
        private ArrayList<IListener> listeners;

        public EventEmitter(BaseEntity entity, ArrayList<IListener> listeners) {
            this.entity = entity;
            this.listeners = listeners;
        }
        public void run() {
            JsonNode data = this.entity.toJson();

            for (IListener listener : this.listeners) {
                listener.onTimeUnit(data);
            }
        }
    }
}
