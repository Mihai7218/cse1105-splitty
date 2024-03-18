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
import commons.Event;
import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
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
}