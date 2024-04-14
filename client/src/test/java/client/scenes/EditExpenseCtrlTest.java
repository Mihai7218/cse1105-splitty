package client.scenes;

import client.commands.ICommand;
import client.utils.ConfigInterface;
import client.utils.CurrencyConverter;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.sun.javafx.collections.ObservableListWrapper;
import commons.*;
import jakarta.mail.Part;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
public class EditExpenseCtrlTest {

    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    @FXML
    private Button done;
    private StompSession.Subscription expenseSubscription;
    MainCtrl mainCtrl;
    ConfigInterface config;
    LanguageManager languageManager;
    ServerUtils serverUtils;
    Alert alert;
    CurrencyConverter currencyConverter;
    EditExpenseCtrl sut;


    Event testEvent;
    Expense expense;
    ParticipantPayment p;
    ParticipantPayment p3;
    Participant p1;
    Participant p2;
    public ColorPicker colorPicker;

    protected ChoiceBox<Participant> payee;
    protected List<Participant> participantsList;
    protected ChoiceBox<String> currency;
    protected Map<CheckBox, Participant> checkBoxParticipantMap;
    protected Map<Participant, CheckBox> participantCheckBoxMap;
    protected ComboBox<Tag> expenseType;
    protected CheckBox everyone;
    protected Button addTag;
    protected CheckBox only;
    protected VBox namesContainer;
    protected Label question;
    protected ScrollPane scrollNames;
    protected TextField newTag;
    protected Label instructions;
    protected TextField title;
    protected TextField price;
    protected DatePicker date;
    protected Button add;
    protected Button cancelButton;
    protected Button addExpense;
    StringProperty contentSp;
    StringProperty titleSp;
    StringProperty headerSp;

    @Start
    public void setUp(Stage stage){
        done = mock(Button.class);
        expenseSubscription = mock(StompSession.Subscription.class);
        alert = mock(Alert.class);
        mainCtrl = mock(MainCtrl.class);
        config = mock(ConfigInterface.class);
        languageManager = mock(LanguageManager.class);
        serverUtils = mock(ServerUtils.class);
        currencyConverter = mock(CurrencyConverter.class);
        payee = mock(ChoiceBox.class);
        currency = mock(ChoiceBox.class);
        expenseType = mock(ComboBox.class);
        colorPicker = mock(ColorPicker.class);
        everyone = mock(CheckBox.class);
        addTag = mock(Button.class);
        only = mock(CheckBox.class);
        namesContainer = mock(VBox.class);
        question = mock(Label.class);
        scrollNames = mock(ScrollPane.class);
        newTag = mock(TextField.class);
        instructions = mock(Label.class);
        price = mock(TextField.class);
        cancelButton = mock(Button.class);
        addExpense = mock(Button.class);
        add= mock(Button.class);
        sut = new EditExpenseCtrl(mainCtrl, config, languageManager, serverUtils, alert, currencyConverter);

        sut.setAdd(add);
        sut.setDone(done);
        sut.setPayee(payee);
        sut.setCurrency(currency);
        sut.setExpenseType(expenseType);
        sut.setColorPicker(colorPicker);
        sut.setEveryone(everyone);
        sut.setAddTag(addTag);
        sut.setOnly(only);
        sut.setNamesContainer(namesContainer);
        sut.setQuestion(question);
        sut.setScrollNames(scrollNames);
        sut.setNewTag(newTag);
        sut.setInstructions(instructions);
        sut.setPrice(price);
        sut.setCancelButton(cancelButton);
        sut.setAddExpense(addExpense);

        contentSp = new SimpleStringProperty("Hello");
        titleSp = new SimpleStringProperty("Hello");
        headerSp = new SimpleStringProperty("Hello");
        when(languageManager.bind(anyString())).thenReturn(Bindings.createStringBinding(() -> ""));
        when(alert.contentTextProperty()).thenReturn(contentSp);
        when(alert.titleProperty()).thenReturn(titleSp);
        when(alert.headerTextProperty()).thenReturn(headerSp);

        checkBoxParticipantMap = mock(Map.class);
        participantCheckBoxMap = mock(Map.class);

        sut.setCheckBoxParticipantMap(checkBoxParticipantMap);
        sut.setParticipantCheckBoxMap(participantCheckBoxMap);

        testEvent = new Event("testEvent", null, null);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
        var mockOverviewCtrl = mock(OverviewCtrl.class);
        doNothing().when(mockOverviewCtrl).populateParticipants();
        when(mainCtrl.getOverviewCtrl()).thenReturn(mockOverviewCtrl);
        p1 = new Participant("bob", null, null, null);
        p2 = new Participant("jill", null, null, null);
        testEvent.addParticipant(p1);
        testEvent.addParticipant(p2);
        testEvent.setInviteCode(1);
        p1.setId(1);
        p2.setId(2);
        p = new ParticipantPayment(p1, 15);
        p3 = new ParticipantPayment(p2,15);
        expense = new Expense(15, "EUR", "Transfer", "transfer", java.sql.Date.valueOf(LocalDate.now()), List.of(p, p3), null, p2);
        sut.setExpense(expense);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
    }

