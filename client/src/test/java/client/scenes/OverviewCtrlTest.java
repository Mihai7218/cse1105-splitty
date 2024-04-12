package client.scenes;

import client.commands.ICommand;
import client.utils.*;
import commons.*;
import jakarta.ws.rs.WebApplicationException;
import javafx.scene.control.*;
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
    Participant bob = new Participant("Bob", null, null, null);
    Participant mary = new Participant("Mary", null, null, null);
    Participant tom = new Participant("Tom", null, null, null);
    Expense expense1 = new Expense(10.0, "EUR", "title", "desc", java.sql.Date.valueOf("2024-01-01"), List.of(
            new ParticipantPayment(bob, 5.0),
            new ParticipantPayment(mary, 5.0)
    ), null, bob);
    Expense expense2 = new Expense(15.0, "EUR", "title2", "desc", java.sql.Date.valueOf("2023-01-01"), List.of(
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

    private Event getEvent() {
        Event event = new Event("title", new Date(), new Date());
        event.getParticipantsList().addAll(List.of(bob, mary, tom));
        event.getExpensesList().addAll(List.of(expense1, expense2));
        event.setInviteCode(1);
        return event;
    }
}