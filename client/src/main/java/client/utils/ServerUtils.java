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
import commons.*;
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
    private String server;

    private final List<ExecutorService> runningServices;

    private StompSession session;

    /**
     * Constructor for the ServerUtils
     * @param config - config
     */
    @Inject
    public ServerUtils(ConfigInterface config) {
        this.config = config;
        runningServices = new ArrayList<>();
    }

    /**
     * Method that connects to the server.
     */
    public void connectToServer() {
        final List<ExecutorService> runningServices;
        if (config.getProperty("server") != null)
            this.server = config.getProperty("server");
        else {
            this.server = "http://localhost:8080";
            config.setProperty("server", server);
        }
        this.session = connect("ws://" + server.substring(7) + "/websocket");
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
    public <T> StompSession.Subscription registerForMessages(String dest,
                                                             Class<T> type,
                                                             Consumer<T> consumer) {
        return session.subscribe(dest, new StompFrameHandler() {
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
    public Object send(String dest, Object o) {
        return session.send(dest,o);
    }

    /**
     * Method that gets all expenses from the server.
     * @param id - invite code of the event.
     * @return - a list of all expenses associated with that event.
     */
    public List<Expense> getAllExpenses(int id) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/events/" + id + "/expenses") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    /**
     * Method that adds a new participant to the server.
     * @param inviteCode - invite code of the event.
     * @param participant - participant that needs to be added.
     * @return - the participant added to the server.
     */
    public Participant addParticipant(int inviteCode, Participant participant) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/events/" + inviteCode + "/participants")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(participant, APPLICATION_JSON), Participant.class);
    }

    /**
     * Method that removes a new participant to the server.
     * @param inviteCode  - invite code of the event.
     * @param participant - participant that needs to be added.
     * @return - the participant added to the server.
     */
    public Participant removeParticipant(int inviteCode, Participant participant) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("api/events/" + inviteCode + "/participants/" + participant.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete(Participant.class);
    }

    /**
     * Method that removes a new participant to the server.
     * @param inviteCode  - invite code of the event.
     * @param participant - participant that needs to be added.
     * @return - the participant added to the server.
     */
    public Participant updateParticipant(int inviteCode, Participant participant) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("api/events/" + inviteCode + "/participants/" + participant.getId())
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(participant, APPLICATION_JSON), Participant.class);
    }

    /**
     * Method that gets all participants from the server.
     * @param id - invite code of the event.
     * @return - a list of all participants associated with that event.
     */
    public List<Participant> getAllParticipants(int id) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/events/" + id + "/participants") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    /**
     * Method that adds a participant payment to the server.
     * @param inviteCode - invite code of the event.
     * @param expenseId - id of the expense.
     * @param pp - the participant payment to be added.
     * @return - the participant payment that was added.
     */
    public ParticipantPayment addParticipantPayment(int inviteCode,
                                                    long expenseId,
                                                    ParticipantPayment pp) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/events/" + inviteCode +
                        "/expenses/" + expenseId + "/participantpayment")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(pp, APPLICATION_JSON), ParticipantPayment.class);
    }

    /**
     * Method that adds an expense to the server.
     * @param inviteCode - invite code of the event.
     * @param expense - expense that needs to be added.
     * @return - the expense that was added.
     */
    public Expense addExpense(int inviteCode, Expense expense) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/events/" + inviteCode + "/expenses")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(expense, APPLICATION_JSON), Expense.class);
    }

    /**
     * Method that gets all tags from the server.
     * @param id - invite code of the event.
     * @return - a list of all tags associated with that event.
     */
    public List<Tag> getAllTags(int id) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/events/" + id + "/tags") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    /**
     * Method that adds a tag to the server.
     * @param id - invite code of the event.
     * @param tag - tag that needs to be added.
     * @return - the tag that was added.
     */
    public Tag addTag(int id, Tag tag) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path("api/events/" + id + "/tags")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(tag, APPLICATION_JSON), Tag.class);
    }

    /**
<<<<<<< HEAD
     * Method that sends a change of an event to the server.
     * @param event - the event
     * @return - the updated event
     */
    public Event changeEvent(Event event) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path(String.format("/api/events/%s",
                        event.getInviteCode()))
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(event, APPLICATION_JSON), Event.class);
    }
    /**
     * Method that updates the expense on the server.
     * @param id - the id of the event.
     * @param expense -
     * @return
     */
    public Expense updateExpense(int id, Expense expense) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path(String.format("/api/events/%s/expenses/%s",
                        id, expense.getId()))
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(expense, APPLICATION_JSON), Expense.class);
    }

    /**
     * Method that gets an expense from the server.
     * @param eventID - the id of the event.
     * @param expenseID - the id of the expense.
     * @return - the expense.
     */
    public Expense getExpense(int eventID, long expenseID) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(server).path(String.format("/api/events/%s/expenses/%s",
                        eventID, expenseID))
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Expense.class);
    }

    /**
     * Method that removes an expense from the server.
     * @param eventID - the id of the event.
     * @param expenseID - the id of the expense.
     */
    public void removeExpense(int eventID, long expenseID) {
        ClientBuilder.newClient(new ClientConfig())
                .target(server).path(String.format("/api/events/%s/expenses/%s",
                        eventID, expenseID))
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    /**
     * Method that gets the rate for the specified date and currency pair.
     * @param date - the date.
     * @param from - the currency to convert from.
     * @param to - the currency to convert to.
     * @return - the rate for the requested day.
     */
    public double getRate(String date, String from, String to) {
        return ClientBuilder.newClient(new ClientConfig()).target(server)
                .path(String.format("api/rates/%s/%s/%s", date, from, to))
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Double.class);
    }
}