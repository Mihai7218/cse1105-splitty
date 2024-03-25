package server.api;

import commons.Expense;
import commons.Participant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;


@RestController
@RequestMapping("/api/events/{id}/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    private final GerneralServerUtil serverUtil;

    /**
     * Constructor for the ExpenseController
     * @param expenseService the associated service for the expense class
     */
    public ExpenseController(ExpenseService expenseService,
                             @Qualifier("serverUtilImpl") GerneralServerUtil serverUtil) {
        this.expenseService = expenseService;
        this.serverUtil = serverUtil;
    }

    /**
     * @param id the id of the event to list all expenses from
     * @return the list of all expenses within an event
     */
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

    /**
     * @param id the id of the event we want to add the expense to
     * @param expense the expense to be added to the event
     * @return whether the expense could be added
     */
    @PostMapping(path = {""})
    public ResponseEntity<Expense> add(@PathVariable("id") long id,
                                       @RequestBody Expense expense) {
        return expenseService.add(id, expense, serverUtil);
    }

    /**
     * @param title the new title for the expense
     * @param expenseId the id of the expense to be changed
     * @param id the id of the event which the expense is associated with
     * @return whether the title could be changed
     */
    @PutMapping(path = {"/{expenseId}/title"})
    public ResponseEntity<Void> changeTitle(@RequestBody String title,
                                            @PathVariable("expenseId") long expenseId,
                                            @PathVariable("id") long id){
        return expenseService.changeTitle(title, expenseId, id,serverUtil);
    }

    /**
     * @param amount the new amount of the expense
     * @param expenseId the id of the expense to be changed
     * @param id the id of the event
     * @return whether the amount could be updated
     */
    @PutMapping(path = {"/{expenseId}/amount"})
    public ResponseEntity<Void> changeAmount(@RequestBody double amount,
                                            @PathVariable("expenseId") long expenseId,
                                             @PathVariable("id") long id){
        return expenseService.changeAmount(amount, expenseId, id,serverUtil);
    }

    /**
     * @param payee the new payee of the expense
     * @param expenseId the id of the expense
     * @param id the id of the event
     * @return whether the payee could be updated
     */
    @PutMapping(path = {"/{expenseId}/payee"})
    public ResponseEntity<Void> changePayee(@RequestBody Participant payee,
                                             @PathVariable("expenseId") long expenseId,
                                            @PathVariable("id") long id){
        return expenseService.changePayee(payee, expenseId, id,serverUtil);
    }

    /**
     * @param expenseId the id of the expense to be deleted
     * @param id the id of the event
     * @return whether the expense was deleted
     */
    @DeleteMapping(path = {"/{expenseId}"})
    public ResponseEntity<Expense> deleteExpense(@PathVariable("expenseId") long expenseId,
                                              @PathVariable("id") long id){
        return expenseService.deleteExpense(expenseId, id,serverUtil);
    }


    /**
     * Post method to allow an admin to upload new expenses
     * @param password string password
     * @param expenses the list of expenses to be added
     * @return the list of events if succesfully added
     */
    @PostMapping(path = {"/admin/{password}"})
    public ResponseEntity<Expense> addJsonImport(@PathVariable("id") long id,
            @PathVariable("password") String password,
                                                 @RequestBody Expense expenses){
        if (PasswordService.getPassword().equals(password)) {
            if(expenseService.validateExpense(expenses).getStatusCode().equals(OK)){
                expenseService.addCreatedExpense(expenses);
                return ResponseEntity.ok(expenses);

            }else{
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }



}