    @Test
    public void loadTest(){
        List<String> paths = new ArrayList<>();
        doAnswer(invocation -> {
            String s = String.valueOf(invocation.getArgument(0, String.class));
            paths.add(s);
            return null;
        }).when(serverUtils).registerForMessages(any(), any(),any());
        ObservableList nodes = mock(ObservableList.class);
        ObservableList nodesS = new ObservableListWrapper(new ArrayList());
        when(namesContainer.getChildren()).thenReturn(nodesS);
        when(nodes.add(any())).thenReturn(true);
        when(payee.getItems()).thenReturn(nodes);
        when(expenseType.getItems()).thenReturn(nodes);

        sut.load();
        assertEquals(paths.getFirst(), "/topic/events/1/participants/1");

    }

    @Test
    public void loadFieldsTest(){
        TextField t = new TextField();
        sut.setTitle(t);
        TextField priceNew = new TextField();
        sut.setPrice(priceNew);
        CheckBox onlyBox = new CheckBox();
        sut.setOnly(onlyBox);
        DatePicker dp = new DatePicker();
        sut.setDate(dp);

        doNothing().when(scrollNames).setVisible(anyBoolean());
        doNothing().when(payee).setValue(any());
        doNothing().when(checkBoxParticipantMap).clear();
        doNothing().when(participantCheckBoxMap).clear();
        List<Participant> participants = new ArrayList<>();

        when(participantCheckBoxMap.put(any(), any())).thenReturn(null);

        when(checkBoxParticipantMap.put(any(),any())).thenReturn(null);

        ObservableList nodes = mock(ObservableList.class);
        ObservableList nodesS = new ObservableListWrapper(new ArrayList());
        when(namesContainer.getChildren()).thenReturn(nodesS);
        when(nodes.add(any())).thenReturn(true);
        when(expenseType.getItems()).thenReturn(nodes);
        Collection c = new ArrayList();
        when(nodes.addAll(anyCollection())).thenReturn(true);
        doNothing().when(currency).setValue(anyString());

        sut.loadFields();

        assertEquals(t.getText(), "Transfer");
        assertEquals(priceNew.getText(),"15.0");
        verify(expenseType).setValue(any());
        verify(expenseType, times(2)).getItems();

    }

