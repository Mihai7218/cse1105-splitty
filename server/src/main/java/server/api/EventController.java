package server.api;

import java.util.*;

import commons.Event;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import static org.springframework.http.HttpStatus.*;



@RestController
@RequestMapping("/api/events")

public class EventController {

    private final EventService eventService;

    private final GerneralServerUtil serverUtil;


    /**
     * constructor for the EventController
     * @param eventService the service with all the necessary functions for the api
     */
    public EventController(EventService eventService, GerneralServerUtil serverUtil) {
        this.eventService = eventService;
        this.serverUtil = serverUtil;
    }

    /**
     * Get method to get a specific event from the database
     * @param inviteCode the invite code of that specific event
     * @return the requested event
     */
    @GetMapping(path = { "/{inviteCode}/updates" })
    public DeferredResult<ResponseEntity<Event>> getPolling(
            @PathVariable("inviteCode") long inviteCode) {

        return eventService.getPolling(inviteCode);
    }

    /**
     * Websocket implemmentation of the add
     * @param inviteCode code of the event
     * @param event new values of event
     */
    @MessageMapping("/events/expenses")
    @SendTo("/topic/expenses")
    public Event addExpense(long inviteCode, Event event) {
        return change(inviteCode,event).getBody();
    }

    /**
     * Get method to get a specific event from the database
     * @param inviteCode the invite code of that specific event
     * @return the requested event
     */
    @GetMapping(path = { "/{inviteCode}" })
    public ResponseEntity<Event> get(@PathVariable("inviteCode") long inviteCode) {
        return eventService.getEvent(inviteCode);
    }

    /**
     * Get methode to get all the events on the server
     * @return returns a list of all events on the server
     */
    @GetMapping(path = { "/admin/{password}" })
    public ResponseEntity<List<Event>> get(@PathVariable("password") String password) {
        if (PasswordService.getPassword().equals(password)) {
            return eventService.getAllEvents();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * A post method to add and event to the repository
     * @param event an event in the requestBody to add to the repository
     * @return the event if succesfully made
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Event> add(@RequestBody Event event) {
        return eventService.addEvent(event);
    }

    /**
     * Change an existing event
     * @param inviteCode the invite code of the event to change
     * @param event the data that the event should have
     * @return the new changed event
     */
    @PutMapping(path = {"/{inviteCode}" })
    public ResponseEntity<Event> change(@PathVariable("inviteCode") long inviteCode,
                                        @RequestBody Event event) {
        return eventService.changeEvent(inviteCode,event,serverUtil);
    }

    /**
     * Delete an existing event
     * @param inviteCode the invitecode of the event
     * @return the event that was deleted
     */
    @DeleteMapping(path = {"/{inviteCode}" })
    public ResponseEntity<Event> delete(@PathVariable("inviteCode") long inviteCode) {
        return eventService.deleteEvent(inviteCode);
    }

    /**
     * Post method to allow an admin to upload new events
     * @param password string password
     * @param event the list of events to be added
     * @return the list of events if succesfully added
     */
    @PostMapping(path = {"/admin/{password}"})
    public ResponseEntity<Event> addJsonImport(@PathVariable("password") String password,
                                               @RequestBody  Event event){
        if (PasswordService.getPassword().equals(password)) {
            if(eventService.validateEvent(event).getStatusCode().equals(OK)){
                eventService.addCreatedEvent(event);
                return ResponseEntity.ok(event);
            }else{
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint to access and calculated the debts for a certain participant
     * @param eventId id of the event the particpant is in
     * @param participantId id of the participant
     * @return amount that is owed if calculable
     */
    @GetMapping(path = {"/{invitecode}/debts/{participantId}"})
    public ResponseEntity<Double> getDebts(@PathVariable("invitecode") Long eventId,
                                           @PathVariable("participantId") Long participantId){
        return eventService.getDebts(eventId, participantId);
    }




}
