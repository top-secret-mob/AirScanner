package com.wostrowski.airscanner;

import com.wostrowski.airscanner.storage.model.Config;
import com.wostrowski.airscanner.storage.model.StorageConnector;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        final Config config;
        try {
            config = StorageConnector.open().selectConfig();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        new StationMonitor(config).startMonitoring();
    }
}
