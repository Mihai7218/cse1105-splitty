package admin;

import commons.Event;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

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

    @Test
    void deleteEventTest() {
        ServerUtils util = new ServerUtils("http://localhost:8080");

        AdminConsole adminConsole = new AdminConsole();
        adminConsole.setUtils(util);

        List<Event> events = new ArrayList<>();
        events.add(new Event("test",null,null));
        adminConsole.setEvents(events);

        Event deletedEvent = (Event) adminConsole.delete(adminConsole, 0).getEntity();

        assertEquals("test", deletedEvent.getTitle());
    }

}