    @Test
    public void refreshTest(){
        TextField t = new TextField();
        sut.setTitle(t);
        TextField priceNew = new TextField();
        sut.setPrice(priceNew);
        CheckBox onlyBox = new CheckBox();
        sut.setOnly(onlyBox);
        DatePicker dp = new DatePicker();
        sut.setDate(dp);

        doNothing().when(scrollNames).setVisible(anyBoolean());
        doNothing().when(payee).setValue(any());
        doNothing().when(checkBoxParticipantMap).clear();
        doNothing().when(participantCheckBoxMap).clear();
        List<Participant> participants = new ArrayList<>();

        when(participantCheckBoxMap.put(any(), any())).thenReturn(null);

        when(checkBoxParticipantMap.put(any(),any())).thenReturn(null);

        ObservableList nodes = mock(ObservableList.class);
        ObservableList nodesS = new ObservableListWrapper(new ArrayList());
        when(namesContainer.getChildren()).thenReturn(nodesS);
        when(nodes.add(any())).thenReturn(true);
        when(payee.getItems()).thenReturn(nodes);
        when(expenseType.getItems()).thenReturn(nodes);
        Collection c = new ArrayList();
        when(nodes.addAll(anyCollection())).thenReturn(true);
        doNothing().when(currency).setValue(anyString());
        doNothing().when(add).setGraphic(any());
        EditExpenseCtrl spy = spy(sut);
        spy.refresh();

        verify(spy).loadFields();
    }

    @Test
    public void doneExpenseTest(){
        TextField t = new TextField();
        sut.setTitle(t);
        TextField priceNew = new TextField();
        sut.setPrice(priceNew);
        CheckBox onlyBox = new CheckBox();
        sut.setOnly(onlyBox);
        DatePicker dp = new DatePicker();
        sut.setDate(dp);

        when(currency.getValue()).thenReturn("EUR");
        when(payee.getValue()).thenReturn(p1);
        t.setText("newTitle");
        priceNew.setText("15");
        dp.setValue(LocalDate.now());
        doNothing().when(scrollNames).setVisible(anyBoolean());
        doNothing().when(payee).setValue(any());
        doNothing().when(checkBoxParticipantMap).clear();
        doNothing().when(participantCheckBoxMap).clear();
        List<Participant> participants = new ArrayList<>();

        when(participantCheckBoxMap.put(any(), any())).thenReturn(null);

        when(checkBoxParticipantMap.put(any(),any())).thenReturn(null);

        ObservableList nodes = mock(ObservableList.class);
        ObservableList nodesS = new ObservableListWrapper(new ArrayList());
        when(namesContainer.getChildren()).thenReturn(nodesS);
        when(nodes.add(any())).thenReturn(true);

        when(expenseType.getItems()).thenReturn(null);
        Collection c = new ArrayList();
        when(nodes.addAll(anyCollection())).thenReturn(true);
        doNothing().when(currency).setValue(anyString());
        doNothing().when(add).setGraphic(any());
        EditExpenseCtrl spy = spy(sut);
        spy.doneExpense();

        verify(spy, times(2)).removeHighlight();
        verify(spy, times(2)).clearFields();
        verify(spy).modifyExpense(anyString(),anyInt(), any(),any());
        verify(mainCtrl).showEditConfirmation();
    }

    KeyEvent mockEvent = mock(KeyEvent.class);


    @Test
    void testEnterPress() {
        TextField t = new TextField();
        sut.setTitle(t);
        TextField priceNew = new TextField();
        sut.setPrice(priceNew);
        CheckBox onlyBox = new CheckBox();
        sut.setOnly(onlyBox);
        DatePicker dp = new DatePicker();
        sut.setDate(dp);

        when(currency.getValue()).thenReturn("EUR");
        when(payee.getValue()).thenReturn(p1);
        t.setText("newTitle");
        priceNew.setText("15");
        dp.setValue(LocalDate.now());
        doNothing().when(scrollNames).setVisible(anyBoolean());
        doNothing().when(payee).setValue(any());
        doNothing().when(checkBoxParticipantMap).clear();
        doNothing().when(participantCheckBoxMap).clear();
        List<Participant> participants = new ArrayList<>();

        when(participantCheckBoxMap.put(any(), any())).thenReturn(null);

        when(checkBoxParticipantMap.put(any(),any())).thenReturn(null);

        ObservableList nodes = mock(ObservableList.class);
        ObservableList nodesS = new ObservableListWrapper(new ArrayList());
        when(namesContainer.getChildren()).thenReturn(nodesS);
        when(nodes.add(any())).thenReturn(true);

        when(expenseType.getItems()).thenReturn(null);
        Collection c = new ArrayList();
        when(nodes.addAll(anyCollection())).thenReturn(true);
        doNothing().when(currency).setValue(anyString());
        doNothing().when(add).setGraphic(any());
        EditExpenseCtrl spies = spy(sut);

        when(mockEvent.getCode()).thenReturn(KeyCode.ENTER);
        when(mockEvent.isControlDown()).thenReturn(true);


        spies.keyPressed(mockEvent);
        verify(spies).doneExpense();
    }

