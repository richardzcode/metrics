package com.rz.metrics;

import com.rz.metrics.core.Tracker;
import com.rz.metrics.core.listeners.ConsoleListener;

import java.util.Random;

/**
 * Author: richardz
 * Date: 6/7/12 4:27 PM
 */
public class App {
    public static void main(String[] args) throws InterruptedException{
        System.out.println("Hello World");

        Random random = new Random();

        Tracker.setTimeUnit(5000L);
        Tracker.addListener(new ConsoleListener());
        for (int i = 0; i < 5000; i ++) {
            Tracker.count("counter1");
            if (random.nextInt(5) == 1) {
                Tracker.decr("counter2");
            } else {
                Tracker.incr("counter2");
            }
            Tracker.gauge("gauger1", (long) random.nextInt(1000));
            if (i % 100 == 0) {
                Tracker.log("logger1", String.format("i == %d", i));
            }
            Thread.sleep((long) random.nextInt(10));
        }
    }
}
