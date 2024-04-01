package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.ParticipantRepository;


import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;


class EventServiceTest {

    public TestEventRepository eventRepository;
    public TestTagRepository tagRepository;

    public EventService eventService;
    public GerneralServerUtil serverUtil;

    public Event event1;
    public Event event2;
    public Event event3;


    @BeforeEach
    public void setup(){
        serverUtil = new ServerUtilModule();
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
    public void calculateTotalSumZero(){
        assertEquals(0, eventService.getTotal(0).getBody());
    }

    @Test
    public void calculateTotalSumOne(){
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
        event.getExpensesList().add(e);
        eventRepository.save(event);
        assertEquals(50, eventService.getTotal(3).getBody());
    }

    @Test
    public void calculateTotalSumMult(){
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
        Expense e2 = new Expense(40, "USD", "secondExample", "desc",
                null, split, t, p);
        Expense e3 = new Expense(10, "USD", "secondExample", "desc",
                null, split, t, p);
        event.getExpensesList().add(e);
        event.getExpensesList().add(e2);
        event.getExpensesList().add(e3);
        eventRepository.save(event);
        assertEquals(100, eventService.getTotal(3).getBody());
    }

    @Test
    public void calculateDebtsTest(){
        ParticipantRepository participantRepo = new TestParticipantRepository();
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
        eventRepository.save(event);
        participantRepo.save(p);
        participantRepo.save(other);
        assertEquals(eventService.getShare(3L,1L).getBody(), -25.0);
        assertEquals(eventService.getShare(3L, 0L).getBody(), 50.0);

    }

    @Test
    public void invalidDebtsTest(){
        ParticipantRepository participantRepo = new TestParticipantRepository();
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
        participantRepo.save(p);
        participantRepo.save(other);
        assertEquals(eventService.getShare(3L,0L).getStatusCode(), NOT_FOUND);
        eventRepository.save(event);
        assertEquals(eventService.getShare(3L, -2L).getStatusCode(), BAD_REQUEST);
        assertEquals(eventService.getShare(3L, 2L).getStatusCode(), NOT_FOUND);

    }

    @Test
    public void calculateComplexDebts(){
        ParticipantRepository participantRepo = new TestParticipantRepository();
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
        ParticipantPayment pp1 = new ParticipantPayment(p, 15);
        Expense ee = new Expense(15, "USD", "secondExpense", "description",
                null, List.of(pp1), t, other);
        event.getParticipantsList().add(p);
        event.getParticipantsList().add(other);
        event.getExpensesList().add(e);
        event.getExpensesList().add(ee);
        Tag one = new Tag("food", "#93c47d");
        Tag two = new Tag("entrance fees", "#4a86e8");
        Tag three = new Tag("travel", "#e06666");
        event.setTagsList(List.of(t, one, two, three));
        event.setInviteCode(5);
        eventRepository.save(event);
        participantRepo.save(p);
        participantRepo.save(other);
        assertEquals(eventService.getShare(3L,1L).getBody(), -10.0);
        assertEquals(eventService.getShare(3L, 0L).getBody(), 35.0);

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
        assertEquals("asd", eventService.changeEvent(0,new Event("asd",null,null),serverUtil).getBody().getTitle());
    }
    @Test
    public void changeEventEmptyName(){
        assertEquals(BAD_REQUEST, eventService.changeEvent(0,new Event("",null,null),serverUtil).getStatusCode());
    }
    @Test
    public void changeEventNullName(){
        assertEquals(BAD_REQUEST, eventService.changeEvent(0,new Event(null,null,null),serverUtil).getStatusCode());
    }
    @Test
    public void changeEventNotFound(){
        assertEquals(BAD_REQUEST, eventService.changeEvent(-100,new Event("asd",null,null),serverUtil).getStatusCode());
    }

    @Test
    public void lastActivityNotChange2Test(){
        Event event = eventRepository.getById(0L);
        Date tmpdate = event.getLastActivity();
        eventService.getEvent(0L);
        event = eventRepository.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }

    @Test
    public void addImportInvalidDuplicate(){
        assertEquals(BAD_REQUEST, eventService.addCreatedEvent(event1).getStatusCode());
    }

    @Test
    public void addImportInvalidFormat(){
        Event e = new Event("", null, null);
        Event e1 = new Event(null, null, null);
        assertEquals(BAD_REQUEST, eventService.addCreatedEvent(null).getStatusCode());
        assertEquals(BAD_REQUEST, eventService.addCreatedEvent(e).getStatusCode());
        assertEquals(BAD_REQUEST, eventService.addCreatedEvent(e1).getStatusCode());
    }

    @Test
    public void addImportValidTest(){
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
        assertEquals(OK, eventService.validateEvent(event).getStatusCode());
        assertEquals(eventService.addCreatedEvent(event).getStatusCode(), OK);
        assertEquals(eventService.getAllEvents().getBody().size(), 4);
    }



}