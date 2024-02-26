package client.scenes;

import client.utils.Config;
import client.utils.LanguageCell;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

public class StartScreenCtrl implements Initializable {

    @Inject
    Config config;

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
        File languagesFolder = new File(String.valueOf(Path.of("client",
                "src", "main", "resources", "languages")));
        List<String> languageNames = Arrays.stream(Objects
                .requireNonNull(languagesFolder.listFiles()))
                .map(File::getName)
                .filter(name -> !name.equals("template.properties"))
                .map(filename -> filename.substring(0, 2)).toList();
        languages.getItems().addAll(languageNames);
        languages.setCellFactory(param -> new LanguageCell(config));
        languages.setButtonCell(new LanguageCell(config));
        String language = config.getProperty("language");
        if (language == null) {
            language = "en";
        }
        languages.setValue(language);
        this.refreshLanguage();
    }

    /**
     * Refreshes labels to the newly selected language.
     */
    public void refreshLanguage() {
        String language = languages.getValue();
        try {
            config.setProperty("language", language);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                throw new RuntimeException(ex);
            }
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