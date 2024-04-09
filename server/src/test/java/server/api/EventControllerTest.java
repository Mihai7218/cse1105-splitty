/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.EventRepository;
import server.database.ParticipantRepository;
import server.database.TagRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.*;
import static server.api.PasswordService.setPassword;

public class EventControllerTest {

    boolean eventValid;
    List<Event> eventsList;

    private EventController sut;
    private EventService serviceStub;
    private EventController sutStubbed;
    private PasswordService ps;

    public class EventServiceStub extends EventService {


        /**
         * Constructor for de EventService
         *
         * @param eventRepository the event repository
         * @param tagRepository   the tag repository
         */
        public EventServiceStub(EventRepository eventRepository, TagRepository tagRepository) {
            super(eventRepository, tagRepository);
            eventsList = new ArrayList<>();
            eventValid = true;
        }

        public ResponseEntity<Event> getEvent(long inviteCode){
            if(inviteCode < 0){
                return ResponseEntity.badRequest().build();
            }else if(inviteCode >= 50){
                return ResponseEntity.notFound().build();
            }else{
                return ResponseEntity.ok(eventsList.get((int) inviteCode));
            }
        }

        public ResponseEntity<List<Event>> getAllEvents(){
            return ResponseEntity.ok(eventsList);
        }

        public ResponseEntity<Event> addEvent(Event event){
            if(event == null) return ResponseEntity.badRequest().build();
            if(event.getTitle() == null || event.getTitle().isEmpty()){
                return ResponseEntity.badRequest().build();
            }else{
                eventsList.add(event);
                return ResponseEntity.ok(event);
            }
        }

        public ResponseEntity<Event> changeEvent(long inviteCode, Event event,
                                                 GerneralServerUtil serverUtil){
            if(inviteCode < 0){
                return ResponseEntity.badRequest().build();
            }else if(inviteCode > 50){
                return ResponseEntity.notFound().build();
            }

            if(event.getTitle() == null || event.getTitle().isEmpty()){
                return ResponseEntity.badRequest().build();
            } else {
                eventsList.get((int) inviteCode).setTitle(event.getTitle());
                return ResponseEntity.ok(event);
            }
        }

        public ResponseEntity<Event> deleteEvent(long inviteCode){
            if(inviteCode < 0){
                return ResponseEntity.badRequest().build();
            }else if(inviteCode > 50){
                return ResponseEntity.notFound().build();
            }else{

                return ResponseEntity.ok(eventsList.remove((int) inviteCode));
            }
        }

        public ResponseEntity<Event> validateEvent(Event event){
            if(eventValid) return ResponseEntity.ok(event);
            else return ResponseEntity.badRequest().build();
        }

        public ResponseEntity<Event> addCreatedEvent(Event event){
            eventsList.add(event);
            return ResponseEntity.ok(event);
        }

        public ResponseEntity<Double> getTotal(long id){
            if(id < 0){
                return ResponseEntity.badRequest().build();
            }else if(id > 50){
                return ResponseEntity.notFound().build();
            }else return ResponseEntity.ok(10.0);
        }

        public ResponseEntity<Double> getShare(Long eventId, Long participantId){
            if(eventId < 0 || participantId < 0){
                return ResponseEntity.badRequest().build();
            }else if(eventId > 40 || participantId > 40){
                return ResponseEntity.notFound().build();
            }else return ResponseEntity.ok(10.0);
        }

        public ResponseEntity<Double> getDebt(Long eventId, Long participantId){
            if(eventId < 0 || participantId < 0){
                return ResponseEntity.badRequest().build();
            }else if(eventId > 40 || participantId > 40){
                return ResponseEntity.notFound().build();
            }else return ResponseEntity.ok(10.0);
        }

        public ResponseEntity<Double> getOwed(Long eventId, Long participantId){
            if(eventId < 0 || participantId < 0){
                return ResponseEntity.badRequest().build();
            }else if(eventId >40 || participantId > 40){
                return ResponseEntity.notFound().build();
            }else return ResponseEntity.ok(10.0);
        }

        public ResponseEntity<Double> validateDebt(Long eventId, Long participantId) {
            if(eventId < 0 || participantId < 0){
                return ResponseEntity.badRequest().build();
            }else if(eventId > 40 || participantId > 40){
                return ResponseEntity.notFound().build();
            }else return ResponseEntity.ok(10.0);
        }

        public ResponseEntity<List<Expense>> getExpensesInvolvingPayee(long inviteCode,
                                                                       long payeeId){
            if(inviteCode < 0 || payeeId < 0){
                return ResponseEntity.badRequest().build();
            }else return ResponseEntity.ok(null);
        }

        public ResponseEntity<List<Expense>> getExpensesInvolvingParticipant(long inviteCode,
                                                                             long partId){
            if(inviteCode < 0 || partId < 0){
                return ResponseEntity.badRequest().build();
            }else return ResponseEntity.ok(null);
        }

    }

    public TestEventRepository repo = new TestEventRepository();

