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

    // This aspect tracker is accumulative means numbers wouldn't be reset after each time unit.
    private boolean isAccumulative;

    private long start;
    private long end_bound;

    // Stacked data
    private long _ts;

    private ArrayList<IListener> listeners;

    // Provide a helper lock to make sure onTimeUnit thread-safe.
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

    public boolean IsAccumulative() {
        return this.isAccumulative;
    }

    public void setAccumulative(boolean accum) {
        this.isAccumulative = accum;
    }

    public long getStackedTs() {
        return this._ts;
    }

    public void reset() {
        this.start = (new Date()).getTime();

        if (this.start < this.timeUnit) {
            this.end_bound = this.timeUnit;
        } else {
            long end = this.start + this.timeUnit;
            this.end_bound = end - end % this.timeUnit;
        }

        if (!this.isAccumulative) {
            resetValues();
        }
    }

    public void resetValues() {
        //
    }

    /**
     * To check if current tracking started more than one timeUnit ago.
     * If so, a onTimeUnit should be emitted.
     *
     * @param ts The timestamp
     */
    public boolean isOverBound(long ts) {
        return ts > this.end_bound;
    }

    /**
     * Make sure previous tracked data are purged.
     *
     * Sub-classes should call CheckBound before any data tracking.
     */
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

    /**
     * Emit onTimeUnit event with current collected data, collection continues.
     */
    public void peek() {
        synchronized (this.lockOnTimeUnit) {
            this.stackData();
            // this.reset(); Do not reset
            this.onTimeUnit();
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
        this._ts = this.start;
    }

    /**
     * Get the stacked data for emitting onTimeUnit event.
     *
     * Sub-class should override this method.
     *
     * @return Stacked data entity object.
     */
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
                listener.onTimeUnit(data.toString());
            }
        }
    }
}
