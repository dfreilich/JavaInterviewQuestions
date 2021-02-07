package interview2.poll;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

class StopEta {
    int stopId;
    Date eta;
}


interface INextBusProvider {
    List<StopEta> getLineEta(String lineNumber);
}

class PollerConfig {
    INextBusProvider provider;
    List<String> lineNumbers;
    int pollIntervalSeconds;
    int maxConcurrency;
}

class LineEta {
    String lineNumber;
    Date eta;

    public LineEta(String line, Date eta) {
        this.lineNumber = line;
        this.eta = eta;
    }
}

interface IPoller {
    void init(PollerConfig provider);
    List<LineEta> getStopArrivals(int stopId);
}

//    poller.getStopArrivals(4234);
//    	List< {"5", 10:05}, {"6"}, 10:08} >
public class Poller implements IPoller {
    ScheduledExecutorService scheduler;
    Map<Integer, Map<String, LineEta>> stopMap;

    public static void main(String[] args) {
        Poller poll = new Poller();
//        poll.init(PollerConfig());
    }

    public Poller(){}

    @Override
    public void init(PollerConfig config) {
        if(config.maxConcurrency < 1 || config.pollIntervalSeconds < 0) {
            throw new IllegalArgumentException();
        }

        scheduler = Executors.newScheduledThreadPool(config.maxConcurrency);
        stopMap = new ConcurrentHashMap<>();
        scheduleLinePolls(config);
    }

    public void scheduleLinePolls(PollerConfig config) {
        Runnable pollRunnable = () -> {
            for(String line: config.lineNumbers) {
                scheduler.submit(() -> {
                    pollForLine(config.provider, line);
                    return null;
                });
            }
        };

        try {
            scheduler.scheduleAtFixedRate(pollRunnable, 0, config.pollIntervalSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Something unexpected happened!");
            e.printStackTrace();
            scheduler.shutdown();
        }
    }

    public void pollForLine(INextBusProvider provider, String line) {
        List<StopEta> curr = provider.getLineEta(line);
        for(StopEta stop: curr) {
            // Initialize map with ConcurrentHashMap, so all operations are thread safe and everyone has same map
            if(!stopMap.containsKey(stop.stopId)) {
                stopMap.putIfAbsent(stop.stopId, new ConcurrentHashMap<>());
            }

            stopMap.get(stop.stopId).put(line, new LineEta(line, stop.eta));
        }
    }

    @Override
    public List<LineEta> getStopArrivals(int stopID) {
        if(stopMap.containsKey(stopID)) {
            return (List<LineEta>) stopMap.get(stopID).values();
        }
        return new ArrayList<>();
    }
}
