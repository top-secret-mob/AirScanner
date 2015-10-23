package com.wostrowski.airscanner.net;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.impl.provider.entity.StringProvider;
import com.wostrowski.airscanner.Log;

import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wojtek on 22.10.15.
 */
public class WebServiceAdapter {
    private final String gcmApi;
    private final String resetApi;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public WebServiceAdapter(String gcmApi, String resetApi) {
        this.gcmApi = gcmApi;
        this.resetApi = resetApi;
    }

    public void sendWelcomeMsg(String address) {
        sendMsg(gcmApi, new StatusRequest(address, true));
    }

    public void sendGoodbyMsg(String address) {
        sendMsg(gcmApi, new StatusRequest(address, false));
    }

    public void sendResetMessage() {
        sendMsg(resetApi);
    }

    private void sendMsg(final String api) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                ClientConfig cc = new DefaultClientConfig();

                cc.getClasses().add(JacksonJsonProvider.class);
                cc.getClasses().add(StringProvider.class);
                final Client client = ClientHelper.create(cc);

                try {
                    final Response response = client.resource(api)
                            .accept(MediaType.APPLICATION_JSON_TYPE)
                            .type(MediaType.APPLICATION_JSON_TYPE)
                            .post(Response.class);

                    if (response.getStatus() == Response.Status.error) {
                        Log.e("Failed to send GCM message, error: " + response.getError());
                    } else {
                        Log.d("GCM message sent");
                    }
                } catch (Exception ex) {
                    Log.e("Failed to send GCM message, error: " + ex.getMessage());
                }
            }
        });
    }

    private void sendMsg(final String api, final StatusRequest msg) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                ClientConfig cc = new DefaultClientConfig();

                cc.getClasses().add(JacksonJsonProvider.class);
                cc.getClasses().add(StringProvider.class);
                final Client client = ClientHelper.create(cc);

                try {
                    final Response response = client.resource(api)
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
        });
    }
}
