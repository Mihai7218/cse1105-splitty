package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.EventRepository;
import server.database.TagRepository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.*;
import static server.api.PasswordService.setPassword;

public class TagControllerTest {

    List<Tag> tagList;
    boolean validTag;

    public class TagServiceStub extends TagService{

        /**
         * Constructor of the tagservice
         *
         * @param eventRepo The repository in which the events are stored
         * @param tagRepo
         */
        public TagServiceStub(EventRepository eventRepo, TagRepository tagRepo) {
            super(eventRepo, tagRepo);
        }

        public ResponseEntity<List<Expense>> getAllExpensesWithTag(long inviteCode, String tagName){
            if(inviteCode < 0 || tagName == null || tagName.isEmpty()) return ResponseEntity.badRequest().build();
            else if (inviteCode > 40) return ResponseEntity.notFound().build();
            else return ResponseEntity.ok(null);
        }

        public ResponseEntity<List<Tag>> getTagsFromEvent(long inviteCode){
            if(inviteCode < 0 ) return ResponseEntity.badRequest().build();
            else if (inviteCode > 40) return ResponseEntity.notFound().build();
            else return ResponseEntity.ok(tagList);
        }

        public ResponseEntity<Tag> addNewToEvent(long inviteCode, Tag tag,
                                                 GerneralServerUtil serverUtil){
            if(inviteCode < 0 || !validTag) return ResponseEntity.badRequest().build();
            else if (inviteCode > 40) return ResponseEntity.notFound().build();
            else return ResponseEntity.ok(tag);
        }

        public ResponseEntity<Tag> changeTag(long inviteCode, long tagId, Tag tag,
                                             GerneralServerUtil serverUtil){
            if(inviteCode < 0 || tagId <0 || !validTag) return ResponseEntity.badRequest().build();
            else if (inviteCode > 40 || tagId > 40) return ResponseEntity.notFound().build();
            else return ResponseEntity.ok(tag);
        }

        public ResponseEntity<Tag> deleteTagFromEvent(long inviteCode, long tagId,
                                                      GerneralServerUtil serverUtil){
            if(inviteCode < 0 || tagId < 0 ) return ResponseEntity.badRequest().build();
            else if (inviteCode > 40 || tagId > 40) return ResponseEntity.notFound().build();
            else {
                Tag returnTag = new Tag("placeholder","placeholder");
                returnTag.setId(tagId);
                return ResponseEntity.ok(returnTag);
            }
        }

        public ResponseEntity<Tag> validateTag(Tag tag){
            if(validTag) return ResponseEntity.ok(tag);
            else return ResponseEntity.badRequest().build();
        }

        public ResponseEntity<Tag> addCreatedTag(Tag tag){
            return ResponseEntity.ok(tag);
        }
    }
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

    public GerneralServerUtil serverUtil;

    public SimpMessagingTemplate smt = mock(SimpMessagingTemplate.class);

    public TagService serviceStub;
    public TagController ctrlStub;

    @BeforeEach
    public void setup(){
        serverUtil = new ServerUtilModule();
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        eventRepo = new TestEventRepository();
        expenseRepo = new TestExpenseRepository();
        tagRepo = new TestTagRepository();
        tagService = new TagService(eventRepo, tagRepo);
        ctrl = new TagController(tagService,serverUtil,smt);

        payee = new Participant("joe", null, null, null);
        tag1 = new Tag("food", "#FF1493");
        tag2 = new Tag("drinks", "#FFC0CB");
        tagRepo.save(tag1);
        tagRepo.save(tag2);

        event1 = new Event("bowling", timestamp2, timestamp2);
        event2 = new Event("picnic", timestamp2, timestamp2);

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

        serviceStub = new TagServiceStub(eventRepo, tagRepo);
        ctrlStub = new TagController(serviceStub, serverUtil,smt);
    }

    /***
     * Tests for the getAllExpensesWithTag method
     */
    @Test
    public void getAllExpensesWithTagTest(){
        assertEquals(OK, ctrlStub.getAllExpensesWithTag(0, "valid").getStatusCode());
    }

    @Test
    public void getAllExpensesWithTagTestInvalid(){
        assertEquals(BAD_REQUEST, ctrlStub.getAllExpensesWithTag(-1, "valid").getStatusCode());
        assertEquals(BAD_REQUEST, ctrlStub.getAllExpensesWithTag(0, "").getStatusCode());
        assertEquals(BAD_REQUEST, ctrlStub.getAllExpensesWithTag(0, null).getStatusCode());
    }

    @Test
    public void getAllExpensesWithTagTestDoesntExist(){
        assertEquals(NOT_FOUND, ctrlStub.getAllExpensesWithTag(100, "valid").getStatusCode());
    }

