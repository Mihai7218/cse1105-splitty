package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import server.database.EventRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ParticipantServiceTest {

    @Configuration
    @EnableJpaRepositories(basePackages = "server.database")
    static class TestConfiguration {

        @Bean
        public EventRepository eventRepositoryMock() {
            return mock(EventRepository.class);
        }

    }

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

    TestConfiguration t;
    ParticipantService participantService;


    @BeforeEach
    public void init(){
        t = new TestConfiguration();
        eventRepository = t.eventRepositoryMock();
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
                "janedoe@hotmail.com","NL85RABO5253446745",
                "HBUKGB4B");

    }

    @Test
    public void getAllParticipantsTest(){
        List<Participant> result = participantService.getAllParticipants(123).getBody();
        assertNotNull(result);
        assertEquals(participantList.size(), result.size());
        assertTrue(result.containsAll(participantList));
    }

}