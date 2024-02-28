package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageComboBox;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.net.URL;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StartScreenCtrlTest {
    MainCtrl mainCtrl = mock(MainCtrl.class);
    ConfigInterface config = new TestConfig();
    StartScreenCtrl sut;
    LanguageComboBox lcb;
    private TextField newEventTitle;
    private Label createNewEventLabel;
    private Label joinEventLabel;
    private Label recentEventsLabel;
    private Button createEventButton;
    private TextField eventInvite;
    private Button joinEventButton;
    private String currentLCBValue;

    @BeforeAll
    static void initJavaFX() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setup() {
        sut = new StartScreenCtrl(mainCtrl, config);
        lcb = mock(LanguageComboBox.class);
        doAnswer((Answer<Void>) invocationOnMock -> {
            currentLCBValue = invocationOnMock.getArgument(0);
            return null;
        }).when(lcb).setValue(anyString());
        doAnswer((Answer<String>) invocationOnMock -> currentLCBValue).when(lcb).getValue();
        newEventTitle = mock(TextField.class);
        createNewEventLabel = mock(Label.class);
        joinEventLabel = mock(Label.class);
        recentEventsLabel = mock(Label.class);
        createEventButton = mock(Button.class);
        eventInvite = mock(TextField.class);
        joinEventButton = mock(Button.class);
        sut.setLanguages(lcb);
        sut.setNewEventTitle(newEventTitle);
        sut.setCreateNewEventLabel(createNewEventLabel);
        sut.setJoinEventLabel(joinEventLabel);
        sut.setRecentEventsLabel(recentEventsLabel);
        sut.setCreateEventButton(createEventButton);
        sut.setEventInvite(eventInvite);
        sut.setJoinEventButton(joinEventButton);
    }
    @Test
    void initialize() {
        config.setProperty("language", "fr");
        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
        assertEquals("fr", lcb.getValue());
    }
    @Test
    void initializeNoLangInConfig() {
        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
        assertEquals("en", lcb.getValue());
    }

    @Test
    void changeLanguage() {
        lcb.setValue("test");
        sut.changeLanguage();
        assertEquals("default", config.getProperty("language"));
    }

    @Test
    void refreshLanguage() {
        sut.refreshLanguage();
        verify(newEventTitle).setPromptText(anyString());
        verify(createNewEventLabel).setText(anyString());
        verify(joinEventLabel).setText(anyString());
        verify(recentEventsLabel).setText(anyString());
        verify(createEventButton).setText(anyString());
        verify(eventInvite).setPromptText(anyString());
        verify(joinEventButton).setText(anyString());

    }
}