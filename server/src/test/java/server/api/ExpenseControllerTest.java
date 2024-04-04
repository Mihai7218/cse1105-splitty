package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantPaymentRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.*;
import static server.api.PasswordService.setPassword;

public class ExpenseControllerTest {
    public boolean validExpense;
    public boolean titleChanged;
    public boolean amountChanged;
    public boolean payeeChanged;
    public final Expense deletable = new Expense(10, "USD", "deleted", "desc", null, null, null, null);


    public class ExpenseServiceStub extends ExpenseService{



        List<Expense> allExpenses;
        /**
         * Constructor for the ExpenseService
         *
         * @param eventRepo   the repo of events
         * @param expenseRepo the repo of expenses
         * @param ppRepo
         */
        public ExpenseServiceStub(EventRepository eventRepo, ExpenseRepository expenseRepo, ParticipantPaymentRepository ppRepo) {
            super(eventRepo, expenseRepo, ppRepo);
            titleChanged = false;
            amountChanged = false;
            payeeChanged = false;
            validExpense = false;
            allExpenses = new ArrayList<>();
            allExpenses.add(new Expense(10, "eur", "exp1","desc",null,null,null,null));
            allExpenses.add(new Expense(10, "eur", "exp2","desc",null,null,null,null));
            allExpenses.add(new Expense(10, "eur", "exp3","desc",null,null,null,null));
            allExpenses.get(0).setId(0);
            allExpenses.get(1).setId(1);
            allExpenses.get(2).setId(2);

        }
        public ResponseEntity<Void> changeTitle(String e, long expId,
                                                long id, GerneralServerUtil serverUtil){
            if(expId < 0 || id < 0 || e == null || e.isEmpty()) {
                titleChanged = false;
                return ResponseEntity.badRequest().build();
            }else if( expId > 50 || id > 50 ){
                titleChanged = false;
                return ResponseEntity.notFound().build();
            }
            titleChanged = true;
            return ResponseEntity.ok(null);
        }
        public ResponseEntity<Void> changeTitle(Expense e, long expId, long id, GerneralServerUtil sum){
            if(expId < 0 || id < 0 || e == null || e.getTitle() == null || e.getTitle().isEmpty()) {
                titleChanged = false;
                return ResponseEntity.badRequest().build();
            }else if( expId > 50 || id > 50 ){
                titleChanged = false;
                return ResponseEntity.notFound().build();
            }
            titleChanged = true;
            return ResponseEntity.ok(null);
        }

        public ResponseEntity<Void> changeAmount(double amount, long expId, long id, GerneralServerUtil sum){
            if(expId < 0 || id < 0 || amount<=0 ) {
                amountChanged = false;
                return ResponseEntity.badRequest().build();
            }else if( expId > 50 || id > 50 ){
                amountChanged = false;
                return ResponseEntity.notFound().build();
            }
            amountChanged = true;
            return ResponseEntity.ok(null);
        }

        public ResponseEntity<Void> changePayee(Participant p, long expId, long id, GerneralServerUtil sum){
            if(expId < 0 || id < 0 || p == null || p.getName() == null || p.getName().isEmpty() ) {
                payeeChanged = false;
                return ResponseEntity.badRequest().build();
            }else if( expId > 50 || id > 50 ){
                payeeChanged = false;
                return ResponseEntity.notFound().build();
            }
            payeeChanged = true;
            return ResponseEntity.ok(null);
        }

