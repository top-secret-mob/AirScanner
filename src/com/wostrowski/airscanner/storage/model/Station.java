package com.wostrowski.airscanner.storage.model;

/**
 * Represents a device with WIFI adapter enabled
 */
public class Station {
    // MAC address
    public String address;
    // identifier
    public String id;

    @Override
    public String toString() {
        return "Station{" +
                "address='" + address + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
