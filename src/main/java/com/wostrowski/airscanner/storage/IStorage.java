package com.wostrowski.airscanner.storage;

import com.wostrowski.airscanner.storage.model.Config;

import java.util.List;

/**
 * Data storage interface
 */
public interface IStorage {

    /**
     * Retrieves stations configuration
     *
     * @return
     */
    Config selectConfig();
}
