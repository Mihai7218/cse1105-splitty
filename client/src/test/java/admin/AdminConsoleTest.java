package admin;

import commons.Event;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class AdminConsoleTest {
    @Test
    void setPassword() {
        AdminConsole adminConsole = new AdminConsole();
        assertEquals("", adminConsole.getPassword());
        adminConsole.setPassword("ewa");
        assertEquals("ewa", adminConsole.getPassword());
    }
    @Test
    void initialisationTest() {
        AdminConsole adminConsole = new AdminConsole();
        assertNotEquals(null, adminConsole.getUtils());
    }

    @Test
    void localEventsTest() {
        AdminConsole adminConsole = new AdminConsole();
        assertEquals(0, adminConsole.getEvents().size());
        List<Event> events = new ArrayList<>();
        events.add(new Event("test",null,null));
        adminConsole.setEvents(events);
        assertEquals(1, adminConsole.getEvents().size());
        assertEquals(events, adminConsole.getEvents());
    }

}
