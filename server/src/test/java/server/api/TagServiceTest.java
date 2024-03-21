package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.*;

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

        event1.getTagsList().add(tag1);
        event1.getTagsList().add(tag2);
        event2.getTagsList().add(tag2);

        eventRepo.save(event1);
        eventRepo.save(event2);

        expenseRepo.save(expense1);
        expenseRepo.save(expense2);
    }

    @Test
    public void importTag(){
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
        tagRepo.save(one);
        tagRepo.save(two);
        tagRepo.save(three);
        Tag four = new Tag("misc", "#e06866");
        event.setTagsList(List.of(t, one, two, three, four));
        event.setInviteCode(5);
        eventRepo.save(event);
        expenseRepo.save(e);
        assertEquals(tagService.validateTag(four).getStatusCode(), OK);
        tagRepo.save(four);
        assertTrue(tagService.getTagsFromEvent(2).getBody().contains(four));

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
    @Test
    public void getAllExpensesWithTagTestInvalid(){
        ResponseEntity<List<Expense>> res = tagService.getAllExpensesWithTag(-10, "picnic");
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void getAllExpensesWithTagTestDoesntExist(){
        ResponseEntity<List<Expense>> res = tagService.getAllExpensesWithTag(10, "picnic");
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void getAllExpensesWithTagTestDoesntExist2(){
        ResponseEntity<List<Expense>> res = tagService.getAllExpensesWithTag(1, "fishing");
        assertEquals(0, res.getBody().size());
    }

    /***
     * Tests for the getTagsFromEvent method
     */
    @Test
    public void getTagsFromEventTest(){
        ResponseEntity<List<Tag>> res = tagService.getTagsFromEvent(0);
        assertEquals(2, res.getBody().size());
        ResponseEntity<List<Tag>> res2 = tagService.getTagsFromEvent(1);
        assertEquals(1, res2.getBody().size());
    }
    @Test
    public void getTagsFromEventTestDoesntExist() {
        ResponseEntity<List<Tag>> res = tagService.getTagsFromEvent(10);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void getTagsFromEventTestInvalid() {
        ResponseEntity<List<Tag>> res = tagService.getTagsFromEvent(-10);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    /***
     * Tests for the addNewToEvent method
     */
    @Test
    public void addNewToEventTest(){
        Tag tester = new Tag("cool", "#000000");
        ResponseEntity<Tag> res = tagService.addNewToEvent(0, tester);
        assertEquals(3, event1.getTagsList().size());
    }
    @Test
    public void addNewToEventTestNull(){
        Tag tester = null;
        ResponseEntity<Tag> res = tagService.addNewToEvent(0, tester);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addNewToEventTestNoName(){
        Tag tester = new Tag("", "#000000");
        ResponseEntity<Tag> res = tagService.addNewToEvent(0, tester);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addNewToEventTestNullName(){
        Tag tester = new Tag(null, "#000000");
        ResponseEntity<Tag> res = tagService.addNewToEvent(0, tester);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addNewToEventTestDNE(){
        Tag tester = new Tag("cool", "#000000");
        ResponseEntity<Tag> res = tagService.addNewToEvent(10, tester);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void addNewToEventTestInvalid(){
        Tag tester = new Tag("cool", "#000000");
        ResponseEntity<Tag> res = tagService.addNewToEvent(-10, tester);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void lastActivityTest(){
        Event event = eventRepo.getById(0L);
        Date tmpdate = event.getLastActivity();
        tagService.deleteTagFromEvent(0L,0L);
        event = eventRepo.getById(0L);
        assertNotEquals(event.getLastActivity(),tmpdate);
    }

    @Test
    public void lastActivityNotChangeTest(){
        Event event = eventRepo.getById(0L);
        Date tmpdate = event.getLastActivity();
        tagService.getTagsFromEvent(0L);
        event = eventRepo.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }

    @Test
    public void lastActivityAfterColorChangeTest(){
        Event event = eventRepo.getById(0L);
        Date tmpdate = event.getLastActivity();
        tagService.changeColor(0L,0L,"blue");
        event = eventRepo.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }
    @Test
    public void lastActivityAfterAddChangeTest(){
        Event event = eventRepo.getById(0L);
        Date tmpdate = event.getLastActivity();
        tagService.addNewToEvent(0L,new Tag("new","blue"));
        event = eventRepo.getById(0L);
        assertNotEquals(event.getLastActivity(),tmpdate);
    }

}
