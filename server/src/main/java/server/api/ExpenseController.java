package server.api;

import java.sql.Timestamp;
import java.util.*;


import commons.Expense;
import commons.Event;

import commons.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.database.ExpenseRepository;
import server.database.EventRepository;

@RestController
@RequestMapping("/api/events/{id}/expenses")
public class ExpenseController {
    private final EventRepository eventRepo;
    private final ExpenseRepository expenseRepo;

    public ExpenseController(EventRepository eventRepo, ExpenseRepository expenseRepo) {
        this.eventRepo = eventRepo;
        this.expenseRepo = expenseRepo;
    }


    /**
     * Get method to get all the expenses associated with a certain event
     * @return returns a list of all expenses within a certain event
     */
    @GetMapping(path = { "/api/events/{id}/expenses" })
    public ResponseEntity<List<Expense>> getAllExpenses(@PathVariable("id") long id) {
        if (id < 0 || !eventRepo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Event event = eventRepo.findById(id).get();
        List<Expense> expenses = event.getExpensesList();
        return ResponseEntity.ok(expenses);
    }


    /***
     * @param expense an expense to be added to the repo
     * @return code OK when the expense has been added successfully, Bad Request
     * otherwise
     */
    @PostMapping(path = {"/api/events/{id}/expenses"})
    public ResponseEntity<Expense> add(@RequestBody Expense expense) {
        if (expense == null || expense.getTitle() == null || Objects.equals(expense.getTitle(), "")) {
            return ResponseEntity.badRequest().build();
        }
        expenseRepo.save(expense);
        return ResponseEntity.ok(expense);
    }


    /***
     * @param expenseId ID of the expense to be deleted
     * @return whether or not the expense could be deleted from the event
     */
    @DeleteMapping(path = {"/api/events/{id}/expenses/{expenseId}"})
    public ResponseEntity<Void> delete(@PathVariable("expenseId") long expenseId){

        if (expenseId < 0 || !expenseRepo.existsById(expenseId)) {
            return ResponseEntity.badRequest().build();
        }
        expenseRepo.deleteAllById(Collections.singleton(expenseId));
        return ResponseEntity.ok(null);
    }
}
