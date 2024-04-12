package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//TODO: edge case testing, null testing, etc
class EventTest {

    private Date creationDate;
    private Date lastActivity;
    private Event sameEvent;
    private Event differentEvent;
    private Event event;

    @BeforeEach
    void setUp() {
        creationDate = new Date(2024, 2, 15);
        lastActivity = new Date(2022, 3, 17);

        event = new Event("Test Event", creationDate, lastActivity);
        sameEvent = new Event("Test Event", creationDate, lastActivity);
        differentEvent = new Event("Different Event", creationDate, lastActivity);
    }

    /**
     * Tests the getInviteCode method for 3 different instances
     */
    @Test
    void getInviteCode() {
        assertEquals(0, event.getInviteCode());
        assertEquals(0, sameEvent.getInviteCode());
        assertEquals(0, differentEvent.getInviteCode());
    }

    /**
     * Tests the setInviteCode method for 3 different instances
     */
    @Test
    void setInviteCode() {
        event.setInviteCode(2);
        assertEquals(2, event.getInviteCode());
        sameEvent.setInviteCode(3);
        assertEquals(3, sameEvent.getInviteCode());
        differentEvent.setInviteCode(4);
        assertEquals(4, differentEvent.getInviteCode());
    }

    /**
     * Tests the getTitle method for 3 different instances
     */
    @Test
    void getTitle() {
        assertEquals("Test Event", event.getTitle());
        assertEquals("Test Event", sameEvent.getTitle());
        assertEquals("Different Event", differentEvent.getTitle());
    }

    /**
     * Tests the setTitle method for 3 different instances
     */
    @Test
    void setTitle() {
        event.setTitle("Updated Title");
        assertEquals("Updated Title", event.getTitle());
        sameEvent.setTitle("New Title");
        assertEquals("New Title", sameEvent.getTitle());
        differentEvent.setTitle("Different Title");
        assertEquals("Different Title", differentEvent.getTitle());
    }

    /**
     * Tests the getExpensesList method for 3 different instances
     * by comparing the size
     */
    @Test
    void getExpensesList() {
        assertEquals(0, event.getExpensesList().size());
        assertEquals(0, sameEvent.getExpensesList().size());
        assertEquals(0, differentEvent.getExpensesList().size());
    }

    /**
     * Tests the setExpensesList method by changing the expenseList and comparing this to the
     * originally added list
     */
    @Test
    void setExpensesList() {
        List<Expense> newExpensesList = new ArrayList<>();
        List<ParticipantPayment> split = new ArrayList<>();
        Tag tag = new Tag("food", "blue");
        Participant participant = new Participant("joe", "hello@world.nl", "iban", "bic");
        Date partyDate = new Date(2023, 4, 5);
        Expense testExpense = new Expense(0.0, "EUR", "party",
                "party", partyDate, split, tag, participant);
        newExpensesList.add(testExpense);
        event.setExpensesList(newExpensesList);
        assertEquals(1, event.getExpensesList().size());
    }

    /**
     * Tests the getParticipantsList method for 3 different instances
     * by comparing the size
     */
    @Test
    void getParticipantsList() {
        assertEquals(0, event.getParticipantsList().size());
        assertEquals(0, sameEvent.getParticipantsList().size());
        assertEquals(0, differentEvent.getParticipantsList().size());
    }

    /**
     * Tests the setExpensesList method by changing the expenseList and comparing this to the
     * originally added list
     */
    @Test
    void setParticipantsList() {
        List<Participant> newParticipantList = new ArrayList<>();
        Participant testParticipant = new Participant("Joe", "hello@world.com", "iban", "bic");
        newParticipantList.add(testParticipant);
        event.setParticipantsList(newParticipantList);
        assertEquals(newParticipantList, event.getParticipantsList());
    }

    /**
     * Tests the getTagsList method for 3 different instances
     * by comparing the size
     */
    @Test
    void getTagsList() {
        assertEquals(0, event.getTagsList().size());
        assertEquals(0, sameEvent.getTagsList().size());
        assertEquals(0, differentEvent.getTagsList().size());
    }

    /**
     * Tests the setTagsList method by changing the expenseList and comparing this to the
     * originally added list
     */
    @Test
    void setTagsList() {
        List<Tag> newTagList = new ArrayList<>();
        Tag testTag = new Tag("blue", "blue");
        newTagList.add(testTag);
        event.setTagsList(newTagList);
        assertEquals(newTagList, event.getTagsList());
    }

    /**
     * Tests the getter for the creationDate attribute for 3 instances
     */
    @Test
    void getCreationDate() {
        assertEquals(creationDate, event.getCreationDate());
        assertEquals(creationDate, sameEvent.getCreationDate());
        assertEquals(creationDate, differentEvent.getCreationDate());
    }

    /**
     * Tests the setter for the creationDate attribute for 3 instances
     */
    @Test
    void setCreationDate() {
        Date newCreationDate = new Date(2023, 1, 15);
        event.setCreationDate(newCreationDate);
        sameEvent.setCreationDate(newCreationDate);
        differentEvent.setCreationDate(newCreationDate);
        assertEquals(newCreationDate, event.getCreationDate());
        assertEquals(newCreationDate, sameEvent.getCreationDate());
        assertEquals(newCreationDate, differentEvent.getCreationDate());
    }

    /**
     * Tests the getter for the lastActivity attribute for 3 instances
     */
    @Test
    void getLastActivity() {
        assertEquals(lastActivity, event.getLastActivity());
        assertEquals(lastActivity, sameEvent.getLastActivity());
        assertEquals(lastActivity, differentEvent.getLastActivity());
    }

