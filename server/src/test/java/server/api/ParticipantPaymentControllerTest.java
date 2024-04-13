package server.api;

import commons.*;
import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.EventRepository;
import server.database.ParticipantPaymentRepository;
import server.database.ParticipantRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;
import static server.api.PasswordService.setPassword;

public class ParticipantPaymentControllerTest {

    List<ParticipantPayment> participantPayments;
    boolean validPp;
    public class ParticipantPaymentServiceStub extends ParticipantPaymentService{

        /**
         * Constructor for the participantpayment service
         *
         * @param participantRepository        repository for participants
         * @param participantPaymentRepository repository for the participantPayments
         * @param eventRepository              repository for events
         */
        public ParticipantPaymentServiceStub(ParticipantRepository participantRepository, ParticipantPaymentRepository participantPaymentRepository, EventRepository eventRepository) {
            super(participantRepository, participantPaymentRepository, eventRepository);
        }

        public ResponseEntity<List<ParticipantPayment>> getAllParticipantPayment(
                long eventId, long expenseId){
            if(eventId < 0 || expenseId < 0 ) return ResponseEntity.badRequest().build();
            else if (eventId > 40 || expenseId > 40) return ResponseEntity.notFound().build();
            else return ResponseEntity.ok(participantPayments);
        }

        public ResponseEntity<ParticipantPayment> getParticipantPayment(
                long eventId, long expenseId, long id){
            if(eventId < 0 || expenseId < 0 || id <0 ) return ResponseEntity.badRequest().build();
            else if (eventId > 40 || expenseId > 40 || id > 40) return ResponseEntity.notFound().build();
            else return ResponseEntity.ok(null);
        }

        public ResponseEntity<ParticipantPayment> createParticipantPayment(
                long eventId, long expenseId, ParticipantPayment participantPayment,
                GerneralServerUtil serverUtil){
            if(eventId < 0 || expenseId < 0 ) return ResponseEntity.badRequest().build();
            else if (eventId > 40 || expenseId > 40) return ResponseEntity.notFound().build();
            else return ResponseEntity.ok(null);
        }

        public ResponseEntity<ParticipantPayment> updateParticipantPayment(
                long eventId, long expenseId, long id, ParticipantPayment participantPayment,
                GerneralServerUtil serverUtil){
            if(eventId < 0 || expenseId < 0 || id < 0) return ResponseEntity.badRequest().build();
            else if (eventId > 40 || expenseId > 40 || id > 40) return ResponseEntity.notFound().build();
            else if(participantPayment.getParticipant() == null || participantPayment.getPaymentAmount() <= 0){
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(participantPayment);
        }

        public ResponseEntity<ParticipantPayment> deleteParticipantPayment(
                long eventId, long expenseId, long id, GerneralServerUtil serverUtil) {
            if(eventId < 0 || expenseId < 0 || id < 0) return ResponseEntity.badRequest().build();
            else if (eventId > 40 || expenseId > 40 || id > 40) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(null);
        }

        public ResponseEntity<ParticipantPayment> validateParticipantPayment(ParticipantPayment p) {
            if(validPp) return ResponseEntity.ok(p);
            else return ResponseEntity.badRequest().build();
        }

        public ResponseEntity<ParticipantPayment> addCreatedParticipantPayment(ParticipantPayment p) {
            return ResponseEntity.ok(p);
        }

    }

    TestEventRepository eventRepository;
    TestExpenseRepository expenseRepository;
    TestParticipantPaymentRepository participantPaymentRepository;
    TestParticipantRepository participantRepository;
    ParticipantService participantService;
    ParticipantPaymentService participantPaymentService;

    ParticipantPaymentController participantPaymentController;

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
    public ParticipantPaymentController ctrlStubbed;
    public ParticipantPaymentService serviceStubbed;


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
        expense = new Expense(50, "euro", "someExpense",
                "somedescription", creationDate,
                participantPaymentList, new Tag("blue","blue"),valid2);
        baseEvent.setExpensesList(List.of(expense));
        expenseRepository.save(expense);
        eventRepository.save(baseEvent);
        eventRepository.getById(0L).setParticipantsList(participantList);
        participantPaymentController = new ParticipantPaymentController(participantPaymentService,serverUtil);

        serviceStubbed = new ParticipantPaymentServiceStub(participantRepository, participantPaymentRepository, eventRepository);
        ctrlStubbed = new ParticipantPaymentController(serviceStubbed, serverUtil);
    }



    @Test
    public void testGetAll(){
        assertEquals(ctrlStubbed.getParticipantPayment(0,0).getStatusCode(), OK);
        assertEquals(ctrlStubbed.getParticipantPayment(-1,0).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.getParticipantPayment(0,-1).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.getParticipantPayment(60,0).getStatusCode(), NOT_FOUND);
        assertEquals(ctrlStubbed.getParticipantPayment(0,60).getStatusCode(), NOT_FOUND);
    }

