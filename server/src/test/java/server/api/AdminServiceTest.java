package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

public class AdminServiceTest {
    public EventRepository eventRepository;
    public TagRepository tagRepository;
    public ParticipantRepository participantRepository;
    public ParticipantPaymentRepository participantPaymentRepository;
    public ExpenseRepository expenseRepository;
    public AdminService adminService;

    public Event event1;

    @BeforeEach
    public void setup(){
        eventRepository = new TestEventRepository();
        tagRepository = new TestTagRepository();
        participantRepository = new TestParticipantRepository();
        participantPaymentRepository = new TestParticipantPaymentRepository();
        expenseRepository = new TestExpenseRepository();
        adminService = new AdminService(eventRepository,
                tagRepository,participantRepository,
                participantPaymentRepository,expenseRepository);

        event1 = new Event("Title1",null,null);
        eventRepository.save(event1);
    }

    @Test
    public void addImportInvalidDuplicate(){
        assertEquals(NOT_FOUND, adminService.addCreatedEvent(event1).getStatusCode());
    }

    @Test
    public void addImportInvalidFormat(){
        Event e = new Event("", null, null);
        Event e1 = new Event(null, null, null);
        var resp = adminService.addCreatedEvent(null);
        assertEquals(NOT_FOUND, adminService.addCreatedEvent(null).getStatusCode());
        assertEquals(NOT_FOUND, adminService.addCreatedEvent(e).getStatusCode());
        assertEquals(NOT_FOUND, adminService.addCreatedEvent(e1).getStatusCode());
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
        assertEquals(OK, adminService.validateEvent(event).getStatusCode());
        assertEquals(adminService.addCreatedEvent(event).getStatusCode(), OK);
        assertEquals(adminService.getAllEvents().getBody().size(), 2);
    }



}
