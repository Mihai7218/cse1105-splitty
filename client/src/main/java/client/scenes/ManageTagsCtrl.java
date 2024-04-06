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

    private StompSession.Subscription expensesSubscription;
    private Map<Tag, StompSession.Subscription> expenseSubscriptionMap;

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
        expenseSubscriptionMap = new HashMap<>();
        refresh();
    }

    /**
     * Getter for the expense subscription map.
     *
     * @return - the expense subscription map of the overview controller.
     */
    public Map<Tag, StompSession.Subscription> getExpenseSubscriptionMap() {
        return expenseSubscriptionMap;
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
        if (expenseSubscriptionMap != null) {
            expenseSubscriptionMap.forEach((k, v) -> v.unsubscribe());
            expenseSubscriptionMap = new HashMap<>();
        }
        if (expensesSubscription != null) {
            expensesSubscription.unsubscribe();
            expensesSubscription = null;
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
            if (expensesSubscription == null)
                expensesSubscription = serverUtils.registerForMessages("/topic/events/" +
                                mainCtrl.getEvent().getInviteCode() + "/tags", Tag.class,
                        tag -> {
                            Platform.runLater(() -> {
                                tagsListView.getItems().add(tag);
                                mainCtrl.getEvent().getTagsList().add(tag);
                                tagsListView.refresh();
                                subscribeToExpense(tag);
                            });
                        });
        }
    }

    /**
     * Extra setup to fix statistic refresh
     */
    public void setup() {
        tagsListView.getItems().clear();
        Event e = serverUtils.getEvent(mainCtrl.getEvent().getInviteCode());
        mainCtrl.setEvent(e);
        for (Tag expense : mainCtrl.getEvent().getTagsList()) {
            if (!expenseSubscriptionMap.containsKey(expense))
                subscribeToExpense(expense);
        }
        tagsListView.getItems().addAll(mainCtrl.getEvent().getTagsList());
        tagsListView.refresh();
        refresh();
    }

    /**
     * Method that subscribes to updates for an expense.
     * @param expense - the expense to subscribe to.
     */
    private void subscribeToExpense(Tag expense) {
        if (!expenseSubscriptionMap.containsKey(expense)) {
            String dest = "/topic/events/" +
                    mainCtrl.getEvent().getInviteCode() + "/tags/"
                    + expense.getId();
            var subscription = serverUtils.registerForMessages(dest, Tag.class,
                    exp -> Platform.runLater(() -> {
                        tagsListView.getItems().remove(expense);
                        mainCtrl.getEvent().getExpensesList().remove(expense);
                        tagsListView.refresh();
                        if (!"deleted".equals(exp.getColor())) {
                            tagsListView.getItems().add(exp);
                            mainCtrl.getEvent().getTagsList().add(exp);
                        }
                        tagsListView.refresh();
                    }));
            expenseSubscriptionMap.put(expense, subscription);
        }
    }
}
