package com.wostrowski.airscanner.gcm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GcmRequest {
    private String gcm_token;
    private String address;
    private GcmMessage data;

    public GcmRequest() {
        // Jackson deserialization
    }

    public GcmRequest(String gcm_token, String address, GcmMessage data) {
        this.gcm_token = gcm_token;
        this.address = address;
        this.data = data;
    }


    @JsonProperty
    public String getGcm_token() {
        return gcm_token;
    }

    @JsonProperty
    public String getAddress() {
        return address;
    }

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public GcmMessage getData() {
        return data;
    }
}