    @Test
    void testKeyPressedMWithControl() {
        TextField t = new TextField();
        sut.setTitle(t);
        TextField priceNew = new TextField();
        sut.setPrice(priceNew);
        CheckBox onlyBox = new CheckBox();
        sut.setOnly(onlyBox);
        DatePicker dp = new DatePicker();
        sut.setDate(dp);

        when(currency.getValue()).thenReturn("EUR");
        when(payee.getValue()).thenReturn(p1);
        t.setText("newTitle");
        priceNew.setText("15");
        dp.setValue(LocalDate.now());
        doNothing().when(scrollNames).setVisible(anyBoolean());
        doNothing().when(payee).setValue(any());
        doNothing().when(checkBoxParticipantMap).clear();
        doNothing().when(participantCheckBoxMap).clear();
        List<Participant> participants = new ArrayList<>();

        when(participantCheckBoxMap.put(any(), any())).thenReturn(null);

        when(checkBoxParticipantMap.put(any(),any())).thenReturn(null);

        ObservableList nodes = mock(ObservableList.class);
        ObservableList nodesS = new ObservableListWrapper(new ArrayList());
        when(namesContainer.getChildren()).thenReturn(nodesS);
        when(nodes.add(any())).thenReturn(true);

        when(expenseType.getItems()).thenReturn(null);
        Collection c = new ArrayList();
        when(nodes.addAll(anyCollection())).thenReturn(true);
        doNothing().when(currency).setValue(anyString());
        doNothing().when(add).setGraphic(any());
        EditExpenseCtrl spies = spy(sut);

        when(mockEvent.getCode()).thenReturn(KeyCode.M);
        when(mockEvent.isControlDown()).thenReturn(true);


        spies.keyPressed(mockEvent);
        verify(spies).startMenu();
    }

    @Test
    void testKeyPressedDefault() {
        EditExpenseCtrl spies = spy(sut);
        when(mockEvent.getCode()).thenReturn(KeyCode.P);

        spies.keyPressed(mockEvent);

        verify(spies, never()).abort();
        verify(spies, never()).startMenu();
    }

    @Test
    public void testReturnStart(){
        ObservableList e = mock(ObservableList.class);
        when(payee.getItems()).thenReturn(e);
        when(expenseType.getItems()).thenReturn(e);
        doNothing().when(e).clear();

        TextField t = new TextField();
        sut.setTitle(t);
        TextField priceNew = new TextField();
        sut.setPrice(priceNew);
        CheckBox onlyBox = new CheckBox();
        sut.setOnly(onlyBox);
        DatePicker dp = new DatePicker();
        sut.setDate(dp);

        when(currency.getValue()).thenReturn("EUR");
        when(payee.getValue()).thenReturn(p1);
        t.setText("newTitle");
        priceNew.setText("15");
        dp.setValue(LocalDate.now());
        doNothing().when(scrollNames).setVisible(anyBoolean());
        doNothing().when(payee).setValue(any());
        doNothing().when(checkBoxParticipantMap).clear();
        doNothing().when(participantCheckBoxMap).clear();
        List<Participant> participants = new ArrayList<>();

        when(participantCheckBoxMap.put(any(), any())).thenReturn(null);

        when(checkBoxParticipantMap.put(any(),any())).thenReturn(null);

        ObservableList nodes = mock(ObservableList.class);
        ObservableList nodesS = new ObservableListWrapper(new ArrayList());
        when(namesContainer.getChildren()).thenReturn(nodesS);
        when(nodes.add(any())).thenReturn(true);

        when(expenseType.getItems()).thenReturn(null);
        Collection c = new ArrayList();
        when(nodes.addAll(anyCollection())).thenReturn(true);
        doNothing().when(currency).setValue(anyString());
        doNothing().when(add).setGraphic(any());
        doNothing().when(mainCtrl).showStartMenu();
        EditExpenseCtrl spy = spy(sut);
        spy.startMenu();

        verify(mainCtrl).showStartMenu();
    }

