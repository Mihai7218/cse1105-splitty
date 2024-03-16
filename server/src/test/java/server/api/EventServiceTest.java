package server.api;

import commons.Event;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;


class EventServiceTest {

    public TestEventRepository eventRepository;
    public TestTagRepository tagRepository;

    public EventService eventService;

    public Event event1;
    public Event event2;
    public Event event3;


    @BeforeEach
    public void setup(){
        eventRepository = new TestEventRepository();
        tagRepository = new TestTagRepository();
        eventService = new EventService(eventRepository, tagRepository);

        event1 = new Event("Title1",null,null);
        event2 = new Event("Title2",null,null);
        event3 = new Event("Title3",null,null);

        eventService.addEvent(event1);
        eventService.addEvent(event2);
        eventService.addEvent(event3);
    }

    @Test
    public void getAllTest(){
        List<Event> res = eventService.getAllEvents().getBody();
        assertEquals(3, res.size());
    }

    @Test
    public void getById(){
        ResponseEntity<Event> res = eventService.getEvent(0);
        assertEquals(event1, res.getBody());
    }

    @Test
    public void getByIdFail(){
        ResponseEntity<Event> res = eventService.getEvent(12);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void getByIdNotValid(){
        ResponseEntity<Event> res = eventService.getEvent(-12);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void addingEventTest(){
        Event tmp = new Event("tmp", null,null);
        assertEquals(tmp, eventService.addEvent(tmp).getBody());
        assertEquals(tmp, eventService.getEvent(3).getBody());
    }
    @Test
    public void addingEventEmptyName(){
        Event tmp = new Event("", null,null);
        assertEquals(BAD_REQUEST, eventService.addEvent(tmp).getStatusCode());
    }

    @Test
    public void addingEventNullName(){
        Event tmp = new Event(null, null,null);
        assertEquals(BAD_REQUEST, eventService.addEvent(tmp).getStatusCode());
    }

    @Test
    public void deleteEvent(){
        assertEquals(event1, eventService.deleteEvent(0).getBody());
        assertEquals(3, eventService.getAllEvents().getBody().size());
    }

    @Test
    public void deleteEventNotFound(){
        assertEquals(NOT_FOUND, eventService.deleteEvent(12).getStatusCode());
    }
    @Test
    public void deleteEventBad(){
        assertEquals(BAD_REQUEST, eventService.deleteEvent(-12).getStatusCode());
    }

    @Test
    public void changeEvent(){
        assertEquals("asd", eventService.changeEvent(0,new Event("asd",null,null)).getBody().getTitle());
    }
    @Test
    public void changeEventEmptyName(){
        assertEquals(BAD_REQUEST, eventService.changeEvent(0,new Event("",null,null)).getStatusCode());
    }
    @Test
    public void changeEventNullName(){
        assertEquals(BAD_REQUEST, eventService.changeEvent(0,new Event(null,null,null)).getStatusCode());
    }
    @Test
    public void changeEventNotFound(){
        assertEquals(BAD_REQUEST, eventService.changeEvent(-100,new Event("asd",null,null)).getStatusCode());
    }




}