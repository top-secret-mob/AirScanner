package com.wostrowski.airscanner.net;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by wojtek on 21.10.15.
 */
public class Response {
    public enum Status {
        success,
        error
    }

    private Status status;
    private String error;


    public Response() {
        // Jackson deserialization
    }

    public Response(Status status) {
        this.status = status;
    }

    public Response(Status status, String error) {
        this.status = status;
        this.error = error;
    }

    @JsonProperty
    public Status getStatus() {
        return status;
    }

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                ", error='" + error + '\'' +
                '}';
    }
}
