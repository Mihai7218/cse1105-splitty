package client.scenes;

import client.utils.ConfigInterface;
import client.utils.CurrencyConverter;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(ApplicationExtension.class)
class AddTransferCtrlTest {

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
    AddTransferCtrl sut;

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
        sut = new AddTransferCtrl(mainCtrl, config, languageManager,
                serverUtils, alert, currencyConverter);
        from = mock(ChoiceBox.class);
        currencyVal = mock(ChoiceBox.class);
        to = mock(ChoiceBox.class);
        amount = mock(TextField.class);
        date = mock(DatePicker.class);
        confirm = mock(Button.class);
        cancel = mock(Button.class);
        mockSut = mock(AddTransferCtrl.class, withSettings().spiedInstance(sut).name("mockedService"));
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

    }

    @Test
    public void abortTest(){
        doNothing().when(to).setStyle(anyString());
        doNothing().when(from).setStyle(anyString());
        doNothing().when(currencyVal).setStyle(anyString());
        doNothing().when(date).setStyle(anyString());
        doNothing().when(amount).setStyle(anyString());
        doNothing().when(sub).unsubscribe();
        doAnswer(invocation -> {
//            Map.Entry<Participant, StompSession.Subscription> entry =
//                    (Map.Entry<Participant, StompSession.Subscription>) invocation.getArguments()[0];
            // Call unsubscribe() method on each value
            sub.unsubscribe();
            return null;
        }).when(participantSubscriptionMap).forEach(any());
        sut.abort();
        verify(participantSubscriptionMap, times(1)).forEach(any());


    }

    @Test
    public void populateTo(){
        when(from.getValue()).thenReturn(p1);
        when(to.getValue()).thenReturn(null);
        sut.populateToBox();
        assertEquals(List.of(p2), to.getItems());
    }

    @Test
    public void populateFrom(){
        when(to.getValue()).thenReturn(p1);
        when(from.getValue()).thenReturn(null);
        sut.populateFromBox();
        assertEquals(List.of(p2), from.getItems());
    }
    /*
    !expensePriceText.isEmpty() || expenseDate != null
                || currencyVal.getValue() != null || to.getValue() != null
                || from.getValue() != null
     */
    @Test
    public void validateTestNotValid(){
//        doNothing().when(mockSut).highlightMissing(anyBoolean(), anyBoolean(), anyBoolean(),anyBoolean(), anyBoolean());
//        doNothing().when(mockSut).removeHighlight();
        doNothing().when(to).setStyle(anyString());
        doNothing().when(from).setStyle(anyString());
        doNothing().when(currencyVal).setStyle(anyString());
        doNothing().when(date).setStyle(anyString());
        doNothing().when(amount).setStyle(anyString());
        when(currencyVal.getValue()).thenReturn(null);
        when(to.getValue()).thenReturn(null);
        assertFalse(sut.validate("15", LocalDate.now()));
//        verify(mockSut, times(1)).removeHighlight();
//        verify(mockSut, times(1)).highlightMissing(anyBoolean(), anyBoolean(),anyBoolean(),anyBoolean(),anyBoolean());
    }

    @Test
    public void refreshTest(){
        List<Date> dates = new ArrayList<>();
        doAnswer(invocation -> {
            // Retrieve the second parameter passed to addExpense
            Date d = java.sql.Date.valueOf((LocalDate) invocation.getArguments()[0]);
            // Add the expense to the list
            dates.add(d);
            when(mainCtrl.getEvent()).thenReturn(null);
            // Return null as addExpense might have void return type
            return null;
        }).when(date).setValue(any());
        sut.refresh();
        assertEquals(to.getItems().size(), 2);
        assertEquals(from.getItems().size(), 2);
        assertEquals(from.getItems(), List.of(p1,p2));
        assertEquals(to.getItems(), List.of(p1,p2));
        assertEquals(dates.getFirst(), java.sql.Date.valueOf(LocalDate.now()));

    }


    @Test
    public void validateTestValid(){
        doNothing().when(to).setStyle(anyString());
        doNothing().when(from).setStyle(anyString());
        doNothing().when(currencyVal).setStyle(anyString());
        doNothing().when(date).setStyle(anyString());
        doNothing().when(amount).setStyle(anyString());
        when(currencyVal.getValue()).thenReturn("15");
        when(to.getValue()).thenReturn(p1);
        when(from.getValue()).thenReturn(p2);
        assertEquals(true, sut.validate("15", LocalDate.now()));
    }

    @Test
    public void addValidTransfer(){
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
        }).when(serverUtils).addExpense(anyInt(),any());
        doNothing().when(mainCtrl).showOverview();
        sut.doneTransfer();
        ParticipantPayment p = new ParticipantPayment(p1, 15);
        ParticipantPayment p3 = new ParticipantPayment(p2,15);
        Expense curr = new Expense(15, "EUR", "Transfer", "transfer", java.sql.Date.valueOf(LocalDate.now()), List.of(p, p3), null, p2);
        assertEquals(1, expenses.size());
        assertEquals(expenses.get(0), curr);

    }




}