    @Test
    public void testGetOne(){
        assertEquals(ctrlStubbed.getParticipantPayment(0,0,0).getStatusCode(), OK);
        assertEquals(ctrlStubbed.getParticipantPayment(-1,0,0).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.getParticipantPayment(0,-1,0).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.getParticipantPayment(0,0,-1).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.getParticipantPayment(100,0,0).getStatusCode(), NOT_FOUND);
        assertEquals(ctrlStubbed.getParticipantPayment(0,100,0).getStatusCode(), NOT_FOUND);
        assertEquals(ctrlStubbed.getParticipantPayment(0,0,100).getStatusCode(), NOT_FOUND);
    }

    @Test
    public void testCreate(){
        Participant other = new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        ParticipantPayment pp = new ParticipantPayment(other, 25);
        setPassword("password");
        assertEquals(ctrlStubbed.createParticipantPayment(0,0, pp).getStatusCode(), OK);
        assertEquals(ctrlStubbed.createParticipantPayment(-1,0, pp).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.createParticipantPayment(0,-1, pp).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.createParticipantPayment(100,0, pp).getStatusCode(), NOT_FOUND);
        assertEquals(ctrlStubbed.createParticipantPayment(0,100, pp).getStatusCode(), NOT_FOUND);
    }

    @Test
    public void testUpdate(){
        Participant other = new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        ParticipantPayment pp = new ParticipantPayment(other, 25);
        setPassword("password");
        assertEquals(ctrlStubbed.updateParticipantPayment(0,0,0,pp).getStatusCode(), OK);
        assertEquals(ctrlStubbed.updateParticipantPayment(-1,0,0,pp).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.updateParticipantPayment(0,-1,0,pp).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.updateParticipantPayment(0,0,-1,pp).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.updateParticipantPayment(100,0,0,pp).getStatusCode(), NOT_FOUND);
        assertEquals(ctrlStubbed.updateParticipantPayment(0,100,0,pp).getStatusCode(), NOT_FOUND);
        assertEquals(ctrlStubbed.updateParticipantPayment(0,0,100,pp).getStatusCode(), NOT_FOUND);
        ParticipantPayment bad1 = new ParticipantPayment(null, 10);
        ParticipantPayment bad2 = new ParticipantPayment(other, -10);
        assertEquals(ctrlStubbed.updateParticipantPayment(0,0,0,bad1).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.updateParticipantPayment(0,0,0,bad2).getStatusCode(), BAD_REQUEST);
    }

    @Test
    public void testDelete(){
        assertEquals(ctrlStubbed.deleteParticipantPayment(0,0,0).getStatusCode(), OK);
        assertEquals(ctrlStubbed.deleteParticipantPayment(-1,0,0).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.deleteParticipantPayment(0,-1,0).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.deleteParticipantPayment(0,0,-1).getStatusCode(), BAD_REQUEST);
        assertEquals(ctrlStubbed.deleteParticipantPayment(100,0,0).getStatusCode(), NOT_FOUND);
        assertEquals(ctrlStubbed.deleteParticipantPayment(0,100,0).getStatusCode(), NOT_FOUND);
        assertEquals(ctrlStubbed.deleteParticipantPayment(0,0,100).getStatusCode(), NOT_FOUND);
    }

    @Test
    public void lastActivityNotChangeTest(){
        Event event = eventRepository.getById(0L);
        Date tmpdate = event.getLastActivity();
        participantPaymentController.getParticipantPayment(0L,0L);
        event = eventRepository.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }
    @Test
    public void lastActivityNotChange2Test(){
        Event event = eventRepository.getById(0L);
        Date tmpdate = event.getLastActivity();
        participantPaymentController.getParticipantPayment(0L,0L,0L);
        event = eventRepository.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }
    @Test
    public void lastActivityAfterDeleteTest() throws InterruptedException {
        Event event = eventRepository.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        participantPaymentController.deleteParticipantPayment(0L,0L,0L);
        event = eventRepository.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

    @Test
    public void lastActivityAfterChangeTest() throws InterruptedException {
        Event event = eventRepository.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        participantPaymentController.updateParticipantPayment(0L,0L,0L,new ParticipantPayment(new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B"), 5));
        event = eventRepository.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

    @Test
    public void lastActivityAddTest() throws InterruptedException {
        Event event = eventRepository.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        participantPaymentController.createParticipantPayment(0L,0L,new ParticipantPayment(new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B"), 5));
        event = eventRepository.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

}