    @BeforeEach
    public void setup() {
        GerneralServerUtil test = new ServerUtilModule();
        TestTagRepository tagRepo = new TestTagRepository();
        EventService ev = new EventService(repo, tagRepo);
        sut = new EventController(ev, test, mock(SimpMessagingTemplate.class));

        serviceStub = new EventServiceStub(repo, tagRepo);
        sutStubbed = new EventController(serviceStub, test, mock(SimpMessagingTemplate.class));

        ps=new PasswordService();
        setPassword("password");
    }

    @Test
    public void adminTestPassword(){
        setPassword("testPs");
        assertEquals(sutStubbed.get("testPs").getStatusCode(), OK);
        assertEquals(sutStubbed.get("wrongPs").getStatusCode(), BAD_REQUEST);
    }

    @Test
    public void getInvolvedPayTest(){

        assertEquals(OK, sutStubbed.getInvolvingPayee(0L, 0L).getStatusCode());
        assertEquals(OK, sutStubbed.getInvolvingPayee(0L, 50).getStatusCode());
        assertEquals(BAD_REQUEST, sutStubbed.getInvolvingPayee(0L, -1L).getStatusCode());
        assertEquals(BAD_REQUEST, sutStubbed.getInvolvingPayee(-1, 1L).getStatusCode());

        assertEquals(OK, sutStubbed.getInvolvingPart(1L, 0L).getStatusCode());
        assertEquals(BAD_REQUEST, sutStubbed.getInvolvingPart(1L, -1L).getStatusCode());
        assertEquals(BAD_REQUEST, sutStubbed.getInvolvingPart(-1, 1L).getStatusCode());

//        Participant p = new Participant("j doe", "example@email.com","NL85RABO5253446745", "HBUKGB4B");
//        Participant other = new Participant("John Doe",
//                "jdoe@gmail.com","NL85RABO5253446745",
//                "HBUKGB4B");
//        ParticipantPayment p1 = new ParticipantPayment(p, 5);
//        ParticipantPayment p2 = new ParticipantPayment(other,5);
//        Expense exp1 = new Expense(10, "USD", "title", "desc", null, List.of(p1,p2), new Tag("yellow", "yellow"),p);
//
//        ParticipantPayment p3 = new ParticipantPayment(p, 10);
//        ParticipantPayment p4 = new ParticipantPayment(other,10);
//        Expense exp2 = new Expense(20, "USD", "title2", "desc", null, List.of(p3,p4), new Tag("yellow", "yellow"),other);
//
//        Event event4 = new Event("test1", null, null);
//        ParticipantRepository participantRepo = new TestParticipantRepository();
//
//        Participant uninvolved = new Participant("name", null, null, null);
//
//        event4.getParticipantsList().add(p);
//        event4.getParticipantsList().add(other);
//        event4.getExpensesList().add(exp1);
//        event4.getExpensesList().add(exp2);
//        Tag one = new Tag("food", "#93c47d");
//        Tag two = new Tag("entrance fees", "#4a86e8");
//        Tag three = new Tag("travel", "#e06666");
//        event4.setTagsList(List.of(one, two, three));
//        repo.save(event4);
//        participantRepo.save(p);
//        participantRepo.save(other);
//        participantRepo.save(uninvolved);
//        assertEquals(OK, sut.getInvolvingPayee(event4.getInviteCode(), 0L).getStatusCode());
//        assertEquals(OK, sut.getInvolvingPayee(event4.getInviteCode(), uninvolved.getId()).getStatusCode());
//        assertEquals(OK, sut.getInvolvingPayee(event4.getInviteCode(), uninvolved.getId()+5).getStatusCode());
//        assertEquals(BAD_REQUEST, sut.getInvolvingPayee(event4.getInviteCode(), -1L).getStatusCode());
//        assertEquals(BAD_REQUEST, sut.getInvolvingPayee(-1, 1L).getStatusCode());
//
//        assertEquals(OK, sut.getInvolvingPart(event4.getInviteCode(), 0L).getStatusCode());
//        assertEquals(BAD_REQUEST, sut.getInvolvingPart(event4.getInviteCode(), -1L).getStatusCode());
//        assertEquals(BAD_REQUEST, sut.getInvolvingPart(-1, 1L).getStatusCode());
    }


