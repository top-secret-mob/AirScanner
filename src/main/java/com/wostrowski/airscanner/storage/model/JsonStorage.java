package com.wostrowski.airscanner.storage.model;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.wostrowski.airscanner.storage.IStorage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Storage based on local JSON file
 */
public class JsonStorage implements IStorage {
    private final String path;
    private Wrapper wrapper;

    /**
     * @param file JSON file path
     */
    JsonStorage(final String file) {
        Preconditions.checkNotNull(file, "file must not be null");
        this.path = file;
    }

    /**
     * Opens json file for reading
     *
     * @throws IOException
     */
    void open() throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(path));
        final Gson gson = new Gson();
        this.wrapper = gson.fromJson(reader, Wrapper.class);
    }

    @Override
    public Config selectConfig() {
        Preconditions.checkState(wrapper != null, "open() must be called first");

        return wrapper.config;
    }

    /**
     * Config wrapper class
     */
    private class Wrapper {
        Config config;
    }
}
