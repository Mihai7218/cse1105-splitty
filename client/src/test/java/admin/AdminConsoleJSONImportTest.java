package admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import commons.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AdminConsoleJSONImportTest {

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
    List<String> json;
    @BeforeEach
    public void init() throws JsonProcessingException {
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
        json = ac.eventToJson();

    }

    @Test
    public void tryOneDumpJSON(){
        Scanner s = new Scanner(json.get(0));
        Event events = ac.importWithJson(s);
        assertEquals(ac.getEvents().get(0), events);
    }

    @Test
    public void tryEmptyDumpJSON(){
        String empty = "";
        Scanner s = new Scanner(empty);
        assertEquals(null, ac.importWithJson(s));
    }

    @Test
    public void invalidJSON(){
        String invalidJSON = "{\"inviteCode\"::0,\"expensesList\":[],\"participantsList\":[],\"tagsList\":[],\"creationDate\":61570191600000,\"lastActivity\":61666959600000}";
        Scanner s = new Scanner(invalidJSON);
        assertEquals(null, ac.importWithJson(s));
    }
}