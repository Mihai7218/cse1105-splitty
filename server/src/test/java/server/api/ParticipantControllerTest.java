package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.EventRepository;
import server.database.ParticipantRepository;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;
import static server.api.PasswordService.setPassword;


public class ParticipantControllerTest {

    List<Participant> participantList;
    static boolean validName;
    static boolean validIban;
    static boolean validBic;
    static boolean validPayment;
    static boolean validEmail;
    public class ParticipantServiceStub extends ParticipantService {

        /**
         * ParticipantService Constructor
         *
         * @param eventRepository       the event repository to retrieve events from
         * @param participantRepository
         */
        public ParticipantServiceStub(EventRepository eventRepository, ParticipantRepository participantRepository) {
            super(eventRepository, participantRepository);
            validName = false;
            validIban = false;
            validBic = false;
            validEmail = false;
            validPayment = false;
            participantList = new ArrayList<>();

        }

        public ResponseEntity<List<Participant>> getAllParticipants(long eventId){
            if(eventId < 0) return ResponseEntity.badRequest().build();
            else if (eventId > 50) return ResponseEntity.notFound().build();
            else return ResponseEntity.ok(participantList);
        }

        public ResponseEntity<Participant> getParticipant(long eventId, long id){
            if(eventId < 0 || id < 0) return ResponseEntity.badRequest().build();
            else if (eventId > 50 || id > 50)  return ResponseEntity.notFound().build();
            else return ResponseEntity.ok(participantList.get((int) id));
        }

        public ResponseEntity<Participant> addParticipant(long eventId, Participant participant,
                                                          GerneralServerUtil serverUtil){
            if(eventId < 0) return ResponseEntity.badRequest().build();
            else if (eventId > 50) return ResponseEntity.notFound().build();
            else if(!validEmail || !validBic || !validName || !validIban){
                return ResponseEntity.badRequest().build();
            }
            else{
                participantList.add(participant);
                Event event = eventRepository.findById(eventId).get();
                serverUtil.updateDate(eventRepository,eventId);
                eventRepository.save(event);
                return ResponseEntity.ok(participantList.getLast());
            }
        }

        public ResponseEntity<Participant> updateParticipant(long eventId,
                                                             long id, Participant participant,
                                                             GerneralServerUtil serverUtil){
            if(eventId < 0 || id < 0) return ResponseEntity.badRequest().build();
            else if (eventId > 50 || id > 50) return ResponseEntity.notFound().build();
            else if(!validEmail || !validBic || !validName || !validIban){
                return ResponseEntity.badRequest().build();
            }
            else{
                participantList.add(participant);
                serverUtil.updateDate(eventRepository,eventId);
                return ResponseEntity.ok(participantList.getLast());
            }
        }

        public ResponseEntity<Participant> deleteParticipant(long eventId, long id,
                                                             GerneralServerUtil serverUtil){
            if(eventId < 0 || id < 0) return ResponseEntity.badRequest().build();
            else if (eventId > 50 || id > 50) return ResponseEntity.notFound().build();
            else{
                return ResponseEntity.ok(participantList.remove((int)id));
            }
        }

        public static boolean validateBic(String bic){
            return validBic;
        }

        public static boolean validateBankInfo(Participant p){
            return validPayment;
        }

        public static boolean validateName(String name){
            return validName;
        }

        public static boolean validateIban(String iban){
            return validIban;
        }

        public static boolean validateEmail(String email){
            return validEmail;
        }

        public ResponseEntity<Participant> addPriorParticipant(Participant participant){
            participantList.add(participant);
            return ResponseEntity.ok(participant);
        }
    }

    public TestEventRepository eventRepository;
    public TestParticipantRepository participantRepository;
    public Participant valid;
    public Event baseEvent;
    public Participant invalid;
    public final Participant baseParticipant = new Participant("Chris Smith",
            "chrismsmith@gmail.com","NL85RABO5253446745",
            "HBUKGB4B");
    public List<Participant> participantsList;
    public Date creationDate;
    public Date lastActivity;

    ParticipantController participantController;
    ParticipantService participantService;

    public GerneralServerUtil serverUtil;

    ParticipantService serviceStubbed;
    ParticipantController ctrlStubbed;


