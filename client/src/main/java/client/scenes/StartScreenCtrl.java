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
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.*;

public class StartScreenCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    @FXML
    private LanguageComboBox languages;
    @FXML
    private ListView<Event> recentEvents;
    @FXML
    private TextField newEventTitle;
    @FXML
    private TextField eventInvite;
    private Alert alert;

    /**
     * Constructor for the StartScreenCtrl
     * @param mainCtrl - main controller
     * @param config - config
     */
    @Inject
    public StartScreenCtrl(MainCtrl mainCtrl,
                           ConfigInterface config,
                           LanguageManager languageManager,
                           ServerUtils serverUtils,
                           Alert alert) {
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageManager = languageManager;
        this.serverUtils = serverUtils;
        this.alert = alert;
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
        alert.titleProperty().bind(languageManager.bind("commons.warning"));
        alert.headerTextProperty().bind(languageManager.bind("commons.warning"));
        recentEvents.setCellFactory(x -> new RecentEventCell(mainCtrl));
        recentEvents.getItems().addAll(getRecentEventsFromConfig());
        if (languages != null) languages.setValue(language);
        this.refreshLanguage();
    }

    private List<Event> getRecentEventsFromConfig() {
        String eventString = config.getProperty("recentEvents");
        if (eventString == null) return new ArrayList<>();
        List<Event> events = new ArrayList<>();
        for (String s : eventString.split(",")) {
            events.add(serverUtils.getEvent(Integer.parseInt(s)));
        }
        return events;
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
        recentEvents.getItems().remove(event);
        recentEvents.getItems().addFirst(event);
        int limit;
        try {
            limit = Integer.parseInt(config.getProperty("recentEventsLimit"));
        } catch (NumberFormatException e) {
            limit = 5;
            config.setProperty("recentEventsLimit", "5");
        }
        while (recentEvents.getItems().size() > limit) {
            recentEvents.getItems().removeLast();
        }
        recentEvents.refresh();
        refreshConfig();
    }

    public void removeRecentEvent(Event event) {
        recentEvents.getItems().remove(event);
        recentEvents.refresh();
        refreshConfig();
    }

    private void refreshConfig() {
        if (recentEvents.getItems().isEmpty()) {
            this.config.setProperty("recentEvents", "");
            return;
        }
        StringBuilder sb = new StringBuilder();
        recentEvents.getItems().stream()
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
            alert.contentTextProperty().bind(languageManager.bind("startScreen.createEventEmpty"));
            alert.show();
            return;
        }
        Date date = new Date();
        Event e = new Event(newEventTitle.getText(), date, date);
        try {
            e = serverUtils.addEvent(e);
        } catch (WebApplicationException ex) {
            switch (ex.getResponse().getStatus()) {
                case 500 -> alert.contentTextProperty()
                        .bind(languageManager.bind("startScreen.createEvent500"));
                case 404 -> alert.contentTextProperty()
                        .bind(languageManager.bind("startScreen.createEvent404"));
            }
            alert.show();
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
        if (eventInvite.getText() == null || eventInvite.getText() == null
                || eventInvite.getText().isEmpty()) {
            alert.contentTextProperty().bind(languageManager.bind("startScreen.joinEventEmpty"));
            alert.show();
            return;
        }
        Event e;
        try {
            e = serverUtils.getEvent(Integer.parseInt(eventInvite.getText()));
            addRecentEvent(e);
        } catch (WebApplicationException | NumberFormatException ex) {
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
            alert.show();
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
     * Setter for the event invite text field.
     * @param eventInvite - the text field for the invite code.
     */
    void setEventInvite(TextField eventInvite) {
        this.eventInvite = eventInvite;
    }

    /**
     * Setter for the language combo box.
     * @param languages - the language combo box.
     */
    public void setLanguages(LanguageComboBox languages) {
        this.languages = languages;
    }

    public ListView<Event> getRecentEvents() {
        return recentEvents;
    }
}