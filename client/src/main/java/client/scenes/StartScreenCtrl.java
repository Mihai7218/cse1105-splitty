package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageComboBox;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import java.net.URL;
import java.util.*;

public class StartScreenCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    List<Event> recentEventsList = new ArrayList<>();
    @FXML
    private LanguageComboBox languages;
    @FXML
    private ListView<Event> recentEvents;
    @FXML
    private TextField newEventTitle;
    @FXML
    private TextField eventInvite;

    /**
     * Constructor for the StartScreenCtrl
     * @param mainCtrl - main controller
     * @param config - config
     */
    @Inject
    public StartScreenCtrl(MainCtrl mainCtrl,
                           ConfigInterface config,
                           LanguageManager languageManager,
                           ServerUtils serverUtils) {
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageManager = languageManager;
        this.serverUtils = serverUtils;
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
        if (languages != null) languages.setValue(language);
        this.refreshLanguage();
    }

    /**
     * Changes language
     */
    public void changeLanguage() {
        String language = "";
        if (languages != null) language = languages.getValue();
        config.setProperty("language", language);
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
     * Getter for the language manager observable map.
     * @return - the language manager observable map.
     */
    public ObservableMap<String, Object> getLanguageManager() {
        return languageManager.get();
    }

    /**
     * Setter for the language manager observable map.
     * @param languageManager - the language manager observable map.
     */
    public void setLanguageManager(ObservableMap<String, Object> languageManager) {
        this.languageManager.set(languageManager);
    }

    /**
     * Getter for the language manager property.
     * @return - the language manager property.
     */
    public LanguageManager languageManagerProperty() {
        return languageManager;
    }

    /**
     * Method that adds a new recent event to the list and updates the config.
     * @param event - the event to be added.
     */
    public void addRecentEvent(Event event) {
        recentEventsList.addFirst(event);
        int limit;
        try {
            limit = Integer.parseInt(config.getProperty("recentEventsLimit"));
        } catch (NumberFormatException e) {
            limit = 5;
            config.setProperty("recentEventsLimit", "5");
        }
        while (recentEventsList.size() > limit) {
            recentEventsList.removeLast();
        }
        StringBuilder sb = new StringBuilder();
        recentEventsList.stream()
                .map(x -> Integer.toString(x.getInviteCode()))
                .forEach(x -> sb.append(x).append(","));
        sb.deleteCharAt(sb.length() - 1);
        this.config.setProperty("recentEvents", sb.toString());
    }

    /**
     *
     * @param newEventTitle
     */
    public void setNewEventTitle(TextField newEventTitle) {
        this.newEventTitle = newEventTitle;
    }

    /**
     * Method that creates a new event when the button "Create" is pressed.
     */
    public void createEventButtonHandler() {
        if (newEventTitle == null || newEventTitle.getText() == null
                || newEventTitle.getText().isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.contentTextProperty().bind(languageManager.bind("startScreen.createEventEmpty"));
            var alertButton = alert.getDialogPane().lookupButton(ButtonType.OK);
            alertButton.setId("okAlertButton");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.show();
            return;
        }
        Date date = new Date();
        Event e = new Event(newEventTitle.getText(), date, date);
        try {
            e = serverUtils.addEvent(e);
        } catch (WebApplicationException ex) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            switch (ex.getResponse().getStatus()) {
                case 500 -> alert.contentTextProperty()
                        .bind(languageManager.bind("startScreen.createEvent500"));
                case 404 -> alert.contentTextProperty()
                        .bind(languageManager.bind("startScreen.createEvent404"));
            }
            alert.showAndWait();
            return;
        }
        addRecentEvent(e);
        //TODO: redirect the user to the add/edit event scene
    }

    /**
     * Method that equates pressing ENTER in createEventTitleTextField
     * to pressing the "Create" button.
     * @param e - the KeyEvent triggered when pressing a key.
     */
    public void createEventTextFieldHandler(KeyEvent e) {
        if (Objects.requireNonNull(e.getCode()) == KeyCode.ENTER) {
            createEventButtonHandler();
        }
    }

    /**
     * Method that loads the event with the specified invite code when the button "Join" is pressed.
     */
    public void joinEventButtonHandler() {
        if (eventInvite.getText() == null
                || eventInvite.getText().isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.contentTextProperty().bind(languageManager.bind("startScreen.joinEventEmpty"));
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        Event e;
        try {
            e = serverUtils.getEvent(Integer.parseInt(eventInvite.getText()));
            addRecentEvent(e);
        } catch (WebApplicationException | NumberFormatException ex) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.initModality(Modality.APPLICATION_MODAL);
            if (ex instanceof NumberFormatException) {
                alert.contentTextProperty().bind(languageManager.bind("startScreen.joinEvent400"));
            } else {
                WebApplicationException exp = (WebApplicationException) ex;
                switch (exp.getResponse().getStatus()) {
                    case 404 -> alert.contentTextProperty()
                            .bind(languageManager.bind("startScreen.joinEvent404"));
                    case 500 -> alert.contentTextProperty()
                            .bind(languageManager.bind("startScreen.joinEvent500"));
                    case 400 -> alert.contentTextProperty()
                            .bind(languageManager.bind("startScreen.joinEvent400"));
                }
            }
            alert.showAndWait();
            return;
        }
        //TODO: redirect the user to the overview of the event
    }

    /**
     * Method that equates pressing ENTER in
     * joinEventInviteCodeTextField to pressing the "Join" button.
     * @param e - the KeyEvent triggered when pressing a key.
     */
    public void joinEventTextFieldHandler(KeyEvent e) {
        if (Objects.requireNonNull(e.getCode()) == KeyCode.ENTER) {
            joinEventButtonHandler();
        }
    }

    /**
     * Getter for the list of recent events.
     * @return - the list of recent events.
     */
    public List<Event> getRecentEventsList() {
        return recentEventsList;
    }
}