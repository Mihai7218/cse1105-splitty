package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
        List<Expense> expensesList = new ArrayList<>();
        List<Participant> participantsList = new ArrayList<>();
        List<Tag> tagsList = new ArrayList<>();
        creationDate = new Date(2024, 2, 15);
        lastActivity = new Date(2022, 3, 17);

        event = new Event(1, "Test Event", expensesList, participantsList, tagsList, creationDate, lastActivity);
        sameEvent = new Event(1, "Test Event", expensesList, participantsList, tagsList, creationDate, lastActivity);
        differentEvent = new Event(2, "Different Event", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), creationDate, lastActivity);
    }

    /**
     * Tests the getInviteCode method for 3 different instances
     */
    @Test
    void getInviteCode() {
        assertEquals(1, event.getInviteCode());
        assertEquals(1, sameEvent.getInviteCode());
        assertEquals(2, differentEvent.getInviteCode());
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
        Date newLastActivity = new Date(2023, 1, 15);
        event.setCreationDate(newLastActivity);
        sameEvent.setCreationDate(newLastActivity);
        differentEvent.setCreationDate(newLastActivity);
        assertEquals(newLastActivity, event.getCreationDate());
        assertEquals(newLastActivity, sameEvent.getCreationDate());
        assertEquals(newLastActivity, differentEvent.getCreationDate());
    }

    /**
     * Tests whether event and sameEvent are equal (they are)
     * and event/sameEvent are equal to differentEvent (they are not)
     */
    @Test
    void testEquals() {
        assertTrue(event.equals(sameEvent) && sameEvent.equals(event));
        assertTrue(event.equals(event) && sameEvent.equals(sameEvent)
                && differentEvent.equals(differentEvent));
        assertFalse(event.equals(differentEvent) && differentEvent.equals(sameEvent));
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

    /**
     * Tests whether the toString version of event is as expected
     */
    //@Test
    //void testToString() {
    //   String expected = "Event{" +
    //           "inviteCode='" + event.getInviteCode() + '\'' +
    //          ", title='" + event.getTitle() + '\'' +
    //        ", creationDate='" + creationDate + '\'' +
    //      ", lastActivity='" + lastActivity + '\'' +
    //    '}';
    //  assertEquals(expected, event.toString());
    //}
}