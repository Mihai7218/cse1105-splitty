package server.api;

import java.util.*;


import commons.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.database.ExpenseRepository;
import server.database.EventRepository;

@RestController
@RequestMapping("/api/events/{id}/expenses")
public class ExpenseController {
    private final EventRepository eventRepo;
    private final ExpenseRepository expenseRepo;

    /***
     * @param eventRepo the repo which stores the events
     * @param expenseRepo the repo which stores the expenses
     */
    public ExpenseController(EventRepository eventRepo, ExpenseRepository expenseRepo) {
        this.eventRepo = eventRepo;
        this.expenseRepo = expenseRepo;
    }


    /**
     * Get method to get all the expenses associated with a certain event
     * @return returns a list of all expenses within a certain event
     */
    @GetMapping(path = { "" })
    public ResponseEntity<List<Expense>> getAllExpenses(@PathVariable("id") long id) {
        if (id < 0 || !eventRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Event event = eventRepo.findById(id).get();
        List<Expense> expenses = event.getExpensesList();
        return ResponseEntity.ok(expenses);
    }

    /***
     * @param id the event of which we want to sum the total of expenses
     * @return the sum of all expenses
     */
    @GetMapping(path = { "/total" })
    public ResponseEntity<Double> getTotal(@PathVariable("id") long id) {
        if (id < 0 || !eventRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
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
    @PostMapping(path = {""})
    public ResponseEntity<Expense> add(@RequestBody Expense expense) {
        if (expense == null || expense.getTitle() == null ||
                Objects.equals(expense.getTitle(), "") ||
                        expense.getAmount() == 0 || expense.getPayee() == null) {
            return ResponseEntity.notFound().build();
        }
        expenseRepo.save(expense);
        return ResponseEntity.ok(expense);
    }

    /***
     * @param title the desired new title
     * @param expenseId the id of the expense to be updated
     * @return whether the title of the expense could be updated
     */
    @PutMapping(path = {"/{expenseId}/title"})
    public ResponseEntity<Void> changeTitle(@RequestBody String title,
                                            @PathVariable("expenseId") long expenseId){
        if (expenseId < 0 || !expenseRepo.existsById(expenseId)) {
            return ResponseEntity.notFound().build();
        }
        if (title == null || Objects.equals(title, "")) {
            return ResponseEntity.notFound().build();
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
    @PutMapping(path = {"/{expenseId}/amount"})
    public ResponseEntity<Void> changeAmount(@RequestBody double amount,
                                            @PathVariable("expenseId") long expenseId){
        if (expenseId < 0 || !expenseRepo.existsById(expenseId)) {
            return ResponseEntity.notFound().build();
        }
        if (amount <= 0.0) {
            return ResponseEntity.notFound().build();
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
    @PutMapping(path = {"/{expenseId}/payee"})
    public ResponseEntity<Void> changePayee(@RequestBody Participant payee,
                                             @PathVariable("expenseId") long expenseId){
        if (expenseId < 0 || !expenseRepo.existsById(expenseId)) {
            return ResponseEntity.notFound().build();
        }
        if (payee == null || Objects.equals(payee.getName(), "")) {
            return ResponseEntity.notFound().build();
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
    @DeleteMapping(path = {"/{expenseId}"})
    public ResponseEntity<Void> deleteExpense(@PathVariable("expenseId") long expenseId){

        if (expenseId < 0 || !expenseRepo.existsById(expenseId)) {
            return ResponseEntity.notFound().build();
        }
        expenseRepo.deleteAllById(Collections.singleton(expenseId));
        return ResponseEntity.ok(null);
    }

}
