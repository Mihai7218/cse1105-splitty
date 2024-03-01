package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageComboBox;
import client.utils.LanguageManager;
import com.google.inject.Inject;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class StartScreenCtrl implements Initializable {

    private ConfigInterface config;

    @FXML
    private LanguageComboBox languages;

    private final MainCtrl mainCtrl;

    private LanguageManager languageManager;

    /**
     * Constructor for the StartScreenCtrl
     * @param mainCtrl - main controller
     * @param config - config
     */
    @Inject
    public StartScreenCtrl(MainCtrl mainCtrl, ConfigInterface config, LanguageManager languageManager) {
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageManager = languageManager;
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

    private void refreshLanguage() {
        String language = config.getProperty("language");
        if (language == null) {
            language = "en";
        }
        languageManager.changeLanguage(Locale.of(language));
    }

    public ObservableMap<String, Object> getLanguageManager() {
        return languageManager.get();
    }

    public LanguageManager languageManagerProperty() {
        return languageManager;
    }

    public void setLanguageManager(ObservableMap<String, Object> languageManager) {
        this.languageManager.set(languageManager);
    }
}