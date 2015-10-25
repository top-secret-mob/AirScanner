package com.wostrowski.airscanner;

import com.google.common.collect.Sets;
import com.wostrowski.airscanner.net.Response;
import com.wostrowski.airscanner.net.StatusResponse;
import com.wostrowski.airscanner.net.WebServiceAdapter;
import com.wostrowski.airscanner.scanner.StationDetector;
import com.wostrowski.airscanner.storage.model.Config;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Monitors state of all detected stations
 */
public class StationMonitor implements Runnable, StationDetector.StationDetectorListener {
    private static final long DEFAULT_STATION_UPDATE_FREQUENCY = TimeUnit.SECONDS.toMillis(10);
    private static final long DEFAULT_SYNC_FREQUENCY = TimeUnit.SECONDS.toMillis(10);
    private long updateFrequency = DEFAULT_STATION_UPDATE_FREQUENCY;
    private long syncFrequency = DEFAULT_SYNC_FREQUENCY;
    private final StationDetector detector;
    private final WebServiceAdapter webServiceAdapter;
    private Thread monitorTread;
    private long lastSyncTime = System.currentTimeMillis();
    private final BlockingQueue<String[]> detectionQueue = new LinkedBlockingQueue<>(1000);
    // <MAC, StationTtl>
    private final Map<String, Station> detectedStations = new HashMap<>();

    class Station {
        String address;
        boolean active = true;
        long lastUpdateTime;

        public Station(String address) {
            this.address = address;
        }
    }

    public StationMonitor(Config config) {
        detector = new StationDetector(this);
        webServiceAdapter = new WebServiceAdapter(config.gcmApi, config.resetApi, config.syncApi);
        if (config.stationUpdateFrequency > 0) {
            this.updateFrequency = config.stationUpdateFrequency;
        }

        if (config.syncFrequency > 0) {
            this.syncFrequency = config.syncFrequency;
        }
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

        // reset previous station statuses
        webServiceAdapter.sendResetMessage();

        try {
            detector.start();

            while (true) {
                if (lastSyncTime < (System.currentTimeMillis() - syncFrequency)) {
                    syncActive(lastSyncTime);
                    lastSyncTime = System.currentTimeMillis();
                }

                final String[] detectedStations = detectionQueue.poll(10, TimeUnit.SECONDS);
                if (detectedStations != null && detectedStations.length > 0) {
                    final Set<String> detectedUnique = Sets.newHashSet(detectedStations);

                    onStationsDetected(detectedUnique.toArray(new String[detectedUnique.size()]));
                }
            }
        } catch (InterruptedException ignore) {
        } catch (Exception ex) {
            Log.e("Stations monitor error", ex);
        } finally {
            detector.stop();
        }

        Log.d("Stations monitor stopped");
    }

    private void onStationsDetected(final String[] newStations) {
        final List<String> newActiveStations = new ArrayList<>();

        for (String address : newStations) {
            final Station cachedStation = detectedStations.get(address);
            if (cachedStation == null) {
                detectedStations.put(address, new Station(address));
                newActiveStations.add(address);
                Log.d("New station detected: " + address);
                continue;
            }

            if (cachedStation.active && cachedStation.lastUpdateTime <= (System.currentTimeMillis() - updateFrequency)) {
                cachedStation.lastUpdateTime = System.currentTimeMillis();
                newActiveStations.add(address);
                Log.d("TTL updated for station: " + address);
            }
        }

        if (!newActiveStations.isEmpty()) {
            final String[] stationsArr = newActiveStations.toArray(new String[newActiveStations.size()]);
            final StatusResponse response = webServiceAdapter.sendStatusMsg(stationsArr);

            if (response != null && response.getStatus() == Response.Status.success) {
                updateStationsState(stationsArr, response.getActive());
            }
        }
    }

    private void updateStationsState(final String[] allStations, final String[] activeStations) {
        final Set<String> activeSet = activeStations != null ? Sets.newHashSet(activeStations) : null;
        for (String station : allStations) {
            final Station cachedStation = detectedStations.get(station);
            if (cachedStation != null) {
                cachedStation.active = (activeSet != null && activeSet.contains(station));
            }
        }
    }

    /**
     * Retrieves lately activated stations
     *
     * @param lastSyncTime
     */
    private void syncActive(long lastSyncTime) {
        final StatusResponse response = webServiceAdapter.sendSyncMessage(lastSyncTime);
        if (response != null && response.getActive() != null) {
            for (String active : response.getActive()) {
                final Station cachedStation = detectedStations.get(active);
                if (cachedStation != null) {
                    cachedStation.active = true;
                }
            }
        }
    }

    @Override
    public void onDetect(String[] stations) {
        synchronized (detectionQueue) {
            detectionQueue.offer(stations);
        }
    }
}
