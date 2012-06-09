package com.rz.metrics.core;

import com.rz.metrics.core.aspects.Aspect;
import com.rz.metrics.core.aspects.Gauger;
import com.rz.metrics.core.aspects.Logger;
import com.rz.metrics.core.listeners.IListener;
import com.rz.metrics.core.aspects.Counter;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: richardz
 * Date: 6/6/12 Time: 11:43 AM
 */
public class Tracker {
    private static long timeUnit = 60000L; // in milliseconds, default 60 seconds.

    private static Tracker instance;

    private ArrayList<IListener> listeners;

    private ConcurrentHashMap<String, Counter> counters;
    private ConcurrentHashMap<String, Gauger> gaugers;
    private ConcurrentHashMap<String, Logger> loggers;

    static {
        instance = new Tracker();
    }

    private Tracker() {
        this.listeners = new ArrayList<IListener>();

        this.counters = new ConcurrentHashMap<String, Counter>();
        this.gaugers = new ConcurrentHashMap<String, Gauger>();
        this.loggers = new ConcurrentHashMap<String, Logger>();
    }

    public static Tracker getInstance() {
        return instance;
    }

    public static void setTimeUnit(long time) {
        timeUnit = time;
    }

    public static void addListener(IListener listener) {
        getInstance().doAddListener(listener);
    }

    public static void count(String key) {
        getInstance().doCount(key);
    }

    public static void gauge(String key, long val) {
        getInstance().doGauge(key, val);
    }

    public static void log(String key, String log) {
        getInstance().doLog(key, log);
    }

    public void doAddListener(IListener listener) {
        this.listeners.add(listener);

        for (Counter counter : this.counters.values()) {
            counter.addListener(listener);
        }
    }

    public void doCount(String key) {
        if (this.listeners.isEmpty()) {
            return;
        }

        Counter counter = this.getAspect(Counter.class, this.counters, key);
        if (counter != null) {
            counter.count();
        }
    }

    public void doGauge(String key, long val) {
        if (this.listeners.isEmpty()) {
            return;
        }

        Gauger gauger = this.getAspect(Gauger.class, this.gaugers, key);
        if (gauger != null) {
            gauger.gauge(val);
        }
    }

    public void doLog(String key, String val) {
        if (this.listeners.isEmpty()) {
            return;
        }

        Logger logger = this.getAspect(Logger.class, this.loggers, key);
        if (logger != null) {
            logger.log(val);
        }
    }

    private <T extends Aspect> T getAspect(Class<T> klass, ConcurrentHashMap<String, T> collection, String key) {
        T t = collection.get(key);
        if (t != null) {
            return t;
        }

        // No need to worry too much about multi-thread. Worst case is the first timeUnit not accurate.
        // So no locking introduced.
        try {
            t = klass.getDeclaredConstructor(String.class, long.class).newInstance(key, timeUnit);
            if (!collection.contains(key)) {
                collection.put(key, t);
                t.addListeners(this.listeners);
            } else {
                t = collection.get(key);
            }
        } catch (Exception e) {
            //
        }
        return t;
    }
}
