package server.api;

import java.util.List;


import commons.Quote;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import server.database.EventRepository;

@RestController
@RequestMapping("/api/events")

public class EventController {

    private final EventRepository repo;


    /**
     * ewa
     * @param repo ewa
     */
    public EventController(EventRepository repo) {
        this.repo = repo;
    }

    /**
     * ewa
     * @return awe
     */
    @GetMapping(path = { "", "/" })
    public List<Quote> getAll() {
        return repo.findAll();
    }
}
