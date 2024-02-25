package client.scenes;

import client.utils.LanguageCell;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class StartScreenCtrl implements Initializable {

    private static final Properties prop = new Properties();
    @FXML
    private ComboBox<String> languages;

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

    private final Properties languageConfig = new Properties();

    /**
     * Initializes the start screen view
     * @param url - URL of the FXML file
     * @param resourceBundle - resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            prop.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        languages.getItems().addAll(
                "de",
                "en",
                "fr",
                "nl"
        );
        languages.setCellFactory(param -> new LanguageCell());
        languages.setButtonCell(new LanguageCell());
        String language = getProperty("language");
        if (language == null) {
            language = "en";
        }
        languages.setValue(language);
        this.refreshLanguage();
    }

    /**
     * Getter for a property.
     * @param key - key of the property
     * @return - value of the property
     */
    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    /**
     * Setter for a property.
     * @param key - key of the property
     * @param value - value of the property
     * @throws IOException - if the file is not found
     */
    public static void setProperty(String key, String value) throws IOException {
        prop.setProperty(key, value);
        prop.store(new PrintWriter("src/main/resources/config.properties"),
                "Splitty Configuration File");
    }

    /**
     * Refreshes labels to the newly selected language.
     */
    public void refreshLanguage() {
        String language = getProperty("language");
        try {
            languageConfig.load(new FileInputStream(
                    String.format("src/main/resources/languages/%s.properties", language)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        newEventTitle.setPromptText(languageConfig.getProperty("newEventTitle"));
        createNewEventLabel.setText(languageConfig.getProperty("createNewEventLabel"));
        joinEventLabel.setText(languageConfig.getProperty("joinEventLabel"));
        recentEventsLabel.setText(languageConfig.getProperty("recentEventsLabel"));
        createEventButton.setText(languageConfig.getProperty("createEventButton"));
        eventInvite.setPromptText(languageConfig.getProperty("eventInvite"));
        joinEventButton.setText(languageConfig.getProperty("joinEventButton"));
    }

}