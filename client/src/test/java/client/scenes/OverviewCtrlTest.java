package client.scenes;

import client.commands.ICommand;
import client.utils.*;
import commons.*;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableMap;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

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
    LanguageComboBox languages;
    Label sumLabel;
    Tab fromTab;
    Tab includingTab;
    Button addParticipant;
    Button settleDebts;
    Button settings;
    Button addExpenseButton;
    Button addTransferButton;
    Button showStatisticsButton;
    Button cancel;
    Button undoButton;
    Label title;
    Label code;
    Alert alert;
    Participant bob = new Participant("Bob", null, null, null);
    Participant mary = new Participant("Mary", null, null, null);
    Participant tom = new Participant("Tom", null, null, null);
    Expense expense1 = new Expense(10.0, "EUR", "title", "desc", java.sql.Date.valueOf("2024-01-01"), new ArrayList<>(List.of(
            new ParticipantPayment(bob, 5.0),
            new ParticipantPayment(mary, 5.0)
    )), null, bob);
    Expense expense2 = new Expense(15.0, "EUR", "title2", "desc", java.sql.Date.valueOf("2023-01-01"), new ArrayList<>(List.of(
            new ParticipantPayment(bob, 5.0),
            new ParticipantPayment(mary, 5.0),
            new ParticipantPayment(tom, 5.0)
    )), null, mary);

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
        alert = mock(Alert.class);
        sut = new OverviewCtrl(languageManager, config, server, mainCtrl, currencyConverter, alert);
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
        participantFrom = new Label();
        sut.setParticipantFrom(participantFrom);
        participantIncluding = new Label();
        sut.setParticipantIncluding(participantIncluding);
        languages = new LanguageComboBox();
        sut.setLanguages(languages);
        sumLabel = new Label();
        sut.setSumLabel(sumLabel);
        fromTab = new Tab();
        sut.setFromTab(fromTab);
        includingTab = new Tab();
        sut.setIncludingTab(includingTab);
        addParticipant = new Button();
        sut.setAddparticipant(addParticipant);
        settleDebts = new Button();
        sut.setSettleDebts(settleDebts);
        settings = new Button();
        sut.setSettings(settings);
        addExpenseButton = new Button();
        sut.setAddExpenseButton(addExpenseButton);
        addTransferButton = new Button();
        sut.setAddTransferButton(addTransferButton);
        showStatisticsButton = new Button();
        sut.setShowStatisticsButton(showStatisticsButton);
        cancel = new Button();
        sut.setCancel(cancel);
        undoButton = new Button();
        sut.setUndoButton(undoButton);
        title = new Label();
        sut.setTitle(title);
        code = new Label();
        sut.setCode(code);

        sut.initialize(mock(URL.class), mock(ResourceBundle.class));

        expenseSubscriptionMap = new HashMap<>();
        sut.setExpenseSubscriptionMap(expenseSubscriptionMap);
        participantSubscriptionMap = new HashMap<>();
        sut.setParticipantSubscriptionMap(participantSubscriptionMap);
        tagSubscriptionMap = new HashMap<>();
        sut.setTagSubscriptionMap(tagSubscriptionMap);
    }

    @Test
    void populateExpensesSuccess() {
        Event event = getEvent();
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
        Event event = getEvent();
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
        Event event = getEvent();
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
    void populateParticipantsNullEvent() {
        expenseParticipant.getItems().clear();
        participants.getItems().clear();

        sut.populateParticipants();

        verify(server, never()).getAllParticipants(anyInt());
        assertTrue(participants.getItems().isEmpty());
        assertTrue(expenseParticipant.getItems().isEmpty());
    }

    @Test
    void populateParticipantsNullPList() {
        Event event = getEvent();
        expenseParticipant.getItems().clear();
        participants.getItems().clear();
        mainCtrl.setEvent(event);
        event.setParticipantsList(null);

        sut.populateParticipants();

        verify(server, never()).getAllParticipants(anyInt());
        assertTrue(participants.getItems().isEmpty());
        assertTrue(expenseParticipant.getItems().isEmpty());
    }

    @Test
    void populateParticipantsWAException() {
        Event event = getEvent();
        expenseParticipant.getItems().clear();
        participants.getItems().clear();
        mainCtrl.setEvent(event);
        event.setParticipantsList(new ArrayList<>());
        when(server.getAllParticipants(anyInt())).thenThrow(WebApplicationException.class);

        sut.populateParticipants();

        verify(server).getAllParticipants(1);
        assertTrue(participants.getItems().isEmpty());
        assertTrue(expenseParticipant.getItems().isEmpty());
    }

    @Test
    void populateParticipantsSuccess() {
        Event event = getEvent();
        expenseParticipant.getItems().clear();
        participants.getItems().clear();
        mainCtrl.setEvent(event);
        event.setParticipantsList(new ArrayList<>());
        when(server.getAllParticipants(1)).thenReturn(List.of(bob, tom, mary));

        sut.populateParticipants();

        verify(server).getAllParticipants(1);
        assertEquals(List.of(bob, tom, mary), participants.getItems());
        assertEquals(List.of(bob, tom, mary), expenseParticipant.getItems());
    }

    @Test
    void populateParticipantsSuccessEPSet() {
        Event event = getEvent();
        expenseParticipant.getItems().clear();
        participants.getItems().clear();
        mainCtrl.setEvent(event);
        event.setParticipantsList(new ArrayList<>());
        when(server.getAllParticipants(1)).thenReturn(List.of(bob, tom, mary));
        expenseParticipant.setValue(tom);

        sut.populateParticipants();

        verify(server).getAllParticipants(1);
        assertEquals(List.of(bob, tom, mary), participants.getItems());
        assertEquals(List.of(bob, tom, mary), expenseParticipant.getItems());
        assertEquals(tom, expenseParticipant.getValue());
    }

    @Test
    void refreshEmptyHistory() {
        Stack<ICommand> history = new Stack<>();
        mainCtrl.setHistory(history);

        sut.refresh();

        assertFalse(undoButton.isVisible());
    }

    @Test
    void refreshWithEvent() {
        Event event = getEvent();
        event.setTagsList(new ArrayList<>(List.of(new Tag("food", "red"),
                new Tag("drinks", "blue"))));
        mainCtrl.setEvent(event);

        sut.refresh();

        assertEquals("title", title.getText());
        assertEquals("1", code.getText());
    }

    @Test
    void refreshEventSubscription() throws InterruptedException {
        Event event = getEvent();
        event.setTagsList(new ArrayList<>(List.of(new Tag("food", "red"),
                new Tag("drinks", "blue"))));
        mainCtrl.setEvent(event);
        AtomicReference<Consumer<Event>> lambda = new AtomicReference<>();
        when(server.registerForMessages(any(), any(), any())).then(mock -> {
            if (mock.getArgument(1).equals(Event.class))
                lambda.set(mock.getArgument(2));
            return null;
        });

        sut.refresh();

        assertEquals("title", title.getText());
        assertEquals("1", code.getText());

        lambda.get().accept(new Event("title2", new Date(), new Date()));
        Thread.sleep(100);

        assertEquals("title2", title.getText());
        assertEquals("title2", event.getTitle());
    }

    @Test
    void refreshExpenseSubscription() throws InterruptedException {
        Event event = getEvent();
        mainCtrl.setEvent(event);
        AtomicReference<Consumer<Expense>> lambda = new AtomicReference<>();
        when(server.registerForMessages(any(), any(), any())).then(mock -> {
            if (mock.getArgument(0).equals("/topic/events/1/expenses")
                    && mock.getArgument(1).equals(Expense.class))
                lambda.set(mock.getArgument(2));
            return null;
        });
        Expense expense3 = new Expense(50.0, "CHF", "exp3", "desc", java.sql.Date.valueOf("2025-01-01"), List.of(
                new ParticipantPayment(tom, 25.0),
                new ParticipantPayment(bob, 25.0)
        ), null, tom);
        DebtsCtrl debtsCtrl = mock(DebtsCtrl.class);
        mainCtrl.setDebtsCtrl(debtsCtrl);
        when(currencyConverter.convert(any(), any(), any(), anyDouble())).then(mock -> mock.getArgument(3));

        sut.refresh();

        assertFalse(all.getItems().contains(expense3));
        assertFalse(event.getExpensesList().contains(expense3));
        assertEquals("", sumExpense.getText());
        assertFalse(expenseSubscriptionMap.containsKey(expense3));

        lambda.get().accept(expense3);
        Thread.sleep(100);

        assertTrue(all.getItems().contains(expense3));
        assertTrue(event.getExpensesList().contains(expense3));
        assertEquals("75.00 EUR", sumExpense.getText());
        assertTrue(expenseSubscriptionMap.containsKey(expense3));
        verify(debtsCtrl).refresh();
    }

    @Test
    void refreshParticipantSubscription() throws InterruptedException {
        Event event = getEvent();
        mainCtrl.setEvent(event);
        AtomicReference<Consumer<Participant>> lambda = new AtomicReference<>();
        when(server.registerForMessages(any(), any(), any())).then(mock -> {
            if (mock.getArgument(0).equals("/topic/events/1/participants")
                    && mock.getArgument(1).equals(Participant.class))
                lambda.set(mock.getArgument(2));
            return null;
        });
        Participant coati = new Participant("Coati", null, null, null);
        coati.setId(42);
        DebtsCtrl debtsCtrl = mock(DebtsCtrl.class);
        mainCtrl.setDebtsCtrl(debtsCtrl);
        when(server.getAllParticipants(1)).thenReturn(List.of(coati));

        sut.refresh();

        assertFalse(participants.getItems().contains(coati));
        assertFalse(event.getParticipantsList().contains(coati));
        assertFalse(participantSubscriptionMap.containsKey(coati));

        lambda.get().accept(coati);
        Thread.sleep(100);

        assertTrue(participants.getItems().contains(coati));
        assertTrue(expenseParticipant.getItems().contains(coati));
        assertTrue(event.getParticipantsList().contains(coati));
        assertTrue(participantSubscriptionMap.containsKey(coati));
        verify(debtsCtrl).refresh();
    }

    @Test
    void refreshTagSubscription() throws InterruptedException {
        Event event = getEvent();
        mainCtrl.setEvent(event);
        AtomicReference<Consumer<Tag>> lambda = new AtomicReference<>();
        when(server.registerForMessages(any(), any(), any())).then(mock -> {
            if (mock.getArgument(0).equals("/topic/events/1/tags")
                    && mock.getArgument(1).equals(Tag.class))
                lambda.set(mock.getArgument(2));
            return null;
        });
        Tag food = new Tag("food", "red");
        food.setId(42);

        sut.refresh();

        assertFalse(tagSubscriptionMap.containsKey(food));

        lambda.get().accept(food);
        Thread.sleep(100);

        assertTrue(tagSubscriptionMap.containsKey(food));
    }

    @Test
    void participantSubscription() throws InterruptedException {
        Event event = getEvent();
        mainCtrl.setEvent(event);
        AtomicReference<Consumer<Participant>> lambda = new AtomicReference<>();
        when(server.registerForMessages(any(), any(), any())).then(mock -> {
            if (mock.getArgument(0).equals("/topic/events/1/participants/1")
                    && mock.getArgument(1).equals(Participant.class))
                lambda.set(mock.getArgument(2));
            return null;
        });
        DebtsCtrl debtsCtrl = mock(DebtsCtrl.class);
        mainCtrl.setDebtsCtrl(debtsCtrl);
        when(server.getAllParticipants(1)).thenReturn(event.getParticipantsList());
        when(server.getEvent(1)).thenReturn(event);

        sut.populateExpenses();

        assertTrue(event.getParticipantsList().contains(bob));

        lambda.get().accept(new Participant("title", "", "", ""));
        Thread.sleep(100);

        assertFalse(event.getParticipantsList().contains(bob));
        assertTrue(event.getParticipantsList().contains(new Participant()));
        verify(debtsCtrl).refresh();
    }

    @Test
    void participantSubscriptionDeleted() throws InterruptedException {
        Event event = getEvent();
        mainCtrl.setEvent(event);
        AtomicReference<Consumer<Participant>> lambda = new AtomicReference<>();
        when(server.registerForMessages(any(), any(), any())).then(mock -> {
            if (mock.getArgument(0).equals("/topic/events/1/participants/1")
                    && mock.getArgument(1).equals(Participant.class))
                lambda.set(mock.getArgument(2));
            return null;
        });
        DebtsCtrl debtsCtrl = mock(DebtsCtrl.class);
        mainCtrl.setDebtsCtrl(debtsCtrl);
        when(server.getAllParticipants(1)).thenReturn(event.getParticipantsList());
        when(server.getEvent(1)).thenReturn(event);

        sut.populateExpenses();

        assertTrue(event.getParticipantsList().contains(bob));

        lambda.get().accept(new Participant("title", "", "deleted", ""));
        Thread.sleep(100);

        assertFalse(event.getParticipantsList().contains(bob));
        assertFalse(event.getParticipantsList().contains(new Participant()));
        verify(debtsCtrl).refresh();
    }

    @Test
    void tagSubscription() throws InterruptedException {
        Event event = getEvent();
        Tag food = new Tag("food", "red");
        food.setId(1);
        Tag drinks = new Tag("drinks", "blue");
        drinks.setId(2);
        event.setTagsList(new ArrayList<>(List.of(food, drinks)));
        mainCtrl.setEvent(event);
        AtomicReference<Consumer<Tag>> lambda = new AtomicReference<>();
        when(server.registerForMessages(any(), any(), any())).then(mock -> {
            if (mock.getArgument(0).equals("/topic/events/1/tags/1")
                    && mock.getArgument(1).equals(Tag.class))
                lambda.set(mock.getArgument(2));
            return null;
        });

        sut.refresh();

        assertTrue(event.getTagsList().contains(food));

        lambda.get().accept(new Tag());
        Thread.sleep(100);

        assertFalse(event.getTagsList().contains(food));
        assertTrue(event.getTagsList().contains(new Tag()));
    }

    @Test
    void tagSubscriptionDeleted() throws InterruptedException {
        Event event = getEvent();
        Tag food = new Tag("food", "red");
        food.setId(1);
        Tag drinks = new Tag("drinks", "blue");
        drinks.setId(2);
        event.setTagsList(new ArrayList<>(List.of(food, drinks)));
        mainCtrl.setEvent(event);
        AtomicReference<Consumer<Tag>> lambda = new AtomicReference<>();
        when(server.registerForMessages(any(), any(), any())).then(mock -> {
            if (mock.getArgument(0).equals("/topic/events/1/tags/1")
                    && mock.getArgument(1).equals(Tag.class))
                lambda.set(mock.getArgument(2));
            return null;
        });

        sut.refresh();

        assertTrue(event.getTagsList().contains(food));

        lambda.get().accept(new Tag("", "deleted"));
        Thread.sleep(100);

        assertFalse(event.getTagsList().contains(food));
        assertFalse(event.getTagsList().contains(new Tag()));
    }

    @Test
    void expenseSubscription() throws InterruptedException {
        Event event = getEvent();
        mainCtrl.setEvent(event);
        AtomicReference<Consumer<Expense>> lambda = new AtomicReference<>();
        when(server.registerForMessages(any(), any(), any())).then(mock -> {
            if (mock.getArgument(0).equals("/topic/events/1/expenses/1")
                    && mock.getArgument(1).equals(Expense.class))
                lambda.set(mock.getArgument(2));
            return null;
        });
        when(server.getAllExpenses(1)).thenReturn(event.getExpensesList());
        when(server.getEvent(1)).thenReturn(event);
        when(currencyConverter.convert(any(), any(), any(), anyDouble())).then(mock -> mock.getArguments()[3]);
        DebtsCtrl debtsCtrl = mock(DebtsCtrl.class);
        mainCtrl.setDebtsCtrl(debtsCtrl);

        sut.populateExpenses();

        assertTrue(all.getItems().contains(expense1));
        assertEquals("25.00 EUR", sumExpense.getText());

        Expense expense3 = new Expense(50.0, "CHF", "exp3", "desc", java.sql.Date.valueOf("2025-01-01"), List.of(
                new ParticipantPayment(tom, 25.0),
                new ParticipantPayment(bob, 25.0)
        ), null, tom);
        expense3.setId(3);
        lambda.get().accept(expense3);
        Thread.sleep(100);

        assertFalse(all.getItems().contains(expense1));
        assertFalse(event.getExpensesList().contains(expense1));
        assertTrue(all.getItems().contains(expense3));
        assertTrue(event.getExpensesList().contains(expense3));
        assertEquals("65.00 EUR", sumExpense.getText());
    }

    @Test
    void expenseSubscriptionDeleted() throws InterruptedException {
        Event event = getEvent();
        mainCtrl.setEvent(event);
        AtomicReference<Consumer<Expense>> lambda = new AtomicReference<>();
        when(server.registerForMessages(any(), any(), any())).then(mock -> {
            if (mock.getArgument(0).equals("/topic/events/1/expenses/1")
                    && mock.getArgument(1).equals(Expense.class))
                lambda.set(mock.getArgument(2));
            return null;
        });
        when(server.getAllExpenses(1)).thenReturn(event.getExpensesList());
        when(server.getEvent(1)).thenReturn(event);
        when(currencyConverter.convert(any(), any(), any(), anyDouble())).then(mock -> mock.getArguments()[3]);
        DebtsCtrl debtsCtrl = mock(DebtsCtrl.class);
        mainCtrl.setDebtsCtrl(debtsCtrl);

        sut.populateExpenses();

        assertTrue(all.getItems().contains(expense1));
        assertEquals("25.00 EUR", sumExpense.getText());

        Expense expense3 = new Expense(50.0, "CHF", "exp3", "deleted", java.sql.Date.valueOf("2025-01-01"), List.of(
                new ParticipantPayment(tom, 25.0),
                new ParticipantPayment(bob, 25.0)
        ), null, tom);
        expense3.setId(3);
        lambda.get().accept(expense3);
        Thread.sleep(100);

        assertFalse(all.getItems().contains(expense1));
        assertFalse(event.getExpensesList().contains(expense1));
        assertFalse(all.getItems().contains(expense3));
        assertFalse(event.getExpensesList().contains(expense3));
        assertEquals("15.00 EUR", sumExpense.getText());
    }

    @Test
    void addParticipant() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        sut2.addParticipant();
        verify(mainCtrl2).showParticipant();
    }

    @Test
    void keyPressedCTRLP() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getCode()).thenReturn(KeyCode.P);
        when(keyEvent.isControlDown()).thenReturn(true);
        sut2.keyPressed(keyEvent);
        verify(mainCtrl2).showParticipant();
    }

    @Test
    void addTransfer() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        sut2.setParticipants(participants);
        participants.getItems().addAll(List.of(bob, tom, mary));
        sut2.addTransfer();
        verify(mainCtrl2).showTransfer();
    }

    @Test
    void addTransferZeroParticipants() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        StringProperty sp = new SimpleStringProperty();
        when(alert.contentTextProperty()).thenReturn(sp);
        sut2.setParticipants(participants);
        sut2.addTransfer();
        verify(mainCtrl2, never()).showTransfer();
        verify(alert).contentTextProperty();
        verify(alert).show();
    }

    @Test
    void addExpense() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        sut2.setParticipants(participants);
        participants.getItems().addAll(List.of(bob, tom, mary));
        sut2.addExpense();
        verify(mainCtrl2).showAddExpense();
    }

    @Test
    void addExpenseZeroParticipants() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        StringProperty sp = new SimpleStringProperty();
        when(alert.contentTextProperty()).thenReturn(sp);
        sut2.setParticipants(participants);
        sut2.addExpense();
        verify(mainCtrl2, never()).showAddExpense();
        verify(alert).contentTextProperty();
        verify(alert).show();
    }

    @Test
    void keyPressedCTRLE() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        sut2.setParticipants(participants);
        participants.getItems().addAll(List.of(bob, tom, mary));
        KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getCode()).thenReturn(KeyCode.E);
        when(keyEvent.isControlDown()).thenReturn(true);
        sut2.keyPressed(keyEvent);
        verify(mainCtrl2).showAddExpense();
    }

    @Test
    void startMenu() {
        StompSession.Subscription subscription = mock(StompSession.Subscription.class);
        sut.setExpensesSubscription(subscription);
        sut.setTagSubscription(subscription);
        sut.setEventSubscription(subscription);
        sut.setParticipantSubscription(subscription);

        Scene startMenu = mock(Scene.class);
        mainCtrl.setStartScreen(startMenu);
        Stage primaryStage = mock(Stage.class);
        StringProperty sp = new SimpleStringProperty();
        when(primaryStage.titleProperty()).thenReturn(sp);
        mainCtrl.setHistory(new Stack<>());
        mainCtrl.setPrimaryStage(primaryStage);

        sut.startMenu();

        verify(subscription, times(4)).unsubscribe();
        assertTrue(expenseSubscriptionMap.isEmpty());
        assertTrue(tagSubscriptionMap.isEmpty());
        assertTrue(participantSubscriptionMap.isEmpty());
    }

    @Test
    void sendInvites() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        sut2.sendInvites();
        verify(mainCtrl2).showInvitation();
    }

    @Test
    void statistics() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        sut2.statistics();
        verify(mainCtrl2).showStatistics();
    }

    @Test
    void keyPressedCTRLS() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getCode()).thenReturn(KeyCode.S);
        when(keyEvent.isControlDown()).thenReturn(true);
        sut2.keyPressed(keyEvent);
        verify(mainCtrl2).showStatistics();
    }

    @Test
    void settleDebts() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        sut2.settleDebts();
        verify(mainCtrl2).showDebts();
    }

    @Test
    void changeTitle() {
        sut.changeTitle();

        assertEquals("", title.getText());
    }

    @Test
    void removeParticipant() {
        Event event = getEvent();
        mainCtrl.setEvent(event);
        when(server.getEvent(1)).thenReturn(event);
        when(server.getAllParticipants(1)).thenReturn(event.getParticipantsList());
        when(server.getAllExpenses(1)).thenReturn(event.getExpensesList());
        when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
        StringProperty sp = new SimpleStringProperty();
        when(alert.contentTextProperty()).thenReturn(sp);
        when(alert.titleProperty()).thenReturn(sp);
        when(alert.headerTextProperty()).thenReturn(sp);
        AtomicReference<Expense> modified = new AtomicReference<>();
        when(server.updateExpense(anyInt(), any())).then(mock -> {
            modified.set(mock.getArgument(1));
            return null;
        });
        sut.populateExpenses();

        sut.removeParticipant(bob);

        verify(alert).setAlertType(Alert.AlertType.CONFIRMATION);
        verify(alert).setAlertType(Alert.AlertType.WARNING);
        verify(server).updateExpense(1, expense2);
        verify(server).removeExpense(1, 1);
        assertEquals(2, modified.get().getSplit().size());
        assertEquals(7.5, modified.get().getSplit().getFirst().getPaymentAmount());
    }

    @Test
    void removeParticipantWAExc() {
        Event event = getEvent();
        mainCtrl.setEvent(event);
        Participant coati = new Participant("Coati", null, null, null);
        Expense expense3 = new Expense(20.0, "EUR", "title", "desc", java.sql.Date.valueOf("2024-01-01"), new ArrayList<>(List.of(
                new ParticipantPayment(bob, 5.0),
                new ParticipantPayment(mary, 5.0),
                new ParticipantPayment(tom, 5.0),
                new ParticipantPayment(coati, 5.0)
        )), null, coati);
        event.getParticipantsList().add(coati);
        event.getExpensesList().add(expense3);
        when(server.getEvent(1)).thenReturn(event);
        when(server.getAllParticipants(1)).thenReturn(event.getParticipantsList());
        when(server.getAllExpenses(1)).thenReturn(event.getExpensesList());
        when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));
        StringProperty sp = new SimpleStringProperty();
        when(alert.contentTextProperty()).thenReturn(sp);
        when(alert.titleProperty()).thenReturn(sp);
        when(alert.headerTextProperty()).thenReturn(sp);
        AtomicReference<Expense> modified = new AtomicReference<>();
        when(server.updateExpense(anyInt(), any())).then(mock -> {
            if (mock.getArgument(1).getClass().equals(Expense.class)
                    && ((Expense) mock.getArgument(1)).getId() == 2)
                modified.set(mock.getArgument(1));
            throw new WebApplicationException();
        });
        doAnswer(mock -> {
            throw new WebApplicationException();
        }).when(server).removeExpense(anyInt(), anyLong());
        sut.populateExpenses();
        mainCtrl.setOverviewCtrl(sut);

        sut.removeParticipant(bob);

        verify(alert).setAlertType(Alert.AlertType.CONFIRMATION);
        verify(alert).setAlertType(Alert.AlertType.WARNING);
        verify(server).updateExpense(1, expense2);
        verify(server).removeExpense(1, 1);
        assertEquals(2, modified.get().getSplit().size());
        assertEquals(7.5, modified.get().getSplit().getFirst().getPaymentAmount());
    }

    @Test
    void setLanguageManager() {
        ObservableMap<String, Object> map = new LanguageManager(config);
        sut.setLanguageManager(map);
        assertEquals(map, sut.getLanguageManager());
    }

    @Test
    void getMainCtrl() {
        assertEquals(mainCtrl, sut.getMainCtrl());
    }

    @Test
    void getLanguages() {
        assertEquals(languages, sut.getLanguages());
    }

    @Test
    void getNotificationLabel() {
        Label expenseAdded = new Label();
        sut.setExpenseAdded(expenseAdded);
        assertEquals(expenseAdded, sut.getNotificationLabel());
    }

    @Test
    void languageManagerProperty() {
        assertEquals(languageManager, sut.languageManagerProperty());
    }

    @Test
    void getParticipants() {
        Event event = getEvent();
        mainCtrl.setEvent(event);

        List<Participant> participants = sut.getParticipants();

        assertEquals(List.of(bob, mary, tom), participants);
    }

    @Test
    void settings() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        SettingsCtrl settingsCtrl = mock(SettingsCtrl.class);
        when(mainCtrl2.getSettingsCtrl()).thenReturn(settingsCtrl);
        sut2.settings();
        verify(settingsCtrl).setPrevScene(true);
        verify(mainCtrl2).showSettings();
    }

    @Test
    void keyPressedCTRLT() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        SettingsCtrl settingsCtrl = mock(SettingsCtrl.class);
        when(mainCtrl2.getSettingsCtrl()).thenReturn(settingsCtrl);
        KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getCode()).thenReturn(KeyCode.T);
        when(keyEvent.isControlDown()).thenReturn(true);
        sut2.keyPressed(keyEvent);
        verify(settingsCtrl).setPrevScene(true);
        verify(mainCtrl2).showSettings();
    }

    @Test
    void keyPressedESC() {
        StompSession.Subscription subscription = mock(StompSession.Subscription.class);
        sut.setExpensesSubscription(subscription);
        sut.setTagSubscription(subscription);
        sut.setEventSubscription(subscription);
        sut.setParticipantSubscription(subscription);

        Scene startMenu = mock(Scene.class);
        mainCtrl.setStartScreen(startMenu);
        Stage primaryStage = mock(Stage.class);
        StringProperty sp = new SimpleStringProperty();
        when(primaryStage.titleProperty()).thenReturn(sp);
        mainCtrl.setHistory(new Stack<>());
        mainCtrl.setPrimaryStage(primaryStage);

        KeyEvent keyEvent = mock(KeyEvent.class);
        when (keyEvent.getCode()).thenReturn(KeyCode.ESCAPE);
        sut.keyPressed(keyEvent);

        verify(subscription, times(4)).unsubscribe();
        assertTrue(expenseSubscriptionMap.isEmpty());
        assertTrue(tagSubscriptionMap.isEmpty());
        assertTrue(participantSubscriptionMap.isEmpty());
    }

    @Test
    void keyPressedCTRLM() {
        StompSession.Subscription subscription = mock(StompSession.Subscription.class);
        sut.setExpensesSubscription(subscription);
        sut.setTagSubscription(subscription);
        sut.setEventSubscription(subscription);
        sut.setParticipantSubscription(subscription);

        Scene startMenu = mock(Scene.class);
        mainCtrl.setStartScreen(startMenu);
        Stage primaryStage = mock(Stage.class);
        StringProperty sp = new SimpleStringProperty();
        when(primaryStage.titleProperty()).thenReturn(sp);
        mainCtrl.setHistory(new Stack<>());
        mainCtrl.setPrimaryStage(primaryStage);

        KeyEvent keyEvent = mock(KeyEvent.class);
        when (keyEvent.getCode()).thenReturn(KeyCode.M);
        when (keyEvent.isControlDown()).thenReturn(true);
        sut.keyPressed(keyEvent);

        verify(subscription, times(4)).unsubscribe();
        assertTrue(expenseSubscriptionMap.isEmpty());
        assertTrue(tagSubscriptionMap.isEmpty());
        assertTrue(participantSubscriptionMap.isEmpty());
    }

    private Event getEvent() {
        Event event = new Event("title", new Date(), new Date());
        event.getParticipantsList().addAll(List.of(bob, mary, tom));
        event.getExpensesList().addAll(List.of(expense1, expense2));
        event.setInviteCode(1);
        return event;
    }

    @Test
    void addToHistory() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        when(mainCtrl2.getHistory()).thenReturn(new Stack<>());
        undoButton.setVisible(false);
        ICommand iCommand = mock(ICommand.class);
        undoButton = new Button();
        sut2.setUndoButton(undoButton);
        sut2.addToHistory(iCommand);
        verify(mainCtrl2).addToHistory(iCommand);
        assertTrue(undoButton.isVisible());
    }

    @Test
    void undo() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        when(mainCtrl2.getHistory()).thenReturn(new Stack<>());
        undoButton = new Button();
        sut2.setUndoButton(undoButton);
        sut2.undo();
        verify(mainCtrl2).undo();
        assertFalse(undoButton.isVisible());
    }

    @Test
    void keyPressedCTRLZ() {
        MainCtrl mainCtrl2 = mock(MainCtrl.class);
        OverviewCtrl sut2 = new OverviewCtrl(languageManager, config, server, mainCtrl2, currencyConverter, alert);
        when(mainCtrl2.getHistory()).thenReturn(new Stack<>());
        KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getCode()).thenReturn(KeyCode.Z);
        when(keyEvent.isControlDown()).thenReturn(true);
        undoButton = new Button();
        sut2.setUndoButton(undoButton);
        sut2.keyPressed(keyEvent);
        verify(mainCtrl2).undo();
        assertFalse(undoButton.isVisible());
    }
}