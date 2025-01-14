package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.TagRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.*;

public class ExpenseServiceTest {
    public EventRepository eventRepo;
    public ExpenseRepository expenseRepo;
    public TagRepository tagRepo;
    public EventService eventService;
    public ExpenseService expenseService;
    public Event event;
    public Expense expense1;
    public Expense expense2;
    public Expense expense3;
    public Participant payee;
    public long eventId;

    public GerneralServerUtil serverUtil;
    private TestParticipantPaymentRepository ppRepo;

    @BeforeEach
    public void setup(){
        serverUtil = new ServerUtilModule();
        eventRepo = new TestEventRepository();
        expenseRepo = new TestExpenseRepository();
        tagRepo = new TestTagRepository();
        ppRepo = new TestParticipantPaymentRepository();
        expenseService = new ExpenseService(eventRepo, expenseRepo, ppRepo);
        eventService = new EventService(eventRepo, tagRepo);
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        event = new Event("main", timestamp2, timestamp2);

        expense1 = new Expense(2.0, "eur", "drinks", "drinks", null, null, null, payee);
        expense2 = new Expense(23.60, "try", "bowling", "fun activity", null, null, null, payee);
        expense3 = new Expense(600, "eur", "birthday", "cake", null, null, null, payee);

        event.getExpensesList().add(expense1);
        event.getExpensesList().add(expense2);
        event.getExpensesList().add(expense3);

        eventRepo.save(event);

        eventId = event.getInviteCode();
        payee = new Participant("joe", null, null, null);

        expenseRepo.save(expense1);
        expenseRepo.save(expense2);
        expenseRepo.save(expense3);
    }


    @Test
    public void changeTitleTestInvalid(){
        assertEquals(BAD_REQUEST, expenseService.changeTitle(expense1,-1, 0, serverUtil).getStatusCode());
        assertEquals(BAD_REQUEST, expenseService.changeTitle(expense1,0, -1, serverUtil).getStatusCode());

        assertEquals(NOT_FOUND, expenseService.changeTitle(expense1,1000, 0, serverUtil).getStatusCode());
        assertEquals(NOT_FOUND, expenseService.changeTitle(expense1,0, 1000, serverUtil).getStatusCode());

    }

    @Test
    public void changeTitleTestValid(){
        Expense invalidE1 = new Expense(10, "EUR", "", "desc", null, null, null, null );
        Expense invalidE2 = new Expense(10, "EUR", null, "desc", null, null, null, null );
        assertEquals(BAD_REQUEST, expenseService.changeTitle(invalidE1, 0, 0, serverUtil).getStatusCode());
        assertEquals(BAD_REQUEST, expenseService.changeTitle(invalidE2, 0, 0, serverUtil).getStatusCode());
        expense1.setSplit(new ArrayList<>());
        expense1.getSplit().add(new ParticipantPayment(payee, 10));
        expense2.setSplit(new ArrayList<>());
        Expense replacement = new Expense(5, "CHF", "title", "desc", null, new ArrayList<>(), null, null);
        assertEquals(OK, expenseService.changeTitle(replacement, 0,0, serverUtil).getStatusCode());
        replacement.setId(3);
        assertEquals(replacement, expenseService.getExpense(0,3).getBody());
    }

    @Test
    public void retrieveExpenseTest(){
        assertEquals(BAD_REQUEST, expenseService.getExpense(-1,0).getStatusCode());
        assertEquals(BAD_REQUEST, expenseService.getExpense(0,-1).getStatusCode());
        assertEquals(NOT_FOUND, expenseService.getExpense(1000,0).getStatusCode());
        assertEquals(NOT_FOUND, expenseService.getExpense(0,1000).getStatusCode());

        assertEquals(OK, expenseService.getExpense(0,0).getStatusCode());
        assertEquals(expense1, expenseService.getExpense(0,0).getBody());
    }

    /***
     * Tests for the getAllExpenses method
     */
    @Test
    public void getAllExpensesTest(){
        List<Expense> expenseList = expenseService.getAllExpenses(eventId).getBody();
        assertEquals(3, expenseList.size());
    }

