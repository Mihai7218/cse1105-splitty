package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageComboBox;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.framework.junit5.Stop;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class StartScreenCtrlTest {

    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    MainCtrl mainCtrl;
    ConfigInterface config;
    ObservableMap observableMap = FXCollections.observableHashMap();
    LanguageManager languageManager;
    ServerUtils serverUtils;
    StartScreenCtrl sut;
    TextField createTextField;
    TextField joinTextField;
    Alert alert;
    StringProperty sp;
    LanguageComboBox languageComboBox;

    @Start
    void setUp(Stage stage) {
        mainCtrl = mock(MainCtrl.class);
        createTextField = mock(TextField.class);
        joinTextField = mock(TextField.class);
        config = new TestConfig();
        languageManager = mock(LanguageManager.class);
        serverUtils = mock(ServerUtils.class);
        languageComboBox = mock(LanguageComboBox.class);
        alert = mock(Alert.class);
        sp = new SimpleStringProperty("Hello");
        when(languageManager.bind(anyString())).thenReturn(Bindings.createStringBinding(() -> ""));
        when(alert.contentTextProperty()).thenReturn(sp);
        when(alert.titleProperty()).thenReturn(sp);
        when(alert.headerTextProperty()).thenReturn(sp);
        sut = new StartScreenCtrl(mainCtrl, config, languageManager, serverUtils, alert);
        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
        sut.setNewEventTitle(createTextField);
        sut.setEventInvite(joinTextField);
        sut.setLanguages(languageComboBox);
        when(languageManager.get()).thenReturn(observableMap);
        when(languageManager.bind(anyString())).thenReturn(Bindings.createStringBinding(() -> "Hello World"));
    }

    @Test
    void initializeLanguageNotSet() {
        sut.initialize(mock(URL.class), mock(ResourceBundle.class));

        verify(languageManager, times(2)).changeLanguage(Locale.ENGLISH);
    }
    @Test
    void initializeLanguageSet() {
        config.setProperty("language", "fr");

        sut.initialize(mock(URL.class), mock(ResourceBundle.class));

        verify(languageManager).changeLanguage(Locale.FRENCH);
    }


    @Test
    void changeLanguage() {
        when(languageComboBox.getValue()).thenReturn("nl");

        sut.changeLanguage();

        assertEquals("nl", config.getProperty("language"));
        verify(languageManager).changeLanguage(Locale.of("nl"));
    }

    @Test
    void getLanguageManager() {
        assertEquals(observableMap, sut.getLanguageManager());
    }

    @Test
    void setLanguageManager() {
        ObservableMap om2 = FXCollections.observableHashMap();

        sut.setLanguageManager(om2);

        verify(languageManager).set(om2);
    }

    @Test
    void languageManagerProperty() {
        assertEquals(languageManager, sut.languageManagerProperty());
    }

    @Test
    void addRecentEvent() {
        Event event = new Event();
        event.setInviteCode(42);

        sut.addRecentEvent(event);

        assertEquals("42", config.getProperty("recentEvents"));
        assertEquals("5", config.getProperty("recentEventsLimit"));
        assertEquals(List.of(event), sut.getRecentEventsList());
    }

    @Test
    void recentEventsSizeLimit() {
        Event event = new Event();
        event.setInviteCode(42);
        Event filler = new Event();
        filler.setInviteCode(43);
        Stream.iterate(0, x -> x + 1).limit(10).forEach(x -> sut.getRecentEventsList().add(filler));

        sut.addRecentEvent(event);

        assertEquals("42,43,43,43,43", config.getProperty("recentEvents"));
        assertEquals("5", config.getProperty("recentEventsLimit"));
        assertEquals(List.of(event, filler, filler, filler, filler), sut.getRecentEventsList());
    }

    @Test
    void recentEventsSizeLimitSetTo1() {
        config.setProperty("recentEventsLimit", "1");
        Event event = new Event();
        event.setInviteCode(42);
        Event filler = new Event();
        filler.setInviteCode(43);
        Stream.iterate(0, x -> x + 1).limit(10).forEach(x -> sut.getRecentEventsList().add(filler));

        sut.addRecentEvent(event);

        assertEquals("42", config.getProperty("recentEvents"));
        assertEquals("1", config.getProperty("recentEventsLimit"));
        assertEquals(List.of(event), sut.getRecentEventsList());
    }

    @Test
    void createEventButtonHandlerNullText() {
        sut.createEventButtonHandler();

        verify(alert).show();
        assertTrue(sp.isBound());
        verify(serverUtils, never()).addEvent(any());
    }

    @Test
    void createEventButtonHandlerEmptyText() {
        when(createTextField.getText()).thenReturn("");

        sut.createEventButtonHandler();

        verify(alert).show();
        assertTrue(sp.isBound());
        verify(serverUtils, never()).addEvent(any());
    }

    @Test
    void createEventButtonHandlerSuccess() {
        when(createTextField.getText()).thenReturn("Hello, World!");
        Event fakeReturnedEvent = new Event("Hello, World", new Date(), new Date());
        when(serverUtils.addEvent(any())).thenReturn(fakeReturnedEvent);

        sut.createEventButtonHandler();

        verify(serverUtils).addEvent(any());
        assertEquals(fakeReturnedEvent, sut.getRecentEventsList().getFirst());
    }

    @Test
    void createEventButtonHandler404() {
        when(createTextField.getText()).thenReturn("Hello, World!");
        WebApplicationException ex = new WebApplicationException(Response.status(404).build());
        when(serverUtils.addEvent(any())).thenThrow(ex);
        when(languageManager.bind("startScreen.createEvent404")).thenReturn(Bindings.createStringBinding(() -> "404"));

        sut.createEventButtonHandler();

        verify(alert).show();
        verify(serverUtils).addEvent(any());
        assertEquals("404", sp.get());
        assertTrue(sut.getRecentEventsList().isEmpty());
    }

    @Test
    void createEventButtonHandler500() {
        when(createTextField.getText()).thenReturn("Hello, World!");
        WebApplicationException ex = new WebApplicationException(Response.status(500).build());
        when(serverUtils.addEvent(any())).thenThrow(ex);
        when(languageManager.bind("startScreen.createEvent500")).thenReturn(Bindings.createStringBinding(() -> "500"));

        sut.createEventButtonHandler();

        verify(alert).show();
        verify(serverUtils).addEvent(any());
        assertEquals("500", sp.get());
        assertTrue(sut.getRecentEventsList().isEmpty());
    }

    @Test
    void createEventTextFieldHandlerEnter() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.ENTER);

        sut.createEventTextFieldHandler(ke);

        verify(alert).show();
        assertTrue(sp.isBound());
        verify(serverUtils, never()).addEvent(any());
    }

    @Test
    void joinEventButtonHandlerEmpty() {
        sut.joinEventButtonHandler();

        verify(alert).show();
        assertTrue(sp.isBound());
        verify(serverUtils, never()).getEvent(anyInt());
    }

    @Test
    void joinEventButtonHandlerEmptyText() {
        when(joinTextField.getText()).thenReturn("");

        sut.joinEventButtonHandler();

        verify(alert).show();
        assertTrue(sp.isBound());
        verify(serverUtils, never()).getEvent(anyInt());
    }

    @Test
    void joinEventButtonHandlerSuccess() {
        when(joinTextField.getText()).thenReturn("1");
        Event fakeReturnedEvent = new Event("Hello, World", new Date(), new Date());
        when(serverUtils.getEvent(1)).thenReturn(fakeReturnedEvent);

        sut.joinEventButtonHandler();

        verify(serverUtils).getEvent(1);
        assertEquals(fakeReturnedEvent, sut.getRecentEventsList().getFirst());
    }

    @Test
    void joinEventButtonHandler404() {
        when(joinTextField.getText()).thenReturn("1");
        WebApplicationException ex = new WebApplicationException(Response.status(404).build());
        when(serverUtils.getEvent(1)).thenThrow(ex);
        when(languageManager.bind("startScreen.joinEvent404")).thenReturn(Bindings.createStringBinding(() -> "404"));

        sut.joinEventButtonHandler();

        verify(alert).show();
        verify(serverUtils).getEvent(1);
        assertEquals("404", sp.get());
        assertTrue(sut.getRecentEventsList().isEmpty());
    }

    @Test
    void joinEventButtonHandler500() {
        when(joinTextField.getText()).thenReturn("1");
        WebApplicationException ex = new WebApplicationException(Response.status(500).build());
        when(serverUtils.getEvent(1)).thenThrow(ex);
        when(languageManager.bind("startScreen.joinEvent500")).thenReturn(Bindings.createStringBinding(() -> "500"));

        sut.joinEventButtonHandler();

        verify(alert).show();
        verify(serverUtils).getEvent(1);
        assertEquals("500", sp.get());
        assertTrue(sut.getRecentEventsList().isEmpty());
    }

    @Test
    void joinEventButtonHandler400() {
        when(joinTextField.getText()).thenReturn("1");
        WebApplicationException ex = new WebApplicationException(Response.status(400).build());
        when(serverUtils.getEvent(1)).thenThrow(ex);
        when(languageManager.bind("startScreen.joinEvent400")).thenReturn(Bindings.createStringBinding(() -> "400"));

        sut.joinEventButtonHandler();

        verify(alert).show();
        verify(serverUtils).getEvent(1);
        assertEquals("400", sp.get());
        assertTrue(sut.getRecentEventsList().isEmpty());
    }

    @Test
    void joinEventButtonHandlerNFE() {
        when(joinTextField.getText()).thenReturn("this should throw a NumberFormatException");
        when(languageManager.bind("startScreen.joinEvent400")).thenReturn(Bindings.createStringBinding(() -> "NFE"));

        sut.joinEventButtonHandler();

        verify(alert).show();
        verify(serverUtils, never()).getEvent(anyInt());
        assertEquals("NFE", sp.get());
        assertTrue(sut.getRecentEventsList().isEmpty());
    }

    @Test
    void joinEventTextFieldHandlerEnter() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.ENTER);

        sut.joinEventTextFieldHandler(ke);

        verify(alert).show();
        assertTrue(sp.isBound());
        verify(serverUtils, never()).addEvent(any());
    }

    @Stop
    void stop() {
    }
}