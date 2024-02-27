package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageComboBox;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;
import java.util.ResourceBundle;

public class StartScreenCtrl implements Initializable {

    private ConfigInterface config;

    @FXML
    private LanguageComboBox languages;

    @FXML
    private TextField newEventTitle;

    @FXML
    private Label createNewEventLabel;

    @FXML
    private Label joinEventLabel;

    @FXML
    private Label recentEventsLabel;

    @FXML
    private Button createEventButton;

    @FXML
    private TextField eventInvite;

    @FXML
    private Button joinEventButton;

    @FXML
    private ListView<HBox> recentEvents;

    private final MainCtrl mainCtrl;

    /**
     * Constructor for the StartScreenCtrl
     * @param mainCtrl - main controller
     * @param config - config
     */
    @Inject
    public StartScreenCtrl(MainCtrl mainCtrl, ConfigInterface config) {
        this.mainCtrl = mainCtrl;
        this.config = config;
    }

    /**
     * Initializes the start screen view
     * @param url - URL of the FXML file
     * @param resourceBundle - resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String language = config.getProperty("language");
        if (language == null) {
            language = "en";
        }
        languages.setValue(language);
        this.refreshLanguage();
    }

    /**
     * Changes language
     */
    public void changeLanguage() {
        String language = languages.getValue();
        config.setProperty("language", language);
        this.refreshLanguage();
    }
    /**
     * Refreshes labels to the newly selected language.
     */
    public void refreshLanguage() {
        String language = config.getProperty("language");
        Properties languageConfig = getLanguageConfig(language);
        newEventTitle.setPromptText(languageConfig.getProperty("startScreen.newEventTitle"));
        createNewEventLabel.setText(languageConfig.getProperty("startScreen.createNewEventLabel"));
        joinEventLabel.setText(languageConfig.getProperty("startScreen.joinEventLabel"));
        recentEventsLabel.setText(languageConfig.getProperty("startScreen.recentEventsLabel"));
        createEventButton.setText(languageConfig.getProperty("startScreen.createEventButton"));
        eventInvite.setPromptText(languageConfig.getProperty("startScreen.eventInvite"));
        joinEventButton.setText(languageConfig.getProperty("startScreen.joinEventButton"));
    }

    /**
     * Getter for the language file.
     * @param language - target language.
     * @return - Properties containing the strings in the specified language.
     */
    private Properties getLanguageConfig (String language) {
        Properties languageConfig = new Properties();
        try {
            languageConfig.load(new FileInputStream(
                    String.valueOf(Path.of("client", "src", "main",
                            "resources", "languages", language + ".properties"))));
        } catch (IOException e) {
            try { //defaults to English if the language in the config is not found
                config.setProperty("language", "en");
                languageConfig.load(new FileInputStream(
                        String.valueOf(Path.of("client", "src", "main",
                                "resources", "languages", "en.properties"))));
            } catch (IOException ex) {
                //defaults to strings with the name of the fields if English is not found
                //(should only happen in testing)
                config.setProperty("language", "default");
                try {
                    languageConfig.load(new StringReader("""
                            language.name=DefaultName
                            startScreen.newEventTitle=DefaultNewEventTitle
                            startScreen.createNewEventLabel=DefaultCreateNewEventLabel
                            startScreen.joinEventLabel=DefaultJoinEventLabel
                            startScreen.recentEventsLabel=DefaultRecentEventsLabel
                            startScreen.createEventButton=DefaultCreateEventButton
                            startScreen.eventInvite=DefaultEventInvite
                            startScreen.joinEventButton=DefaultJoinEventButton"""));
                } catch (IOException exc) {
                    throw new RuntimeException(exc);
                }
            }
        }
        return languageConfig;
    }

    void setLanguages(LanguageComboBox languages) {
        this.languages = languages;
    }

    void setNewEventTitle(TextField newEventTitle) {
        this.newEventTitle = newEventTitle;
    }

    void setCreateNewEventLabel(Label createNewEventLabel) {
        this.createNewEventLabel = createNewEventLabel;
    }

    void setJoinEventLabel(Label joinEventLabel) {
        this.joinEventLabel = joinEventLabel;
    }

    void setRecentEventsLabel(Label recentEventsLabel) {
        this.recentEventsLabel = recentEventsLabel;
    }

    void setCreateEventButton(Button createEventButton) {
        this.createEventButton = createEventButton;
    }

    void setEventInvite(TextField eventInvite) {
        this.eventInvite = eventInvite;
    }

    void setJoinEventButton(Button joinEventButton) {
        this.joinEventButton = joinEventButton;
    }

    void setRecentEvents(ListView<HBox> recentEvents) {
        this.recentEvents = recentEvents;
    }
}