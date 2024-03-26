/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import com.google.inject.Inject;
import commons.Event;
import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private final ConfigInterface config;
    private final String server;

    private final List<ExecutorService> runningServices;

    private StompSession session;

    /**
     * Constructor for the ServerUtils
     * @param config - config
     */
    @Inject
    public ServerUtils(ConfigInterface config) {
        this.config = config;
        if (config.getProperty("server") != null)
            server = config.getProperty("server");
        else {
            server = "http://localhost:8080";
            config.setProperty("server", server);
        }
        runningServices = new ArrayList<>();
        session = connect("ws://" + server + "/websocket");
    }

    /**
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    public void getQuotesTheHardWay() throws IOException, URISyntaxException {
        var url = new URI("http://localhost:8080/api/quotes").toURL();
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     *
     * @return
     */
    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {
                });
    }

    /**
     *
     * @param quote
     * @return
     */
    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    }

    /**
     * Method that sends a request to the server to create a new Event.
     * @param e - the event to be created
     * @return - the event that was created
     */
    public Event addEvent(Event e) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/events")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(e, APPLICATION_JSON), Event.class);
    }

    /**
     * Method that gets the event from the server with the given id.
     * @param i - id of the event.
     * @return - the event with the given id from the server.
     */
    public Event getEvent(int i) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/events/" + i)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Event.class);
    }

    /**
     * Makes a thread to Long Poll event
     * @param i inviteCode
     */
    public void getEventUpdate(int i, Consumer<Event> customer) {

        ExecutorService exec = Executors.newSingleThreadExecutor();
        runningServices.add(exec);
        exec.submit(() -> {
            while (!Thread.interrupted()) {
                var res = ClientBuilder.newClient(new ClientConfig())
                        .target(server).path("api/events/" + i + "/updates")
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .get(Response.class);
                if(res.getStatus() == 200) {
                    var event = res.readEntity(Event.class);
                    customer.accept(event);
                }
            }
        });


    }

    /**
     * Stop All running Threads
     */
    public void stop() {
        for (ExecutorService exec : runningServices) {
            exec.shutdown();
            exec.shutdownNow();
        }
    }

    /**
     * connecting to server websocket
     * @param url url of the server to connect to
     * @return return a stomp session with the connection
     */
    public StompSession connect(String url) {
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() {}).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw  new RuntimeException(e);
        }
        throw new IllegalStateException();
    }

    /**
     * register for messages from the websocket
     * @param dest the destination on the server
     * @param consumer a consumer who waits for a response
     */
    public <T> void registerForMessages(String dest, Class<T> type, Consumer<T> consumer) {
        session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }

    /**
     * Send data over the websocket
     * @param dest destination to send the data to
     * @param o object to send
     */
    public void send(String dest, Object o) {
        session.send(dest,o);
    }
}