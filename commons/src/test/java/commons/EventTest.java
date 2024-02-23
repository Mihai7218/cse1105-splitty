package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import static org.junit.jupiter.api.Assertions.*;

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
        creationDate = new Date();
        lastActivity = new Date();

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
        Expense testExpense = new Expense("Expense1", 100.0);
        newExpensesList.add(testExpense);
        event.setExpensesList(newExpensesList);
        assertEquals(newExpensesList, event.getExpensesList());
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

    @Test
    void getCreationDate() {
    }

    @Test
    void setCreationDate() {
    }

    @Test
    void getLastActivity() {
    }

    @Test
    void setLastActivity() {
    }

    @Test
    void testEquals() {
    }

    @Test
    void testHashCode() {
    }

    @Test
    void testToString() {
    }
}