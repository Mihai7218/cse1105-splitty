package server.api;

import java.util.List;


import commons.Quote;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import server.database.SplittyRepository;

@RestController
@RequestMapping("/api/events")

public class SplittyController {

    private final SplittyRepository repo;


    /**
     * ewa
     * @param repo ewa
     */
    public SplittyController(SplittyRepository repo) {
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
