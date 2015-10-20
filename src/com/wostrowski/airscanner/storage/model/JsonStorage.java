package com.wostrowski.airscanner.storage.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.wostrowski.airscanner.storage.IStorage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Storage based on local JSON file
 */
public class JsonStorage implements IStorage {
    private final String path;
    private BufferedReader reader;
    private Gson gson;

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
        this.reader = new BufferedReader(new FileReader(path));
        this.gson = new Gson();
    }

    @Override
    public List<Station> selectStations() {
        Preconditions.checkState(reader != null, "open() must be called first");

        Station[] stations = gson.fromJson(reader, Station[].class);
        return Lists.newArrayList(stations);
    }
}
