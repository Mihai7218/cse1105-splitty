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

    @Test
    void getInviteCode() {
    }

    @Test
    void setInviteCode() {
    }

    @Test
    void getTitle() {
    }

    @Test
    void setTitle() {
    }

    @Test
    void getExpensesList() {
    }

    @Test
    void setExpensesList() {
    }

    @Test
    void getParticipantsList() {
    }

    @Test
    void setParticipantsList() {
    }

    @Test
    void getTagsList() {
    }

    @Test
    void setTagsList() {
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