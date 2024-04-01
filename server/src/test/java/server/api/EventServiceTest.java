package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.ParticipantRepository;


import java.util.ArrayList;
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
    public Event event4;
    public Participant p;
    public Participant other;
    ParticipantPayment p1;
    ParticipantPayment p2;
    ParticipantPayment p3;
    ParticipantPayment p4;
    Expense exp1;
    Expense exp2;


    @BeforeEach
    public void setup(){
        serverUtil = new ServerUtilModule();
        eventRepository = new TestEventRepository();
        tagRepository = new TestTagRepository();
        eventService = new EventService(eventRepository, tagRepository);

        event1 = new Event("Title1",null,null);
        event2 = new Event("Title2",null,null);
        event3 = new Event("Title3",null,null);
        event4 = new Event("Title4", null,null);

        p = new Participant("j doe", "example@email.com","NL85RABO5253446745", "HBUKGB4B");
        other = new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        p1 = new ParticipantPayment(p, 5);
        p2 = new ParticipantPayment(other,5);
        exp1 = new Expense(10, "USD", "title", "desc", null, List.of(p1,p2), new Tag("yellow", "yellow"),p);

        p3 = new ParticipantPayment(p, 10);
        p4 = new ParticipantPayment(other,10);
        exp2 = new Expense(20, "USD", "title2", "desc", null, List.of(p3,p4), new Tag("yellow", "yellow"),other);

        eventService.addEvent(event1);
        eventService.addEvent(event2);
        eventService.addEvent(event3);
    }

    @Test
    public void getExpenseWithParticipant(){
        ParticipantRepository participantRepo = new TestParticipantRepository();

        Participant uninvolved = new Participant("name", null, null, null);

        event4.getParticipantsList().add(p);
        event4.getParticipantsList().add(other);
        event4.getExpensesList().add(exp1);
        event4.getExpensesList().add(exp2);
        Tag one = new Tag("food", "#93c47d");
        Tag two = new Tag("entrance fees", "#4a86e8");
        Tag three = new Tag("travel", "#e06666");
        event4.setTagsList(List.of(one, two, three));
        eventRepository.save(event4);
        participantRepo.save(p);
        participantRepo.save(other);
        participantRepo.save(uninvolved);

        assertEquals(eventService.getExpensesInvolvingParticipant(event4.getInviteCode(), p.getId()).getBody(), List.of(exp1, exp2));
        assertEquals(eventService.getExpensesInvolvingParticipant(event4.getInviteCode(), other.getId()).getBody(), List.of(exp1, exp2));
        assertEquals(eventService.getExpensesInvolvingParticipant(event4.getInviteCode(), uninvolved.getId()).getBody(), new ArrayList<>());

        // testing bad requests:

        assertEquals(eventService.getExpensesInvolvingParticipant(-1, other.getId()).getStatusCode(), BAD_REQUEST);
        assertEquals(eventService.getExpensesInvolvingParticipant(event4.getInviteCode()+5, other.getId()).getStatusCode(), NOT_FOUND);

    }

    @Test
    public void getExpenseWithPayee(){
        ParticipantRepository participantRepo = new TestParticipantRepository();

        Participant uninvolved = new Participant("name", null, null, null);

        event4.getParticipantsList().add(p);
        event4.getParticipantsList().add(other);
        event4.getExpensesList().add(exp1);
        event4.getExpensesList().add(exp2);
        Tag one = new Tag("food", "#93c47d");
        Tag two = new Tag("entrance fees", "#4a86e8");
        Tag three = new Tag("travel", "#e06666");
        event4.setTagsList(List.of(one, two, three));
        eventRepository.save(event4);
        participantRepo.save(p);
        participantRepo.save(other);
        participantRepo.save(uninvolved);

        assertEquals(eventService.getExpensesInvolvingPayee(event4.getInviteCode(), p.getId()).getBody(), List.of(exp1));
        assertEquals(eventService.getExpensesInvolvingPayee(event4.getInviteCode(), other.getId()).getBody(), List.of(exp2));
        assertEquals(eventService.getExpensesInvolvingPayee(event4.getInviteCode(), uninvolved.getId()).getBody(), new ArrayList<>());

        // testing bad requests:

        assertEquals(eventService.getExpensesInvolvingPayee(-1, other.getId()).getStatusCode(), BAD_REQUEST);
        assertEquals(eventService.getExpensesInvolvingPayee(event4.getInviteCode()+5, other.getId()).getStatusCode(), NOT_FOUND);

    }

    @Test
    public void calculateTotalSumZero(){
        assertEquals(0, eventService.getTotal(0).getBody());
    }

    @Test
    public void calculateTotalSumOne(){
        Event event = new Event("Title4", null, null);

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

        ParticipantPayment pp = new ParticipantPayment(other, 25);
        ParticipantPayment pp1 = new ParticipantPayment(p, 25);
        List<ParticipantPayment> split = List.of(pp,pp1);
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
        assertEquals(eventService.getShare(3L, 0L).getBody(), 25.00);

    }

    @Test
    public void invalidDebtsTest(){
        ParticipantRepository participantRepo = new TestParticipantRepository();
        Event event = new Event("Title4", null, null);

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

        ParticipantPayment pp1_1 = new ParticipantPayment(other, 25);
        ParticipantPayment pp1_2 = new ParticipantPayment(p, 25);

        Tag t = new Tag("red", "red");
        Expense e= new Expense(50, "USD", "exampleExpense", "description",
                null,List.of(pp1_1,pp1_2) ,t, p);
        ParticipantPayment pp2_1 = new ParticipantPayment(other, 15);
        ParticipantPayment pp2_2 = new ParticipantPayment(p, 15);
        Expense ee = new Expense(15, "USD", "secondExpense", "description",
                null, List.of(pp2_1,pp2_2), t, other);
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
        assertEquals(eventService.getShare(3L, 0L).getBody(), 10.0);
        assertEquals(eventService.getDebt(3l, 1l).getBody(),25.0);
        assertEquals(eventService.getOwed(3L, 1L).getBody(), 15);
        assertEquals(eventService.getOwed(3L, 0L).getBody(), 25);
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