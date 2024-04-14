package client.scenes;

import client.utils.*;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import jakarta.mail.MessagingException;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class DebtsCtrlTest {

    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }
    MainCtrl mainCtrl;
    LanguageManager languageManager;
    ServerUtils serverUtils;
    @Mock
    CurrencyConverter currencyConverter;
    Alert alert;
    MailSender mailSender;
    DebtsCtrl sut;
    ConfigInterface config;
    Stage stage;
    String stageTitle;
    String titleKey;
    StringProperty sp;
    StringBinding sb;
    Participant p1;
    Participant p2;

    Event testEvent;

    ObservableList menulist = FXCollections.observableArrayList();
    Label confirmation;
    Accordion menu;
    Button back;
    Date date;


    @Mock
    KeyEvent mockEvent = mock(KeyEvent.class);

    @Start
    void setUp(Stage ignored) {
        mainCtrl = mock(MainCtrl.class);
        config = new TestConfig();
        languageManager = mock(LanguageManager.class);
        serverUtils = mock(ServerUtils.class);
        currencyConverter = mock(CurrencyConverter.class);
        alert = mock(Alert.class);
        mailSender = mock(MailSender.class);
        confirmation = mock(Label.class);
        menu = mock(Accordion.class);
        back = mock(Button.class);

        sp = new SimpleStringProperty("Hello");
        when(languageManager.bind(anyString())).thenReturn(Bindings.createStringBinding(() -> ""));
        when(alert.contentTextProperty()).thenReturn(sp);
        when(alert.titleProperty()).thenReturn(sp);
        when(alert.headerTextProperty()).thenReturn(sp);
        when(back.textProperty()).thenReturn(new SimpleStringProperty("Back"));

        sut = new DebtsCtrl(mainCtrl,config,languageManager,serverUtils,currencyConverter,alert,mailSender);
        sut.setBack(back);
        sut.setNotificationLabel(confirmation);
        sut.setMenu(menu);

        doNothing().when(back).setGraphic(any(Node.class));

        sut.initialize(mock(URL.class), mock(ResourceBundle.class));

        testEvent = new Event("testEvent", null, null);
        p1 = new Participant("p1", "p1@gmail.com", "NL123ABN", "BRT");
        p2 = new Participant("p2", null, "NL123ABZ", "BCT");
        p1.setId(0);
        p2.setId(1);
        testEvent.addParticipant(p1);
        testEvent.addParticipant(p2);
        ArrayList plist = new ArrayList<>();
        ParticipantPayment pay1 = new ParticipantPayment(p1,50.0);
        ParticipantPayment pay2 = new ParticipantPayment(p2,50.0);
        plist.add(pay1);
        plist.add(pay2);
        date = new Date();

        testEvent.addExpense(new Expense(100.0, "EUR", "Title", "Desc", date, plist, null, p1));

        when(menu.getPanes()).thenReturn(menulist);
        when(mainCtrl.getEvent()).thenReturn(testEvent);

    }

    @Test
    void setTitles_shouldPopulateAccordionWithDebts() {
        // Given
        testEvent = new Event("testEvent", null, null);
        p1 = new Participant("p1", "p1@gmail.com", "NL123ABN", "BRT");
        p2 = new Participant("p2", "p2@gmail.com", "NL123ABZ", "BCT");
        p1.setId(0);
        p2.setId(1);
        testEvent.addParticipant(p1);
        testEvent.addParticipant(p2);
        ArrayList plist = new ArrayList<>();
        ParticipantPayment pay1 = new ParticipantPayment(p1,50.0);
        ParticipantPayment pay2 = new ParticipantPayment(p2,50.0);
        plist.add(pay1);
        plist.add(pay2);
        date = new Date();

        testEvent.addExpense(new Expense(100.0, "EUR", "Title", "Desc", date, plist, null, p1));
        TitledPane paneMock = mock(TitledPane.class);
        when(menu.getPanes()).thenReturn(menulist);
        when(menu.getExpandedPane()).thenReturn(paneMock);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
        when(currencyConverter.convert(any(Date.class),anyString(),anyString(),anyDouble())).thenReturn(50.0);
        // When
        sut.refresh();
        // Then
        verify(menu, atLeastOnce()).getPanes();
    }

    @Test
    void setTitles_WithRemindersButEmptyEmail() {
        // Given
        testEvent = new Event("testEvent", null, null);
        p1 = new Participant("p1", "p1@gmail.com", "NL123ABN", "BRT");
        p2 = new Participant("p2", null, "NL123ABZ", "BCT");
        p1.setId(0);
        p2.setId(1);
        testEvent.addParticipant(p1);
        testEvent.addParticipant(p2);
        ArrayList plist = new ArrayList<>();
        ParticipantPayment pay1 = new ParticipantPayment(p1,50.0);
        ParticipantPayment pay2 = new ParticipantPayment(p2,50.0);
        plist.add(pay1);
        plist.add(pay2);
        date = new Date();

        testEvent.addExpense(new Expense(100.0, "EUR", "Title", "Desc", date, plist, null, p1));
        TitledPane paneMock = mock(TitledPane.class);
        when(menu.getPanes()).thenReturn(menulist);
        when(menu.getExpandedPane()).thenReturn(paneMock);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
        when(currencyConverter.convert(any(Date.class),anyString(),anyString(),anyDouble())).thenReturn(50.0);
        sut.setCanRemind(true);
        // When
        sut.setTitles(testEvent);
        // Then
        verify(menu, atLeastOnce()).getPanes();
    }

    @Test
    void setTitles_WithReminders() {
        // Given
        testEvent = new Event("testEvent", null, null);
        p1 = new Participant("p1", "p1@gmail.com", "NL123ABN", "BRT");
        p2 = new Participant("p2", "p2@gmail.com", "NL123ABZ", "BCT");
        p1.setId(0);
        p2.setId(1);
        testEvent.addParticipant(p1);
        testEvent.addParticipant(p2);
        ArrayList plist = new ArrayList<>();
        ParticipantPayment pay1 = new ParticipantPayment(p1,50.0);
        ParticipantPayment pay2 = new ParticipantPayment(p2,50.0);
        plist.add(pay1);
        plist.add(pay2);
        date = new Date();

        testEvent.addExpense(new Expense(100.0, "EUR", "Title", "Desc", date, plist, null, p1));
        TitledPane paneMock = mock(TitledPane.class);
        when(menu.getPanes()).thenReturn(menulist);
        when(menu.getExpandedPane()).thenReturn(paneMock);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
        when(confirmation.textProperty()).thenReturn(sp);
        when(currencyConverter.convert(any(Date.class),anyString(),anyString(),anyDouble())).thenReturn(50.0);
        sut.setCanRemind(true);
        // When
        sut.setTitles(testEvent);
        sut.getRemind().fire();
        // Then
        verify(menu, atLeastOnce()).getPanes();
    }

    @Test
    void createExpense_shouldAddExpenseToServerUtils() {
        // Given
        Debt debt = new Debt(new Participant("bob", null, null, null),
                new Participant("jill", null, null, null), 100.0);
        // When
        sut.createExpense(debt);
        // Then
        verify(serverUtils, times(1)).addExpense(anyInt(), any(Expense.class));
    }

    @Test
    void createExpense_404() {
        Debt debt = new Debt(new Participant("bob", null, null, null),
                new Participant("jill", null, null, null), 100.0);
        when(serverUtils.addExpense(anyInt(),any(Expense.class))).thenThrow(new WebApplicationException(404));

        sut.createExpense(debt);

        verify(languageManager).bind("addExpense.notFoundHeader");
        verify(languageManager).bind("addExpense.notFoundBody");
        verify(alert).showAndWait();
    }

    @Test
    void createExpense_400() {
        Debt debt = new Debt(new Participant("bob", null, null, null),
                new Participant("jill", null, null, null), 100.0);
        when(serverUtils.addExpense(anyInt(),any(Expense.class))).thenThrow(new WebApplicationException(400));

        sut.createExpense(debt);

        verify(languageManager).bind("addExpense.badReqHeader");
        verify(languageManager).bind("addExpense.badReqBody");
        verify(alert).showAndWait();
    }

    @Test
    void testKeyPressedEscape() {
        DebtsCtrl spies = spy(sut);

        when(mockEvent.getCode()).thenReturn(KeyCode.ESCAPE);
        spies.keyPressed(mockEvent);

        // Verify that the abort method is called
        verify(spies).goBack();
    }

    @Test
    void testKeyPressedMWithControl() {
        DebtsCtrl spies = spy(sut);

        when(mockEvent.getCode()).thenReturn(KeyCode.M);
        when(mockEvent.isControlDown()).thenReturn(true);
        spies.keyPressed(mockEvent);

        // Verify that the abort method is called
        verify(spies).startMenu();
    }

    @Test
    void testKeyPressedOWithControl() {
        DebtsCtrl spies = spy(sut);

        when(mockEvent.getCode()).thenReturn(KeyCode.O);
        when(mockEvent.isControlDown()).thenReturn(true);


        spies.keyPressed(mockEvent);

        verify(spies).backToOverview();
    }

    @Test
    void testKeyPressedDefault() {
        DebtsCtrl spies = spy(sut);
        when(mockEvent.getCode()).thenReturn(KeyCode.P);

        spies.keyPressed(mockEvent);

        verify(spies, never()).goBack();
        verify(spies, never()).startMenu();
        verify(spies, never()).backToOverview();
    }
}