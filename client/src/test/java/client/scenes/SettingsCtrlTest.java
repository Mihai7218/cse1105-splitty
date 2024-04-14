package client.scenes;

import client.utils.*;
import jakarta.mail.MessagingException;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class SettingsCtrlTest {
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    SettingsCtrl sut;
    ConfigInterface config;
    LanguageManager languageManager;
    MainCtrl mainCtrl;
    CurrencyConverter currencyConverter;
    MailSender mailSender;
    LanguageComboBox languages;
    Button cancelButton;
    Button saveButton;
    Spinner<Integer> noRecentEvents;
    ChoiceBox<String> currency;
    TextField mailHost;
    TextField mailPort;
    TextField mailUser;
    TextField mailEmail;
    Alert alert;
    StringProperty tsp;
    StringProperty hsp;
    StringProperty csp;
    Deque<String> stack;
    Label notificationLabel;

    @Start
    void setup(Stage ignored) {
        config = new TestConfig();
        languageManager = mock(LanguageManager.class);
        mainCtrl = mock(MainCtrl.class);
        currencyConverter = mock(CurrencyConverter.class);
        mailSender = mock(MailSender.class);
        alert = mock(Alert.class);
        sut = new SettingsCtrl(config, languageManager, mainCtrl, currencyConverter, mailSender, alert);
        languages = new LanguageComboBox();
        sut.setLanguages(languages);
        cancelButton = new Button();
        sut.setCancelButton(cancelButton);
        saveButton = new Button();
        sut.setSaveButton(saveButton);
        noRecentEvents = new Spinner<>();
        sut.setNoRecentEvents(noRecentEvents);
        currency = new ChoiceBox<>();
        sut.setCurrency(currency);
        when(currencyConverter.getCurrencies()).thenReturn(List.of("EUR", "USD", "CHF"));
        mailHost = new TextField();
        sut.setMailHost(mailHost);
        mailPort = new TextField();
        sut.setMailPort(mailPort);
        mailUser = new TextField();
        sut.setMailUser(mailUser);
        mailEmail = new TextField();
        sut.setMailEmail(mailEmail);
        tsp = new SimpleStringProperty();
        hsp = new SimpleStringProperty();
        csp = new SimpleStringProperty();
        when(alert.titleProperty()).thenReturn(tsp);
        when(alert.headerTextProperty()).thenReturn(hsp);
        when(alert.contentTextProperty()).thenReturn(csp);
        stack = new LinkedList<>();
        when(languageManager.bind(anyString())).then(mock -> {
            stack.push(mock.getArgument(0));
            return mock(StringBinding.class);
        });
        notificationLabel = new Label();
        sut.setNotificationLabel(notificationLabel);

        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
    }

    @Test
    void testInitialize() {
        assertEquals(List.of("EUR", "USD", "CHF"), currency.getItems());
        assertFalse(languages.getItems().contains("template"));
    }

    @Test
    void testRefresh() {
        sut.refresh();

        assertEquals(5, noRecentEvents.getValue());
        assertEquals("EUR", currency.getValue());
        assertEquals("en", languages.getValue());
        assertEquals("", mailHost.getText());
        assertEquals("", mailPort.getText());
        assertEquals("", mailUser.getText());
        assertEquals("", mailEmail.getText());
    }

    @Test
    void testRefreshWithConfig() {
        config.setProperty("recentEventsLimit", "10");
        config.setProperty("currency", "CHF");
        config.setProperty("language", "fr");
        config.setProperty("mail.host", "smtp.gmail.com");
        config.setProperty("mail.port", "587");
        config.setProperty("mail.user", "email@example.com");
        config.setProperty("mail.email", "email@example.com");

        sut.refresh();

        assertEquals(10, noRecentEvents.getValue());
        assertEquals("CHF", currency.getValue());
        assertEquals("fr", languages.getValue());
        assertEquals("smtp.gmail.com", mailHost.getText());
        assertEquals("587", mailPort.getText());
        assertEquals("email@example.com", mailUser.getText());
        assertEquals("email@example.com", mailEmail.getText());
    }

    @Test
    void testCancelWithChangesToOverview() {
        config.setProperty("recentEventsLimit", "10");
        config.setProperty("currency", "CHF");
        config.setProperty("language", "fr");
        config.setProperty("mail.host", "smtp.gmail.com");
        config.setProperty("mail.port", "587");
        config.setProperty("mail.user", "email@example.com");
        config.setProperty("mail.email", "email@example.com");
        sut.setPrevScene(true);

        noRecentEvents.increment();
        when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));

        sut.cancel();

        assertEquals("settings.cancelAlert", stack.pop());
        assertEquals("commons.warning", stack.pop());
        assertEquals("commons.warning", stack.pop());
        verify(alert).setAlertType(Alert.AlertType.CONFIRMATION);
        verify(alert).setAlertType(Alert.AlertType.WARNING);

        assertEquals(0, noRecentEvents.getValue());
        assertNull(currency.getValue());
        assertNull(languages.getValue());
        assertEquals("", mailHost.getText());
        assertEquals("", mailPort.getText());
        assertEquals("", mailUser.getText());
        assertEquals("", mailEmail.getText());
        verify(mainCtrl, never()).showStartMenu();
        verify(mainCtrl).showOverview();
    }

    @Test
    void testKeyEventESCAPE() {
        config.setProperty("recentEventsLimit", "10");
        config.setProperty("currency", "CHF");
        config.setProperty("language", "fr");
        config.setProperty("mail.host", "smtp.gmail.com");
        config.setProperty("mail.port", "587");
        config.setProperty("mail.user", "email@example.com");
        config.setProperty("mail.email", "email@example.com");
        sut.setPrevScene(true);

        noRecentEvents.increment();
        when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));

        KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getCode()).thenReturn(KeyCode.ESCAPE);
        sut.keyPressed(keyEvent);

        assertEquals("settings.cancelAlert", stack.pop());
        assertEquals("commons.warning", stack.pop());
        assertEquals("commons.warning", stack.pop());
        verify(alert).setAlertType(Alert.AlertType.CONFIRMATION);
        verify(alert).setAlertType(Alert.AlertType.WARNING);

        assertEquals(0, noRecentEvents.getValue());
        assertNull(currency.getValue());
        assertNull(languages.getValue());
        assertEquals("", mailHost.getText());
        assertEquals("", mailPort.getText());
        assertEquals("", mailUser.getText());
        assertEquals("", mailEmail.getText());
        verify(mainCtrl, never()).showStartMenu();
        verify(mainCtrl).showOverview();
    }

    @Test
    void testKeyEventCTRLM() {
        config.setProperty("recentEventsLimit", "10");
        config.setProperty("currency", "CHF");
        config.setProperty("language", "fr");
        config.setProperty("mail.host", "smtp.gmail.com");
        config.setProperty("mail.port", "587");
        config.setProperty("mail.user", "email@example.com");
        config.setProperty("mail.email", "email@example.com");
        sut.setPrevScene(true);

        noRecentEvents.increment();
        when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));

        KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getCode()).thenReturn(KeyCode.M);
        when(keyEvent.isControlDown()).thenReturn(true);
        sut.keyPressed(keyEvent);

        assertEquals("settings.cancelAlert", stack.pop());
        assertEquals("commons.warning", stack.pop());
        assertEquals("commons.warning", stack.pop());
        verify(alert).setAlertType(Alert.AlertType.CONFIRMATION);
        verify(alert).setAlertType(Alert.AlertType.WARNING);

        assertEquals(0, noRecentEvents.getValue());
        assertNull(currency.getValue());
        assertNull(languages.getValue());
        assertEquals("", mailHost.getText());
        assertEquals("", mailPort.getText());
        assertEquals("", mailUser.getText());
        assertEquals("", mailEmail.getText());
        verify(mainCtrl).showStartMenu();
        verify(mainCtrl, never()).showOverview();
    }

    @Test
    void testKeyEventCTRLO() {
        config.setProperty("recentEventsLimit", "10");
        config.setProperty("currency", "CHF");
        config.setProperty("language", "fr");
        config.setProperty("mail.host", "smtp.gmail.com");
        config.setProperty("mail.port", "587");
        config.setProperty("mail.user", "email@example.com");
        config.setProperty("mail.email", "email@example.com");
        sut.setPrevScene(true);

        noRecentEvents.increment();
        when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));

        KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getCode()).thenReturn(KeyCode.O);
        when(keyEvent.isControlDown()).thenReturn(true);
        sut.keyPressed(keyEvent);

        assertEquals("settings.cancelAlert", stack.pop());
        assertEquals("commons.warning", stack.pop());
        assertEquals("commons.warning", stack.pop());
        verify(alert).setAlertType(Alert.AlertType.CONFIRMATION);
        verify(alert).setAlertType(Alert.AlertType.WARNING);

        assertEquals(0, noRecentEvents.getValue());
        assertNull(currency.getValue());
        assertNull(languages.getValue());
        assertEquals("", mailHost.getText());
        assertEquals("", mailPort.getText());
        assertEquals("", mailUser.getText());
        assertEquals("", mailEmail.getText());
        verify(mainCtrl, never()).showStartMenu();
        verify(mainCtrl).showOverview();
    }

    @Test
    void testKeyEventOtherwise() {
        config.setProperty("recentEventsLimit", "10");
        config.setProperty("currency", "CHF");
        config.setProperty("language", "fr");
        config.setProperty("mail.host", "smtp.gmail.com");
        config.setProperty("mail.port", "587");
        config.setProperty("mail.user", "email@example.com");
        config.setProperty("mail.email", "email@example.com");
        sut.setPrevScene(true);

        sut.refresh();

        KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getCode()).thenReturn(KeyCode.A);
        when(keyEvent.isControlDown()).thenReturn(true);
        sut.keyPressed(keyEvent);

        assertTrue(stack.isEmpty());
        verify(alert, never()).setAlertType(Alert.AlertType.CONFIRMATION);
        verify(alert, never()).setAlertType(Alert.AlertType.WARNING);

        assertEquals(10, noRecentEvents.getValue());
        assertEquals("CHF", currency.getValue());
        assertEquals("fr", languages.getValue());
        assertEquals("smtp.gmail.com", mailHost.getText());
        assertEquals("587", mailPort.getText());
        assertEquals("email@example.com", mailUser.getText());
        assertEquals("email@example.com", mailEmail.getText());
    }

    @Test
    void testCancelWithChangesToStartScreen() {
        config.setProperty("recentEventsLimit", "10");
        config.setProperty("currency", "CHF");
        config.setProperty("language", "fr");
        config.setProperty("mail.host", "smtp.gmail.com");
        config.setProperty("mail.port", "587");
        config.setProperty("mail.user", "email@example.com");
        config.setProperty("mail.email", "email@example.com");
        sut.setPrevScene(false);

        noRecentEvents.increment();
        when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));

        sut.cancel();

        assertEquals("settings.cancelAlert", stack.pop());
        assertEquals("commons.warning", stack.pop());
        assertEquals("commons.warning", stack.pop());
        verify(alert).setAlertType(Alert.AlertType.CONFIRMATION);
        verify(alert).setAlertType(Alert.AlertType.WARNING);

        assertEquals(0, noRecentEvents.getValue());
        assertNull(currency.getValue());
        assertNull(languages.getValue());
        assertEquals("", mailHost.getText());
        assertEquals("", mailPort.getText());
        assertEquals("", mailUser.getText());
        assertEquals("", mailEmail.getText());
        verify(mainCtrl).showStartMenu();
        verify(mainCtrl, never()).showOverview();
    }

    @Test
    void testCancelNoAlert() {
        config.setProperty("recentEventsLimit", "10");
        config.setProperty("currency", "CHF");
        config.setProperty("language", "fr");
        config.setProperty("mail.host", "smtp.gmail.com");
        config.setProperty("mail.port", "587");
        config.setProperty("mail.user", "email@example.com");
        config.setProperty("mail.email", "email@example.com");

        sut.refresh();

        sut.setPrevScene(false);

        when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));

        sut.cancel();

        assertTrue(stack.isEmpty());
        verify(alert, never()).setAlertType(Alert.AlertType.CONFIRMATION);
        verify(alert, never()).setAlertType(Alert.AlertType.WARNING);

        assertEquals(0, noRecentEvents.getValue());
        assertNull(currency.getValue());
        assertNull(languages.getValue());
        assertEquals("", mailHost.getText());
        assertEquals("", mailPort.getText());
        assertEquals("", mailUser.getText());
        assertEquals("", mailEmail.getText());
        verify(mainCtrl).showStartMenu();
        verify(mainCtrl, never()).showOverview();
    }

    @Test
    void testSave() {
        sut.setPrevScene(false);

        sut.refresh();

        noRecentEvents.increment(5);
        currency.setValue("CHF");
        languages.setValue("fr");
        mailHost.setText("smtp.gmail.com");
        mailPort.setText("587");
        mailUser.setText("email@example.com");
        mailEmail.setText("email@example.com");
        when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));

        StartScreenCtrl startScreenCtrl = mock(StartScreenCtrl.class);
        OverviewCtrl overviewCtrl = mock(OverviewCtrl.class);
        when(mainCtrl.getStartScreenCtrl()).thenReturn(startScreenCtrl);
        when(mainCtrl.getOverviewCtrl()).thenReturn(overviewCtrl);

        sut.save();

        assertEquals("10", config.getProperty("recentEventsLimit"));
        assertEquals("CHF", config.getProperty("currency"));
        assertEquals("fr", config.getProperty("language"));
        assertEquals("smtp.gmail.com", config.getProperty("mail.host"));
        assertEquals("587", config.getProperty("mail.port"));
        assertEquals("email@example.com", config.getProperty("mail.user"));
        assertEquals("email@example.com", config.getProperty("mail.email"));
        verify(startScreenCtrl).removeExcess();
        verify(overviewCtrl).populateExpenses();
        verify(overviewCtrl).populateParticipants();
        verify(mainCtrl).showStartMenu();
        verify(mainCtrl, never()).showOverview();
    }


    @Test
    void testKeyPressedENTER() {
        sut.setPrevScene(false);

        sut.refresh();

        noRecentEvents.increment(5);
        currency.setValue("CHF");
        languages.setValue("fr");
        mailHost.setText("smtp.gmail.com");
        mailPort.setText("587");
        mailUser.setText("email@example.com");
        mailEmail.setText("email@example.com");
        when(alert.showAndWait()).thenReturn(Optional.of(ButtonType.OK));

        StartScreenCtrl startScreenCtrl = mock(StartScreenCtrl.class);
        OverviewCtrl overviewCtrl = mock(OverviewCtrl.class);
        when(mainCtrl.getStartScreenCtrl()).thenReturn(startScreenCtrl);
        when(mainCtrl.getOverviewCtrl()).thenReturn(overviewCtrl);

        KeyEvent keyEvent = mock(KeyEvent.class);
        when(keyEvent.getCode()).thenReturn(KeyCode.ENTER);
        sut.keyPressed(keyEvent);

        assertEquals("10", config.getProperty("recentEventsLimit"));
        assertEquals("CHF", config.getProperty("currency"));
        assertEquals("fr", config.getProperty("language"));
        assertEquals("smtp.gmail.com", config.getProperty("mail.host"));
        assertEquals("587", config.getProperty("mail.port"));
        assertEquals("email@example.com", config.getProperty("mail.user"));
        assertEquals("email@example.com", config.getProperty("mail.email"));
        verify(startScreenCtrl).removeExcess();
        verify(overviewCtrl).populateExpenses();
        verify(overviewCtrl).populateParticipants();
        verify(mainCtrl).showStartMenu();
        verify(mainCtrl, never()).showOverview();
    }

    @Test
    void testGetLanguageManager() {
        ObservableMap om = FXCollections.observableHashMap();
        when(languageManager.get()).thenReturn(om);
        assertEquals(om, sut.getLanguageManager());
    }

    @Test
    void testSetLanguageManager() {
        ObservableMap om2 = FXCollections.observableHashMap();
        LanguageManager languageManager2 = mock(LanguageManager.class);
        when(languageManager2.get()).thenReturn(om2);
        AtomicReference<ObservableMap> om = new AtomicReference<>(FXCollections.observableHashMap());
        doAnswer(mock -> {
            om.set(mock.getArgument(0));
            return null;
        }).when(languageManager).set(any());
        when(languageManager.get()).thenReturn(om.get());
        sut.setLanguageManager(languageManager2);
        assertEquals(om2, sut.getLanguageManager());
    }

    @Test
    void testTestMailMissingFields() {
        mailHost.setText("not empty");
        mailPort.setText("42");
        mailUser.setText("user@");
        mailEmail.setText("");

        sut.testMail();

        assertEquals("mail.missingFields", stack.pop());
        verify(alert).showAndWait();
    }

    @Test
    void testTestMailNoPassword() throws MessagingException {
        mailHost.setText("not empty");
        mailPort.setText("42");
        mailUser.setText("user@");
        mailEmail.setText("user@");
        doAnswer(x -> {
            throw new MissingPasswordException();
        }).when(mailSender).sendTestMail("not empty", "42", "user@", "user@");

        sut.testMail();

        assertEquals("mail.noPassword", stack.pop());
        verify(alert).showAndWait();
    }

    @Test
    void testTestMailMessagingException() throws MessagingException {
        mailHost.setText("not empty");
        mailPort.setText("42");
        mailUser.setText("user@");
        mailEmail.setText("user@");
        doAnswer(x -> {
            throw new MessagingException();
        }).when(mailSender).sendTestMail("not empty", "42", "user@", "user@");

        sut.testMail();

        assertFalse(alert.contentTextProperty().isBound());
        verify(alert).showAndWait();
    }

    @Test
    void testTestMailSuccess() throws MessagingException {
        mailHost.setText("not empty");
        mailPort.setText("42");
        mailUser.setText("user@");
        mailEmail.setText("user@");

        sut.testMail();

        assertEquals("settings.emailTestConfirmation", stack.pop());
        assertEquals("mail.sending", stack.pop());
        verify(mailSender).sendTestMail("not empty", "42", "user@", "user@");
        verify(alert, never()).showAndWait();
    }

    @Test
    void testHighlightMissing() {
        sut.highlightMissing(true, true, true, true);

        assertEquals("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;", mailHost.getStyle());
        assertEquals("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;", mailPort.getStyle());
        assertEquals("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;", mailUser.getStyle());
        assertEquals("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;", mailEmail.getStyle());
    }

    @Test
    void testRemoveHighlight() {
        sut.removeHighlight();

        assertEquals("-fx-border-color: none;", mailHost.getStyle());
        assertEquals("-fx-border-color: none;", mailPort.getStyle());
        assertEquals("-fx-border-color: none;", mailUser.getStyle());
        assertEquals("-fx-border-color: none;", mailEmail.getStyle());
    }
}