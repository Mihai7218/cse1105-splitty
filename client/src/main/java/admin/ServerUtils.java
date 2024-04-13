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
     * @return
     */
    public List<Event> getEvents(String password) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/admin/" + password) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Event>>() {
                });
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
     *
     */
    public Response deleteEvent(int i) {
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
     *
     * @param events   list of events from JSON import
     * @param password admin password string to allow endpoint access
     * @return
     */
    public Response setEvents(Event events, String password){
        if(events==null){
            System.out.println("Error - empty import.");
            return null;
        }


        return ClientBuilder.newClient(new ClientConfig()) //
                .target(server).path("api/admin/" + password )//
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(events, APPLICATION_JSON), Response.class);
//        setParticipants(events.getParticipantsList(), password, events);
//        setExpenses(events.getExpensesList(), password, events);
//        setTags(events.getTagsList(), password, events);

    }


    /**
     * @return the current server
     */
    public String getServer() {
        return server;
    }
}