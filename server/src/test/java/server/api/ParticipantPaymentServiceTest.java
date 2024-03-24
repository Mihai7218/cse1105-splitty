package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParticipantPaymentServiceTest {
    TestEventRepository eventRepository;
    TestExpenseRepository expenseRepository;
    TestParticipantPaymentRepository participantPaymentRepository;
    TestParticipantRepository participantRepository;
    ParticipantService participantService;
    ParticipantPaymentService participantPaymentService;

    public Event baseEvent;


    public Participant valid;
    public Participant valid2;
    public Participant valid3;
    public final Participant baseParticipant = new Participant("Chris Smith",
            "chrismsmith@gmail.com","NL85RABO5253446745",
            "HBUKGB4B");
    public List<Participant> participantList;

    public Date creationDate;
    public Date lastActivity;

    public ParticipantPayment p1;
    public ParticipantPayment p2;

    public Expense expense;

    public GerneralServerUtil serverUtil;


    @BeforeEach
    public void init(){
        serverUtil = new ServerUtilModule();
        eventRepository = new TestEventRepository();
        participantPaymentRepository = new TestParticipantPaymentRepository();
        participantRepository = new TestParticipantRepository();
        participantService = new ParticipantService(
                eventRepository, participantRepository);
        participantPaymentService = new ParticipantPaymentService(participantRepository,
                participantPaymentRepository,eventRepository);
        expenseRepository = new TestExpenseRepository();
        valid = new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        valid2 = new Participant("Jane Doe",
                "janedoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        valid3 = new Participant("Ella", "ellabella@gmail.com",
                "NL85RABO5253446745", "HBUKGB4B");
        participantList = new ArrayList<>();
        participantRepository.save(baseParticipant);
        participantRepository.save(valid);
        participantList.add(baseParticipant);
        participantList.add(valid);
        creationDate = new Date(124, 4, 20);
        lastActivity = new Date(124, 4, 25);
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        p1 = new ParticipantPayment(baseParticipant, 20);
        p2 = new ParticipantPayment(valid, 5);
        participantPaymentRepository.save(p1);
        participantPaymentRepository.save(p2);
        baseEvent = new Event("Mock Event",timestamp2,timestamp2);
        List<ParticipantPayment> participantPaymentList = new ArrayList<>();
        participantPaymentList.add(p1);
        participantPaymentList.add(p2);
        expense = new Expense(50, "euro", "someExpense", "somedescription", creationDate,
                participantPaymentList, new Tag("blue","blue"),valid2);
        baseEvent.setExpensesList(List.of(expense));
        expenseRepository.save(expense);
        eventRepository.save(baseEvent);
        eventRepository.getById(0L).setParticipantsList(participantList);
    }

    @Test
    public void importPriorPP(){
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
        participantRepository.save(other);
        expenseRepository.save(e);
        assertEquals(participantPaymentService.validateParticipantPayment(pp).getStatusCode(), OK );
        participantPaymentService.addCreatedParticipantPayment(pp);
        assertEquals(participantPaymentService.getParticipantPayment(1, 1,2).getBody(), pp);
    }

    @Test
    public void testGetAllParticipants(){
        List<ParticipantPayment> participantPaymentList =
                participantPaymentService.getAllParticipantPayment(0,0).getBody();
        assertEquals(participantPaymentList.size(), 2);
        assertEquals(participantPaymentList, List.of(p1,p2));
    }

    @Test
    public void testGetSingleParticipantPayment(){
        ParticipantPayment pp = participantPaymentService
                .getParticipantPayment(0,0,1).getBody();
        assertEquals(pp, p2);
    }

    @Test
    public void testAddParticipantPayment(){
        ParticipantPayment newPP = new ParticipantPayment(valid3, 5);
        ResponseEntity<ParticipantPayment> result = participantPaymentService
                .createParticipantPayment(0,0, newPP, serverUtil);
        assertEquals(result.getStatusCode(), OK);
        assertEquals(participantPaymentService.
                getAllParticipantPayment(0,0).getBody().size(), 3);
        assertEquals(participantPaymentService
                .getParticipantPayment(0,0,2).getBody(), newPP);
        assertEquals(participantPaymentRepository.participantPayments.size(), 3);
    }

    @Test
    public void testInvalidParticipantPayment(){
        ParticipantPayment newPP = new ParticipantPayment(valid3, -5);
        ResponseEntity<ParticipantPayment> result = participantPaymentService
                .createParticipantPayment(0,0, newPP, serverUtil);
        assertEquals(result.getStatusCode(), BAD_REQUEST);
        assertEquals(participantPaymentService.
                getAllParticipantPayment(0,0).getBody().size(), 2);
        assertEquals(participantPaymentService.getParticipantPayment(0,0,2).getStatusCode(), NOT_FOUND);
    }

    @Test
    public void testUpdateValidParticipantPayment(){
        ParticipantPayment newPP = new ParticipantPayment(valid3, 5);
        participantPaymentService.createParticipantPayment(0,0, newPP, serverUtil);
        ParticipantPayment updatedPP = new ParticipantPayment(valid3, 3);
        ResponseEntity<ParticipantPayment> updated = participantPaymentService
                .updateParticipantPayment(0,0,2,updatedPP, serverUtil);
        assertEquals(updated.getStatusCode(), OK);
        assertEquals(participantPaymentService
                .getParticipantPayment(0,0,2)
                .getBody().getPaymentAmount(), 3);
        assertEquals(participantPaymentService.
                getAllParticipantPayment(0,0).getBody().size(), 3);
    }

    @Test
    public void testUpdateinvalidParticipantPayment(){
        ParticipantPayment newPP = new ParticipantPayment(valid3, 5);
        participantPaymentService.createParticipantPayment(0,0, newPP, serverUtil);
        ParticipantPayment updatedPP = new ParticipantPayment(valid3, -3);
        ResponseEntity<ParticipantPayment> updated = participantPaymentService
                .updateParticipantPayment(0,0,2,updatedPP, serverUtil);
        assertEquals(updated.getStatusCode(), BAD_REQUEST);
        assertEquals(participantPaymentService
                .getParticipantPayment(0,0,2)
                .getBody().getPaymentAmount(), 5);
        assertEquals(participantPaymentService.
                getAllParticipantPayment(0,0).getBody().size(), 3);
    }

    @Test
    public void testDeleteParticipantPayment(){
        ResponseEntity<ParticipantPayment> result =
                participantPaymentService.deleteParticipantPayment(0,0,0, serverUtil);
        assertEquals(result.getBody(), p1);
        assertEquals(participantPaymentRepository.participantPayments.size(), 1);
        assertEquals(participantPaymentRepository.participantPayments, List.of(p2));
    }

    @Test
    public void testDeleteInvalidParticipantPayment(){
        ResponseEntity<ParticipantPayment> result =
                participantPaymentService.deleteParticipantPayment(0,0,3, serverUtil);
        assertEquals(result.getStatusCode(), BAD_REQUEST);
        assertEquals(participantPaymentRepository.participantPayments.size(), 2);
        assertEquals(participantPaymentRepository.participantPayments, List.of(p1, p2));
    }


    @Test
    public void lastActivityNotChangeTest(){
        Event event = eventRepository.getById(0L);
        Date tmpdate = event.getLastActivity();
        participantPaymentService.getAllParticipantPayment(0L,0L);
        event = eventRepository.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }
    @Test
    public void lastActivityNotChange2Test(){
        Event event = eventRepository.getById(0L);
        Date tmpdate = event.getLastActivity();
        participantPaymentService.getParticipantPayment(0L,0L,0L);
        event = eventRepository.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }
    @Test
    public void lastActivityAfterDeleteTest() throws InterruptedException {
        Event event = eventRepository.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        participantPaymentService.deleteParticipantPayment(0L,0L,0L, serverUtil);
        event = eventRepository.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

    @Test
    public void lastActivityAfterChangeTest() throws InterruptedException {
        Event event = eventRepository.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        participantPaymentService.updateParticipantPayment(0L,0L,0L,new ParticipantPayment(new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B"), 5), serverUtil);
        event = eventRepository.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

    @Test
    public void lastActivityAddTest() throws InterruptedException {
        Event event = eventRepository.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        participantPaymentService.createParticipantPayment(0L,0L,new ParticipantPayment(new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B"), 5), serverUtil);
        event = eventRepository.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

}
