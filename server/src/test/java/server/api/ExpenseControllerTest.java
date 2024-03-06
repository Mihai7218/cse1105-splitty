package server.api;

import org.junit.jupiter.api.BeforeEach;

public class ExpenseControllerTest {
    private ExpenseController ctrl;

    @BeforeEach
    public void setup() {
        TestEventRepository eventRepo = new TestEventRepository();
        TestExpenseRepository expenseRepo = new TestExpenseRepository();
        ExpenseService serv = new ExpenseService(eventRepo, expenseRepo);
        ctrl = new ExpenseController(serv);
    }
}
