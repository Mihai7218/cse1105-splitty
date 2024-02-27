package server.api;

import java.util.List;


import commons.Event;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.database.EventRepository;

@RestController
@RequestMapping("/api/events")

public class EventController {

    private final EventRepository repo;


    /**
     * EventController constructor
     * @param repo a Event Repository to store the events
     */
    public EventController(EventRepository repo) {
        this.repo = repo;
    }

    /**
     * Get methode to get all the events on the server
     * @return returns a list of all events on the server
     */
    @GetMapping(path = { "", "/" })
    public List<Event> getAll() {
        return repo.findAll();
    }

    /**
     * A post method to add and event to the repository
     * @param event an event in the requestBody to add to the repository
     * @return the event if succesfully made
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Event> add(@RequestBody Event event) {

//        if (quote.person == null || isNullOrEmpty(quote.person.firstName)
//                || isNullOrEmpty(quote.person.lastName)
//                || isNullOrEmpty(quote.quote)) {
//            return ResponseEntity.badRequest().build();
//        }

        Event saved = repo.save(event);
        return ResponseEntity.ok(saved);
    }
}
