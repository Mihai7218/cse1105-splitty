package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.EventRepository;
import server.database.TagRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagServiceTest {
    public TestEventRepository eventRepo;
    public TestTagRepository tagRepo;
    public TestExpenseRepository expenseRepo;
    public TagService tagService;
    public Event event1;
    public Event event2;
    public Participant payee;
    public Tag tag1;
    public Tag tag2;
    public Expense expense1;
    public Expense expense2;

    @BeforeEach
    public void setup() {
        eventRepo = new TestEventRepository();
        expenseRepo = new TestExpenseRepository();
        tagRepo = new TestTagRepository();
        tagService = new TagService(eventRepo, tagRepo);

        payee = new Participant("joe", null, null, null);
        tag1 = new Tag("food", "#ffffff");
        tag2 = new Tag("drinks", "#ffd1dc");
        tagRepo.save(tag1);
        tagRepo.save(tag2);

        event1 = new Event("bowling", null, null);
        event2 = new Event("picnic", null, null);

        expense1 = new Expense(2.0, "eur", "drinks", "drinks", null, null, tag1, payee);
        expense2 = new Expense(23.60, "try", "bowling", "fun activity", null, null, tag2, payee);

        event1.getExpensesList().add(expense1);
        event1.getExpensesList().add(expense2);
        event2.getExpensesList().add(expense1);

        eventRepo.save(event1);
        eventRepo.save(event2);

        expenseRepo.save(expense1);
        expenseRepo.save(expense2);
    }

    /***
     * Tests for getAllExpensesWithTag method
     */
    @Test
    public void getAllExpensesWithTagTest(){
        ResponseEntity<List<Expense>> res = tagService.getAllExpensesWithTag(0, "food");
        assertEquals(1, res.getBody().size());
    }
    @Test
    public void getAllExpensesWithTagTest2(){
        ResponseEntity<List<Expense>> res = tagService.getAllExpensesWithTag(0, "picnic");
        assertEquals(0, res.getBody().size());
    }

}
