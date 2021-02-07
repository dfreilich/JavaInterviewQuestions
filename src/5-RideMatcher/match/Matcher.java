package interview2.match;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

interface INextBusProvider {
    List<StopEta> getLineEta(String lineNumber);
    List<StopInterval> getLineIntervals(String lineNumber);
}

class StopEta {
    int stopId;
    Date eta;
}

class StopInterval {
    int toStopId;
    int intervalSeconds;
}

class MatcherConfig {
    INextBusProvider provider;
    List<String> lineNumbers;
    int pollIntervalSeconds;
    int maxConcurrency;
    String outputPath;
}

interface IMatcher {
    void init(MatcherConfig config);
}

class BusLocation {
    int id;
    String line;
    Date eta;
    int nextStop;

    public BusLocation(int id, String line, Date eta, int nextStop) {
        this.id = id;
        this.line = line;
        this.eta = eta;
        this.nextStop = nextStop;
    }
}

class LineManager {
    List<StopInterval> intervals;
    List<BusLocation> buses;
    int nextID;

    public LineManager(int nextID, List<BusLocation> buses, List<StopInterval> intervals) {
        this.intervals = intervals;
        this.buses = buses;
        this.nextID = nextID;
    }

    public StopInterval getInterval(int i) {
        return intervals.get(i);
    }

    public int getNextID(){
        return nextID++;
    }

    public BusLocation getBus(int i) {
        return buses.get(i);
    }

    public void updateBus(BusLocation bus, StopEta eta) {
        bus.nextStop = eta.stopId;
        bus.eta = eta.eta;
    }

    public void addBus(BusLocation bus) {
        buses.add(0, bus);
    }

    public void removeBus(int i) {
        buses.remove(i);
    }
}

public class Matcher implements IMatcher {
    ScheduledExecutorService scheduler;
    Map<String, LineManager> managerMap;
    INextBusProvider provider;
    FileWriter writer;

    public Matcher() {}

    @Override
    public void init(MatcherConfig config) {
        if(config.maxConcurrency < 1 || config.pollIntervalSeconds < 0) {
            throw new IllegalArgumentException();
        }

        provider = config.provider;

        for(String line: config.lineNumbers) {
            LineManager manager = new LineManager(1, new ArrayList<>(), provider.getLineIntervals(line));
            managerMap.put(line, manager);
        }

        scheduler = Executors.newScheduledThreadPool(config.maxConcurrency);
        initWriter(config.outputPath);
        scheduleLinePolls(config.lineNumbers, config.pollIntervalSeconds);
    }

    public void scheduleLinePolls(List<String> lineNumbers, int pollIntervalSeconds) {
        Runnable pollRunnable = () -> {
            for(String line: lineNumbers) {
                scheduler.submit(() -> {
                    pollForLine(line);
                    return null;
                });
            }
        };

        try {
            scheduler.scheduleAtFixedRate(pollRunnable, 0, pollIntervalSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Something unexpected happened!");
            e.printStackTrace();
            scheduler.shutdown();
        }
    }

    public void pollForLine(String line) {
        LineManager manager = managerMap.get(line);
        int busIdx = manager.buses.size()-1;

        List<StopEta> etas = provider.getLineEta(line);
        int etaIdx = etas.size()-1;

        List<BusLocation> printList = new ArrayList<>();

        while(etaIdx >= 0) {
            StopEta currEta = etas.get(etaIdx);
            // Assumption: Always add a new bus for the first stop
            boolean foundBus = true;
            if(etaIdx > 0) {
                StopEta prev = etas.get(etaIdx-1);
                StopInterval interval = manager.getInterval(etaIdx-1);
                // This currently naively assumes that the intervals and stops match up
                foundBus = isBus(currEta, prev, interval);
            }

            if(foundBus) {
                // remove last bus in cases where bus has moved into last interval, but haven't removed the previous last
                if(busIdx >= 0 && etaIdx == etas.size()-1 && manager.getBus(busIdx).eta.after(currEta.eta)) {
                    manager.removeBus(busIdx);
                    busIdx--;
                }

                if(busIdx >= 0) {
                    BusLocation currBus = manager.getBus(busIdx);
                    if(currBus.nextStop != currEta.stopId) {
                        manager.updateBus(currBus, currEta);
                        printList.add(0, currBus);
                    }
                    busIdx--;

                    // May potentially have multiple buses in a box
                    // update buses until the buses ETA is before the etaIdx-1 (meaning, it has passed that idx)
                } else {
                    BusLocation currBus = new BusLocation(manager.getNextID(), line, currEta.eta, currEta.stopId);
                    manager.addBus(currBus);
                    printList.add(0, currBus);
                }


            } else if (hasFinishedRoute(busIdx, manager, currEta)) {
                    // This bus has finished the route, and should be removed
                    manager.removeBus(busIdx);
                    busIdx--;
            }

            etaIdx--;
        }

        for(BusLocation toPrint: printList) {
            writeToCSV(toPrint);
        }
    }

    public boolean isBus(StopEta etaA, StopEta etaB, StopInterval interval){
        if (etaA.eta.after(etaB.eta)) return true;

        long diff = etaB.eta.getTime() - etaA.eta.getTime();
        diff = TimeUnit.MILLISECONDS.toSeconds(diff);

        return diff < interval.intervalSeconds;
    }

    public boolean hasFinishedRoute(int busIdx, LineManager manager, StopEta currEta) {
        return busIdx >= 0 && manager.buses.get(busIdx).nextStop == currEta.stopId;
    }

    public void initWriter(String filePath){
        try {
            writer = new FileWriter(filePath);
        } catch (IOException e) {
            System.out.printf("Failed to open/create file at %s\n", filePath);
            e.printStackTrace();
        }

        try {
            List<String> writeList = List.of("Timestamp",",", "LineNumber",",","NextStopId",",","ETA",",","TripId", "\n");
            for(String str: writeList) {
                writer.append(str);
            }
            writer.flush();
        } catch (IOException e) {
            System.out.printf("Failed to write to file at %s\n", filePath);
            e.printStackTrace();
        }
    }

    public void writeToCSV(BusLocation bus){
        synchronized (writer) {
            try {
                writer.append((new Date()).toString());
                writer.append(",");
                writer.append(bus.line);
                writer.append(",");
                writer.append(""+bus.nextStop);
                writer.append(",");
                writer.append(bus.eta.toString());
                writer.append(",");
                writer.append(""+bus.id);
                writer.append("\n");

                writer.flush();
            } catch(IOException e) {
                System.out.println("Failed to write to file");
                e.printStackTrace();
            }
        }
    }
}
