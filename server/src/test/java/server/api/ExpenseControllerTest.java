package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ExpenseControllerTest {
    private ExpenseController ctrl;
    public Event event;
    public Expense expense1;
    public Expense expense2;
    public Expense expense3;
    public long eventId;
    public Participant payee;

    @BeforeEach
    public void setup() {
        TestEventRepository eventRepo = new TestEventRepository();
        TestExpenseRepository expenseRepo = new TestExpenseRepository();
        ExpenseService serv = new ExpenseService(eventRepo, expenseRepo);
        ctrl = new ExpenseController(serv);

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

    /***
     * Tests for the getAllExpenses method
     */
    @Test
    public void getAllExpenses(){
        List<Expense> expenseList = ctrl.getAllExpenses(eventId).getBody();
        assertEquals(3, expenseList.size());
    }
    @Test
    public void getAllExpensesInvalid(){
        ResponseEntity<List<Expense>> res = ctrl.getAllExpenses(-90);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }

    @Test
    public void getAllExpensesNonexistent(){
        ResponseEntity<List<Expense>> res = ctrl.getAllExpenses(90);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }
    /***
     * Tests for the getTotal method
     */

    @Test
    public void getTotalTest(){
        double total = ctrl.getTotal(eventId).getBody();
        assertEquals(total, 625.60);
    }

    @Test
    public void getTotalTestInvalid(){
        ResponseEntity<Double> res = ctrl.getTotal(-20);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }

    @Test
    public void getTotalTestNonexistent(){
        ResponseEntity<Double> res = ctrl.getTotal(20);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }

    /***
     * Tests for the add method
     */
    @Test
    public void addTest(){
        Expense expense4 = new Expense(60, "party", "drinks",
                null, null, null, null, payee);
        ctrl.add(eventId, expense4);
        //there should be 4 expenses in the event now
        assertEquals(4, ctrl.getAllExpenses(eventId).getBody().size());
    }
    @Test
    public void addTestNull(){
        Expense expense4 = null;
        ResponseEntity<Expense> res = ctrl.add(eventId, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestNoTitle(){
        Expense expense4 = new Expense(60, "party", null,
                null, null, null, null, payee);
        ResponseEntity<Expense> res = ctrl.add(eventId, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestNoPayee(){
        Expense expense4 = new Expense(60, "party", null,
                null, null, null, null, null);
        ResponseEntity<Expense> res = ctrl.add(eventId, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestFree(){
        Expense expense4 = new Expense(0, "party", null,
                null, null, null, null, payee);
        ResponseEntity<Expense> res = ctrl.add(eventId, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestEventInvalid(){
        Expense expense4 = new Expense(60, "party", "drinks",
                null, null, null, null, payee);
        ResponseEntity<Expense> res = ctrl.add(-30, expense4);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void addTestEventDoesntExist(){
        Expense expense4 = new Expense(60, "party", "drinks",
                null, null, null, null, payee);
        ResponseEntity<Expense> res = ctrl.add(100, expense4);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    /***
     * Tests for the changeTitle method
     */
    @Test
    public void changeTitleTest(){
        long expenseId1 = expense1.getId();
        ctrl.changeTitle("food", expenseId1, eventId);
        assertEquals("food", expense1.getTitle());

        long expenseId2 = expense2.getId();
        ctrl.changeTitle("skating", expenseId2, eventId);
        assertEquals("skating", expense2.getTitle());

        long expenseId3 = expense3.getId();
        ctrl.changeTitle("concert", expenseId3, eventId);
        assertEquals("concert", expense3.getTitle());
    }
    @Test
    public void changeTitleEventInvalid() {
        long expenseId1 = expense1.getId();
        ResponseEntity<Void> res = ctrl.changeTitle("food", expenseId1, -60);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changeTitleEventDoesntExist() {
        long expenseId1 = expense1.getId();
        ResponseEntity<Void> res = ctrl.changeTitle("food", expenseId1, 100);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseDoesntExist() {
        ResponseEntity<Void> res = ctrl.changeTitle("food", 100, eventId);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseInvalid() {
        ResponseEntity<Void> res = ctrl.changeTitle("food", -100, eventId);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseNoTitle() {
        ResponseEntity<Void> res = ctrl.changeTitle("", expense1.getId(), eventId);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseNullTitle() {
        ResponseEntity<Void> res = ctrl.changeTitle("", expense1.getId(), eventId);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    
}
