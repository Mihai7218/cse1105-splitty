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
package admin;

import com.google.inject.Inject;
import commons.*;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {
    private String server;

    /**
     * Constructor for the ServerUtils
     * @param serverAddress address of the server you want to connect to
     */
    @Inject
    public ServerUtils(String serverAddress) {
        server = serverAddress;
    }

    /**
     * a
      * @param server a
     */
    public void setServer(String server) {
        this.server = server;
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
     * @return
     */
    public List<Event> getEvents(String password) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/events/admin/" + password) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Event>>() {
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
     * @param i the invite code of the event to be deleted
     *          TODO: check for correctness
     */
    public Response deleteEvent(int i) {
//        return ClientBuilder.newClient(new ClientConfig())
//                .target(server).path("api/events/" + i)
//                .request(APPLICATION_JSON)
//                .accept(APPLICATION_JSON)
//                .delete(Event.class);
        Response response = ClientBuilder.newClient(new ClientConfig())
                .target(server)
                .path("api/events/" + i)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();
        return response;
    }

    /**
     * Method to add events from a JSON import file
     * @param events list of events from JSON import
     * @param password admin password string to allow endpoint access
     */
    public void setEvents(Event events, String password){
        if(events==null){
            System.out.println("Error - empty import.");
            return;
        }


        ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/events/admin/" + password )//
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(events, APPLICATION_JSON), Event.class);
        setParticipants(events.getParticipantsList(), password, events);
        setExpenses(events.getExpensesList(), password, events);
        setTags(events.getTagsList(), password, events);

    }

    /**
     * Method to add participants to the database based on JSON import
     * @param participants list of participnats in JSON import
     * @param password admin password to allow endpoint access
     */
    public void setParticipants(List<Participant> participants, String password, Event e){
        for(Participant p: participants){
            ClientBuilder.newClient(new ClientConfig())
                    .target(server).path("api/events/admin/participants/" + password )
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(p, APPLICATION_JSON), Participant.class);
        }
    }

    /**
     * Method to add expenses from event import
     * @param expenses list of expenses from the imported event
     * @param password admin password to allow access to endpoints
     */
    public void setExpenses(List<Expense> expenses, String password, Event event){
        for(Expense e: expenses){
            ClientBuilder.newClient(new ClientConfig())
                    .target(server).path("api/events" + event.getInviteCode() +
                            "/admin/" + password )
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(e, APPLICATION_JSON), Expense.class);
            setParticipantPayment(e.getSplit(),password, event);
        }
    }

    /**
     * Method to add participantPayments from an event import
     * @param participantPayment list of participantPayments from the imported event
     * @param password admin password to allow access to endpoints
     */
    public void setParticipantPayment(List<ParticipantPayment> participantPayment, String password,
                                      Event e){
        for(ParticipantPayment p: participantPayment){
            ClientBuilder.newClient(new ClientConfig())
                    .target(server).path("api/events/admin/participantPayment/" + password )
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(p, APPLICATION_JSON), ParticipantPayment.class);
        }
    }

    /**
     * Method to add tags from an imported event
     * @param tags list of tags associated with event
     * @param password admin password to allow access to endpoints
     */
    public void setTags(List<Tag> tags, String password, Event e){
        for(Tag t: tags){
            ClientBuilder.newClient(new ClientConfig())
                    .target(server).path("api/events/admin/tag/" + password)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .post(Entity.entity(t, APPLICATION_JSON), Tag.class);
        }
    }

    /**
     * @return the current server
     */
    public String getServer() {
        return server;
    }
}