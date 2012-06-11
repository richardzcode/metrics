metrics
=======

Capture Java application metrics

Inspired by [Metrics](http://metrics.codahale.com/) written by Coda Hale. But don't want to get too complicated. So write a very light one just enough for basic usages.

Minimum overhead on data collecting. Use thread to emit event when time unit reached.

Depend on listener implementation, data can be send to database, in-memory, another service, or wherever useful.

##Classes

###Tracker
Start point. Singleton class. Default time unit is 1 minute.

###Aspect

Base class

If aspect is set to accumulative then values do not reset on each time unit. Default is non-accumulative.

######Counter
Counts number per time unit.

Incr/Decr are used to keep counts over time. If the counter is used with incr/decr then number doesn't reset on time unit.

######Gauger
Collect dataset per time unit and calculates count/total/main/mean/90 percentile/95 percentile/99 percentile/max

######Logger
Keep special logs per one time unit.

###Listener
Implements IListener interface. The method onTimeUnit will be triggered on every time unit with collected data in JSON string.

Application should implement IListener and add to Tracker to deal with data.

    public interface IListener {
        public void onTimeUnit(String data);
    }

######ConsoleListener
An example of listener. Writes data to console.

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
