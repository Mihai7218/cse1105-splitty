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
package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.*;
import static server.api.PasswordService.setPassword;

public class EventControllerTest {

    private EventController sut;
    private PasswordService ps;

    @BeforeEach
    public void setup() {
        TestEventRepository repo = new TestEventRepository();
        TestTagRepository tagRepo = new TestTagRepository();
        EventService ev = new EventService(repo, tagRepo);
        ps=new PasswordService();
        setPassword("password");
        sut = new EventController(ev);
    }

    @Test
    public void cannotAddEmptyEvent() {
        var actual = sut.add(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddEmptyWithNullTitle() {
        var actual = sut.add(new Event(null,null,null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    @Test
    public void cannotAddEmptyWithEmptyTitle() {
        var actual = sut.add(new Event("",null,null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    @Test
    public void canGetData() {
        var actual = sut.add(new Event("dwa",null,null));
        var actual2 = sut.get(0);
        assertEquals(OK, actual2.getStatusCode());
    }

    @Test
    public void cantGetData() {
        var actual = sut.add(new Event("dwa",null,null));
        var actual2 = sut.get(1);
        assertEquals(NOT_FOUND, actual2.getStatusCode());
    }

    @Test
    public void putTest() {
        var actual = sut.add(new Event("dwa",null,null));
        var actual2 = sut.change(0,new Event("das",null,null));
        var actual3 = sut.get(0);
        assertEquals("das", actual3.getBody().getTitle());
    }

    @Test
    public void deleteTest() {
        var actual = sut.add(new Event("dwa",null,null));
        var actual2 = sut.delete(0);
        assertEquals(OK, actual2.getStatusCode());
    }



    private static Event getEvent(String q) {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("food", "green"));
        tags.add(new Tag("entrance fees", "blue"));
        tags.add(new Tag("travel", "red"));
        return new Event(q,null,null);
    }


    @Test
    public void importEvent(){
        Event event = new Event("Title4", null, null);
        Participant p = new Participant("j doe", "example@email.com","NL85RABO5253446745", "HBUKGB4B");
        Participant other = new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        ParticipantPayment pp = new ParticipantPayment(other, 25);
        List<ParticipantPayment> split = List.of(pp);
        Tag t = new Tag("red", "red");
        Expense e= new Expense(50, "USD", "exampleExpense", "description",
                null,split ,t, p);
        event.getParticipantsList().add(p);
        event.getParticipantsList().add(other);
        event.getExpensesList().add(e);
        Tag one = new Tag("food", "#93c47d");
        Tag two = new Tag("entrance fees", "#4a86e8");
        Tag three = new Tag("travel", "#e06666");
        event.setTagsList(List.of(t, one, two, three));
        event.setInviteCode(5);
        assertEquals(OK,sut.addJsonImport("password", event).getStatusCode());
    }
}