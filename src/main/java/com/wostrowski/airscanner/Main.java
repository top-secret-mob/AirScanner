package com.wostrowski.airscanner;

import com.wostrowski.airscanner.scanner.StationDetector;
import com.wostrowski.airscanner.storage.model.Station;
import com.wostrowski.airscanner.storage.model.StorageConnector;
import org.tudelft.aircrack.frame.Address;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        new StationMonitor(TimeUnit.SECONDS.toMillis(15)).startMonitoring();
    }
}
