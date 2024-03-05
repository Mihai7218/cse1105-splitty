package server.api;

import java.sql.Timestamp;
import java.util.*;


import commons.Event;

import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.database.EventRepository;
import server.database.TagRepository;

@RestController
@RequestMapping("/api/events")

public class EventController {

    private final EventService eventService;


    /**
     * constructor for the EventController
     * @param eventService the service with all the necessary functions for the api
     */
    public EventController(EventService eventService) {
        this.eventService = eventService;
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
    @GetMapping(path = { "/{inviteCode}" })
    public ResponseEntity<List<Event>> get() {
        return eventService.getAllEvents();
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
        return eventService.changeEvent(inviteCode,event);
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



}
