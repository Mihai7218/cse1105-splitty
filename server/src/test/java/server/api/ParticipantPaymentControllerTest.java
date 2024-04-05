package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.OK;
import static server.api.PasswordService.setPassword;

public class ParticipantPaymentControllerTest {
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
    }

    @Test
    public void importParticipantPayment(){
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
        setPassword("password");
        assertEquals(OK, participantPaymentController.addJsonImport("password", pp).getStatusCode());
    }

    @Test
    public void testGetAll(){
        participantPaymentController.getParticipantPayment(0,0);
        assertEquals(participantPaymentRepository.calledMethods, List.of("save","save"));
        assertEquals(participantPaymentRepository.calledMethods.size(), 2);
    }

    @Test
    public void testGetOne(){
        participantPaymentController.getParticipantPayment(0,0,0);
        assertEquals(participantPaymentRepository.calledMethods, List.of("save","save"));
        assertEquals(participantPaymentRepository.calledMethods.size(), 2);
    }

    @Test
    public void testCreate(){
        ParticipantPayment newPP = new ParticipantPayment(valid3, 5);
        participantPaymentController.createParticipantPayment(0,0, newPP);
        assertEquals(participantPaymentRepository.calledMethods, List.of("save","save", "save"));
        assertEquals(participantPaymentRepository.calledMethods.size(), 3);
        assertEquals(participantPaymentRepository.participantPayments.size(), 3);
    }

    @Test
    public void testUpdate(){
        ParticipantPayment updatePP = new ParticipantPayment(valid2, 3);
        participantPaymentController.updateParticipantPayment(0,0,1, updatePP);
        assertEquals(participantPaymentRepository.calledMethods, List.of("save", "save"));
        assertEquals(participantPaymentRepository.participantPayments.size(), 2);
        assertEquals(participantPaymentRepository.participantPayments.get(1).getPaymentAmount(), 3);
    }

    @Test
    public void testDelete(){
        participantPaymentController.deleteParticipantPayment(0,0,1);
        assertEquals(participantPaymentRepository.calledMethods, List.of("save", "save"));
        assertEquals(participantPaymentRepository.participantPayments.size(), 1);
        assertTrue(participantPaymentRepository.participantPayments.get(0).fullEquals(p1));
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
