package server.api;

import org.junit.jupiter.api.BeforeEach;
import server.database.ExpenseRepository;

public class ExpenseControllerTest {
    private TestEventRepository eventRepo;
    private TestExpenseRepository expenseRepo;
    private ExpenseController ctrl;

    @BeforeEach
    public void setup() {
        eventRepo = new TestEventRepository();
        expenseRepo = new TestExpenseRepository();
        ctrl = new ExpenseController(eventRepo, expenseRepo);
    }
}