        public ResponseEntity<Expense> deleteExpense(long expId, long id, GerneralServerUtil sum){
            if(expId < 0 || id < 0 ) {
                return ResponseEntity.badRequest().build();
            }else if( expId > 50 || id > 50 ){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(deletable);
        }

        public ResponseEntity<Expense> validateExpense(Expense expense){
            if(validExpense) return ResponseEntity.ok(expense);
            else return ResponseEntity.badRequest().build();
        }

        public ResponseEntity<Expense> addCreatedExpense(Expense expense){
            return ResponseEntity.ok(expense);
        }

        public ResponseEntity<Expense> add(long id, Expense e, GerneralServerUtil m){
            if(id < 0 || e == null || !validExpense) return ResponseEntity.badRequest().build();
            else if(id > 50) return ResponseEntity.notFound().build();
            else {
                allExpenses.add(e);
                return ResponseEntity.ok(e);
            }
        }

        public ResponseEntity<List<Expense>> getAllExpenses(long id){
            if(id < 0 ) {
                return ResponseEntity.badRequest().build();
            }else if (id > 2){
                return ResponseEntity.notFound().build();
            }else{
                return ResponseEntity.ok(allExpenses);
            }
        }

        public ResponseEntity<Expense> getExpense(long id, long expenseId){
            if(id < 0 || expenseId < 0) {
                return ResponseEntity.badRequest().build();
            }else{
                return ResponseEntity.ok(null);
            }
        }

    }

    private ExpenseController ctrl;
    private ExpenseController stubbedCtrl;
    private ExpenseService servStub;

    public Event event;
    public Expense expense1;
    public long eventId;
    public Participant payee;

    public TestEventRepository eventRepo = new TestEventRepository();

    public GerneralServerUtil serverUtil;

    public SimpMessagingTemplate smt = mock(SimpMessagingTemplate.class);

    @BeforeEach
    public void setup() {
        serverUtil = new ServerUtilModule();
        TestExpenseRepository expenseRepo = new TestExpenseRepository();
        ParticipantPaymentRepository ppRepo = new TestParticipantPaymentRepository();

        ExpenseService serv = new ExpenseService(eventRepo, expenseRepo, ppRepo);
        ctrl = new ExpenseController(serv,serverUtil, smt);


        servStub = new ExpenseServiceStub(eventRepo, expenseRepo, ppRepo);
        stubbedCtrl = new ExpenseController(servStub, serverUtil, smt);


        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        event = new Event("main", timestamp2, timestamp2);
        expense1 = new Expense(2.0, "eur", "drinks", "drinks", null, null, null, payee);
        event.getExpensesList().add(expense1);
        eventRepo.save(event);
        eventId = event.getInviteCode();
        payee = new Participant("joe", null, null, null);
        expenseRepo.save(expense1);

        setPassword("password");
    }

    @Test
    public void getExpenseTesting(){
        assertEquals(OK, stubbedCtrl.getExpense(0,0).getStatusCode());
        assertEquals(BAD_REQUEST, stubbedCtrl.getExpense(-1,0).getStatusCode());
        assertEquals(BAD_REQUEST, stubbedCtrl.getExpense(1,-1).getStatusCode());
    }

    @Test
    public void changeExpenseTest(){
        Expense e = new Expense(10, "USD", "title", "desc", null, null, null, null);
        assertEquals(OK, stubbedCtrl.changeExpense(e, 0,0).getStatusCode());
        verify(smt).convertAndSend("/topic/events/0/expenses/0", e);

        assertEquals(BAD_REQUEST, stubbedCtrl.changeExpense(e, -1, 0).getStatusCode());
        assertEquals(BAD_REQUEST, stubbedCtrl.changeExpense(e, 0, -1).getStatusCode());

        assertEquals(NOT_FOUND, stubbedCtrl.changeExpense(e, 100, 0).getStatusCode());
        assertEquals(NOT_FOUND, stubbedCtrl.changeExpense(e, 0, 100).getStatusCode());


    }

    @Test
    public void importExpense(){
        Expense sample  = new Expense(10, "EUR", "test", "desc", null, null, null, null);
        assertEquals(stubbedCtrl.addJsonImport(0, "password", sample).getStatusCode(), BAD_REQUEST);
        validExpense = true;
        assertEquals(stubbedCtrl.addJsonImport(0, "password", sample).getStatusCode(), OK);
        assertEquals(stubbedCtrl.addJsonImport(0, "password", sample).getBody(), sample);
        assertEquals(stubbedCtrl.addJsonImport(0, "wrongPassword", sample).getStatusCode(), BAD_REQUEST);
    }

    /***
     * Tests for the getAllExpenses method
     */
    @Test
    public void getAllExpenses(){
        List<Expense> expenseList = stubbedCtrl.getAllExpenses(eventId).getBody();
        assertEquals(3, expenseList.size());
    }
    @Test
    public void getAllExpensesInvalid(){
        ResponseEntity<List<Expense>> res = stubbedCtrl.getAllExpenses(-90);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }

