package com.wostrowski.airscanner.net;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.impl.provider.entity.StringProvider;
import com.wostrowski.airscanner.Log;

import javax.ws.rs.core.MediaType;

/**
 * Created by wojtek on 22.10.15.
 */
public class WebServiceAdapter {
    private final String gcmApi;
    private final String resetApi;
    private final String syncApi;
    private final Client client;

    public WebServiceAdapter(String gcmApi, String resetApi, String syncApi) {
        this.gcmApi = gcmApi;
        this.resetApi = resetApi;
        this.syncApi = syncApi;

        ClientConfig cc = new DefaultClientConfig();

        cc.getClasses().add(JacksonJsonProvider.class);
        cc.getClasses().add(StringProvider.class);
        this.client = ClientHelper.create(cc);
    }

    public StatusResponse sendStatusMsg(String[] addresses) {
        try {
            final StatusRequest request = new StatusRequest(addresses);
            Log.d("Sending status message: " + request);

            final StatusResponse response = client.resource(gcmApi)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .post(StatusResponse.class, request);

            if (response.getStatus() == Response.Status.error) {
                Log.e("Failed to send message, error: " + response.getError());
            } else {
                Log.d("Response: " + response);
            }
            return response;
        } catch (Exception ex) {
            Log.e("Failed to send message, error: " + ex.getMessage());
        }

        return null;
    }

    public void sendResetMessage() {
        try {
            Log.d("Sending reset message");

            final Response response = client.resource(resetApi)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .post(Response.class);

            if (response.getStatus() == Response.Status.error) {
                Log.e("Failed to send message, error: " + response.getError());
            } else {
                Log.d("Response: " + response);
            }
        } catch (Exception ex) {
            Log.e("Failed to send message, error: " + ex.getMessage());
        }
    }

    public StatusResponse sendSyncMessage(long lastSyncTime) {
        try {
            final SyncRequest request = new SyncRequest(lastSyncTime);
            Log.d("Sending sync message: " + request);

            final StatusResponse response = client.resource(syncApi)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .post(StatusResponse.class, request);

            if (response.getStatus() == Response.Status.error) {
                Log.e("Failed to send message, error: " + response.getError());
            } else {
                Log.d("Response: " + response);
            }
            return response;
        } catch (Exception ex) {
            Log.e("Failed to send  message, error: " + ex.getMessage());
        }

        return null;
    }
}
