package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.HttpStatus.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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


    @BeforeEach
    public void init(){
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
        p1 = new ParticipantPayment(baseParticipant, 20);
        p2 = new ParticipantPayment(valid, 5);
        participantPaymentRepository.save(p1);
        participantPaymentRepository.save(p2);
        baseEvent = new Event("Mock Event",creationDate,lastActivity);
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
                .createParticipantPayment(0,0, newPP);
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
                .createParticipantPayment(0,0, newPP);
        assertEquals(result.getStatusCode(), BAD_REQUEST);
        assertEquals(participantPaymentService.
                getAllParticipantPayment(0,0).getBody().size(), 2);
        assertEquals(participantPaymentService.getParticipantPayment(0,0,2).getStatusCode(), NOT_FOUND);
    }

    @Test
    public void testUpdateValidParticipantPayment(){
        ParticipantPayment newPP = new ParticipantPayment(valid3, 5);
        participantPaymentService.createParticipantPayment(0,0, newPP);
        ParticipantPayment updatedPP = new ParticipantPayment(valid3, 3);
        ResponseEntity<ParticipantPayment> updated = participantPaymentService
                .updateParticipantPayment(0,0,2,updatedPP);
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
        participantPaymentService.createParticipantPayment(0,0, newPP);
        ParticipantPayment updatedPP = new ParticipantPayment(valid3, -3);
        ResponseEntity<ParticipantPayment> updated = participantPaymentService
                .updateParticipantPayment(0,0,2,updatedPP);
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
                participantPaymentService.deleteParticipantPayment(0,0,0);
        assertEquals(result.getBody(), p1);
        assertEquals(participantPaymentRepository.participantPayments.size(), 1);
        assertEquals(participantPaymentRepository.participantPayments, List.of(p2));
    }

    @Test
    public void testDeleteInvalidParticipantPayment(){
        ResponseEntity<ParticipantPayment> result =
                participantPaymentService.deleteParticipantPayment(0,0,3);
        assertEquals(result.getStatusCode(), BAD_REQUEST);
        assertEquals(participantPaymentRepository.participantPayments.size(), 2);
        assertEquals(participantPaymentRepository.participantPayments, List.of(p1, p2));
    }

}