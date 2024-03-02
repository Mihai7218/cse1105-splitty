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

import commons.Event;
import commons.Person;
import commons.Quote;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.*;

public class EventControllerTest {

    private TestEventRepository repo;
    private TestTagRepository tagRepo;
    private EventController sut;

    @BeforeEach
    public void setup() {
        repo = new TestEventRepository();
        tagRepo = new TestTagRepository();
        sut = new EventController(repo, tagRepo);
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
        var actual3 = sut.get(1);
        assertEquals("das", actual3.getBody().getTitle());
    }

    @Test
    public void deleteTest() {
        var actual = sut.add(new Event("dwa",null,null));
        var actual2 = sut.delete(0);
        assertEquals(OK, actual2.getStatusCode());
    }


    @Test
    public void databaseIsUsed() {
        sut.add(getEvent("1"));
        repo.calledMethods.contains("save");
    }

    private static Event getEvent(String q) {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("food", "green"));
        tags.add(new Tag("entrance fees", "blue"));
        tags.add(new Tag("travel", "red"));
        return new Event(q,null,null);
    }


}