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
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

//TODO
public class TagControllerTest {
    private TagController ctrl;
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
    public void setup(){
        eventRepo = new TestEventRepository();
        expenseRepo = new TestExpenseRepository();
        tagRepo = new TestTagRepository();
        tagService = new TagService(eventRepo, tagRepo);
        ctrl = new TagController(tagService);

        payee = new Participant("joe", null, null, null);
        tag1 = new Tag("food", "#FF1493");
        tag2 = new Tag("drinks", "#FFC0CB");
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

    /***
     * Tests for the getAllExpensesWithTag method
     */
    @Test
    public void getAllExpensesWithTagTest(){
        List<Expense> expenseList = ctrl.getAllExpensesWithTag(event1.getInviteCode(), "food").getBody();
        assertEquals(1, expenseList.size());
    }

    @Test
    public void getAllExpensesWithTagTest2(){
        ResponseEntity<List<Expense>> res = ctrl.getAllExpensesWithTag(0, "picnic");
        assertEquals(0, res.getBody().size());
    }

    @Test
    public void getAllExpensesWithTagTestInvalid(){
        ResponseEntity<List<Expense>> res = ctrl.getAllExpensesWithTag(-10, "picnic");
        assertEquals(NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void getAllExpensesWithTagTestDoesntExist(){
        ResponseEntity<List<Expense>> res = ctrl.getAllExpensesWithTag(10, "picnic");
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void getAllExpensesWithTagTestDoesntExist2(){
        ResponseEntity<List<Expense>> res = ctrl.getAllExpensesWithTag(1, "fishing");
        assertEquals(0, res.getBody().size());
    }

    /***
     * Tests for the getTagsFromEvent method
     */

    @Test
    public void getTagsFromEventTest(){
        ResponseEntity<List<Tag>> res = ctrl.getTagsFromEvent(0);
        assertEquals(2, res.getBody().size());
        ResponseEntity<List<Tag>> res2 = ctrl.getTagsFromEvent(1);
        assertEquals(1, res2.getBody().size());
    }
    @Test
    public void getTagsFromEventTestDoesntExist() {
        ResponseEntity<List<Tag>> res = ctrl.getTagsFromEvent(10);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void getTagsFromEventTestInvalid() {
        ResponseEntity<List<Tag>> res = ctrl.getTagsFromEvent(-10);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }

    /***
     * Tests for the addNewToEvent method
     */
    @Test
    public void addNewToEventTest(){
        Tag tester = new Tag("cool", "#000000");
        ResponseEntity<Tag> res = ctrl.addNewToEvent(0, tester);
        assertEquals(3, event1.getTagsList().size());
    }
    @Test
    public void addNewToEventTestNull(){
        Tag tester = null;
        ResponseEntity<Tag> res = ctrl.addNewToEvent(0, tester);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addNewToEventTestNoName(){
        Tag tester = new Tag("", "#000000");
        ResponseEntity<Tag> res = ctrl.addNewToEvent(0, tester);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addNewToEventTestNullName(){
        Tag tester = new Tag(null, "#000000");
        ResponseEntity<Tag> res = ctrl.addNewToEvent(0, tester);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addNewToEventTestDNE(){
        Tag tester = new Tag("cool", "#000000");
        ResponseEntity<Tag> res = ctrl.addNewToEvent(10, tester);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void addNewToEventTestInvalid(){
        Tag tester = new Tag("cool", "#000000");
        ResponseEntity<Tag> res = ctrl.addNewToEvent(-10, tester);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }

}