    @Test
    public void calculateDebts(){
        assertEquals(OK, sutStubbed.getShare(0L,0L).getStatusCode());
        assertEquals(OK, sutStubbed.getOwed(0L,0L).getStatusCode());
        assertEquals(OK, sutStubbed.getShare(0L,1L).getStatusCode());
        assertEquals(OK, sutStubbed.getTotal(0L).getStatusCode());

//        ParticipantRepository participantRepo = new TestParticipantRepository();
//        Event event = new Event("Title4", null, null);
//        Participant p = new Participant("j doe", "example@email.com","NL85RABO5253446745", "HBUKGB4B");
//        Participant other = new Participant("John Doe",
//                "jdoe@gmail.com","NL85RABO5253446745",
//                "HBUKGB4B");
//        ParticipantPayment pp = new ParticipantPayment(other, 25);
//        List<ParticipantPayment> split = List.of(pp);
//        Tag t = new Tag("red", "red");
//        Expense e= new Expense(50, "USD", "exampleExpense", "description",
//                null,split ,t, p);
//
//        event.getParticipantsList().add(p);
//        event.getParticipantsList().add(other);
//        event.getExpensesList().add(e);
//        Tag one = new Tag("food", "#93c47d");
//        Tag two = new Tag("entrance fees", "#4a86e8");
//        Tag three = new Tag("travel", "#e06666");
//        event.setTagsList(List.of(t, one, two, three));
//        event.setInviteCode(5);
//        EventRepository eventRepository = new TestEventRepository();
//        TestTagRepository tagRepo = new TestTagRepository();
//        EventService temp = new EventService(eventRepository, tagRepo);
//        EventController ec = new EventController(temp, new ServerUtilModule(), mock(SimpMessagingTemplate.class));
//        eventRepository.save(event);
//        participantRepo.save(p);
//        participantRepo.save(other);
//        assertEquals(OK, ec.getShare(0L,0L).getStatusCode());
//        assertEquals(OK, ec.getOwed(0L,0L).getStatusCode());
//        assertEquals(OK, ec.getShare(0L,1L).getStatusCode());
//        assertEquals(OK, ec.getTotal(0L).getStatusCode());

    }

    @Test
    public void calculateInvalidDebts(){
        assertEquals(BAD_REQUEST, sutStubbed.getDebt(-1L, -1L).getStatusCode());
        assertEquals(BAD_REQUEST, sutStubbed.getShare(-1L, 1L).getStatusCode());
        assertEquals(BAD_REQUEST, sutStubbed.getDebt(1L, -1L).getStatusCode());
        assertEquals(BAD_REQUEST, sutStubbed.getOwed(-1L, 0L).getStatusCode());
        assertEquals(BAD_REQUEST, sutStubbed.getOwed(1L, -1L).getStatusCode());
        assertEquals(NOT_FOUND, sutStubbed.getOwed(50L, 0L).getStatusCode());
        assertEquals(NOT_FOUND, sutStubbed.getShare(50L, 0L).getStatusCode());
        assertEquals(NOT_FOUND, sutStubbed.getDebt(50L, 0L).getStatusCode());
    }

    @Test
    public void cannotAddEmptyEvent() {
        var actual = sutStubbed.add(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddEmptyWithNullTitle() {
        var actual = sutStubbed.add(new Event(null,null,null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    @Test
    public void cannotAddEmptyWithEmptyTitle() {
        var actual = sutStubbed.add(new Event("",null,null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }
    @Test
    public void canGetData() {
        var actual = sutStubbed.add(new Event("dwa",null,null));
        var actual2 = sutStubbed.get(0);
        assertEquals(OK, actual2.getStatusCode());
    }

    @Test
    public void cantGetData() {
        var actual = sutStubbed.add(new Event("dwa",null,null));
        var actual2 = sutStubbed.get(50);
        assertEquals(NOT_FOUND, actual2.getStatusCode());
    }

    @Test
    public void putTest() {
        var actual = sutStubbed.add(new Event("dwa",null,null));
        var actual2 = sutStubbed.change(0,new Event("das",null,null));
        assertEquals("das", eventsList.get(0).getTitle());
    }

    @Test
    public void deleteTest() {
        var actual = sutStubbed.add(new Event("dwa",null,null));
        var actual2 = sutStubbed.delete(0);
        assertEquals(OK, actual2.getStatusCode());
    }



    private static Event getEvent(String q) {
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("food", "green"));
        tags.add(new Tag("entrance fees", "blue"));
        tags.add(new Tag("travel", "red"));
        return new Event(q,null,null);
    }

    @Test
    public void lastActivityNotChange2Test(){
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        sut.add(new Event("asa",timestamp2,timestamp2));
        Event event = repo.getById(0L);
        Date tmpdate = event.getLastActivity();
        sut.get(0L);
        event = repo.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }

    @Test
    public void importEvent(){
        setPassword("password");
        assertEquals(OK,sut.addJsonImport("password", new Event("title",null,null)).getStatusCode());
        assertEquals(BAD_REQUEST,sut.addJsonImport("wrongPassword", new Event("title",null,null)).getStatusCode());

//        Event event = new Event("Title4", null, null);
//        Participant p = new Participant("j doe", "example@email.com","NL85RABO5253446745", "HBUKGB4B");
//        Participant other = new Participant("John Doe",
//                "jdoe@gmail.com","NL85RABO5253446745",
//                "HBUKGB4B");
//        ParticipantPayment pp = new ParticipantPayment(other, 25);
//        List<ParticipantPayment> split = List.of(pp);
//        Tag t = new Tag("red", "red");
//        Expense e= new Expense(50, "USD", "exampleExpense", "description",
//                null,split ,t, p);
//        event.getParticipantsList().add(p);
//        event.getParticipantsList().add(other);
//        event.getExpensesList().add(e);
//        Tag one = new Tag("food", "#93c47d");
//        Tag two = new Tag("entrance fees", "#4a86e8");
//        Tag three = new Tag("travel", "#e06666");
//        event.setTagsList(List.of(t, one, two, three));
//        event.setInviteCode(5);
//        assertEquals(OK,sut.addJsonImport("password", event).getStatusCode());
    }
}