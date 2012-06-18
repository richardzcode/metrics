package com.rz.metrics;

import com.rz.metrics.core.Tracker;
import com.rz.metrics.core.listeners.ConsoleListener;
import com.rz.metrics.core.listeners.HttpListener;

import java.util.Random;

/**
 * Author: richardz
 * Date: 6/7/12 4:27 PM
 */
public class App {
    public static void main(String[] args) throws InterruptedException{
        System.out.println("Hello World");

        Random random = new Random();

        Tracker.setTimeUnit(2000L);
        Tracker.addListener(new ConsoleListener());
        Tracker.addListener(new HttpListener("http://localhost:3001/metrics"));

        for (int i = 0; i < 100; i ++) {
            Tracker.incr("onlineUser");
        }

        for (int i = 0; i < 50000; i ++) {
            Tracker.count("traffic:/home");
            if (random.nextInt(10) < 5) {
                Tracker.decr("onlineUser");
            } else {
                Tracker.incr("onlineUser");
            }
            Tracker.gauge("latency:/home", (long) random.nextInt(1000));
            if (i % 100 == 0) {
                Tracker.log("malicious", String.format("%d: IP: xxx.xxx.xxx.xxx", i));
            }
            Thread.sleep((long) random.nextInt(10));
        }

        Tracker.clear();
        Tracker.addListener(new ConsoleListener());
        Tracker.setTimeUnit(Long.MAX_VALUE);

        for (int i = 0; i < 6000; i ++) {
            Tracker.count("cached:profiles");
            Thread.sleep((long) random.nextInt(10));
        }
        Tracker.peek();
    }
}