    @Test
    void testHandle400(){
        ICommand undoCommand = mock(ICommand.class);
        doThrow(new WebApplicationException(400)).when(undoCommand).undo();
        KeyEvent event = mock(KeyEvent.class);
        sut.undo(undoCommand);
        verify(languageManager).bind("addExpense.badReqHeader");
        verify(languageManager).bind("addExpense.badReqBody");
        verify(alert).showAndWait();
    }

    @Test
    void testHandle404(){
        ICommand undoCommand = mock(ICommand.class);
        doThrow(new WebApplicationException(404)).when(undoCommand).undo();
        KeyEvent event = mock(KeyEvent.class);
        sut.undo(undoCommand);
        verify(languageManager).bind("addExpense.notFoundHeader");
        verify(languageManager).bind("addExpense.notFoundBody");
        verify(alert).showAndWait();
    }

    @Test
    public void exceptionsInModification(){
        ObservableList e = mock(ObservableList.class);
        when(payee.getItems()).thenReturn(e);
        when(expenseType.getItems()).thenReturn(e);
        doNothing().when(e).clear();

        TextField t = new TextField();
        sut.setTitle(t);
        TextField priceNew = new TextField();
        sut.setPrice(priceNew);
        CheckBox onlyBox = new CheckBox();
        sut.setOnly(onlyBox);
        DatePicker dp = new DatePicker();
        sut.setDate(dp);

        when(currency.getValue()).thenReturn("EUR");
        when(payee.getValue()).thenReturn(p1);
        t.setText("newTitle");
        priceNew.setText("15");
        dp.setValue(LocalDate.now());
        doNothing().when(scrollNames).setVisible(anyBoolean());
        doNothing().when(payee).setValue(any());
        doNothing().when(checkBoxParticipantMap).clear();
        doNothing().when(participantCheckBoxMap).clear();
        List<Participant> participants = new ArrayList<>();

        when(participantCheckBoxMap.put(any(), any())).thenReturn(null);

        when(checkBoxParticipantMap.put(any(),any())).thenReturn(null);

        ObservableList nodes = mock(ObservableList.class);
        ObservableList nodesS = new ObservableListWrapper(new ArrayList());
        when(namesContainer.getChildren()).thenReturn(nodesS);
        when(nodes.add(any())).thenReturn(true);

        when(expenseType.getItems()).thenReturn(null);
        Collection c = new ArrayList();
        when(nodes.addAll(anyCollection())).thenReturn(true);
        doNothing().when(currency).setValue(anyString());
        doNothing().when(add).setGraphic(any());
        doNothing().when(mainCtrl).showStartMenu();
        OverviewCtrl overviewCtrl = mock(OverviewCtrl.class);
        when(mainCtrl.getOverviewCtrl()).thenReturn(overviewCtrl);
        doThrow(new WebApplicationException(404)).when(overviewCtrl).addToHistory(any());
        sut.modifyExpense("title",15, LocalDate.now(), expense);
        verify(languageManager).bind("addExpense.notFoundHeader");
        verify(languageManager).bind("addExpense.notFoundBody");
        verify(alert).showAndWait();

        doThrow(new WebApplicationException(400)).when(overviewCtrl).addToHistory(any());
        sut.modifyExpense("title",15, LocalDate.now(), expense);
        verify(languageManager).bind("addExpense.badReqHeader");
        verify(languageManager).bind("addExpense.badReqBody");
        verify(alert, times(2)).showAndWait();
    }

}
