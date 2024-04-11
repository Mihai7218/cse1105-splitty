package client.scenes;

import client.utils.ConfigInterface;
import client.utils.CurrencyConverter;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.*;
import jakarta.ws.rs.WebApplicationException;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
class OverviewCtrlTest {
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    OverviewCtrl sut;
    LanguageManager languageManager;
    ConfigInterface config;
    ServerUtils server;
    MainCtrl mainCtrl;
    CurrencyConverter currencyConverter;
    ListView<Expense> all;
    ListView<Expense> from;
    ListView<Expense> including;
    ChoiceBox<Participant> expenseParticipant;
    ListView<Participant> participants;
    Label sumExpense;
    Map<Expense, StompSession.Subscription> expenseSubscriptionMap;
    Map<Participant, StompSession.Subscription> participantSubscriptionMap;
    Map<Tag, StompSession.Subscription> tagSubscriptionMap;
    Label participantFrom;
    Label participantIncluding;
    Participant bob = new Participant("Bob", null, null, null);
    Participant mary = new Participant("Mary", null, null, null);
    Participant tom = new Participant("Tom", null, null, null);
    Expense expense1 = new Expense(10.0, "EUR", "title", "desc", java.sql.Date.valueOf("2024-01-01"), List.of(
            new ParticipantPayment(bob, 5.0),
            new ParticipantPayment(mary, 5.0)
    ), null, bob);
    Expense expense2 = new Expense(15.0, "EUR", "title2", "desc", java.sql.Date.valueOf("2023-01-01"),  List.of(
            new ParticipantPayment(bob, 5.0),
            new ParticipantPayment(mary, 5.0),
            new ParticipantPayment(tom, 5.0)
    ), null, mary);

    @Start
    void setup(Stage ignored) {
        expense1.setId(1);
        expense2.setId(2);
        bob.setId(1);
        mary.setId(2);
        tom.setId(3);

        config = new TestConfig();
        languageManager = new LanguageManager(config);
        server = mock(ServerUtils.class);
        mainCtrl = new MainCtrl(config, languageManager);
        currencyConverter = mock(CurrencyConverter.class);
        sut = new OverviewCtrl(languageManager, config, server, mainCtrl, currencyConverter);
        all = new ListView<>();
        sut.setAll(all);
        from = new ListView<>();
        sut.setFrom(from);
        including = new ListView<>();
        sut.setIncluding(including);
        expenseParticipant = new ChoiceBox<>();
        expenseParticipant.getItems().addAll(bob, mary, tom);
        sut.setExpenseparticipants(expenseParticipant);
        participants = new ListView<>();
        sut.setParticipants(participants);
        sumExpense = new Label();
        sut.setSumExpense(sumExpense);
        expenseSubscriptionMap = new HashMap<>();
        sut.setExpenseSubscriptionMap(expenseSubscriptionMap);
        participantSubscriptionMap = new HashMap<>();
        sut.setParticipantSubscriptionMap(participantSubscriptionMap);
        tagSubscriptionMap = new HashMap<>();
        sut.setTagSubscriptionMap(tagSubscriptionMap);
        participantFrom = new Label();
        sut.setParticipantFrom(participantFrom);
        participantIncluding = new Label();
        sut.setParticipantIncluding(participantIncluding);
    }

    @Test
    void populateExpensesSuccess() {
        Event event = new Event("title", new Date(), new Date());
        event.getParticipantsList().addAll(List.of(bob, mary, tom));
        event.getExpensesList().addAll(List.of(expense1, expense2));
        event.setInviteCode(1);
        mainCtrl.setEvent(event);
        when(server.getAllExpenses(1)).thenReturn(event.getExpensesList());
        when(server.getEvent(1)).thenReturn(event);
        expenseParticipant.setValue(tom);
        when(currencyConverter.convert(any(), any(), any(), anyDouble())).then(mock -> mock.getArguments()[3]);

        sut.populateExpenses();

        assertEquals(event.getExpensesList(), all.getItems());
        assertEquals(List.of(), from.getItems());
        assertEquals(List.of(expense2), including.getItems());
        assertEquals(Set.of(expense1, expense2), expenseSubscriptionMap.keySet());
        assertEquals(Set.of(bob, tom, mary), participantSubscriptionMap.keySet());
        assertEquals("25.00 EUR", sumExpense.getText());
    }

    @Test
    void populateExpensesMainCtrlNullEvent() {
        expenseParticipant.setValue(tom);
        when(currencyConverter.convert(any(), any(), any(), anyDouble())).then(mock -> mock.getArguments()[3]);

        sut.populateExpenses();

        assertEquals(List.of(), all.getItems());
        assertEquals(List.of(), from.getItems());
        assertEquals(List.of(), including.getItems());
        assertEquals(Set.of(), expenseSubscriptionMap.keySet());
        assertEquals(Set.of(), participantSubscriptionMap.keySet());
        assertEquals("", sumExpense.getText());
    }

