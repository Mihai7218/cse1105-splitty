package client.scenes;

import client.utils.ConfigInterface;
import client.utils.CurrencyConverter;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
class EditTransferCtrlTest {

    //Needed for the tests to run headless.
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless","true");
    }
    MainCtrl mainCtrl;
    ServerUtils serverUtils;
    ConfigInterface config;
    LanguageManager languageManager;
    Alert alert;
    @Mock
    AddTransferCtrl mockSut;
    EditTransferCtrl sut;

    Label header;

    Label transferFrom;
    ChoiceBox<Participant> from;
    Label transferTo;
    ChoiceBox<Participant> to;
    Label transferAmount;
    TextField amount;
    ChoiceBox<String> currencyVal;
    Label dateLabel;
    DatePicker date;
    Button cancel;
    Button confirm;
    CurrencyConverter currencyConverter;
    ObservableList of = FXCollections.observableArrayList();
    ObservableList oc = FXCollections.observableArrayList();
    ObservableList ot = FXCollections.observableArrayList();
    ObservableList noc = FXCollections.observableArrayList();
    Event testEvent;
    Participant p1;
    Participant p2;
    StompSession.Subscription sub;
    @Mock
    KeyEvent mockEvent = mock(KeyEvent.class);
    @Mock
    private Map<Participant, StompSession.Subscription> participantSubscriptionMap;


    @Start
    void setUp(Stage stage) {
        dateLabel = mock(Label.class);
        transferAmount = mock(Label.class);
        transferFrom = mock(Label.class);
        transferTo = mock(Label.class);
        header = mock(Label.class);
        mainCtrl = mock(MainCtrl.class);
        serverUtils = mock(ServerUtils.class);
        languageManager = mock(LanguageManager.class);
        currencyConverter = mock(CurrencyConverter.class);
        sut = new EditTransferCtrl(mainCtrl, config, languageManager,
                serverUtils, alert, currencyConverter);
        from = mock(ChoiceBox.class);
        currencyVal = mock(ChoiceBox.class);
        to = mock(ChoiceBox.class);
        amount = mock(TextField.class);
        date = mock(DatePicker.class);
        confirm = mock(Button.class);
        cancel = mock(Button.class);
//        mockSut = mock(EditTransferCtrl.class, withSettings().spiedInstance(sut).name("mockedService"));
        sub = mock(StompSession.Subscription.class);
        participantSubscriptionMap = mock(HashMap.class);

        sut.setParticipantSubscription(sub);
        sut.setParticipantSubscriptionMap(participantSubscriptionMap);
        sut.setCancel(cancel);
        sut.setConfirm(confirm);
        sut.setFrom(from);
        sut.setTo(to);
        sut.setTransferTo(transferTo);
        sut.setTransferAmount(transferAmount);
        sut.setTransferFrom(transferFrom);
        sut.setDateLabel(dateLabel);
        sut.setDate(date);
        sut.setHeader(header);
        sut.setCurrencyVal(currencyVal);
        sut.setAmount(amount);

        doNothing().when(confirm).setGraphic(any(Node.class));
        doNothing().when(cancel).setGraphic(any(Node.class));
        when(currencyVal.getItems()).thenReturn(oc);
        when(to.getItems()).thenReturn(ot);
        when(from.getItems()).thenReturn(of);

        testEvent = new Event("testEvent", null, null);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
        p1 = new Participant("bob", null, null, null);
        p2 = new Participant("jill", null, null, null);
        testEvent.addParticipant(p1);
        testEvent.addParticipant(p2);
        testEvent.setInviteCode(1);

    }

    @Test
    public void highlightTest(){
        doNothing().when(to).setStyle(any());
        doNothing().when(from).setStyle(any());
        doNothing().when(date).setStyle(any());
        doNothing().when(amount).setStyle(any());
        doNothing().when(currencyVal).setStyle(any());

        EditTransferCtrl spy = spy(sut);

        spy.highlightMissing(true, true, true, true, true);
        verify(to, times(1)).setStyle(any());
        verify(from, times(1)).setStyle(any());
        verify(amount, times(1)).setStyle(any());
        verify(date, times(1)).setStyle(any());
        verify(currencyVal, times(1)).setStyle(any());
    }

    @Test
    public void subscribeTest(){
        EditTransferCtrl spy = spy(sut);

        p1.setId(1);
        p2.setId(3);
        ParticipantPayment pp1 = new ParticipantPayment(p1,15);
        ParticipantPayment pp2 = new ParticipantPayment(p2,15);
        Expense testExpense = new Expense(15, "EUR","Transfer", "transfer", java.sql.Date.valueOf(LocalDate.now()),
                List.of(pp1,pp2),null,p1);
        sut.setExpense(testExpense);
        spy.setExpense(testExpense);
        when(participantSubscriptionMap.containsKey(any())).thenReturn(false);
        List<String> pathes = new ArrayList<>();
        List<Participant> participants = new ArrayList<>();
        doAnswer(invocation -> {
            Participant s = (Participant) (invocation.getArguments()[0]);
            participants.add(s);
            return null;
        }).when(participantSubscriptionMap).put(any(),any());
        doAnswer(invocation -> {
            String s = String.valueOf(invocation.getArguments()[0]);
            pathes.add(s);
            return null;
        }).when(serverUtils).registerForMessages(any(), any(),any());
        spy.subscribeToParticipant(p1);
        assertEquals(pathes.getFirst(), "/topic/events/1/participants/1");
        assertEquals(participants.getFirst(), p1);
        verify(participantSubscriptionMap, times(1)).put(any(),any());
    }

    @Test
    public void loadTest(){
        EditTransferCtrl spy = spy(sut);

        p1.setId(1);
        p2.setId(3);
        ParticipantPayment pp1 = new ParticipantPayment(p1,15);
        ParticipantPayment pp2 = new ParticipantPayment(p2,15);
        Expense testExpense = new Expense(15, "EUR","Transfer", "transfer", java.sql.Date.valueOf(LocalDate.now()),
                List.of(pp1,pp2),null,p1);
        sut.setExpense(testExpense);
        spy.setExpense(testExpense);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
        sut.setParticipantSubscription(null);
        spy.setParticipantSubscription(null);
        when(participantSubscriptionMap.containsKey(any())).thenReturn(true);
        List<String> pathes = new ArrayList<>();
        doAnswer(invocation -> {
            String s = String.valueOf(invocation.getArguments()[0]);
            pathes.add(s);
            return null;
        }).when(serverUtils).registerForMessages(any(), any(),any());
        spy.load();
        verify(spy, times(2)).subscribeToParticipant(any());
        assertEquals(pathes.getFirst(), "/topic/events/1/participants");
    }

    @Test
    public void doneTransferTest(){
        p1.setId(1);
        p2.setId(3);
        ParticipantPayment pp1 = new ParticipantPayment(p1,15);
        ParticipantPayment pp2 = new ParticipantPayment(p2,15);
        Expense testExpense = new Expense(15, "EUR","Transfer", "transfer", java.sql.Date.valueOf(LocalDate.now()),
                List.of(pp1,pp2),null,p1);
        sut.setExpense(testExpense);
        List<Expense> expenses = new ArrayList<>();
        doNothing().when(to).setStyle(anyString());
        doNothing().when(from).setStyle(anyString());
        doNothing().when(currencyVal).setStyle(anyString());
        doNothing().when(date).setStyle(anyString());
        doNothing().when(amount).setStyle(anyString());

        doNothing().when(to).setValue(any());
        doNothing().when(from).setValue(any());
        doNothing().when(currencyVal).setValue(any());
        doNothing().when(date).setValue(any());
        doNothing().when(amount).setText(any());
        when(amount.getText()).thenReturn("15");
        when(currencyVal.getValue()).thenReturn("EUR");
        when(to.getValue()).thenReturn(p1);
        when(from.getValue()).thenReturn(p2);
        when(date.getValue()).thenReturn(LocalDate.now());
        doAnswer(invocation -> {
            Expense expense = (Expense) invocation.getArguments()[1];
            expenses.add(expense);
            return null;
        }).when(serverUtils).updateExpense(anyInt(),any());
        doNothing().when(mainCtrl).showOverview();
        sut.doneTransfer();
        ParticipantPayment p = new ParticipantPayment(p1, 15);
        ParticipantPayment p3 = new ParticipantPayment(p2,15);
        Expense curr = new Expense(15, "EUR", "Transfer", "transfer", java.sql.Date.valueOf(LocalDate.now()), List.of(p, p3), null, p2);
        assertEquals(1, expenses.size());
        assertEquals(expenses.get(0), curr);

    }

    @Test
    public void initializeTest(){
        p1.setId(1);
        p2.setId(2);
        when(from.getValue()).thenReturn(p1);
        when(to.getValue()).thenReturn(p2);
        EditTransferCtrl spy = spy(sut);
        List<String> currencies = List.of("EUR", "USD", "CHF");
        when(currencyConverter.getCurrencies()).thenReturn(currencies);
        ObservableList<String> currVals = FXCollections.observableArrayList();
        when(currencyVal.getItems()).thenReturn(currVals);

        ObjectProperty<Participant> fromValueProperty = new SimpleObjectProperty<>();
        ObjectProperty<Participant> toValueProperty = new SimpleObjectProperty<>();

        when(from.valueProperty()).thenReturn(fromValueProperty);
        when(to.valueProperty()).thenReturn(toValueProperty);

        doNothing().when(from).setConverter(any());
        doNothing().when(to).setConverter(any());


        spy.initialize(mock(URL.class), mock(ResourceBundle.class));

        assertEquals(currencies, currVals);
        assertEquals(ot, List.of(p2));
        assertEquals(of, List.of(p1));
    }

    @Test
    public void refreshTest(){
        p1.setId(0);
        p2.setId(1);
        ParticipantPayment pp1 = new ParticipantPayment(p1,15);
        ParticipantPayment pp2 = new ParticipantPayment(p2,15);
        Expense testExpense = new Expense(15, "EUR","Transfer", "transfer", java.sql.Date.valueOf(LocalDate.now()),
                List.of(pp1,pp2),null,p1);
        EditTransferCtrl spy = spy(sut);
        spy.setExpense(testExpense);
        List<Date> dates = new ArrayList<>();
        List<Participant> payee = new ArrayList<>();
        List<Participant> receiver = new ArrayList<>();
        List<String> amounts = new ArrayList<>();
        List<String> currencies = new ArrayList<>();
        doAnswer(invocation -> {
            String d = String.valueOf( invocation.getArguments()[0]);
            currencies.add(d);
            when(mainCtrl.getEvent()).thenReturn(null);
            return null;
        }).when(currencyVal).setValue(any());
        doAnswer(invocation -> {
            String d = String.valueOf( invocation.getArguments()[0]);
            amounts.add(d);
            when(mainCtrl.getEvent()).thenReturn(null);
            return null;
        }).when(amount).setText(any());
        doAnswer(invocation -> {
            Participant d = (Participant)( invocation.getArguments()[0]);
            receiver.add(d);
            when(mainCtrl.getEvent()).thenReturn(null);
            return null;
        }).when(to).setValue(any());
        doAnswer(invocation -> {
            Participant d = (Participant)( invocation.getArguments()[0]);
            payee.add(d);
            when(mainCtrl.getEvent()).thenReturn(null);
            return null;
        }).when(from).setValue(any());
        doNothing().when(date).setValue(any());
        spy.refresh();

        assertEquals(to.getItems().size(), 2);
        assertEquals(from.getItems().size(), 2);
        assertEquals(from.getItems(), List.of(p1,p2));
        assertEquals(to.getItems(), List.of(p1,p2));
        assertEquals(payee.getFirst(), p1);
        assertEquals(receiver.getFirst(), p2);
        assertEquals(amounts.getFirst(), "15.0");
        assertEquals(currencies.getFirst(), "EUR");

    }


    @Test
    void testKeyPressedEscape() {
        EditTransferCtrl spies = spy(sut);

        when(mockEvent.getCode()).thenReturn(KeyCode.ESCAPE);
        spies.keyPressed(mockEvent);

        // Verify that the abort method is called
        verify(spies).cancel();
    }

    @Test
    void testKeyPressedMWithControl() {
        EditTransferCtrl spies = spy(sut);

        when(mockEvent.getCode()).thenReturn(KeyCode.M);
        when(mockEvent.isControlDown()).thenReturn(true);


        spies.keyPressed(mockEvent);
        verify(spies).startMenu();
    }

    @Test
    void testKeyPressedOWithControl() {
        EditTransferCtrl spies = spy(sut);

        when(mockEvent.getCode()).thenReturn(KeyCode.O);
        when(mockEvent.isControlDown()).thenReturn(true);


        spies.keyPressed(mockEvent);

        verify(spies).backToOverview();
    }

    @Test
    void testKeyPressedDefault() {
        EditTransferCtrl spies = spy(sut);
        when(mockEvent.getCode()).thenReturn(KeyCode.P);

        spies.keyPressed(mockEvent);

        verify(spies, never()).cancel();
        verify(spies, never()).startMenu();
        verify(spies, never()).backToOverview();
    }


    /*
        public boolean validate(String expensePriceText, LocalDate expenseDate){
        // Perform validation
        if( !expensePriceText.isEmpty() || expenseDate != null
                || currencyVal.getValue() != null || to.getValue() != null
                || from.getValue() != null){
            removeHighlight();
            highlightMissing(to.getValue()==null, from.getValue()==null,
                    expensePriceText.isEmpty(), expenseDate==null, currencyVal.getValue() == null);
            return false;
        }
        return true;
    }
     */

    @Test
    public void validateFalseTest(){
        when(from.getValue()).thenReturn(null);
        when(to.getValue()).thenReturn(p2);
        when(currencyVal.getValue()).thenReturn("EUR");

        EditTransferCtrl spy = spy(sut);

        assertFalse(spy.validate("price", LocalDate.now()));


        verify(spy, times(1)).highlightMissing(anyBoolean(),anyBoolean(),anyBoolean(),anyBoolean(),anyBoolean());
        verify(spy, times(1)).removeHighlight();

    }

    @Test
    public void validateTrue(){
        when(from.getValue()).thenReturn(p1);
        when(to.getValue()).thenReturn(p2);
        when(currencyVal.getValue()).thenReturn("EUR");
        EditTransferCtrl spy = spy(sut);

        assertTrue(spy.validate("price", LocalDate.now()));


        verify(spy, times(0)).highlightMissing(anyBoolean(),anyBoolean(),anyBoolean(),anyBoolean(),anyBoolean());
        verify(spy, times(0)).removeHighlight();

    }

    @Test
    public void modifyTransferTest(){
        p1.setId(0);
        p2.setId(1);
        ParticipantPayment pp1 = new ParticipantPayment(p1,15);
        ParticipantPayment pp2 = new ParticipantPayment(p2,15);
        Expense testExpense = new Expense(15, "EUR","Transfer", "transfer", java.sql.Date.valueOf(LocalDate.now()),
                List.of(pp1,pp2),null,p1);
        EditTransferCtrl spy = spy(sut);
        spy.setExpense(testExpense);
        double expensePrice = 2500;
        LocalDate expenseDate = LocalDate.now();

        when(from.getValue()).thenReturn(p2);
        when(currencyVal.getValue()).thenReturn("USD");
        spy.modifyTransfer(expensePrice,expenseDate,pp2,pp1);

        assertEquals(testExpense.getAmount(), 25);
        assertEquals(testExpense.getCurrency(), "USD");
        assertEquals(testExpense.getSplit(), List.of(pp2,pp1));
        assertEquals(testExpense.getPayee(), p2);
    }

}