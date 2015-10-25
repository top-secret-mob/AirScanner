package com.wostrowski.airscanner.net;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class StatusRequest {
    private String[] discovered;

    public StatusRequest() {
        // Jackson deserialization
    }

    public StatusRequest(String[] discovered) {
        this.discovered = discovered;
    }

    @JsonProperty
    public String[] getDiscovered() {
        return discovered;
    }

    @Override
    public String toString() {
        return "StatusRequest{" +
                "discovered=" + Arrays.toString(discovered) +
                '}';
    }
}