    @Test
    public void getAllExpensesInvalid(){
        ResponseEntity<List<Expense>> res = expenseService.getAllExpenses(-90);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }

    @Test
    public void getAllExpensesNonexistent(){
        ResponseEntity<List<Expense>> res = expenseService.getAllExpenses(90);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }


    /***
     * Tests for the add method
     */
    @Test
    public void addTest(){
        Expense expense4 = new Expense(60, "party", "drinks",
                null, null, null, null, payee);
        expenseService.add(eventId, expense4, serverUtil);
        //there should be 4 expenses in the event now
        assertEquals(4, expenseService.getAllExpenses(eventId).getBody().size());
    }
    @Test
    public void addTestNull(){
        Expense expense4 = null;
        ResponseEntity<Expense> res = expenseService.add(eventId, expense4, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestNoTitle(){
        Expense expense4 = new Expense(60, "party", null,
                null, null, null, null, payee);
        ResponseEntity<Expense> res = expenseService.add(eventId, expense4, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestNoPayee(){
        Expense expense4 = new Expense(60, "party", null,
                null, null, null, null, null);
        ResponseEntity<Expense> res = expenseService.add(eventId, expense4, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestFree(){
        Expense expense4 = new Expense(0, "party", null,
                null, null, null, null, payee);
        ResponseEntity<Expense> res = expenseService.add(eventId, expense4, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestEventInvalid(){
        Expense expense4 = new Expense(60, "party", "drinks",
                null, null, null, null, payee);
        ResponseEntity<Expense> res = expenseService.add(-30, expense4, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestEventDoesntExist(){
        Expense expense4 = new Expense(60, "party", "drinks",
                null, null, null, null, payee);
        ResponseEntity<Expense> res = expenseService.add(100, expense4, serverUtil);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }

    /***
     * Tests for the changeTitle method
     */
    @Test
    public void changeTitleTest(){
        long expenseId1 = expense1.getId();
        expenseService.changeTitle("food", expenseId1, eventId, serverUtil);
        assertEquals("food", expense1.getTitle());

        long expenseId2 = expense2.getId();
        expenseService.changeTitle("skating", expenseId2, eventId, serverUtil);
        assertEquals("skating", expense2.getTitle());

        long expenseId3 = expense3.getId();
        expenseService.changeTitle("concert", expenseId3, eventId, serverUtil);
        assertEquals("concert", expense3.getTitle());
    }
    @Test
    public void changeTitleEventInvalid() {
        long expenseId1 = expense1.getId();
        ResponseEntity<Void> res = expenseService.changeTitle("food", expenseId1, -60, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changeTitleEventDoesntExist() {
        long expenseId1 = expense1.getId();
        ResponseEntity<Void> res = expenseService.changeTitle("food", expenseId1, 100, serverUtil);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseDoesntExist() {
        ResponseEntity<Void> res = expenseService.changeTitle("food", 100, eventId, serverUtil);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseInvalid() {
        ResponseEntity<Void> res = expenseService.changeTitle("food", -100, eventId, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseNoTitle() {
        ResponseEntity<Void> res = expenseService.changeTitle("", expense1.getId(), eventId, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseNullTitle() {
        ResponseEntity<Void> res = expenseService.changeTitle("", expense1.getId(), eventId, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    /***
     * Tests for the addAmount method
     */
    @Test
    public void changeAmountTest(){
        long expenseId1 = expense1.getId();
        expenseService.changeAmount(300, expenseId1, eventId, serverUtil);
        assertEquals(300, expense1.getAmount());

        long expenseId2 = expense2.getId();
        expenseService.changeAmount(50, expenseId2, eventId, serverUtil);
        assertEquals(50, expense2.getAmount());

        long expenseId3 = expense3.getId();
        expenseService.changeAmount(75.30, expenseId3, eventId, serverUtil);
        assertEquals(75.30, expense3.getAmount());
    }
    @Test
    public void changeAmountExpenseDoesntExist() {
        ResponseEntity<Void> res = expenseService.changeAmount(300, 20, eventId, serverUtil);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }
    @Test
    public void changeAmountExpenseInvalid() {
        ResponseEntity<Void> res = expenseService.changeAmount(300, -20, eventId, serverUtil);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }
    @Test
    public void changeAmountEventInvalid() {
        ResponseEntity<Void> res = expenseService.changeAmount(300, expense1.getId(), -100, serverUtil);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }
    @Test
    public void changeAmountEventDoesntExist() {
        ResponseEntity<Void> res = expenseService.changeAmount(300, expense1.getId(), 100, serverUtil);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }
    @Test
    public void changeAmountLessThanZero() {
        ResponseEntity<Void> res = expenseService.changeAmount(-20, expense1.getId(), eventId, serverUtil);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }

    /***
     * Tests for the changePayee method
     */

    @Test
    public void changePayeeTest(){
        long expenseId = expense1.getId();
        Participant part = new Participant("joe", null, null, null);
        expenseService.changePayee(part, expenseId, eventId, serverUtil);
        assertEquals(part, expense1.getPayee());
    }
    @Test
    public void changePayeeNull(){
        Participant part = null;
        ResponseEntity<Void> res = expenseService.changePayee(part, expense1.getId(), eventId, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeNoNamer(){
        Participant part = new Participant("", null, null, null);
        ResponseEntity<Void> res = expenseService.changePayee(part, expense1.getId(), eventId, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeNullNamer(){
        Participant part = new Participant(null, null, null, null);
        ResponseEntity<Void> res = expenseService.changePayee(part, expense1.getId(), eventId, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeExpenseInvalid(){
        Participant part = new Participant("joe", null, null, null);
        ResponseEntity<Void> res = expenseService.changePayee(part, -100, eventId, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeExpenseDoesntExist(){
        Participant part = new Participant("joe", null, null, null);
        ResponseEntity<Void> res = expenseService.changePayee(part, 100, eventId, serverUtil);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changePayeeEventInvalid(){
        Participant part = new Participant("joe", null, null, null);
        ResponseEntity<Void> res = expenseService.changePayee(part, expense1.getId(), -100, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeEventDoesntExist(){
        Participant part = new Participant("joe", null, null, null);
        ResponseEntity<Void> res = expenseService.changePayee(part, expense1.getId(), 100, serverUtil);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }

    /***
     * Tests for the deleteExpense method
     */
    @Test
    public void deleteExpenseTest(){
        long expenseId = expense1.getId();
        expenseService.deleteExpense(expenseId, eventId, serverUtil);
        assertEquals(2, expenseService.getAllExpenses(eventId).getBody().size());
    }
    @Test
    public void deleteExpenseDoesntExistTest(){
        ResponseEntity<Expense> res = expenseService.deleteExpense(100, eventId, serverUtil);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void deleteExpenseInvalidTest(){
        ResponseEntity<Expense> res = expenseService.deleteExpense(-100, eventId, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void deleteEventInvalidTest(){
        ResponseEntity<Expense> res = expenseService.deleteExpense(expense1.getId(), -100, serverUtil);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void deleteEventDoesntExistTest(){
        ResponseEntity<Expense> res = expenseService.deleteExpense(expense1.getId(), 100, serverUtil);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void lastActivityNotChangeTest(){
        Event event = eventRepo.getById(0L);
        Date tmpdate = event.getLastActivity();
        expenseService.getAllExpenses(0L);
        event = eventRepo.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }
    @Test
    public void lastActivityAfterDeleteTest() throws InterruptedException {
        Event event = eventRepo.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        expenseService.deleteExpense(0L,0L, serverUtil);
        event = eventRepo.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }


    @Test
    public void lastActivityAddTest() throws InterruptedException {
        Event event = eventRepo.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        expenseService.add(0L,new Expense(600, "party2", "party2",
                null, null, null, null, new Participant("joe", null, null, null)), serverUtil);
        event = eventRepo.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

}
