package com.wostrowski.airscanner.net;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Retrieves all stations registered after specified by <code>lastSync</code>
 */
public class SyncRequest {
    private long lastSync;

    public SyncRequest() {
    }

    public SyncRequest(long lastSync) {
        this.lastSync = lastSync;
    }

    @JsonProperty
    public long getLastSync() {
        return lastSync;
    }

    public void setLastSync(long lastSync) {
        this.lastSync = lastSync;
    }

    @Override
    public String toString() {
        return "SyncRequest{" +
                "lastSync=" + new Date(lastSync) +
                '}';
    }
}
