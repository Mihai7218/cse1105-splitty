package admin;

import commons.Event;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static admin.AdminConsole.showOptions;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class AdminConsoleMenuTest {


    //Needed for the tests to run headless.
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }
    Date date1;
    Date date2;
    Date date3;
    Date activity1;
    Date activity2;
    Date activity3;
    Event event1;
    Event event2;
    Event event3;
    List<Event> comparison;
    AdminConsole ac;
    ServerUtils server;
    @BeforeEach
    public void init(){
        server = mock(ServerUtils.class);
        ac = new AdminConsoleStub();
        date1 = new Date(2020, 12, 31);
        date2 = new Date(2021, 12, 31);
        date3 = new Date(2022, 12, 31);

        activity1 = new Date(2023, 7, 12);
        activity2 = new Date(2023, 8, 12);
        activity3 = new Date(2024, 1,25);
        event1 =new Event("Aaa",date1,activity3);
        event2 = new Event("Bbb",date3,activity2);
        event3 = new Event("Ccc",date2,activity1);
        ac.getEvents().add(event1);
        ac.getEvents().add(event2);
        ac.getEvents().add(event3);
        comparison = new ArrayList<>();
        when(server.getEvents(anyString())).thenReturn(List.of(event1,event2,event3));


    }


    private static class AdminConsoleStub extends AdminConsole {
        @Override
        public  void performExit() {
            // Do nothing to avoid calling System.exit() in tests
        }
    }

    @Test
    public void testOption1(){
        String input = "1 " +
                "1 " +
                "5 " +
                "5 " +
                "5 " +
                "5 ";
        Scanner s = new Scanner(input);
        ac.setUtils(server);
        showOptions(s, ac);
        comparison = (List.of(event1, event2, event3));
        assertEquals(ac.getEvents(),comparison);
    }

    @Test
    public void testOption2(){
        String input = "1 " +
                "1 " +
                "5 " +
                "5 " +
                "5 " +
                "5 ";
        Scanner s = new Scanner(input);
        ac.setUtils(server);
        showOptions(s, ac);
        comparison = (List.of(event3, event2, event1));
        assertEquals(ac.getEvents(),comparison);
    }

    @Test
    public void testOption4(){
        String input =
                "4 " +
                "0 " +
                "N " +
                "5 " +
                "5 " +
                "5 ";
        Scanner s = new Scanner(input);
        ac.setUtils(server);
        showOptions(s, ac);
        comparison = (List.of(event3, event2, event1));
        assertEquals(ac.getEvents(),comparison);
    }

    @Test
    public void testOption4Cancel(){
        String input =
                "4 " +
                        "cancel " +
                        "5 " +
                        "5 " +
                        "5 ";
        Scanner s = new Scanner(input);
        ac.setUtils(server);
        showOptions(s, ac);
        comparison = (List.of(event3, event2, event1));
        assertEquals(ac.getEvents(),comparison);
    }

    @Test
    public void testOption4Delete(){
        String input =
                "4 " +
                        "0 " +
                        "Y " +
                        "5 " +
                        "5 " +
                        "5 ";
        Scanner s = new Scanner(input);

        ac.setUtils(server);
        Response r = mock(Response.class);
        when(server.deleteEvent(anyInt())).thenReturn(r);
        when(r.getStatus()).thenReturn(200);

        showOptions(s, ac);
        comparison = (List.of(event3, event2, event1));
        assertEquals(ac.getEvents(),comparison);
    }


}