    /**
     * Tests the setter for the lastActivity attribute for 3 instances
     */
    @Test
    void setLastActivity() {
        Event test = new Event();
        Date newLastActivity = new Date(2023, 1, 15);
        event.setLastActivity(newLastActivity);
        sameEvent.setLastActivity(newLastActivity);
        differentEvent.setLastActivity(newLastActivity);
        assertEquals(newLastActivity, event.getLastActivity());
        assertEquals(newLastActivity, sameEvent.getLastActivity());
        assertEquals(newLastActivity, differentEvent.getLastActivity());
    }

    @Test
    void participantEqualsTest() {
        Event event1 = new Event("",null,null);
        Event event2 = new Event("",null,null);
        event1.addParticipant(new Participant("Marco","","",""));
        event2.addParticipant(new Participant("Marco","","",""));
        assertTrue(event1.participantsEquals(event1.getParticipantsList(),event2.getParticipantsList()));
        event1.addParticipant(new Participant("Marco","","",""));
        event2.addParticipant(new Participant("Marco2","","",""));
        assertFalse(event1.participantsEquals(event1.getParticipantsList(),event2.getParticipantsList()));
        event2.addParticipant(null);
        assertFalse(event1.participantsEquals(event1.getParticipantsList(),event2.getParticipantsList()));
        event2.setParticipantsList(null);
        assertFalse(event1.participantsEquals(event1.getParticipantsList(),event2.getParticipantsList()));
        event2.setParticipantsList(null);
        event1.setParticipantsList(null);
        assertTrue(event1.participantsEquals(event1.getParticipantsList(),event2.getParticipantsList()));
    }
    @Test
    void tagsEqualsEqualsTest() {
        Event event1 = new Event("",null,null);
        Event event2 = new Event("",null,null);
        event1.addTag(new Tag("Tag","red"));
        event2.addTag(new Tag("Tag","red"));
        assertTrue(event1.tagsEquals(event1.getTagsList(),event2.getTagsList()));
        event1.addTag(new Tag("Tag","red"));
        event2.addTag(new Tag("Tag1","red"));
        assertFalse(event1.tagsEquals(event1.getTagsList(),event2.getTagsList()));
        event1.addTag(new Tag("Tag11","red"));
        event2.addTag(new Tag("Tag1","red"));
        assertFalse(event1.tagsEquals(event1.getTagsList(),event2.getTagsList()));
        event2.addTag(null);
        assertFalse(event1.tagsEquals(event1.getTagsList(),event2.getTagsList()));
        event2.setTagsList(null);
        assertFalse(event1.tagsEquals(event1.getTagsList(),event2.getTagsList()));
        event2.setTagsList(null);
        event1.setTagsList(null);
        assertTrue(event1.tagsEquals(event1.getTagsList(),event2.getTagsList()));
    }

    @Test
    void expensesEqualsEqualsTest() {
        Participant Marco = new Participant("Marco","","","");
        Event event1 = new Event("",null,null);
        Event event2 = new Event("",null,null);
        event1.addExpense(new Expense(0,"","expense","",new Date(),null,null,Marco));
        event2.addExpense(new Expense(0,"","expense","",new Date(),null,null,Marco));
        assertTrue(event1.expensesEquals(event1.getExpensesList(),event2.getExpensesList()));
        event1.addExpense(new Expense(0,"","expense","",new Date(),null,null,Marco));
        event2.addExpense(new Expense(0,"","expense2","",new Date(),null,null,Marco));
        assertFalse(event1.expensesEquals(event1.getExpensesList(),event2.getExpensesList()));
        event1.addExpense(new Expense(0,"","expense3","",new Date(),null,null,Marco));
        event2.addExpense(new Expense(0,"","expense2","",new Date(),null,null,Marco));
        assertFalse(event1.expensesEquals(event1.getExpensesList(),event2.getExpensesList()));
        event2.addExpense(null);
        assertFalse(event1.expensesEquals(event1.getExpensesList(),event2.getExpensesList()));
        event2.setExpensesList(null);
        assertFalse(event1.expensesEquals(event1.getExpensesList(),event2.getExpensesList()));
        event2.setExpensesList(null);
        event1.setExpensesList(null);
        assertTrue(event1.expensesEquals(event1.getExpensesList(),event2.getExpensesList()));
    }

    /**
     * Tests whether event and sameEvent are equal (they are)
     * and event/sameEvent are equal to differentEvent (they are not)
     */
    @Test
    void testEquals() {
        assertTrue(event.fullEquals(sameEvent) && sameEvent.fullEquals(event));
        assertTrue(event.fullEquals(event) && sameEvent.fullEquals(sameEvent)
                && differentEvent.fullEquals(differentEvent));
        assertFalse(event.fullEquals(differentEvent) && differentEvent.fullEquals(sameEvent));
    }

    /**
     * Tests whether the hash codes of event and sameEvent are equal (they are)
     * and the hash codes of event/sameEvent are equal to differentEvent (they are not)
     */
    @Test
    void testHashCode() {
        assertEquals(event.hashCode(), event.hashCode());
        assertEquals(event.hashCode(), sameEvent.hashCode());
        assertTrue(event.hashCode() != differentEvent.hashCode());
        assertTrue(sameEvent.hashCode() != differentEvent.hashCode());
    }
    @Test
    void equalsTest() {
        assertEquals(event, event);
        assertNotEquals(event, null);
        Event testEvent = new Event("a",null,null);
        testEvent.setInviteCode(event.getInviteCode());
        assertEquals(event,testEvent);
    }

    /**
     * Tests whether the toString version of event is as expected
     */
//    @Test
//    void testToString() {
//       String expected = "0\t\t\t\tTest Event\t\t\t0\t\t\t\t\t\t0" +
//               "\t\t\t\t\tMon Apr 17 00:00:00 CEST 3922\n";
//       event.setInviteCode(0);
//      assertEquals(expected, event.toString());
//    }
}