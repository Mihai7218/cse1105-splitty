package server.api;

import commons.*;
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


class ParticipantServiceTest {

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

    ParticipantService participantService;


    @BeforeEach
    public void init(){
        eventRepository = new TestEventRepository();
        participantRepository = new TestParticipantRepository();
        participantService = new ParticipantService(
                eventRepository, participantRepository);

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
    public void getAllParticipantsTest(){
        List<Participant> result = participantService.getAllParticipants(0).getBody();
        assertEquals(participantList.size(), result.size());
        assertTrue(result.containsAll(participantList));
    }

    @Test
    public void getParticipantBadEventID(){
        ResponseEntity<List<Participant>> result = participantService.getAllParticipants(2);
        assertEquals(result.getStatusCode(), NOT_FOUND);
    }

    @Test
    public void getParticipantBadParticipantID(){
        ResponseEntity<Participant> result = participantService.getParticipant(0,3);
        assertEquals(result.getStatusCode(), NOT_FOUND);
    }

    @Test
    public void getSingularParticipantTest(){
        Participant result = participantService.getParticipant(0, 0).getBody();
        assertEquals(result, baseParticipant);
        Participant secondResult = participantService.getParticipant(0,1).getBody();
        assertEquals(secondResult, valid);
    }

    @Test
    public void addValidParticipantTest(){
        Participant three = new Participant("Ethan", "eyoung@gmail.com",
                "NL85RABO5253446745", "HBUKGB4B");
        ResponseEntity<Participant> response = participantService.addParticipant(0, three);
        Participant result = response.getBody();
        assertEquals(result, three);
        assertEquals(participantService.getAllParticipants(0).getBody().size(), 3);
        assertEquals(participantService.getParticipant(0,2).getBody(), three);
    }

    @Test
    public void addInvalidParticipantTest(){
        ResponseEntity<Participant> result = participantService.addParticipant(0, invalid);
        HttpStatusCode status = result.getStatusCode();
        assertEquals(status, BAD_REQUEST);
        assertEquals(participantService.getAllParticipants(0).getBody().size(), 2);
    }

    @Test
    public void updateParticipantValidTest(){
        ResponseEntity<Participant> result = participantService.updateParticipant(0, 0, "Christina Smith",
                "cmsmith@yahoo.com", "NL85ABNA5253446745", "AMUKGB7B");
        assertEquals(result.getBody().getName(), "Christina Smith");
        assertEquals(result.getBody().getEmail(), "cmsmith@yahoo.com");
        assertEquals(result.getBody().getIban(), "NL85ABNA5253446745");
        assertEquals(result.getBody().getBic(), "AMUKGB7B");
        Participant updated = new Participant("Christina Smith", "cmsmith@yahoo.com", "NL85ABNA5253446745",
                "AMUKGB7B");
        assertEquals(updated, result.getBody());
    }

    @Test
    public void deleteParticipant(){
        ResponseEntity<Participant> result = participantService.deleteParticipant(0, 0);
        assertEquals(baseParticipant, result.getBody());
        assertEquals(participantService.getAllParticipants(0).getBody().size(), 1);
        assertEquals(NOT_FOUND, participantService.getParticipant(0,0).getStatusCode());
    }

    @Test
    public void deleteParticipantInvalid(){
        ResponseEntity<Participant> result = participantService.deleteParticipant(0,2);
        assertEquals(result.getStatusCode(), BAD_REQUEST);
    }

    @Test
    public void testIbanValidity(){
        String v1 = "GB82WEST12345698765432";
        String v2 = "DE89370400440532013000";
        String v3 = "FR1420041010050500013M02606";
        String v4 = "ES9121000418450200051332";
        String v5 = "IT60X0542811101000000123456";

        assertTrue(participantService.validateIban(v1));
        assertTrue(participantService.validateIban(v2));
        assertTrue(participantService.validateIban(v3));
        assertTrue(participantService.validateIban(v4));
        assertTrue(participantService.validateIban(v5));


    }

    @Test
    public void testEmailValidity(){
        String v1 = "user@example.com";
        String v2 = "john.doe@example.co.uk";
        String v3 = "jane_doe1234@gmail.com";
        String v4 = "info+test@example.org";
        String v5 = "alice-123@example-domain.com";

        String i1 = "not_an_email";
        String i2 = "missing@domain";
        String i3 = "invalid.email@domain.";
        String i4 = "@missingusername.com";
        String i5 = "spaces not_allowed@example.com";

        assertTrue(participantService.validateEmail(v1));
        assertTrue(participantService.validateEmail(v2));
        assertTrue(participantService.validateEmail(v3));
        assertTrue(participantService.validateEmail(v4));
        assertTrue(participantService.validateEmail(v5));

        assertFalse(participantService.validateEmail(i1));
        assertFalse(participantService.validateEmail(i2));
        assertFalse(participantService.validateEmail(i3));
        assertFalse(participantService.validateEmail(i4));
        assertFalse(participantService.validateEmail(i5));

    }

}