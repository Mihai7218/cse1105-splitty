package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Event;
import commons.Tag;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import org.springframework.messaging.simp.stomp.StompSession;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ManageTagsCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;

    private StompSession.Subscription tagSubscription;
    private Map<Tag, StompSession.Subscription> tagSubscriptionMap;

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
                       ServerUtils serverUtils) {
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
        tagSubscriptionMap = new HashMap<>();
        refresh();
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
     * Goes back to the startMenu.
     */
    public void backToStatistics() {
        if (tagSubscriptionMap != null) {
            tagSubscriptionMap.forEach((k, v) -> v.unsubscribe());
            tagSubscriptionMap = new HashMap<>();
        }
        if (tagSubscription != null) {
            tagSubscription.unsubscribe();
            tagSubscription = null;
        }
        mainCtrl.showStatistics();
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
        Event event = mainCtrl.getEvent();
        if (event != null) {
            if (tagSubscription == null)
                tagSubscription = serverUtils.registerForMessages("/topic/events/" +
                                mainCtrl.getEvent().getInviteCode() + "/tags", Tag.class,
                        tag -> {
                            Platform.runLater(() -> {onTagReceive(tag);});
                        });
        }
    }

    /**
     * What the system needs to do when a tag is received
     * @param tag the received tag
     */
    public void onTagReceive(Tag tag) {
        tagsListView.getItems().add(tag);
        mainCtrl.getEvent().getTagsList().add(tag);
        tagsListView.refresh();
        subscribeToTag(tag);
    }

    /**
     * Extra setup to fix statistic refresh
     */
    public void setup() {
        tagsListView.getItems().clear();
        Event e = serverUtils.getEvent(mainCtrl.getEvent().getInviteCode());
        mainCtrl.setEvent(e);
        for (Tag tag : mainCtrl.getEvent().getTagsList()) {
            if (!tagSubscriptionMap.containsKey(tag))
                subscribeToTag(tag);
        }
        tagsListView.getItems().addAll(mainCtrl.getEvent().getTagsList());
        tagsListView.refresh();
        refresh();
    }

    /**
     * Method that subscribes to updates for a tag.
     * @param item - the tag to subscribe to.
     */
    public void subscribeToTag(Tag item) {
        if (!tagSubscriptionMap.containsKey(item)) {
            String dest = "/topic/events/" +
                    mainCtrl.getEvent().getInviteCode() + "/tags/"
                    + item.getId();
            var subscription = serverUtils.registerForMessages(dest, Tag.class,
                    tag -> Platform.runLater(() -> {onTagUpdate(tag);}));
            tagSubscriptionMap.put(item, subscription);
        }
    }

    /**
     * What the system needs to do when a tag is updated
     * @param tag the tag to update
     */
    public void onTagUpdate(Tag tag) {
        tagsListView.getItems().remove(tag);
        mainCtrl.getEvent().getTagsList().remove(tag);
        tagsListView.refresh();
        if (!"deleted".equals(tag.getColor())) {
            tagsListView.getItems().add(tag);
            mainCtrl.getEvent().getTagsList().add(tag);
        }
        tagsListView.refresh();
    }

    /**
     * Back to the overview of the expenses of the Event
     */
    public void backToOverview() {
        mainCtrl.showOverview();
    }

    /**
     * When the shortcut is used it goes back to the startmenu.
     */
    public void startMenu() {
        mainCtrl.showStartMenu();
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     *  - if ESCAPE is pressed, then it cancels and returns to the overview.
     *  - if Ctrl + m is pressed, then it returns to the startscreen.
     *  - if Ctrl + o is pressed, then it returns to the overview.
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ESCAPE:
                backToStatistics();
                break;
            case M:
                if(e.isControlDown()){
                    startMenu();
                    break;
                }
            case O:
                if(e.isControlDown()){
                    backToOverview();
                    break;
                }
            default:
                break;
        }
    }


    /**
     * return the tagSubscription
     *
     * @param tagSubscription the tagSubscription of the controller
     */
    public void setTagSubscription(StompSession.Subscription tagSubscription) {
        this.tagSubscription = tagSubscription;
    }

    /**
     * return the tagSubscriptionMap
     *
     * @param tagSubscriptionMap the tagSubscriptionMap of the controller
     */
    public void setTagSubscriptionMap(Map<Tag, StompSession.Subscription> tagSubscriptionMap) {
        this.tagSubscriptionMap = tagSubscriptionMap;
    }

    /**
     * return the cancel
     *
     * @param cancel the cancel of the controller
     */
    public void setCancel(Button cancel) {
        this.cancel = cancel;
    }

    /**
     * return the tagsListView
     *
     * @param tagsListView the tagsListView of the controller
     */
    public void setTagsListView(ListView tagsListView) {
        this.tagsListView = tagsListView;
    }

    /**
     * get the main controller that is being used by the manageTags page
     * @return the mainCtrl from the manageTags page
     */
    public MainCtrl getMainCtrl() {
        return mainCtrl;
    }
}
