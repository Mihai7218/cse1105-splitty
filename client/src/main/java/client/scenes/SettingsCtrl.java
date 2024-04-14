package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import jakarta.mail.MessagingException;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsCtrl implements Initializable, LanguageSwitcher, NotificationSender {

    private final ConfigInterface config;
    private final LanguageManager languageManager;
    private final MainCtrl mainCtrl;
    private final CurrencyConverter currencyConverter;
    private final MailSender mailSender;
    private final Alert alert;
    @FXML
    private TextField mailEmail;
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
                        MailSender mailSender,
                        Alert alert) {
        this.config = config;
        this.languageManager = languageManager;
        this.mainCtrl = mainCtrl;
        this.currencyConverter = currencyConverter;
        this.mailSender = mailSender;
        this.alert = alert;
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
        languages.setCellFactory(languageManager);
        cancelButton.setGraphic(new ImageView(new Image("icons/cancelwhite.png")));
        saveButton.setGraphic(new ImageView(new Image("icons/savewhite.png")));
        noRecentEvents.setValueFactory(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(0, 100));
        currency.getItems().addAll(currencyConverter.getCurrencies());
        languages.getItems().remove("template");
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
        mailEmail.setText(config.getProperty("mail.email"));
        if (mailHost.getText() == null)
            mailHost.setText("");
        if (mailPort.getText() == null)
            mailPort.setText("");
        if (mailUser.getText() == null)
            mailUser.setText("");
        if (mailEmail.getText() == null)
            mailEmail.setText("");
    }

    /**
     * Method that handles the cancel button.
     * It throws a confirmation alert if settings were changed.
     */
    public void cancel() {
        if (!noRecentEvents.getValue().toString().equals(config.getProperty("recentEventsLimit"))
                || !currency.getValue().equals(config.getProperty("currency"))
                || !languages.getValue().equals(config.getProperty("language"))) {
            alert.setAlertType(Alert.AlertType.CONFIRMATION);
            alert.titleProperty().bind(languageManager.bind("commons.warning"));
            alert.headerTextProperty().bind(languageManager.bind("commons.warning"));
            alert.contentTextProperty()
                    .bind(languageManager.bind("settings.cancelAlert"));
            Optional<ButtonType> result = alert.showAndWait();
            alert.setAlertType(Alert.AlertType.WARNING);
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
        mailEmail.setText("");
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
        config.setProperty("mail.email", mailEmail.getText());
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
     * Getter for the main controller
     * @return MainCtrl object
     */
    @Override
    public MainCtrl getMainCtrl() {
        return mainCtrl;
    }

    /**
     * Getter for the languages combo box.
     * @return LanguageComboBox object
     */
    @Override
    public LanguageComboBox getLanguages() {
        return languages;
    }

    /**
     * Getter for the config.
     * @return - the config
     */
    @Override
    public ConfigInterface getConfig() {
        return config;
    }

    /**
     * Method to get the language manager.
     * @return - the language manager.
     */
    public LanguageManager languageManagerProperty() {
        return languageManager;
    }


    /**
     * Method that updates the language combo box.
     * @param language - the new language value.
     */
    @Override
    public void updateLanguageComboBox(String language) {
        languages.setValue(language);
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
     * Method that handles the test mail button.
     */
    public void testMail() {
        if (mailHost.getText() == null
                || mailPort.getText() == null
                || mailUser.getText() == null
                || mailEmail.getText() == null
                || mailHost.getText().isEmpty()
                || mailPort.getText().isEmpty()
                || mailUser.getText().isEmpty()
                || mailEmail.getText().isEmpty()) {
            alert.contentTextProperty().bind(languageManager.bind("mail.missingFields"));
            alert.showAndWait();
            highlightMissing(mailHost.getText().isEmpty(),
                    mailPort.getText().isEmpty(),
                    mailUser.getText().isEmpty(),
                    mailEmail.getText().isEmpty());
            return;
        }
        removeHighlight();
        try {
            showNotification("mail.sending");
            mailSender.sendTestMail(mailHost.getText(),
                    mailPort.getText(),
                    mailUser.getText(),
                    mailEmail.getText());
            showNotification("settings.emailTestConfirmation");
        } catch (MessagingException e) {
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
    public void highlightMissing(boolean host, boolean port, boolean user, boolean email) {
        if (host) mailHost
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if (port) mailPort
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if (user) mailUser
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if (email) mailEmail
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
    }

    /**
     * removes any prior highlighting of required fields
     */
    public void removeHighlight() {
        mailHost.setStyle("-fx-border-color: none;");
        mailPort.setStyle("-fx-border-color: none;");
        mailUser.setStyle("-fx-border-color: none;");
        mailEmail.setStyle("-fx-border-color: none;");
    }

    /**
     * Gets the notification label.
     * @return - the notification label.
     */
    @Override
    public Label getNotificationLabel() {
        return confirmation;
    }

    /**
     * Method that returns to the startmenu. It's used for the shortcut.
     */
    private void startMenu(){
        prevScene = false;
        cancel();
    }

    /**
     * Back to the overview of the expenses of the Event
     */
    public void backToOverview() {
        prevScene = true;
        cancel();
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     *  - if ENTER is pressed, then it adds the participant.
     *  - if ESCAPE is pressed, then it cancels and returns to the overview.
     *  - if Ctrl + m is pressed, then it returns to the startscreen.
     *  - if Ctrl + o is pressed, then it returns to the overview.
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                save();
                break;
            case ESCAPE:
                cancel();
                break;
            case M:
                if(e.isControlDown()){
                    startMenu();
                    break;
                }
            case O:
                if(e.isControlDown()){
                    if(prevScene){
                        backToOverview();
                        break;
                    }
                }
            default:
                break;
        }
    }

    /**
     *
     * @param languages
     */
    void setLanguages(LanguageComboBox languages) {
        this.languages = languages;
    }

    /**
     *
     * @param cancelButton
     */
    void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }

    /**
     *
     * @param saveButton
     */
    void setSaveButton(Button saveButton) {
        this.saveButton = saveButton;
    }

    /**
     *
     * @param noRecentEvents
     */
    void setNoRecentEvents(Spinner<Integer> noRecentEvents) {
        this.noRecentEvents = noRecentEvents;
    }

    /**
     *
     * @param currency
     */
    void setCurrency(ChoiceBox<String> currency) {
        this.currency = currency;
    }

    /**
     *
     * @param mailHost
     */
    void setMailHost(TextField mailHost) {
        this.mailHost = mailHost;
    }

    /**
     *
     * @param mailPort
     */
    void setMailPort(TextField mailPort) {
        this.mailPort = mailPort;
    }

    /**
     *
     * @param mailUser
     */
    void setMailUser(TextField mailUser) {
        this.mailUser = mailUser;
    }

    /**
     *
     * @param mailEmail
     */
    void setMailEmail(TextField mailEmail) {
        this.mailEmail = mailEmail;
    }

    /**
     *
     * @param notificationLabel
     */
    void setNotificationLabel(Label notificationLabel) {
        this.confirmation = notificationLabel;
    }
}
