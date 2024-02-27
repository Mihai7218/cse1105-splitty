package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(ApplicationExtension.class)
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
    private ListView<HBox> recentEvents;

    @Start
    void setup(Stage stage) {
        sut = new StartScreenCtrl(mainCtrl, config);
        lcb = new LanguageComboBox();
        newEventTitle = new TextField();
        createNewEventLabel = new Label();
        joinEventLabel = new Label();
        recentEventsLabel = new Label();
        createEventButton = new Button();
        eventInvite = new TextField();
        joinEventButton = new Button();
        recentEvents = new ListView<>();
        sut.setLanguages(lcb);
        sut.setNewEventTitle(newEventTitle);
        sut.setCreateNewEventLabel(createNewEventLabel);
        sut.setJoinEventLabel(joinEventLabel);
        sut.setRecentEventsLabel(recentEventsLabel);
        sut.setCreateEventButton(createEventButton);
        sut.setEventInvite(eventInvite);
        sut.setJoinEventButton(joinEventButton);
        sut.setRecentEvents(recentEvents);
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
        assertEquals("DefaultNewEventTitle", newEventTitle.getPromptText());
        assertEquals("DefaultCreateNewEventLabel", createNewEventLabel.getText());
        assertEquals("DefaultJoinEventLabel", joinEventLabel.getText());
        assertEquals("DefaultRecentEventsLabel", recentEventsLabel.getText());
        assertEquals("DefaultCreateEventButton", createEventButton.getText());
        assertEquals("DefaultEventInvite", eventInvite.getPromptText());
        assertEquals("DefaultJoinEventButton", joinEventButton.getText());
    }
}