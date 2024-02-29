package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import server.database.EventRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.BAD_REQUEST;


class ParticipantServiceTest {

    public EventRepository eventRepository;
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
        participantService = new ParticipantService(
                eventRepository);

        participantList = new ArrayList<>();
        participantList.add(baseParticipant);
        creationDate = new Date(124, 4, 20);
        lastActivity = new Date(124, 4, 25);

        baseEvent = new Event(123, "Mock Event", new ArrayList<Expense>(),
                participantList,new ArrayList<Tag>(),creationDate,lastActivity);
        eventRepository.save(baseEvent);

        valid = new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        invalid = new Participant("Jane Doe",
                "janedoe.com","NL85RABO5253446745",
                "HBUKGB4B");

    }

    @Test
    public void getAllParticipantsTest(){
        List<Participant> result = participantService.getAllParticipants(0).getBody();
        assertEquals(participantList.size(), result.size());
        assertTrue(result.containsAll(participantList));
    }

    @Test
    public void getSingularParticipantTest(){
        Participant result = participantService.getParticipant(0, 0).getBody();
        assertEquals(result, baseParticipant);
    }

    @Test
    public void addValidParticipantTest(){
        ResponseEntity<Participant> response = participantService.addParticipant(0, valid);
        Participant result = response.getBody();
        assertEquals(result, valid);
        assertEquals(participantService.getAllParticipants(0).getBody().size(), 2);
    }

    @Test
    public void addInvalidParticipantTest(){
        ResponseEntity<Participant> result = participantService.addParticipant(0, invalid);
        HttpStatusCode status = result.getStatusCode();
        assertEquals(status, BAD_REQUEST);
        assertEquals(participantService.getAllParticipants(0).getBody().size(), 1);
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