package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ParticipantControllerTest {

    public TestEventRepository eventRepository;
    public TestParticipantRepository participantRepository;
    public Participant valid;
    public Event baseEvent;
    public Participant invalid;
    public final Participant baseParticipant = new Participant("Chris Smith",
            "chrismsmith@gmail.com","NL85RABO5253446745",
            "HBUKGB4B");
    public List<Participant> participantList;
    public Date creationDate;
    public Date lastActivity;

    ParticipantController participantController;
    ParticipantService participantService;


    @BeforeEach
    public void init(){
        eventRepository = new TestEventRepository();
        participantRepository = new TestParticipantRepository();
        participantService = new ParticipantService(
                eventRepository, participantRepository);
        participantController = new ParticipantController(participantService);
        valid = new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        invalid = new Participant("Jane Doe",
                "janedoe.com","NL85RABO5253446745",
                "HBUKGB4B");

        participantList = new ArrayList<>();
        participantRepository.save(baseParticipant);
        participantRepository.save(valid);
        participantList.add(baseParticipant);
        participantList.add(valid);
        creationDate = new Date(124, 4, 20);
        lastActivity = new Date(124, 4, 25);

        baseEvent = new Event("Mock Event",creationDate,lastActivity);
        eventRepository.save(baseEvent);
        eventRepository.getById(0L).setParticipantsList(participantList);


    }

    @Test
    public void testGetController(){
        participantRepository.flush();
        eventRepository.flush();
        participantController.getParticipants(0);
        List<String> called = List.of("existsById", "findById","findById");
        assertEquals(participantRepository.calledMethods.size(), 0);
        assertEquals(eventRepository.calledMethods.size(), 3);
        assertEquals(eventRepository.calledMethods, called);
    }

    @Test
    public void getAllControllerTest(){
        participantRepository.flush();
        eventRepository.flush();
        participantController.getParticipant(0,0);
        List<String> called = List.of("existsById", "findById","findById","existsById", "findById","findById");
        assertEquals(eventRepository.calledMethods, called);
        assertEquals(participantRepository.calledMethods.size(), 0);
        assertEquals(eventRepository.calledMethods.size(), 6);
    }

    @Test
    public void addParticipantTest(){
        participantRepository.flush();
        eventRepository.flush();
        Participant three = new Participant("Ethan", "eyoung@gmail.com",
                "NL85RABO5253446745", "HBUKGB4B");
        participantController.addParticipant(0, three);
        List<String> called = List.of("existsById", "findById", "findById",
                "existsById", "findById", "findById",
                "existsById", "findById", "findById","findById", "save", "existsById", "getById");
        assertEquals(eventRepository.calledMethods, called);
        assertEquals(participantRepository.calledMethods.size(), 1);
        assertEquals(eventRepository.calledMethods.size(), 13);
    }

    @Test
    public void updateParticipantTest(){
        participantRepository.flush();
        eventRepository.flush();
        Participant p = new Participant("Christina Smith", "cmsmith@yahoo.com",
                "NL85ABNA5253446745", "AMUKGB7B");
        participantController.updateParticipant(0, 0, p);
        List<String> called = List.of("existsById", "findById","findById",
                "existsById", "findById","findById",
                "existsById", "findById","findById",
                "existsById", "findById","findById",
                "existsById", "findById","findById",
                "existsById", "findById","findById",
                "findById", "save", "existsById",
                "getById");
        assertEquals(eventRepository.calledMethods, called);
        assertEquals(participantRepository.participants.get(0).getName(), "Christina Smith");
        assertEquals(participantRepository.participants.size(), 2);
        assertEquals(eventRepository.calledMethods.size(), 22);
    }

    @Test
    public void deleteParticipantsTest(){
        participantRepository.flush();
        eventRepository.flush();
        participantController.deleteParticipant(0,0);
        assertEquals(participantRepository.calledMethods.size(), 1);
        assertEquals(eventRepository.calledMethods.size(), 23);
        List<String> called = List.of("existsById", "findById","findById",
                "existsById", "findById","findById",
                "existsById", "findById","findById",
                "existsById", "findById","findById",
                "existsById", "findById","findById",
                "existsById", "findById","findById",
                "findById", "save", "existsById",
                "getById", "getReferenceById");
        assertEquals(eventRepository.calledMethods, called);

    }


    @Test
    public void lastActivityNotChangeTest(){
        Event event = eventRepository.getById(0L);
        Date tmpdate = event.getLastActivity();
        participantController.getParticipant(0L,0L);
        event = eventRepository.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }
    @Test
    public void lastActivityNotChange2Test(){
        Event event = eventRepository.getById(0L);
        Date tmpdate = event.getLastActivity();
        participantController.getParticipant(0L,0L);
        event = eventRepository.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }
    @Test
    public void lastActivityAfterDeleteTest(){
        Event event = eventRepository.getById(0L);
        Date tmpdate = event.getLastActivity();
        participantController.deleteParticipant(0L,0L);
        event = eventRepository.getById(0L);
        assertNotEquals(event.getLastActivity(),tmpdate);
    }

    @Test
    public void lastActivityAfterChangeTest(){
        Event event = eventRepository.getById(0L);
        Date tmpdate = event.getLastActivity();
        participantController.updateParticipant(0L,0L,new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B"));
        event = eventRepository.getById(0L);
        assertNotEquals(event.getLastActivity(),tmpdate);
    }

    @Test
    public void lastActivityAddTest(){
        Event event = eventRepository.getById(0L);
        Date tmpdate = event.getLastActivity();
        participantController.addParticipant(0L,new Participant("Jon Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B"));
        event = eventRepository.getById(0L);
        assertNotEquals(event.getLastActivity(),tmpdate);
    }

}
