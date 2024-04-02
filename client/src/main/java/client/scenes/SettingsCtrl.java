package client.scenes;

import client.utils.ConfigInterface;
import client.utils.CurrencyConverter;
import client.utils.LanguageComboBox;
import client.utils.LanguageManager;
import com.google.inject.Inject;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsCtrl implements Initializable {

    private final ConfigInterface config;
    private final LanguageManager languageManager;
    private final MainCtrl mainCtrl;
    private final CurrencyConverter currencyConverter;
    @FXML
    private LanguageComboBox languages;
    @FXML
    private Button saveButton;
    @FXML
    private ChoiceBox<String> currency;
    @FXML
    private Spinner<Integer> noRecentEvents;
    @FXML
    private Button cancelButton;
    private boolean prevScene = false;

    /**
     * Constructor for the settings controller.
     * @param config - the config.
     * @param languageManager - the language manager.
     * @param mainCtrl - the main controller.
     */
    @Inject
    public SettingsCtrl(ConfigInterface config,
                        LanguageManager languageManager,
                        MainCtrl mainCtrl,
                        CurrencyConverter currencyConverter) {
        this.config = config;
        this.languageManager = languageManager;
        this.mainCtrl = mainCtrl;
        this.currencyConverter = currencyConverter;
    }

    /**
     * The initialize method for the settings controller.
     * Sets the graphics for the buttons, sets a bound to the number of recent events,
     * and sets the available currencies.
     * @param url - the URL
     * @param resourceBundle - the resource bundle.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cancelButton.setGraphic(new ImageView(new Image("icons/cancelwhite.png")));
        saveButton.setGraphic(new ImageView(new Image("icons/savewhite.png")));
        noRecentEvents.setValueFactory(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(0, 100));
        currency.getItems().addAll(currencyConverter.getCurrencies());
    }

    /**
     * Setter for the previous scene value.
     * @param prevScene - value of the previous scene - true for Overview and false for StartScreen.
     */
    public void setPrevScene(boolean prevScene) {
        this.prevScene = prevScene;
    }

    /**
     * Refresh method for the settings scene.
     * Sets the values of the fields to the current values in the config.
     */
    public void refresh() {
        int i = 5;
        try {
            i = Integer.parseInt(config.getProperty("recentEventsLimit"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        noRecentEvents.increment(i);
        String configCurrency = config.getProperty("currency");
        if (configCurrency == null || configCurrency.isEmpty()) {
            configCurrency = "EUR";
        }
        currency.setValue(configCurrency);
        String configLanguage = config.getProperty("language");
        if (configLanguage == null || configLanguage.isEmpty()) {
            configLanguage = "en";
        }
        languages.setValue(configLanguage);
    }

    /**
     * Method that handles the cancel button.
     * It throws a confirmation alert if settings were changed.
     */
    public void cancel() {
        if (!noRecentEvents.getValue().toString().equals(config.getProperty("recentEventsLimit"))
                || !currency.getValue().equals(config.getProperty("currency"))
                || !languages.getValue().equals(config.getProperty("language"))) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "");
            confirmation.titleProperty().bind(languageManager.bind("commons.warning"));
            confirmation.headerTextProperty().bind(languageManager.bind("commons.warning"));
            confirmation.contentTextProperty()
                    .bind(languageManager.bind("settings.cancelAlert"));
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                clearAndReturn();
            }
        } else clearAndReturn();
    }

    /**
     * Method that clears the fields and returns to the previous scene.
     */
    private void clearAndReturn() {
        clearFields();
        if (prevScene)
            mainCtrl.showOverview();
        else
            mainCtrl.showStartMenu();
    }

    /**
     * Method that clears the fields.
     */
    private void clearFields() {
        noRecentEvents.decrement(noRecentEvents.getValue());
        currency.setValue(null);
        languages.setValue(null);
    }

    /**
     * Method that handles the save button.
     */
    public void save() {
        config.setProperty("recentEventsLimit", noRecentEvents.getValue().toString());
        config.setProperty("currency", currency.getValue());
        mainCtrl.getStartScreenCtrl().removeExcess();
        mainCtrl.getOverviewCtrl().populateExpenses();
        mainCtrl.getOverviewCtrl().populateParticipants();
        changeLanguage();
        clearAndReturn();
    }

    /**
     * Method that gets the language observable map.
     * @return - the language observable map.
     */
    public ObservableMap<String, Object> getLanguageManager() {
        return languageManager.get();
    }

    /**
     * Method to get the language manager.
     * @return - the language manager.
     */
    public LanguageManager languageManagerProperty() {
        return languageManager;
    }

    /**
     * Method to set the language observable map.
     * @param languageManager - the new observable map.
     */
    public void setLanguageManager(ObservableMap<String, Object> languageManager) {
        this.languageManager.set(languageManager);
    }


    /**
     * Changes language
     */
    public void changeLanguage() {
        String language = "";
        if (languages != null) language = languages.getValue();
        config.setProperty("language", language);
        if (mainCtrl != null && mainCtrl.getOverviewCtrl() != null
                && mainCtrl.getStartScreenCtrl() != null) {
            mainCtrl.getStartScreenCtrl().updateLanguageComboBox(languages.getValue());
            mainCtrl.getOverviewCtrl().updateLanguageComboBox(languages.getValue());
        }
        this.refreshLanguage();
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
}
