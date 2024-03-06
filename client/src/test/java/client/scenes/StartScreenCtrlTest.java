package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class StartScreenCtrlTest {

    MainCtrl mainCtrl;
    ConfigInterface config;
    ObservableMap observableMap = FXCollections.observableHashMap();
    LanguageManager languageManager;
    ServerUtils serverUtils;
    StartScreenCtrl sut;

    @BeforeEach
    void setUp() {
        mainCtrl = mock(MainCtrl.class);
        config = new TestConfig();
        languageManager = mock(LanguageManager.class);
        serverUtils = mock(ServerUtils.class);
        sut = new StartScreenCtrl(mainCtrl, config, languageManager, serverUtils);
        when(languageManager.get()).thenReturn(observableMap);
    }

    @Test
    void initializeLanguageNotSet() {
        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
        verify(languageManager).changeLanguage(Locale.ENGLISH);
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
        verify(languageManager).changeLanguage(Locale.ENGLISH);
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
}