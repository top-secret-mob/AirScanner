package com.wostrowski.airscanner.net;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Created by wojtek on 25.10.15.
 */
public class StatusResponse extends Response {
    private String[] active;

    public StatusResponse() {
        super(Status.success);
    }

    /**
     * Active MAC addresses that should be monitored
     *
     * @param active
     */
    public StatusResponse(String[] active) {
        super(Status.success);
        this.active = active;
    }

    public StatusResponse(String error) {
        super(Status.error, error);
    }

    @JsonProperty
    public String[] getActive() {
        return active;
    }

    @Override
    public String toString() {
        return "StatusResponse{" +
                "active=" + Arrays.toString(active) +
                '}';
    }
}