    @BeforeEach
    public void init(){
        serverUtil = new ServerUtilModule();
        eventRepository = new TestEventRepository();
        participantRepository = new TestParticipantRepository();
        participantService = new ParticipantService(
                eventRepository, participantRepository);
        participantController = new ParticipantController(participantService,serverUtil);
        valid = new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        invalid = new Participant("Jane Doe",
                "janedoe.com","NL85RABO5253446745",
                "HBUKGB4B");

        participantsList = new ArrayList<>();
        participantRepository.save(baseParticipant);
        participantRepository.save(valid);
        participantsList.add(baseParticipant);
        participantsList.add(valid);
        creationDate = new Date(124, 4, 20);
        lastActivity = new Date(124, 4, 25);
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        baseEvent = new Event("Mock Event",timestamp2,timestamp2);
        eventRepository.save(baseEvent);
        eventRepository.getById(0L).setParticipantsList(participantsList);

        serviceStubbed = new ParticipantServiceStub(eventRepository, participantRepository);
        ctrlStubbed = new ParticipantController(serviceStubbed, serverUtil);

    }

    @Test
    public void importParticipant(){
        Participant p = new Participant("j doe", "example@email.com","NL85RABO5253446745", "HBUKGB4B");
        setPassword("password");
        assertEquals(ctrlStubbed.addPriorParticipant("password", p).getStatusCode(), OK);
        assertEquals(ctrlStubbed.addPriorParticipant("wrongPassword", p).getStatusCode(), BAD_REQUEST);
    }

    @Test
    public void testGetController(){
        assertEquals(OK, ctrlStubbed.getParticipants(0).getStatusCode());
        assertEquals(BAD_REQUEST, ctrlStubbed.getParticipants(-1).getStatusCode());
        assertEquals(NOT_FOUND, ctrlStubbed.getParticipants(100).getStatusCode());
    }

    @Test
    public void getAllControllerTest(){
        participantList.add(new Participant("test", null, null, null));
        assertEquals(OK, ctrlStubbed.getParticipant(0,0).getStatusCode());
        assertEquals(BAD_REQUEST, ctrlStubbed.getParticipant(-1,0).getStatusCode());
        assertEquals(BAD_REQUEST, ctrlStubbed.getParticipant(0,-1).getStatusCode());
        assertEquals(NOT_FOUND, ctrlStubbed.getParticipant(58,0).getStatusCode());
        assertEquals(NOT_FOUND, ctrlStubbed.getParticipant(0,67).getStatusCode());
    }

    @Test
    public void addParticipantTest(){
        validBic = true;
        validEmail = true;
        validIban = true;
        validPayment = true;
        validName = true;
        assertEquals(ctrlStubbed.addParticipant(0,new Participant("name", null, null,null)).getStatusCode(), OK);
        assertEquals(ctrlStubbed.addParticipant(-1,new Participant("name", null, null,null)).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.addParticipant(51,new Participant("name", null, null,null)).getStatusCode(), NOT_FOUND);
    }

    @Test
    public void updateParticipantTest(){
        validBic = true;
        validEmail = true;
        validIban = true;
        validPayment = true;
        validName = true;
        assertEquals(ctrlStubbed.updateParticipant(0,0,new Participant("test", null, null, null)).getStatusCode(), OK);
        assertEquals(ctrlStubbed.updateParticipant(0,-1,new Participant("test", null, null, null)).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.updateParticipant(-1,0,new Participant("test", null, null, null)).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.updateParticipant(100,0,new Participant("test", null, null, null)).getStatusCode(), NOT_FOUND);
        assertEquals(ctrlStubbed.updateParticipant(0,100,new Participant("test", null, null, null)).getStatusCode(), NOT_FOUND);
        validName = false;
        assertEquals(ctrlStubbed.updateParticipant(0,0, new Participant("", null, null, null)).getStatusCode(), BAD_REQUEST);
    }

    @Test
    public void deleteParticipantsTest(){
        participantList.add(new Participant("test", null,null,null));
        assertEquals(ctrlStubbed.deleteParticipant(0,0).getStatusCode(), OK);
        assertEquals(ctrlStubbed.deleteParticipant(-1,0).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.deleteParticipant(0,-1).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.deleteParticipant(100,0).getStatusCode(), NOT_FOUND);
        assertEquals(ctrlStubbed.deleteParticipant(0,100).getStatusCode(), NOT_FOUND);
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
    public void lastActivityAfterDeleteTest() throws InterruptedException {
        Event event = eventRepository.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        participantController.deleteParticipant(0L,0L);
        event = eventRepository.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

    @Test
    public void lastActivityAfterChangeTest() throws InterruptedException {
        Event event = eventRepository.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        participantController.updateParticipant(0L,0L,new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B"));
        event = eventRepository.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

    @Test
    public void lastActivityAddTest() throws InterruptedException {
        Event event = eventRepository.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        Participant p = new Participant("Jon Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        p.setId(50);
        participantController.addParticipant(0L, p);
        event = eventRepository.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

}
