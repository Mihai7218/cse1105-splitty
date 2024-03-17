package admin;

import commons.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdminConsoleSortingTest {

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
    @BeforeEach
    public void init(){
        ac = new AdminConsole();
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

    }

    @Test
    public void orderTitleAscTest(){
        ac.orderByTitleAsc();
        comparison = (List.of(event1, event2, event3));
        assertEquals(ac.getEvents(),comparison);
    }

    @Test
    public void orderTitleDescTest(){
        ac.orderByTitleDesc();
        comparison = (List.of(event3, event2, event1));
        assertEquals(ac.getEvents(),comparison);
    }

    @Test
    public void orderCreationRecentTest(){
        ac.orderByCreationRecent();
        comparison = (List.of(event1, event3, event2));
        assertEquals(ac.getEvents(),comparison);
    }

    @Test
    public void orderCreationOldTest(){
        ac.orderByCreationOld();
        comparison = (List.of(event2, event3, event1));
        assertEquals(ac.getEvents(),comparison);
    }

    @Test
    public void orderActivityRecentTest(){
        ac.orderByActivityRecent();
        comparison = (List.of(event3, event2, event1));
        assertEquals(ac.getEvents(),comparison);
    }

    @Test
    public void orderActivityOldTest(){
        ac.orderByActivityOld();
        comparison = (List.of(event1, event2, event3));
        assertEquals(ac.getEvents(),comparison);
    }


}