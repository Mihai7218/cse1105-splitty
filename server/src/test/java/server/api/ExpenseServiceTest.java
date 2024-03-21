package server.api;
import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.TagRepository;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    @BeforeEach
    public void setup(){
        eventRepo = new TestEventRepository();
        expenseRepo = new TestExpenseRepository();
        tagRepo = new TestTagRepository();
        expenseService = new ExpenseService(eventRepo, expenseRepo);
        eventService = new EventService(eventRepo, tagRepo);

        event = new Event("main", null, null);

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
    public void importValidExpense(){
        Event even = new Event("Title4", null, null);
        Participant p = new Participant("j doe", "example@email.com","NL85RABO5253446745", "HBUKGB4B");
        Participant other = new Participant("John Doe",
                "jdoe@gmail.com","NL85RABO5253446745",
                "HBUKGB4B");
        ParticipantPayment pp = new ParticipantPayment(other, 25);
        List<ParticipantPayment> split = List.of(pp);
        Tag t = new Tag("red", "red");
        Expense e= new Expense(50, "USD", "exampleExpense", "description",
                null,split ,t, p);
        even.getParticipantsList().add(p);
        even.getParticipantsList().add(other);
        even.getExpensesList().add(e);
        Tag one = new Tag("food", "#93c47d");
        Tag two = new Tag("entrance fees", "#4a86e8");
        Tag three = new Tag("travel", "#e06666");
        even.setTagsList(List.of(t, one, two, three));
        even.setInviteCode(5);
        eventService.addCreatedEvent(even);
        assertEquals(expenseService.validateExpense(e).getStatusCode(), OK);
        expenseService.addCreatedExpense(e);
        assertEquals(expenseService.getAllExpenses(1).getBody().get(0), e);
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
     * Tests for the getTotal method
     */
    @Test
    public void getTotalTest(){
        double total = expenseService.getTotal(eventId).getBody();
        assertEquals(total, 625.60);
    }

    @Test
    public void getTotalTestInvalid(){
        ResponseEntity<Double> res = expenseService.getTotal(-20);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }

    @Test
    public void getTotalTestNonexistent(){
        ResponseEntity<Double> res = expenseService.getTotal(20);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }

    /***
     * Tests for the add method
     */
    @Test
    public void addTest(){
        Expense expense4 = new Expense(60, "party", "drinks",
                null, null, null, null, payee);
        expenseService.add(eventId, expense4);
        //there should be 4 expenses in the event now
        assertEquals(4, expenseService.getAllExpenses(eventId).getBody().size());
    }
    @Test
    public void addTestNull(){
        Expense expense4 = null;
        ResponseEntity<Expense> res = expenseService.add(eventId, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestNoTitle(){
        Expense expense4 = new Expense(60, "party", null,
                null, null, null, null, payee);
        ResponseEntity<Expense> res = expenseService.add(eventId, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestNoPayee(){
        Expense expense4 = new Expense(60, "party", null,
                null, null, null, null, null);
        ResponseEntity<Expense> res = expenseService.add(eventId, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestFree(){
        Expense expense4 = new Expense(0, "party", null,
                null, null, null, null, payee);
        ResponseEntity<Expense> res = expenseService.add(eventId, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestEventInvalid(){
        Expense expense4 = new Expense(60, "party", "drinks",
                null, null, null, null, payee);
        ResponseEntity<Expense> res = expenseService.add(-30, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestEventDoesntExist(){
        Expense expense4 = new Expense(60, "party", "drinks",
                null, null, null, null, payee);
        ResponseEntity<Expense> res = expenseService.add(100, expense4);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }

    /***
     * Tests for the changeTitle method
     */
    @Test
    public void changeTitleTest(){
        long expenseId1 = expense1.getId();
        expenseService.changeTitle("food", expenseId1, eventId);
        assertEquals("food", expense1.getTitle());

        long expenseId2 = expense2.getId();
        expenseService.changeTitle("skating", expenseId2, eventId);
        assertEquals("skating", expense2.getTitle());

        long expenseId3 = expense3.getId();
        expenseService.changeTitle("concert", expenseId3, eventId);
        assertEquals("concert", expense3.getTitle());
    }
    @Test
    public void changeTitleEventInvalid() {
        long expenseId1 = expense1.getId();
        ResponseEntity<Void> res = expenseService.changeTitle("food", expenseId1, -60);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changeTitleEventDoesntExist() {
        long expenseId1 = expense1.getId();
        ResponseEntity<Void> res = expenseService.changeTitle("food", expenseId1, 100);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseDoesntExist() {
        ResponseEntity<Void> res = expenseService.changeTitle("food", 100, eventId);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseInvalid() {
        ResponseEntity<Void> res = expenseService.changeTitle("food", -100, eventId);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseNoTitle() {
        ResponseEntity<Void> res = expenseService.changeTitle("", expense1.getId(), eventId);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseNullTitle() {
        ResponseEntity<Void> res = expenseService.changeTitle("", expense1.getId(), eventId);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    /***
     * Tests for the addAmount method
     */
    @Test
    public void changeAmountTest(){
        long expenseId1 = expense1.getId();
        expenseService.changeAmount(300, expenseId1, eventId);
        assertEquals(300, expense1.getAmount());

        long expenseId2 = expense2.getId();
        expenseService.changeAmount(50, expenseId2, eventId);
        assertEquals(50, expense2.getAmount());

        long expenseId3 = expense3.getId();
        expenseService.changeAmount(75.30, expenseId3, eventId);
        assertEquals(75.30, expense3.getAmount());
    }
    @Test
    public void changeAmountExpenseDoesntExist() {
        ResponseEntity<Void> res = expenseService.changeAmount(300, 20, eventId);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }
    @Test
    public void changeAmountExpenseInvalid() {
        ResponseEntity<Void> res = expenseService.changeAmount(300, -20, eventId);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }
    @Test
    public void changeAmountEventInvalid() {
        ResponseEntity<Void> res = expenseService.changeAmount(300, expense1.getId(), -100);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }
    @Test
    public void changeAmountEventDoesntExist() {
        ResponseEntity<Void> res = expenseService.changeAmount(300, expense1.getId(), 100);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }
    @Test
    public void changeAmountLessThanZero() {
        ResponseEntity<Void> res = expenseService.changeAmount(-20, expense1.getId(), eventId);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }

    /***
     * Tests for the changePayee method
     */

    @Test
    public void changePayeeTest(){
        long expenseId = expense1.getId();
        Participant part = new Participant("joe", null, null, null);
        expenseService.changePayee(part, expenseId, eventId);
        assertEquals(part, expense1.getPayee());
    }
    @Test
    public void changePayeeNull(){
        Participant part = null;
        ResponseEntity<Void> res = expenseService.changePayee(part, expense1.getId(), eventId);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeNoNamer(){
        Participant part = new Participant("", null, null, null);
        ResponseEntity<Void> res = expenseService.changePayee(part, expense1.getId(), eventId);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeNullNamer(){
        Participant part = new Participant(null, null, null, null);
        ResponseEntity<Void> res = expenseService.changePayee(part, expense1.getId(), eventId);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeExpenseInvalid(){
        Participant part = new Participant("joe", null, null, null);
        ResponseEntity<Void> res = expenseService.changePayee(part, -100, eventId);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeExpenseDoesntExist(){
        Participant part = new Participant("joe", null, null, null);
        ResponseEntity<Void> res = expenseService.changePayee(part, 100, eventId);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changePayeeEventInvalid(){
        Participant part = new Participant("joe", null, null, null);
        ResponseEntity<Void> res = expenseService.changePayee(part, expense1.getId(), -100);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeEventDoesntExist(){
        Participant part = new Participant("joe", null, null, null);
        ResponseEntity<Void> res = expenseService.changePayee(part, expense1.getId(), 100);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }

    /***
     * Tests for the deleteExpense method
     */
    @Test
    public void deleteExpenseTest(){
        long expenseId = expense1.getId();
        expenseService.deleteExpense(expenseId, eventId);
        assertEquals(2, expenseService.getAllExpenses(eventId).getBody().size());
    }
    @Test
    public void deleteExpenseDoesntExistTest(){
        ResponseEntity<Expense> res = expenseService.deleteExpense(100, eventId);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void deleteExpenseInvalidTest(){
        ResponseEntity<Expense> res = expenseService.deleteExpense(-100, eventId);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void deleteEventInvalidTest(){
        ResponseEntity<Expense> res = expenseService.deleteExpense(expense1.getId(), -100);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void deleteEventDoesntExistTest(){
        ResponseEntity<Expense> res = expenseService.deleteExpense(expense1.getId(), 100);
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
    public void lastActivityAfterDeleteTest(){
        Event event = eventRepo.getById(0L);
        Date tmpdate = event.getLastActivity();
        expenseService.deleteExpense(0L,0L);
        event = eventRepo.getById(0L);
        assertNotEquals(event.getLastActivity(),tmpdate);
    }


    @Test
    public void lastActivityAddTest(){
        Event event = eventRepo.getById(0L);
        Date tmpdate = event.getLastActivity();
        expenseService.add(0L,new Expense(600, "party2", "party2",
                null, null, null, null, new Participant("joe", null, null, null)));
        event = eventRepo.getById(0L);
        assertNotEquals(event.getLastActivity(),tmpdate);
    }

}