    /***
     * Tests for the getTagsFromEvent method
     */

    @Test
    public void getTagsFromEventTest(){
        assertEquals(OK, ctrlStub.getTagsFromEvent(0).getStatusCode());
    }
    @Test
    public void getTagsFromEventTestDoesntExist() {
        assertEquals(NOT_FOUND, ctrlStub.getTagsFromEvent(100).getStatusCode());
    }
    @Test
    public void getTagsFromEventTestInvalid() {
        assertEquals(BAD_REQUEST, ctrlStub.getTagsFromEvent(-1).getStatusCode());
    }

    /***
     * Tests for the addNewToEvent method
     */
    @Test
    public void addNewToEventTest(){
        validTag = true;
        Tag tester = new Tag("cool", "#000000");

        assertEquals(OK, ctrlStub.addNewToEvent(0,tester).getStatusCode());
        assertEquals(BAD_REQUEST, ctrlStub.addNewToEvent(-1,tester).getStatusCode());
        assertEquals(NOT_FOUND, ctrlStub.addNewToEvent(100,tester).getStatusCode());
    }
    @Test
    public void addNewToEventTestNull(){
        Tag tester = null;
        validTag = false;
        assertEquals(BAD_REQUEST, ctrlStub.addNewToEvent(0, tester).getStatusCode());
    }
    @Test
    public void addNewToEventTestNoName(){
        Tag tester = new Tag("", "#000000");
        validTag = false;
        assertEquals(BAD_REQUEST, ctrlStub.addNewToEvent(0, tester).getStatusCode());
    }
    @Test
    public void addNewToEventTestNullName(){
        Tag tester = new Tag(null, "#000000");
        validTag = false;
        assertEquals(BAD_REQUEST, ctrlStub.addNewToEvent(0, tester).getStatusCode());
    }
    @Test
    public void addNewToEventTestDNE(){
        Tag tester = new Tag("cool", "#000000");
        validTag = true;
        assertEquals(NOT_FOUND, ctrlStub.addNewToEvent(100, tester).getStatusCode());
    }
    @Test
    public void addNewToEventTestInvalid(){
        Tag tester = new Tag("cool", "#000000");
        validTag = true;
        assertEquals(BAD_REQUEST, ctrlStub.addNewToEvent(-1, tester).getStatusCode());
    }

    @Test
    public void updateTagTest(){
        Tag tester = new Tag("cool", "#000000");
        validTag = true;
        assertEquals(OK, ctrlStub.changeTag(0,0,tester).getStatusCode());
        assertEquals(BAD_REQUEST, ctrlStub.changeTag(-1,0,tester).getStatusCode());
        assertEquals(BAD_REQUEST, ctrlStub.changeTag(0,-1,tester).getStatusCode());
        assertEquals(NOT_FOUND, ctrlStub.changeTag(100,0,tester).getStatusCode());
        assertEquals(NOT_FOUND, ctrlStub.changeTag(0,100,tester).getStatusCode());

        validTag = false;
        assertEquals(BAD_REQUEST, ctrlStub.changeTag(0,0,tester).getStatusCode());

    }

    @Test
    public void deleteTagTest(){
        assertEquals(OK, ctrlStub.deleteTagFromEvent(0,0).getStatusCode());
        assertEquals(BAD_REQUEST, ctrlStub.deleteTagFromEvent(-1,0).getStatusCode());
        assertEquals(BAD_REQUEST, ctrlStub.deleteTagFromEvent(0,-1).getStatusCode());
        assertEquals(NOT_FOUND, ctrlStub.deleteTagFromEvent(100,0).getStatusCode());
        assertEquals(NOT_FOUND, ctrlStub.deleteTagFromEvent(0,100).getStatusCode());
    }



    @Test
    public void lastActivityTest() throws InterruptedException {
        Event event = eventRepo.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        tagService.deleteTagFromEvent(0L,0L, serverUtil);
        event = eventRepo.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

    @Test
    public void lastActivityNotChangeTest(){
        Event event = eventRepo.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        tagService.getTagsFromEvent(0L);
        event = eventRepo.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }

    @Test
    public void lastActivityAfterChangeTest() throws InterruptedException {
        Event event = eventRepo.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        tagService.changeTag(0L,0L,new Tag("new tag", "blue"), serverUtil);
        event = eventRepo.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }
    @Test
    public void lastActivityAfterAddChangeTest() throws InterruptedException {
        Event event = eventRepo.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        tagService.addNewToEvent(0L,new Tag("new","blue"), serverUtil);
        event = eventRepo.getById(0L);
        Date kip = event.getLastActivity();
        assertTrue(kip.after(tmpdate));
    }

}
