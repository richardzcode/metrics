metrics
=======

Capture Java application metrics

Inspired by [Metrics](http://metrics.codahale.com/) written by Coda Hale. But don't want to get too complicated. I want something lightweight but just enough.

Minimum overhead on data collecting. Use thread to emit event when time unit reached.

Depend on listener implementation, data can be send to database, in-memory, another service, or wherever useful.

##Version
0.1.2

##Classes

###Tracker
Start point. Singleton class. Default time unit is 1 minute.
    Tracker.count(key);
    Tracker.incr(key); Tracker.decr();
    Tracker.gauge(key, value);
    Tracker.log(key, log);

    Tracker.peek(); // Emit onTimeUnit with current data.

###Aspect

Base class

If aspect is set to accumulative then values do not reset on each time unit. Default is non-accumulative.

######Counter
Count: Counts number per time unit.

Incr/Decr: Keep counts over time. Does not reset number if the counter is used with incr/decr.

######Gauger
Collect dataset per time unit. Calculates count/total/main/mean/90 percentile/95 percentile/99 percentile/max when emitting event.

######Logger
Keep special logs per one time unit.

###Listener
Implements IListener interface. The method onTimeUnit will be triggered on every time unit with collected data in JSON string.

Application should implement IListener and add to Tracker to deal with data.

    public interface IListener {
        public void onTimeUnit(String data);
    }

######ConsoleListener
Listen to data and writes data to console.

######HttpListener
Listen to data and POST to another service.

##Usage

###Initialize
    import com.rz.metrics.Tracker;
    import com.rz.metrics.listeners.ConsoleListener;
    
    Tracker.setTimeUnit(1000L); // 1 second
    Tracker.addListener(new ConsoleListener());

###Tracking
######Count
    Tracker.count("traffic:url1");

If first time calling a counter, which creates the counter, is by incr/decr then the counter is set to accumulative automatically.

    Tracker.incr("onlineUser"); // Login
    Tracker.decr("onlineUser"); // Logout or expire

######Gauge
    long latency = nnn;
    Tracker.gauge("latency:url1", latency);

######Log
    Tracker.log("malicious", "IP xxx.xxx.xxx.xxx");

###Listening
    /**
     * @param data
     *         {
     *             aspect: "counter"|"gauger"|"logger"
     *             , key: "..."
     *             , ts: "nnn" // Timestamp
     *             , timeUnit: "60000"
     *             , data: {...}
     *         }
     */
    public void onTimeUnit(String data) {
        // Do whatever.
    }

##Run example
    java -jar metrics-0.1.0.jar

######Node.js client
    cd nodeClient
    node app.js

Open browser, goto http://localhost:3001/index.html
![nodeClient](https://github.com/richardzcode/metrics/raw/master/screenshots/nodeClient.png)
