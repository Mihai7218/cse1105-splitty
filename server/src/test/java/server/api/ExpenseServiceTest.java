package server.api;
import commons.Event;
import commons.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import commons.Expense;
import server.database.EventRepository;
import server.database.ExpenseRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpenseServiceTest {
    public EventRepository eventRepo;
    public ExpenseRepository expenseRepo;
    public ExpenseService expenseService;
    public Event event;
    public Expense expense1;
    public Expense expense2;
    public Expense expense3;

    @BeforeEach
    public void setup(){
        eventRepo = new TestEventRepository();
        expenseRepo = new TestExpenseRepository();
        expenseService = new ExpenseService(eventRepo, expenseRepo);

        Event event = new Event("main", null, null);

        long eventId = event.getInviteCode();

        Expense expense1 = new Expense(2.0, "eur", "drinks", "drinks", null, null, null, null);
        Expense expense2 = new Expense(23.60, "try", "bowling", "fun activity", null, null, null, null);
        Expense expense3 = new Expense(600, "eur", "birthday", "cake", null, null, null, null);

        expenseService.add(eventId, expense1);
        expenseService.add(eventId, expense2);
        expenseService.add(eventId, expense3);
    }

    //TODO: getAllExpensesTest where the invite code isnt valid/doesnt exist
    @Test
    public void getAllExpensesTest(){
        long eventId = event.getInviteCode();
        List<Expense> expenseList = expenseService.getAllExpenses(eventId).getBody();
        assertEquals(expenseList.size(), 3);
    }


    //TODO: getTotalTest where the invite code isnt valid/doesnt exist
    @Test
    public void getTotalTest(){
        long eventId = event.getInviteCode();
        double total = expenseService.getTotal(eventId).getBody();
        assertEquals(total, 625.60);
    }


    //TODO: addTest where expense == null or title == null, id to add to doesnt exist/invalid
    @Test
    public void addTest(){
        long eventId = event.getInviteCode();
        Expense expense4 = new Expense(60, "party", "drinks", null, null, null, null, null);
        expenseService.add(eventId, expense4);
        //there should be 4 expenses in the event now
        assertEquals(expenseService.getAllExpenses(eventId).getBody().size(), 4);
    }

    //TODO: changeTitleTest where event or expense doesnt exist/invalid IDs/
    // title is empty
    @Test
    public void changeTitleTest(){
        long eventId = event.getInviteCode();

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

    //TODO: changeAmountTest where event/expense doesnt/invalid IDs/
    // exist/amnt <= 0.0
    @Test
    public void changeAmountTest(){
        long eventId = event.getInviteCode();

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

    //TODO: changePayeeTest where event/expense doesnt exist/invalid ids
    // payee == null/doesnt have a name
    @Test
    public void changePayeeTest(){
        long eventId = event.getInviteCode();
        long expenseId = expense1.getId();
        Participant part = new Participant("joe", null, null, null);
        expenseService.changePayee(part, expenseId, eventId);
        assertEquals(part, expense1.getPayee());
    }

    //TODO: event/expense doesnt exist/invalid ids
    @Test
    public void deleteExpenseTest(){
        long eventId = event.getInviteCode();
        long expenseId = expense1.getId();
        assertEquals(expense1, expenseService.deleteExpense(expenseId, eventId).getBody());
        assertEquals(2, expenseService.getAllExpenses(eventId).getBody().size());
    }
}
