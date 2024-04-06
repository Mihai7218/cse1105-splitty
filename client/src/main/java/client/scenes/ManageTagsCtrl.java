package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class ManageTagsCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;

    @FXML
    public Button cancel;
    public ListView tagsListView;


    /**
     * Constructor for the ManageTagsCtrl
     *
     * @param mainCtrl - main controller
     * @param config   - config
     */
    @Inject
    public ManageTagsCtrl(MainCtrl mainCtrl,
                       ConfigInterface config,
                       LanguageManager languageManager,
                       ServerUtils serverUtils,
                       CurrencyConverter currencyConverter) {
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageManager = languageManager;
        this.serverUtils = serverUtils;
    }

    /**
     * Initializes the start screen view
     *
     * @param url            - URL of the FXML file
     * @param resourceBundle - resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String language = config.getProperty("language");
        if (language == null) {
            language = "en";
        }
        this.refreshLanguage();
        tagsListView.setCellFactory(x ->
                new TagListCell(mainCtrl, languageManager, config, serverUtils));
    }

    /**
     * Method that refreshes the language.
     */
    private void refreshLanguage() {
        String language = config.getProperty("language");
        if (language == null) {
            language = "en";
        }
        languageManager.changeLanguage(Locale.of(language));
    }

    /**
     * Getter for the language manager observable map.
     *
     * @return - the language manager observable map.
     */
    public ObservableMap<String, Object> getLanguageManager() {
        return languageManager.get();
    }

    /**
     * Setter for the language manager observable map.
     *
     * @param languageManager - the language manager observable map.
     */
    public void setLanguageManager(ObservableMap<String, Object> languageManager) {
        this.languageManager.set(languageManager);
    }

    /**
     * Getter for the language manager property.
     *
     * @return - the language manager property.
     */
    public LanguageManager languageManagerProperty() {
        return languageManager;
    }

    /**
     * Refreshes the list of Tags
     */
    public void refresh() {
        tagsListView.getItems().addAll(mainCtrl.getEvent().getTagsList());
        tagsListView.refresh();
    }
}
