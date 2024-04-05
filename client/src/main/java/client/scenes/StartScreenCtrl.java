package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Event;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.*;

public class StartScreenCtrl implements Initializable, LanguageSwitcher {

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
    @FXML
    Button createEventButton;
    @FXML
    HBox createButtonHBox;
    @FXML
    HBox joinButtonHBox;
    @FXML
    Button joinEventButton;
    @FXML
    Button logo;
    @FXML
    Button settings;
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
        createEventButton.setGraphic(new ImageView(new Image("icons/whiteplus.png")));
        joinEventButton.setGraphic(new ImageView(new Image("icons/joinwhite.png")));
        settings.setGraphic(new ImageView(new Image("icons/settingswhite.png")));
        if (language == null) {
            language = "en";
        }
        alert.titleProperty().bind(languageManager.bind("commons.warning"));
        alert.headerTextProperty().bind(languageManager.bind("commons.warning"));
        recentEvents.setCellFactory(x -> new RecentEventCell(mainCtrl));
        List<Event> currentlyInConfig = getRecentEventsFromConfig();
        recentEvents.getItems().addAll(currentlyInConfig);
        removeExcess();
        this.refreshConfig();
        updateLanguageComboBox(language);
        this.refreshLanguage();
        setLongPolling(currentlyInConfig);
    }

    /**
     * set the LongPolling for the already added events
     * @param recentEventsFromConfig the list of already existing events
     */
    private void setLongPolling(List<Event> recentEventsFromConfig) {
        for (Event event : recentEventsFromConfig) {
            serverUtils.getEventUpdate(event.getInviteCode(),q -> {
                updateEvent(q);
            });
        }
    }

    /**
     * update the event in the list if anything changes
     * @param q the event to update in the list
     */
    private void updateEvent(Event q) {
        for (int i = 0; i < recentEvents.getItems().size(); i++) {
            if(recentEvents.getItems().get(i).getInviteCode() == q.getInviteCode()) {
                recentEvents.getItems().get(i).setTitle(q.getTitle());
                recentEvents.refresh();
            }
        }

    }

    /**
     * Method that returns a list containing the recent events from the config file.
     * @return - the list of recent events.
     */
    private List<Event> getRecentEventsFromConfig() {
        String eventString = config.getProperty("recentEvents");
        if (eventString == null) return new ArrayList<>();
        List<Event> events = new ArrayList<>();
        for (String s : eventString.split(",")) {
            try {
                events.add(serverUtils.getEvent(Integer.parseInt(s)));
            }
            catch (WebApplicationException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return events;
    }

    /**
     * Method that updates the language combo box with the correct flag.
     * @param language - code of the new language
     */
    public void updateLanguageComboBox(String language) {
        if (languages != null) languages.setValue(language);
    }

    /**
     * Changes language
     */
    @Override
    public void changeLanguage() {
        LanguageSwitcher.super.changeLanguage();
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
        newThreadForMethod(event);
        recentEvents.getItems().remove(event);
        recentEvents.getItems().addFirst(event);
        removeExcess();
        recentEvents.refresh();
        refreshConfig();
    }

    /**
     * Method that removes excess recent events.
     */
    public void removeExcess() {
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
    }

    /**
     * create a new thread for LongPoling for an event
     * @param event event to make the thread for
     */
    private void newThreadForMethod(Event event) {
        boolean check = true;
        for (int i = 0; i < recentEvents.getItems().size(); i++) {
            if(recentEvents.getItems().get(i).getInviteCode() == event.getInviteCode()) {
                check = false;
            }
        }
        if (check) {
            serverUtils.getEventUpdate(event.getInviteCode(), q -> {
                updateEvent(q);
            });
        }
    }

    /**
     * Method that removes an event from the list of recent events.
     * @param event - the event to be removed.
     */
    public void removeRecentEvent(Event event) {
        recentEvents.getItems().remove(event);
        recentEvents.refresh();
        refreshConfig();
    }

    /**
     * Method that updates the config file to store the recentEvents
     */
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
     *  Set the title for a new event
     * @param newEventTitle the text field of the new event
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
        mainCtrl.setEvent(e);
        if (mainCtrl.getOverviewCtrl() != null) {
            mainCtrl.getOverviewCtrl().populateExpenses();
            mainCtrl.getOverviewCtrl().populateParticipants();
        }
        mainCtrl.showOverview();
        serverUtils.stop();
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
        mainCtrl.setEvent(e);
        mainCtrl.showOverview();
        if (mainCtrl.getOverviewCtrl() != null) {
            mainCtrl.getOverviewCtrl().populateExpenses();
            mainCtrl.getOverviewCtrl().populateParticipants();
        }
        serverUtils.stop();
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

    /**
     * Method that gets the ListView of recent events.
     * @return - the ListView of recent events.
     */
    public ListView<Event> getRecentEvents() {
        return recentEvents;
    }

    /**
     * Setter for the recent events ListView.
     * @param list - the recent events ListView.
     */
    void setRecentEvents(ListView<Event> list) {
        this.recentEvents = list;
    }

    /**
     * Stops all Threads when exit
     */
    public void stop() {
        serverUtils.stop();
    }

    /**
     * Method that opens the settings scene.
     */
    public void settings() {
        mainCtrl.getSettingsCtrl().setPrevScene(false);
        mainCtrl.showSettings();
    }
}