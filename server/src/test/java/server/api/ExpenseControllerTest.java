package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExpenseControllerTest {
    private ExpenseController ctrl;

    @BeforeEach
    public void setup() {
        TestEventRepository eventRepo = new TestEventRepository();
        TestExpenseRepository expenseRepo = new TestExpenseRepository();
        ExpenseService serv = new ExpenseService(eventRepo, expenseRepo);
        ctrl = new ExpenseController(serv);
    }

    @Test
    public void getAllExpensesOK(){

    }
}
