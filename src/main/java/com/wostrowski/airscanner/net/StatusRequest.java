package com.wostrowski.airscanner.net;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatusRequest {
    private String address;
    private boolean online;

    public StatusRequest() {
        // Jackson deserialization
    }

    public StatusRequest(String address, boolean online) {
        this.address = address;
        this.online = online;
    }

    @JsonProperty
    public String getAddress() {
        return address;
    }

    @JsonProperty
    public boolean isOnline() {
        return online;
    }

    @Override
    public String toString() {
        return "StatusRequest{" +
                "address='" + address + '\'' +
                ", online=" + online +
                '}';
    }
}
