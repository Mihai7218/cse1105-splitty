package client.scenes;

import client.utils.ConfigInterface;
import client.utils.CurrencyConverter;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import jakarta.ws.rs.core.Response;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;

@ExtendWith(ApplicationExtension.class)
public class AddExpenseCtrlTest {

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
    AddExpenseCtrl sut;
    ChoiceBox<Participant> payee;
    ChoiceBox<String> currency;
    ComboBox<Tag> expenseType;
    CheckBox everyone;
    CheckBox only;
    VBox namesContainer;
    Label question;
    TextField title;
    TextField price;
    DatePicker date;
    ObservableList op = FXCollections.observableArrayList();
    ObservableList oc = FXCollections.observableArrayList();
    ObservableList ot = FXCollections.observableArrayList();
    ObservableList noc = FXCollections.observableArrayList();
    ScrollPane scrollNames;
    Button addTag;
    TextField newTag;
    Label instructions;
    Button cancelButton;
    ColorPicker colorPicker;
    Button addExpenseButton;
    CurrencyConverter currencyConverter;
    Event event;
    List<Participant> participants;
    StringProperty sp;
    Participant participant;

    @Start
    void setUp(Stage stage) {
        mainCtrl = mock(MainCtrl.class);
        serverUtils = mock(ServerUtils.class);
        languageManager = mock(LanguageManager.class);
        currencyConverter = mock(CurrencyConverter.class);
        alert = mock(Alert.class);
        sut = new AddExpenseCtrl(mainCtrl, config, languageManager,
                serverUtils, alert, currencyConverter);
        payee = mock(ChoiceBox.class);
        currency = mock(ChoiceBox.class);
        expenseType = mock(ComboBox.class);
        everyone = mock(CheckBox.class);
        only = mock(CheckBox.class);
        namesContainer = mock(VBox.class);
        question = mock(Label.class);
        title = mock(TextField.class);
        price = mock(TextField.class);
        date = mock(DatePicker.class);
        scrollNames = mock(ScrollPane.class);
        addTag = mock(Button.class);
        newTag = mock(TextField.class);
        instructions = mock(Label.class);
        cancelButton=mock(Button.class);
        addExpenseButton = mock(Button.class);
        colorPicker = mock(ColorPicker.class);
        var creationDate = new Date(2024, 2, 15);
        var lastActivity = new Date(2022, 3, 17);

        event = new Event("Test Event", creationDate, lastActivity);


        sut.setPayee(payee);
        sut.setCurrency(currency);
        sut.setExpenseType(expenseType);
        sut.setEveryone(everyone);
        sut.setOnly(only);
        sut.setNamesContainer(namesContainer);
        sut.setQuestion(question);
        sut.setTitle(title);
        sut.setPrice(price);
        sut.setDate(date);
        sut.setScrollNames(scrollNames);
        sut.setAddTag(addTag);
        sut.setNewTag(newTag);
        sut.setInstructions(instructions);
        sut.setAdd(addExpenseButton);
        sut.setCancelButton(cancelButton);
        sut.setAddExpense(addExpenseButton);
        sut.setColorPicker(colorPicker);

        sp = new SimpleStringProperty("Hello");
        when(languageManager.bind(anyString())).thenReturn(Bindings.createStringBinding(() -> ""));
        when(alert.contentTextProperty()).thenReturn(sp);
        when(alert.titleProperty()).thenReturn(sp);
        when(alert.headerTextProperty()).thenReturn(sp);
        participant = new Participant("john", "erra@gmail.com", "NL123ABN", "BRT");
        participants = new ArrayList<>();
        addParticipants();

        doNothing().when(addExpenseButton).setGraphic(any(Node.class));
        doNothing().when(cancelButton).setGraphic(any(Node.class));
        when(payee.getItems()).thenReturn(op);
        when(currency.getItems()).thenReturn(oc);
        when(expenseType.getItems()).thenReturn(ot);
        when(namesContainer.getChildren()).thenReturn(noc);
        when(payee.getValue()).thenReturn(participant);
        when(currency.getValue()).thenReturn("EUR");
        when(mainCtrl.getEvent()).thenReturn(event);
        event.setParticipantsList(participants);

        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
    }
    public void addPeople(ObservableList noc){
        noc.add(0, "John");
        noc.add(1, "Heather");
    }
    public void addParticipants(){
        participants.add(new Participant("part1", "erra@gmail.com", "NL123ABN", "BRT"));
        participants.add(new Participant("part2", "maru@gmail.com", "NL13323ABN", "BRT"));
    }
    
    @Test
    void initialize() {
        assertFalse(question.isVisible());
        assertNotNull(sut.getNamesContainer());
        assertEquals(2, namesContainer.getChildren().size());
    }

    @Test
    void chooseOne() {
        everyone.setSelected(true);
        sut.everyoneCheck();
        assertFalse(only.isSelected());
        assertEquals(2, namesContainer.getChildren().size());

        everyone.setSelected(false);
        only.setSelected(true);
        sut.onlyCheck();
        assertFalse(everyone.isSelected());
        if(everyone.isSelected()) noc.clear();
        else addPeople(noc);
        assertFalse(namesContainer.getChildren().isEmpty());
    }
    @Test
    void testAddExpenseGoodNumber() {
        // Set up necessary dependencies and data
        String expenseTitle = "Test Expense";
        String expensePriceText = "10.00"; // Assuming valid price text
        LocalDate expenseDate = LocalDate.now();

        // Mock behavior for GUI elements
        when(title.getText()).thenReturn(expenseTitle);
        when(price.getText()).thenReturn(expensePriceText);
        when(date.getValue()).thenReturn(expenseDate);
        //when(serverUtils.addExpense(any(), any())).thenReturn(mock(Response.class)); // Mock server response

        // Invoke the method
        sut.addExpense();

        // Verify behavior
        verify(title, times(1)).getText();
        verify(price, times(1)).getText();
        verify(date, times(1)).getValue();

    }
    @Test
    void testAddExpenseNegativeNumber() {
        // Set up necessary dependencies and data
        String expenseTitle = "Test Expense";
        String expensePriceText = "-10.00"; // Assuming valid price text
        LocalDate expenseDate = LocalDate.now();

        // Mock behavior for GUI elements
        when(title.getText()).thenReturn(expenseTitle);
        when(price.getText()).thenReturn(expensePriceText);
        when(date.getValue()).thenReturn(expenseDate);
        //when(serverUtils.addExpense(any(), any())).thenReturn(mock(Response.class)); // Mock server response

        // Invoke the method
        sut.addExpense();

        // Verify behavior
        verify(title, times(1)).getText();
        verify(price, times(1)).getText();
        verify(date, times(1)).getValue();

    }
    @Test
    void testAddExpenseTooManyDigitsNumber() {
        // Set up necessary dependencies and data
        String expenseTitle = "Test Expense";
        String expensePriceText = "10.00223"; // Assuming valid price text
        LocalDate expenseDate = LocalDate.now();

        // Mock behavior for GUI elements
        when(title.getText()).thenReturn(expenseTitle);
        when(price.getText()).thenReturn(expensePriceText);
        when(date.getValue()).thenReturn(expenseDate);
        //when(serverUtils.addExpense(any(), any())).thenReturn(mock(Response.class)); // Mock server response

        // Invoke the method
        sut.addExpense();

        // Verify behavior
        verify(title, times(1)).getText();
        verify(price, times(1)).getText();
        verify(date, times(1)).getValue();

    }
}
