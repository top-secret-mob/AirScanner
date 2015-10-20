package com.wostrowski.airscanner;

import com.google.common.collect.Lists;
import com.wostrowski.airscanner.scanner.StationDetector;
import com.wostrowski.airscanner.storage.model.Station;
import com.wostrowski.airscanner.storage.model.StorageConnector;
import org.tudelft.aircrack.frame.Address;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Monitors state of all detected stations
 */
public class StationMonitor implements Runnable, StationDetector.StationDetectorListener {
    private static final long DEFAULT_TTL = TimeUnit.MINUTES.toMillis(5);
    private final long ttl;
    private final StationDetector detector;
    private Thread monitorTread;
    private final BlockingQueue<Address[]> detectionQueue = new LinkedBlockingQueue<>(1000);
    // <MAC, Station>
    private final Map<String, Station> stationsConfig = new HashMap<>();
    // <MAC, StationTtl>
    private final Map<String, StationTtl> onlineStations = new HashMap<>();

    class StationTtl {
        Station station;
        long lastUpdateTime;

        public StationTtl(Station station, long lastUpdateTime) {
            this.station = station;
            this.lastUpdateTime = lastUpdateTime;
        }
    }

    public StationMonitor() {
        this(DEFAULT_TTL);
    }

    /**
     * @param ttl    station time to live, after which station is considered as gone (in milliseconds)
     */
    public StationMonitor(long ttl) {
        detector = new StationDetector(this);
        this.ttl = ttl;
    }

    public void startMonitoring() {
        if (monitorTread != null) {
            throw new IllegalStateException("Monitor is already running");
        }

        monitorTread = new Thread(this);
        monitorTread.start();
    }

    public void stopMonitoring() {
        if (monitorTread != null) {
            monitorTread.interrupt();

            try {
                monitorTread.join(3000);
            } catch (InterruptedException e) {
            } finally {
                monitorTread = null;
            }
        }
    }

    @Override
    public void run() {
        Log.d("Stations monitor started");

        try {
            detector.start();
            readStationsConfig();

            while (true) {
                final Address[] detectedStations = detectionQueue.poll(10, TimeUnit.SECONDS);
                if (detectedStations != null) {
                    for (Address address : detectedStations) {
                        final String mac = address.toString();

                        if (stationsConfig.containsKey(mac)) {
                            onStationDetected(stationsConfig.get(mac));
                        }
                    }
                }

                updateStationsState();
            }
        } catch (InterruptedException ignore) {
        } catch (Exception ex) {
            Log.e("Stations monitor error", ex);
        } finally {
            detector.stop();
        }

        Log.d("Stations monitor stopped");
    }

    private void readStationsConfig() {
        Log.d("Reading stations configuration");

        try {
            final List<Station> stations = StorageConnector.open().selectStations();
            if (stations == null || stations.isEmpty()) {
                throw new MonitorException("No stations found in storage configuration");
            }

            for (Station station : stations) {
                stationsConfig.put(station.address, station);
            }

            Log.d("Found " + stations.size() + " stations in configuration");
        } catch (IOException ex) {
            throw new MonitorException("Reading storage configuration failed", ex);
        }
    }

    private void onStationDetected(final Station station) {
        if (onlineStations.containsKey(station.address)) {
            onlineStations.get(station.address).lastUpdateTime = System.currentTimeMillis();

            Log.d("TTL updated for station: " + station.address);
        } else {
            onlineStations.put(station.address, new StationTtl(station, System.currentTimeMillis()));

            Log.d("New station detected: " + station.address);
        }
    }

    private void updateStationsState() {
        final List<StationTtl> stations = Lists.newArrayList(onlineStations.values());
        for (StationTtl station : stations) {
            if (station.lastUpdateTime < (System.currentTimeMillis() - ttl)) {
                Log.d("Station TTL expired, station= " + station.station.address
                        + " last updated=" + new Date(station.lastUpdateTime));
                onlineStations.remove(station.station.address);
            }
        }
    }

    @Override
    public void onDetect(Address[] stations) {
        synchronized (detectionQueue) {
            detectionQueue.offer(stations);
        }
    }
}
