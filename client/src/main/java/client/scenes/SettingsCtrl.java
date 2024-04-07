package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import jakarta.mail.MessagingException;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsCtrl implements Initializable {

    private final ConfigInterface config;
    private final LanguageManager languageManager;
    private final MainCtrl mainCtrl;
    private final CurrencyConverter currencyConverter;
    private final MailSender mailSender;
    @FXML
    private Label confirmation;
    @FXML
    private Button testMail;
    @FXML
    private TitledPane emailPane;
    @FXML
    private TextField mailHost;
    @FXML
    private TextField mailPort;
    @FXML
    private TextField mailUser;
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
     *
     * @param config          - the config.
     * @param languageManager - the language manager.
     * @param mainCtrl        - the main controller.
     */
    @Inject
    public SettingsCtrl(ConfigInterface config,
                        LanguageManager languageManager,
                        MainCtrl mainCtrl,
                        CurrencyConverter currencyConverter,
                        MailSender mailSender) {
        this.config = config;
        this.languageManager = languageManager;
        this.mainCtrl = mainCtrl;
        this.currencyConverter = currencyConverter;
        this.mailSender = mailSender;
    }

    /**
     * The initialize method for the settings controller.
     * Sets the graphics for the buttons, sets a bound to the number of recent events,
     * and sets the available currencies.
     *
     * @param url            - the URL
     * @param resourceBundle - the resource bundle.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cancelButton.setGraphic(new ImageView(new Image("icons/cancelwhite.png")));
        saveButton.setGraphic(new ImageView(new Image("icons/savewhite.png")));
        noRecentEvents.setValueFactory(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(0, 100));
        currency.getItems().addAll(currencyConverter.getCurrencies());
        emailPane.setExpanded(false);
    }

    /**
     * Setter for the previous scene value.
     *
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
        mailHost.setText(config.getProperty("mail.host"));
        mailPort.setText(config.getProperty("mail.port"));
        mailUser.setText(config.getProperty("mail.user"));
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
        mailHost.setText("");
        mailPort.setText("");
        mailUser.setText("");
    }

    /**
     * Method that handles the save button.
     */
    public void save() {
        config.setProperty("recentEventsLimit", noRecentEvents.getValue().toString());
        config.setProperty("currency", currency.getValue());
        config.setProperty("mail.host", mailHost.getText());
        config.setProperty("mail.port", mailPort.getText());
        config.setProperty("mail.user", mailUser.getText());
        mainCtrl.getStartScreenCtrl().removeExcess();
        mainCtrl.getOverviewCtrl().populateExpenses();
        mainCtrl.getOverviewCtrl().populateParticipants();
        changeLanguage();
        clearAndReturn();
    }

    /**
     * Method that gets the language observable map.
     *
     * @return - the language observable map.
     */
    public ObservableMap<String, Object> getLanguageManager() {
        return languageManager.get();
    }

    /**
     * Method to set the language observable map.
     *
     * @param languageManager - the new observable map.
     */
    public void setLanguageManager(ObservableMap<String, Object> languageManager) {
        this.languageManager.set(languageManager);
    }

    /**
     * Method to get the language manager.
     *
     * @return - the language manager.
     */
    public LanguageManager languageManagerProperty() {
        return languageManager;
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

    /**
     * Method that handles the test mail button.
     */
    public void testMail() {
        if (mailHost.getText() == null
                || mailPort.getText() == null
                || mailUser.getText() == null
                || mailHost.getText().isEmpty()
                || mailPort.getText().isEmpty()
                || mailUser.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.contentTextProperty().bind(languageManager.bind("mail.missingFields"));
            alert.showAndWait();
            highlightMissing(mailHost.getText().isEmpty(),
                    mailPort.getText().isEmpty(),
                    mailUser.getText().isEmpty());
            return;
        }
        removeHighlight();
        try {
            mailSender.sendTestMail(mailHost.getText(),
                    mailPort.getText(),
                    mailUser.getText());
            showConfirmationTest();
        } catch (MessagingException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            if (e.getClass().equals(MissingPasswordException.class)) {
                alert.contentTextProperty().bind(languageManager.bind("mail.noPassword"));
            }
            else {
                alert.contentTextProperty().unbind();
                alert.setContentText(e.getMessage());
            }
            alert.showAndWait();
        }
    }

    /**
     * Insert highlight for missing fields in the mail settings
     *
     * @param host boolean if host is present
     * @param port boolean if port is present
     * @param user boolean if user is present
     */
    public void highlightMissing(boolean host, boolean port, boolean user) {
        if (host) mailHost
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if (port) mailPort
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if (user) mailUser
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
    }

    /**
     * removes any prior highlighting of required fields
     */
    public void removeHighlight() {
        mailHost.setStyle("-fx-border-color: none;");
        mailPort.setStyle("-fx-border-color: none; ");
        mailUser.setStyle("-fx-border-color: none; ");
    }

    /**
     * method to display a confirmation message for participant added
     * this message disappears
     */
    public void showConfirmationTest() {
        confirmation.textProperty().bind(languageManager.bind("settings.emailTestConfirmation"));
        confirmation.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), confirmation);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(event -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), confirmation);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(f -> confirmation.setVisible(false));
                fadeOut.play();
            });
            delay.play();
        });

        fadeIn.play();
    }
}
