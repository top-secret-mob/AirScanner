package com.wostrowski.airscanner.storage;

import com.wostrowski.airscanner.storage.model.Config;
import com.wostrowski.airscanner.storage.model.Station;

import java.util.List;

/**
 * Data storage interface
 */
public interface IStorage {

    /**
     * Retrieves stations from storage
     *
     * @return
     */
    List<Station> selectStations();

    /**
     * Retrieves stations configuration
     *
     * @return
     */
    Config selectConfig();
}
