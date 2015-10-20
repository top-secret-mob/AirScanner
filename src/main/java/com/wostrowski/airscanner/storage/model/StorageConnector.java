package com.wostrowski.airscanner.storage.model;

import com.wostrowski.airscanner.storage.IStorage;

import java.io.IOException;

/**
 * Opens connectoion to storage
 */
public class StorageConnector {

    /**
     * Opens storage connections
     *
     * @return opened storage
     */
    public static IStorage open() throws IOException {
        final JsonStorage storage = new JsonStorage("storage.json");
        storage.open();
        return storage;
    }
}
