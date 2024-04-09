package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageComboBox;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

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

    //Needed for the tests to run headless.
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
    StartScreenCtrl mock;
    TextField createTextField;
    TextField joinTextField;
    Alert alert;
    StringProperty sp;
    LanguageComboBox languageComboBox;
    ListView<Event> recentEvents;
    ObservableList<Event> eventsList = FXCollections.observableArrayList();
    Button createEventButton;
    HBox createButtonHBox;
    HBox joinButtonHBox;
    Button joinEventButton;
    DoubleProperty createButtonHBoxWidthProperty;
    DoubleProperty joinButtonHBoxWidthProperty;
    DoubleProperty createEventButtonPrefWidthProperty;
    DoubleProperty joinEventButtonPrefWidthProperty;
    DoubleProperty widthAfter;
    Button settings;
    Button returnToServerSelect;

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
        createEventButton = mock(Button.class);
        joinEventButton = mock(Button.class);
        returnToServerSelect = mock(Button.class);
        settings = mock(Button.class);
        createButtonHBox = mock(HBox.class);
        joinButtonHBox = mock(HBox.class);
        recentEvents = mock(ListView.class);
        createButtonHBoxWidthProperty = mock(DoubleProperty.class);
        joinButtonHBoxWidthProperty = mock(DoubleProperty.class);
        createEventButtonPrefWidthProperty = mock(DoubleProperty.class);
        joinEventButtonPrefWidthProperty = mock(DoubleProperty.class);
        widthAfter = mock(DoubleProperty.class);


        when(recentEvents.getItems()).thenReturn(eventsList);
        sp = new SimpleStringProperty("Hello");
        when(languageManager.bind(anyString())).thenReturn(Bindings.createStringBinding(() -> ""));
        when(alert.contentTextProperty()).thenReturn(sp);
        when(alert.titleProperty()).thenReturn(sp);
        when(alert.headerTextProperty()).thenReturn(sp);
        sut = new StartScreenCtrl(mainCtrl, config, languageManager, serverUtils, alert);
        sut.createEventButton = createEventButton;
        sut.joinEventButton = joinEventButton;
        sut.returnToServerSelect = returnToServerSelect;
        sut.createButtonHBox = createButtonHBox;
        sut.joinButtonHBox = joinButtonHBox;
        sut.settings = settings;

        mock = spy(sut);
        doNothing().when(createEventButton).setGraphic(any(Node.class));
        doNothing().when(joinEventButton).setGraphic(any(Node.class));
        doNothing().when(settings).setGraphic(any(Node.class));

        sut.setRecentEvents(recentEvents);
        sut.setLanguages(new LanguageComboBox());
        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
        sut.setNewEventTitle(createTextField);
        sut.setEventInvite(joinTextField);
        sut.setLanguages(languageComboBox);
        when(languageManager.get()).thenReturn(observableMap);
        when(languageManager.bind(anyString())).thenReturn(Bindings.createStringBinding(() -> "Hello World"));
    }

    /**
     * Tests that, when there is a call to initialize and there is no set language,
     * it defaults to English.
     */
    @Test
    void initializeLanguageNotSet() {
        sut.initialize(mock(URL.class), mock(ResourceBundle.class));

        verify(languageManager, times(2)).changeLanguage(Locale.ENGLISH);
    }

    /**
     * Tests that, when a language is set in the config, it uses that language in the initialize method.
     */
    @Test
    void initializeLanguageSet() {
        config.setProperty("language", "fr");

        sut.initialize(mock(URL.class), mock(ResourceBundle.class));

        verify(languageManager).changeLanguage(Locale.FRENCH);
    }

    /**
     * Tests that, when the languageComboBox is mocked to return a specific language,
     * a call to change language will set the language to the one specified.
     */
    @Test
    void changeLanguage() {
        when(languageComboBox.getValue()).thenReturn("nl");

        sut.changeLanguage();

        assertEquals("nl", config.getProperty("language"));
        verify(languageManager).changeLanguage(Locale.of("nl"));
    }

    /**
     * Tests that the initialize method adds the events
     * from the config to the list of recent events.
     */
    @Test
    void refreshTest() {
        config.setProperty("recentEvents", "1");
        Event event = new Event("1", new Date(), new Date());
        when(serverUtils.getEvent(1)).thenReturn(event);

        sut.refresh();

        verify(languageManager).changeLanguage(Locale.ENGLISH);
        assertEquals(event, eventsList.getFirst());
    }


    /**
     * Tests the getter of the languageManager.
     */
    @Test
    void getLanguageManager() {
        assertEquals(observableMap, sut.getLanguageManager());
    }

    /**
     * Tests the setter of the languageManager.
     */
    @Test
    void setLanguageManager() {
        ObservableMap om2 = FXCollections.observableHashMap();

        sut.setLanguageManager(om2);

        verify(languageManager).set(om2);
    }

    /**
     * Tests the languageManager is returned when calling sut.languageManagerProperty().
     */
    @Test
    void languageManagerProperty() {
        assertEquals(languageManager, sut.languageManagerProperty());
    }

    /**
     * Tests that, when a recent event is added, the config and the list of recent events is updated
     */
    @Test
    void addRecentEvent() {
        Event event = new Event();
        event.setInviteCode(42);

        sut.addRecentEvent(event);

        assertEquals("42", config.getProperty("recentEvents"));
        assertEquals("5", config.getProperty("recentEventsLimit"));
        assertEquals(List.of(event), sut.getRecentEvents().getItems());
    }

    /**
     * Tests that, when the limit is reached, events are removed from the list.
     */
    @Test
    void recentEventsSizeLimit() {
        Event event = new Event();
        event.setInviteCode(42);
        Event filler = new Event();
        filler.setInviteCode(43);
        Stream.iterate(0, x -> x + 1).limit(10).forEach(x -> sut.getRecentEvents().getItems().add(filler));

        sut.addRecentEvent(event);

        assertEquals("42,43,43,43,43", config.getProperty("recentEvents"));
        assertEquals("5", config.getProperty("recentEventsLimit"));
        assertEquals(List.of(event, filler, filler, filler, filler), sut.getRecentEvents().getItems());
    }

    /**
     * Tests that the limit set in the config is respected.
     */
    @Test
    void recentEventsSizeLimitSetTo1() {
        config.setProperty("recentEventsLimit", "1");
        Event event = new Event();
        event.setInviteCode(42);
        Event filler = new Event();
        filler.setInviteCode(43);
        Stream.iterate(0, x -> x + 1).limit(10).forEach(x -> sut.getRecentEvents().getItems().add(filler));

        sut.addRecentEvent(event);

        assertEquals("42", config.getProperty("recentEvents"));
        assertEquals("1", config.getProperty("recentEventsLimit"));
        assertEquals(List.of(event), sut.getRecentEvents().getItems());
    }

    /**
     * Tests that the alert is shown when the text field has no text
     * and that there is no call to add the event to the serverUtils.
     */
    @Test
    void createEventButtonHandlerNullText() {
        sut.createEventButtonHandler();

        verify(alert).show();
        assertTrue(sp.isBound());
        verify(serverUtils, never()).addEvent(any());
    }

    /**
     * Tests that the alert is shown when the text field is empty
     * and that there is no call to add the event to the serverUtils.
     */
    @Test
    void createEventButtonHandlerEmptyText() {
        when(createTextField.getText()).thenReturn("");

        sut.createEventButtonHandler();

        verify(alert).show();
        assertTrue(sp.isBound());
        verify(serverUtils, never()).addEvent(any());
    }

    /**
     * Tests that, when the text field contains valid text, it sends the event to the server
     * and that the event is added to the recent events list.
     */
    @Test
    void createEventButtonHandlerSuccess() {
        when(createTextField.getText()).thenReturn("Hello, World!");
        Event fakeReturnedEvent = new Event("Hello, World", new Date(), new Date());
        when(serverUtils.addEvent(any())).thenReturn(fakeReturnedEvent);

        sut.createEventButtonHandler();

        verify(serverUtils).addEvent(any());
        assertEquals(fakeReturnedEvent, sut.getRecentEvents().getItems().getFirst());
    }

    /**
     * Tests that, when the server returns 404 Not Found, the corresponding alert is shown
     * and that the event is not added to the list of recent events.
     */
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
        assertTrue(sut.getRecentEvents().getItems().isEmpty());
    }

    /**
     * Tests that, when the server returns 500 Internal Server Error, the corresponding alert is shown
     * and that the event is not added to the list of recent events.
     */
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
        assertTrue(sut.getRecentEvents().getItems().isEmpty());
    }

    /**
     * Tests that there is a call to the createEventButtonHandler when the key pressed is ENTER.
     */
    @Test
    void createEventTextFieldHandlerEnter() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.ENTER);

        sut.createEventTextFieldHandler(ke);

        verify(alert).show();
        assertTrue(sp.isBound());
        verify(serverUtils, never()).addEvent(any());
    }

    /**
     * Tests that the alert is shown when the text field has no text
     * and that there is no call to get the event.
     */
    @Test
    void joinEventButtonHandlerEmpty() {
        sut.joinEventButtonHandler();

        verify(alert).show();
        assertTrue(sp.isBound());
        verify(serverUtils, never()).getEvent(anyInt());
    }


    /**
     * Tests that the alert is shown when the text field is empty
     * and that there is no call to get the event.
     */
    @Test
    void joinEventButtonHandlerEmptyText() {
        when(joinTextField.getText()).thenReturn("");

        sut.joinEventButtonHandler();

        verify(alert).show();
        assertTrue(sp.isBound());
        verify(serverUtils, never()).getEvent(anyInt());
    }

    /**
     * Tests that, when the text field contains valid text, it gets the event from the server
     * and that the event is added to the recent events list.
     */
    @Test
    void joinEventButtonHandlerSuccess() {
        when(joinTextField.getText()).thenReturn("1");
        Event fakeReturnedEvent = new Event("Hello, World", new Date(), new Date());
        when(serverUtils.getEvent(1)).thenReturn(fakeReturnedEvent);

        sut.joinEventButtonHandler();

        verify(serverUtils).getEvent(1);
        assertEquals(fakeReturnedEvent, sut.getRecentEvents().getItems().getFirst());
    }

    /**
     * Tests that, when the server returns 404 Not Found, the corresponding alert is shown
     * and that the event is not added to the list of recent events.
     */
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
        assertTrue(sut.getRecentEvents().getItems().isEmpty());
    }

    /**
     * Tests that, when the server returns 500 Internal Server Error, the corresponding alert is shown
     * and that the event is not added to the list of recent events.
     */
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
        assertTrue(sut.getRecentEvents().getItems().isEmpty());
    }

    /**
     * Tests that, when the server returns 400 Bad Request, the corresponding alert is shown
     * and that the event is not added to the list of recent events.
     */
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
        assertTrue(sut.getRecentEvents().getItems().isEmpty());
    }

    /**
     * Tests that, when the text field does not contain a number, there is no call to get the event from the server,
     * the corresponding alert is shown, and the event is not added to the list of recent events.
     */
    @Test
    void joinEventButtonHandlerNFE() {
        when(joinTextField.getText()).thenReturn("this should throw a NumberFormatException");
        when(languageManager.bind("startScreen.joinEvent400")).thenReturn(Bindings.createStringBinding(() -> "NFE"));

        sut.joinEventButtonHandler();

        verify(alert).show();
        verify(serverUtils, never()).getEvent(anyInt());
        assertEquals("NFE", sp.get());
        assertTrue(sut.getRecentEvents().getItems().isEmpty());
    }

    /**
     * Tests that there is a call to the createEventButtonHandler when the key pressed is ENTER.
     */
    @Test
    void joinEventTextFieldHandlerEnter() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.ENTER);

        sut.joinEventTextFieldHandler(ke);

        verify(alert).show();
        assertTrue(sp.isBound());
        verify(serverUtils, never()).addEvent(any());
    }

    /**
     * Tests the removeRecentEvent method.
     */
    @Test
    void removeRecentEvent() {
        Event event = new Event("1", new Date(), new Date());
        eventsList.add(event);

        sut.removeRecentEvent(event);

        assertTrue(eventsList.isEmpty());
        verify(recentEvents).refresh();
        assertEquals("", config.getProperty("recentEvents"));
    }
}