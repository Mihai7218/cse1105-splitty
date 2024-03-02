package server.api;


import commons.Event;

import commons.Expense;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.database.EventRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/events")

public class TagController {

    private final EventRepository repo;


    /**
     * Constructor for the TagController
     * @param repo Repo with the events
     */
    public TagController(EventRepository repo) {
        this.repo = repo;
    }

    /**
     * Get methode to get all the events on the server
     * @return returns a list of all events on the server
     */
    @GetMapping(path = { "/{inviteCode}/tags/{name}/expenses" })
    public ResponseEntity<List<Expense>> getAllExpensesWithTag(
            @PathVariable("inviteCode") long inviteCode, @PathVariable("name") String tagName) {
        if (inviteCode < 0 || !repo.existsById(inviteCode) ||
                repo.findById(inviteCode).get().getExpensesList() == null) {
            return ResponseEntity.badRequest().build();
        }
        Event tmpEvent = repo.findById(inviteCode).get();
        List<Expense> res = new ArrayList<>();
        for (Expense expense : tmpEvent.getExpensesList()) {
            if (expense.getTag().getName().equals(tagName)) {
                res.add(expense);
            }
        }
        return ResponseEntity.ok(res);
    }
}
