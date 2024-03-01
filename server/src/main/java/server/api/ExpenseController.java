package server.api;

import java.sql.Timestamp;
import java.util.*;


import commons.*;

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

    @GetMapping(path = { "/api/events/{id}/expenses" })
    public ResponseEntity<Double> getTotal(@PathVariable("id") long id) {
        if (id < 0 || !eventRepo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Event event = eventRepo.findById(id).get();
        List<Expense> expenses = event.getExpensesList();
        double totalExpense = 0.0;
        for (Expense expense : expenses) {
            totalExpense += expense.getAmount();
        }
        return ResponseEntity.ok(totalExpense);
    }

    /***
     * @param expense an expense to be added to the repo
     * @return code OK when the expense has been added successfully, Bad Request
     * otherwise
     */
    @PostMapping(path = {"/api/events/{id}/expenses"})
    public ResponseEntity<Expense> add(@RequestBody Expense expense) {
        if (expense == null || expense.getTitle() == null ||
                Objects.equals(expense.getTitle(), "") ||
                        expense.getAmount() == 0 || expense.getPayee() == null) {
            return ResponseEntity.badRequest().build();
        }
        expenseRepo.save(expense);
        return ResponseEntity.ok(expense);
    }

    /***
     * @param title the desired new title
     * @param expenseId the id of the expense to be updated
     * @return whether the title of the expense could be updated
     */
    @PutMapping(path = {"/api/events/{eventId}/expenses/{expenseId}"})
    public ResponseEntity<Void> changeTitle(@RequestBody String title,
                                            @PathVariable("expenseId") long expenseId){
        if (expenseId < 0 || !expenseRepo.existsById(expenseId)) {
            return ResponseEntity.badRequest().build();
        }
        if (title == null || Objects.equals(title, "")) {
            return ResponseEntity.badRequest().build();
        }
        Expense change = expenseRepo.findById(expenseId).get();
        change.setTitle(title);
        expenseRepo.save(change);
        return ResponseEntity.ok(null);
    }

    /***
     * @param amount the new amount the expense is worth
     * @param expenseId the id of the expense to change the amount of
     * @return whether the amount could be changed
     */
    @PutMapping(path = {"/api/events/{eventId}/expenses/{expenseId}"})
    public ResponseEntity<Void> changeAmount(@RequestBody double amount,
                                            @PathVariable("expenseId") long expenseId){
        if (expenseId < 0 || !expenseRepo.existsById(expenseId)) {
            return ResponseEntity.badRequest().build();
        }
        if (amount <= 0.0) {
            return ResponseEntity.badRequest().build();
        }
        Expense change = expenseRepo.findById(expenseId).get();
        change.setAmount(amount);
        expenseRepo.save(change);
        return ResponseEntity.ok(null);
    }

    /***
     * @param payee the new payee of the event
     * @param expenseId the ID of the expense to be updated
     * @return whether the payee could be updated
     */
    @PutMapping(path = {"/api/events/{eventId}/expenses/{expenseId}"})
    public ResponseEntity<Void> changePayee(@RequestBody Participant payee,
                                             @PathVariable("expenseId") long expenseId){
        if (expenseId < 0 || !expenseRepo.existsById(expenseId)) {
            return ResponseEntity.badRequest().build();
        }
        if (payee == null || Objects.equals(payee.getName(), "")) {
            return ResponseEntity.badRequest().build();
        }
        Expense change = expenseRepo.findById(expenseId).get();
        change.setPayee(payee);
        expenseRepo.save(change);
        return ResponseEntity.ok(null);
    }

    /***
     * @param expenseId ID of the expense to be deleted
     * @return whether the expense could be deleted from the event
     */
    @DeleteMapping(path = {"/api/events/{id}/expenses/{expenseId}"})
    public ResponseEntity<Void> deleteExpense(@PathVariable("expenseId") long expenseId){

        if (expenseId < 0 || !expenseRepo.existsById(expenseId)) {
            return ResponseEntity.badRequest().build();
        }
        expenseRepo.deleteAllById(Collections.singleton(expenseId));
        return ResponseEntity.ok(null);
    }

}
