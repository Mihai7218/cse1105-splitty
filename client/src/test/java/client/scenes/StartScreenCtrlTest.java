package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class StartScreenCtrlTest {

    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
    }

    MainCtrl mainCtrl;
    ConfigInterface config;
    ObservableMap observableMap = FXCollections.observableHashMap();
    LanguageManager languageManager;
    ServerUtils serverUtils;
    StartScreenCtrl sut;
    TextField textField;

    @Start
    void setUp(Stage stage) {
        mainCtrl = mock(MainCtrl.class);
        textField = new TextField();
        config = new TestConfig();
        languageManager = mock(LanguageManager.class);
        serverUtils = mock(ServerUtils.class);
        sut = new StartScreenCtrl(mainCtrl, config, languageManager, serverUtils);
        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
        sut.setNewEventTitle(textField);
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
        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
        assertNull(config.getProperty("language"));
        verify(languageManager, times(2)).changeLanguage(Locale.ENGLISH);
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
    void createEventButtonHandlerNullText(FxRobot robot) {
        robot.interact(() -> sut.createEventButtonHandler());
        robot.clickOn("#okAlertButton");

        verify(serverUtils, never()).addEvent(any());
    }
}