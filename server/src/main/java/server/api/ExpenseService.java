package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import server.database.EventRepository;
import server.database.ExpenseRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ExpenseService {
    private final EventRepository eventRepo;
    private final ExpenseRepository expenseRepo;

    public ExpenseService(EventRepository eventRepo, ExpenseRepository expenserepo){
        this.eventRepo = eventRepo;
        this.expenseRepo = expenserepo;
    }

    public ResponseEntity<List<Expense>> getAllExpenses(long id) {
        if (id < 0 || !eventRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Event event = eventRepo.findById(id).get();
        List<Expense> expenses = event.getExpensesList();
        return ResponseEntity.ok(expenses);
    }

    public ResponseEntity<Double> getTotal(long id) {
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

    public ResponseEntity<Expense> add(Expense expense) {
        if (expense == null || expense.getTitle() == null ||
                Objects.equals(expense.getTitle(), "") ||
                expense.getAmount() == 0 || expense.getPayee() == null) {
            return ResponseEntity.notFound().build();
        }
        expenseRepo.save(expense);
        return ResponseEntity.ok(expense);
    }

    public ResponseEntity<Void> changeTitle(String title, long expenseId) {
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

    public ResponseEntity<Void> changeAmount(double amount,
                                             long expenseId){
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

    public ResponseEntity<Void> changePayee(Participant payee, long expenseId){
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

    public ResponseEntity<Void> deleteExpense(long expenseId){

        if (expenseId < 0 || !expenseRepo.existsById(expenseId)) {
            return ResponseEntity.notFound().build();
        }
        expenseRepo.deleteAllById(Collections.singleton(expenseId));
        return ResponseEntity.ok(null);
    }
}