    @Test
    public void getAllExpensesNonexistent(){
        ResponseEntity<List<Expense>> res = stubbedCtrl.getAllExpenses(90);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }
    /***
     * Tests for the getTotal method
     */

    /***
     * Tests for the add method
     */
    @Test
    public void addTest(){
        Expense expense4 = new Expense(60, "party", "drinks",
                null, null, null, null, payee);
        validExpense = true;
        stubbedCtrl.add(eventId, expense4);
        verify(smt).convertAndSend("/topic/events/0/expenses", expense4);
        //there should be 4 expenses in the event now
        assertEquals(4, stubbedCtrl.getAllExpenses(eventId).getBody().size());
    }
    @Test
    public void addTestNull(){
        Expense expense4 = null;
        ResponseEntity<Expense> res = stubbedCtrl.add(eventId, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestNoTitle(){
        Expense expense4 = new Expense(60, "party", null,
                null, null, null, null, payee);
        validExpense = false;
        ResponseEntity<Expense> res = stubbedCtrl.add(eventId, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestNoPayee(){
        Expense expense4 = new Expense(60, "party", null,
                null, null, null, null, null);
        validExpense = false;
        ResponseEntity<Expense> res = stubbedCtrl.add(eventId, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestFree(){
        Expense expense4 = new Expense(0, "party", null,
                null, null, null, null, payee);
        validExpense = false;
        ResponseEntity<Expense> res = stubbedCtrl.add(eventId, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestEventInvalid(){
        Expense expense4 = new Expense(60, "party", "drinks",
                null, null, null, null, payee);
        ResponseEntity<Expense> res = stubbedCtrl.add(-30, expense4);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void addTestEventDoesntExist(){
        validExpense = true;
        Expense expense4 = new Expense(60, "party", "drinks",
                null, null, null, null, payee);
        ResponseEntity<Expense> res = stubbedCtrl.add(100, expense4);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    /***
     * Tests for the changeTitle method
     */
    @Test
    public void changeTitleTest(){
        validExpense = true;
        stubbedCtrl.changeTitle("title", 0, 0);
        assertTrue(titleChanged);
    }
    @Test
    public void changeTitleEventInvalid() {
        ResponseEntity<Void> res = stubbedCtrl.changeTitle("food", 0, -60);
        assertTrue(!titleChanged);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }
    @Test
    public void changeTitleEventDoesntExist() {
        ResponseEntity<Void> res = stubbedCtrl.changeTitle("food", 0, 100);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseDoesntExist() {
        ResponseEntity<Void> res = stubbedCtrl.changeTitle("food", 100, 0);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseInvalid() {
        ResponseEntity<Void> res = stubbedCtrl.changeTitle("food", -100, 0);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseNoTitle() {
        ResponseEntity<Void> res = stubbedCtrl.changeTitle("", 0, 0);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changeTitleExpenseNullTitle() {
        ResponseEntity<Void> res = stubbedCtrl.changeTitle(null, 0, 0);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }

    /***
     * Tests for the addAmount method
     */
    @Test
    public void changeAmountTest(){

        stubbedCtrl.changeAmount(300, 0, 0);
        assertTrue(amountChanged);
//
//        long expenseId2 = expense2.getId();
//        ctrl.changeAmount(50, expenseId2, eventId);
//        assertEquals(50, expense2.getAmount());
//
//        long expenseId3 = expense3.getId();
//        ctrl.changeAmount(75.30, expenseId3, eventId);
//        assertEquals(75.30, expense3.getAmount());
    }
    @Test
    public void changeAmountExpenseDoesntExist() {
        ResponseEntity<Void> res = stubbedCtrl.changeAmount(300, 100, 1);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }
    @Test
    public void changeAmountExpenseInvalid() {
        ResponseEntity<Void> res = stubbedCtrl.changeAmount(300, -20, eventId);
        assertFalse(amountChanged);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }
    @Test
    public void changeAmountEventInvalid() {
        ResponseEntity<Void> res = stubbedCtrl.changeAmount(300, 0, -100);
        assertFalse(amountChanged);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }
    @Test
    public void changeAmountEventDoesntExist() {
        ResponseEntity<Void> res = stubbedCtrl.changeAmount(300, 0, 100);
        assertFalse(amountChanged);
        assertEquals(res.getStatusCode(), NOT_FOUND);
    }
    @Test
    public void changeAmountLessThanZero() {
        ResponseEntity<Void> res = stubbedCtrl.changeAmount(-20, 0, 0);
        assertFalse(amountChanged);
        assertEquals(res.getStatusCode(), BAD_REQUEST);
    }

    /***
     * Tests for the changePayee method
     */

    @Test
    public void changePayeeTest(){
        Participant part = new Participant("joe", null, null, null);
        assertEquals(stubbedCtrl.changePayee(part, 0, 0).getStatusCode(), OK);
    }
    @Test
    public void changePayeeNull(){
        Participant part = null;
        ResponseEntity<Void> res = stubbedCtrl.changePayee(part, 1,1);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeNoNamer(){
        Participant part = new Participant("", null, null, null);
        ResponseEntity<Void> res = stubbedCtrl.changePayee(part,1, 1);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeNullNamer(){
        Participant part = new Participant(null, null, null, null);
        ResponseEntity<Void> res = stubbedCtrl.changePayee(part,1, 1);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeExpenseInvalid(){
        Participant part = new Participant("joe", null, null, null);
        ResponseEntity<Void> res = stubbedCtrl.changePayee(part, -100, 0);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeExpenseDoesntExist(){
        Participant part = new Participant("joe", null, null, null);
        ResponseEntity<Void> res = stubbedCtrl.changePayee(part, 100, 0);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void changePayeeEventInvalid(){
        Participant part = new Participant("joe", null, null, null);
        ResponseEntity<Void> res = stubbedCtrl.changePayee(part, 1, -100);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void changePayeeEventDoesntExist(){
        Participant part = new Participant("joe", null, null, null);
        ResponseEntity<Void> res = stubbedCtrl.changePayee(part, 1, 100);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    /***
     * Tests for the deleteExpense method
     */
    @Test
    public void deleteExpenseTest(){
        assertEquals(stubbedCtrl.deleteExpense(1, 1).getStatusCode(), OK);
        assertEquals(stubbedCtrl.deleteExpense(1, 1).getBody(), deletable);
    }
    @Test
    public void deleteExpenseDoesntExistTest(){
        ResponseEntity<Expense> res = stubbedCtrl.deleteExpense(100, 0);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }
    @Test
    public void deleteExpenseInvalidTest(){
        ResponseEntity<Expense> res = stubbedCtrl.deleteExpense(-100, 0);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void deleteEventInvalidTest(){
        ResponseEntity<Expense> res = stubbedCtrl.deleteExpense(1, -100);
        assertEquals(BAD_REQUEST, res.getStatusCode());
    }
    @Test
    public void deleteEventDoesntExistTest(){
        ResponseEntity<Expense> res = stubbedCtrl.deleteExpense(1, 100);
        assertEquals(NOT_FOUND, res.getStatusCode());
    }


    @Test
    public void lastActivityNotChangeTest(){
        Event event = eventRepo.getById(0L);
        Date tmpdate = event.getLastActivity();
        ctrl.getAllExpenses(0L);
        event = eventRepo.getById(0L);
        assertEquals(event.getLastActivity(),tmpdate);
    }
    @Test
    public void lastActivityAfterDeleteTest() throws InterruptedException {
        Event event = eventRepo.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        ctrl.deleteExpense(0L,0L);
        event = eventRepo.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }


    @Test
    public void lastActivityAddTest() throws InterruptedException {
        Event event = eventRepo.getById(0L);
        Date tmpdate = (Date) event.getLastActivity().clone();
        Thread.sleep(500);
        ctrl.add(0L,new Expense(600, "party2", "party2",
                null, null, null, null, new Participant("joe", null, null, null)));
        event = eventRepo.getById(0L);
        assertTrue(event.getLastActivity().after(tmpdate));
    }

}
