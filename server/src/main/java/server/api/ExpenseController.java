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
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping(path = { "" })
    public ResponseEntity<List<Expense>> getAllExpenses(@PathVariable("id") long id){
        return expenseService.getAllExpenses(id);
    }

    /***
     * @param id the event of which we want to sum the total of expenses
     * @return the sum of all expenses
     */
    @GetMapping(path = { "/total" })
    public ResponseEntity<Double> getTotal(@PathVariable("id") long id) {
        return expenseService.getTotal(id);
    }

    @PostMapping(path = {""})
    public ResponseEntity<Expense> add(@RequestBody Expense expense) {
        return expenseService.add(expense);
    }

    @PutMapping(path = {"/{expenseId}/title"})
    public ResponseEntity<Void> changeTitle(@RequestBody String title,
                                            @PathVariable("expenseId") long expenseId){
        return expenseService.changeTitle(title, expenseId);
    }

    @PutMapping(path = {"/{expenseId}/amount"})
    public ResponseEntity<Void> changeAmount(@RequestBody double amount,
                                            @PathVariable("expenseId") long expenseId){
        return expenseService.changeAmount(amount, expenseId);
    }

    @PutMapping(path = {"/{expenseId}/payee"})
    public ResponseEntity<Void> changePayee(@RequestBody Participant payee,
                                             @PathVariable("expenseId") long expenseId){
        return expenseService.changePayee(payee, expenseId);
    }

    @DeleteMapping(path = {"/{expenseId}"})
    public ResponseEntity<Void> deleteExpense(@PathVariable("expenseId") long expenseId){
        return expenseService.deleteExpense(expenseId);
    }

}
