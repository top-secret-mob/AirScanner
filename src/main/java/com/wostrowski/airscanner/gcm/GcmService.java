package com.wostrowski.airscanner.gcm;

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
public class GcmService {
    private final String host;
    private final String gcmToken;

    public GcmService(String host, String gcmToken) {
        this.host = host;
        this.gcmToken = gcmToken;
    }

    public void sendWelcomeMsg(String address) {
        sendMsg(new GcmRequest(gcmToken, address, new GcmMessage(GcmMessage.BTState.enabled)));
    }

    public void sendGoodbyMsg(String address) {
        sendMsg(new GcmRequest(gcmToken, address, new GcmMessage(GcmMessage.BTState.disabled)));
    }

    private void sendMsg(GcmRequest msg) {
        ClientConfig cc = new DefaultClientConfig();

        cc.getClasses().add(JacksonJsonProvider.class);
        cc.getClasses().add(StringProvider.class);
        final Client client = ClientHelper.create(cc);

        try {
            final Response response = client.resource(host)
                    .accept(MediaType.APPLICATION_JSON_TYPE)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .post(Response.class, msg);
            if (response.getStatus() == Response.Status.error) {
                Log.e("Failed to send GCM message for '" + msg.getAddress() + "', error: " + response.getError());
            } else {
                Log.d("GCM message sent to '" + msg.getAddress() + "'");
            }
        } catch (Exception ex) {
            Log.e("Failed to send GCM message for '" + msg.getAddress() + "', error: " + ex.getMessage());
        }
    }
}
