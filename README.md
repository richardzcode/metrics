metrics
=======

Capture Java application metrics

Inspired by [Metrics](http://metrics.codahale.com/) written by Coda Hale. But wanted to write a very light one just enough for my projects.

Try to make data collection as fast as possible. When time unit reached it'll start another thread to emit event.

##Classes

###Tracker
Start point. Singleton class.

###Aspects

If aspect is set to accumulative then values do not reset on each time unit. Default is non-accumulative.

######Counter
Counts number of invocations in one time unit.

######Gauger
Collect dataset in one time unit and calculates count/total/main/mean/90 percentile/95 percentile/99 percentile/max

######Logger
Keep special logs in one time unit.

###Listener
Implements IListener interface. The method onTimeUnit will be triggered on every time unit with collected data in JSON string.

Application should implement IListener and add to Tracker to deal with data.

    public interface IListener {
        public void onTimeUnit(String data);
    }

######ConsoleListener
Writes data to console.

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
    public void onTimeUnit(String data) {
        // Do whatever.
    }