    @Test
    void populateExpensesNullExpenseList() {
        Event event = new Event("title", new Date(), new Date());
        event.getParticipantsList().addAll(List.of(bob, mary, tom));
        event.setInviteCode(1);
        event.setExpensesList(null);
        mainCtrl.setEvent(event);
        when(server.getAllExpenses(1)).thenReturn(event.getExpensesList());
        when(server.getEvent(1)).thenReturn(event);
        expenseParticipant.setValue(tom);
        when(currencyConverter.convert(any(), any(), any(), anyDouble())).then(mock -> mock.getArguments()[3]);

        sut.populateExpenses();

        assertEquals(List.of(), all.getItems());
        assertEquals(List.of(), from.getItems());
        assertEquals(List.of(), including.getItems());
        assertEquals(Set.of(), expenseSubscriptionMap.keySet());
        assertEquals(Set.of(), participantSubscriptionMap.keySet());
        assertEquals("", sumExpense.getText());
    }

    @Test
    void populateExpensesWebApplicationException() {
        Event event = new Event("title", new Date(), new Date());
        event.getParticipantsList().addAll(List.of(bob, mary, tom));
        event.getExpensesList().addAll(List.of(expense1, expense2));
        event.setInviteCode(1);
        mainCtrl.setEvent(event);
        when(server.getAllExpenses(1)).thenThrow(WebApplicationException.class);
        when(server.getEvent(1)).thenThrow(WebApplicationException.class);
        expenseParticipant.setValue(tom);
        when(currencyConverter.convert(any(), any(), any(), anyDouble())).then(mock -> mock.getArguments()[3]);

        sut.populateExpenses();

        assertEquals(List.of(), all.getItems());
        assertEquals(List.of(), from.getItems());
        assertEquals(List.of(), including.getItems());
        assertEquals(Set.of(), expenseSubscriptionMap.keySet());
        assertEquals(Set.of(), participantSubscriptionMap.keySet());
        assertEquals("0.00 EUR", sumExpense.getText());
    }

    @Test
    void populateExpensesAlreadyThere() {
        Event event = new Event("title", new Date(), new Date());
        event.getParticipantsList().addAll(List.of(bob, mary, tom));
        event.getExpensesList().addAll(List.of(expense1, expense2));
        event.setInviteCode(1);
        mainCtrl.setEvent(event);
        when(server.getAllExpenses(1)).thenReturn(event.getExpensesList());
        when(server.getEvent(1)).thenReturn(event);
        expenseParticipant.setValue(tom);
        when(currencyConverter.convert(any(), any(), any(), anyDouble())).then(mock -> mock.getArguments()[3]);
        all.getItems().add(expense1);
        participants.getItems().add(bob);
        expenseSubscriptionMap.put(expense1, null);
        participantSubscriptionMap.put(bob, null);

        sut.populateExpenses();

        assertEquals(event.getExpensesList(), all.getItems());
        assertEquals(List.of(), from.getItems());
        assertEquals(List.of(expense2), including.getItems());
        assertEquals(Set.of(expense1, expense2), expenseSubscriptionMap.keySet());
        assertEquals(Set.of(bob, tom, mary), participantSubscriptionMap.keySet());
        assertEquals("25.00 EUR", sumExpense.getText());
    }

    @Test
    void populateParticipants() {
    }

    @Test
    void refresh() {
    }

    @Test
    void changeLanguage() {
    }

    @Test
    void addParticipant() {
    }

    @Test
    void addExpense() {
    }

    @Test
    void startMenu() {
    }

    @Test
    void sendInvites() {
    }

    @Test
    void statistics() {
    }

    @Test
    void settleDebts() {
    }

    @Test
    void changeTitle() {
    }

    @Test
    void initialize() {
    }

    @Test
    void filterViews() {
    }

    @Test
    void removeParticipant() {
    }

    @Test
    void getSum() {
    }

    @Test
    void getLanguageManager() {
    }

    @Test
    void setLanguageManager() {
    }

    @Test
    void getMainCtrl() {
    }

    @Test
    void getLanguages() {
    }

    @Test
    void getConfig() {
    }

    @Test
    void getNotificationLabel() {
    }

    @Test
    void languageManagerProperty() {
    }

    @Test
    void getParticipantsListView() {
    }

    @Test
    void getParticipants() {
    }

    @Test
    void updateLanguageComboBox() {
    }

    @Test
    void settings() {
    }

    @Test
    void getExpenseSubscriptionMap() {
    }

    @Test
    void keyPressed() {
    }
}