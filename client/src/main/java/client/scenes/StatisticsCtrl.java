package client.scenes;

import client.utils.ConfigInterface;
import client.utils.CurrencyConverter;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Tag;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import org.springframework.messaging.simp.stomp.StompSession;

import java.net.URL;
import java.util.*;

public class StatisticsCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final CurrencyConverter currencyConverter;
    @FXML
    public Button manageTags;
    private StompSession.Subscription subscription;
    @FXML
    public javafx.scene.chart.PieChart pieChart;
    @FXML
    public Button cancel;
    @FXML
    public VBox ownLegend;
    private String currency;

    private StompSession.Subscription tagSubscription;

    private Map<Tag, StompSession.Subscription> tagSubscriptionMap;

    private StompSession.Subscription expensesSubscription;
    private Map<Expense, StompSession.Subscription> expenseSubscriptionMap;


    /**
     * Constructor for the StatisticsCtrl
     *
     * @param mainCtrl - main controller
     * @param config   - config
     */
    @Inject
    public StatisticsCtrl(MainCtrl mainCtrl,
                          ConfigInterface config,
                          LanguageManager languageManager,
                          ServerUtils serverUtils,
                          CurrencyConverter currencyConverter) {
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageManager = languageManager;
        this.serverUtils = serverUtils;
        this.currencyConverter = currencyConverter;
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
        expenseSubscriptionMap = new HashMap<>();
        tagSubscriptionMap = new HashMap<>();
        pieChart = new PieChart();
        ownLegend = new VBox();
    }


    /**
     * Extra setup to fix statistic refresh
     */
    public void setup() {
        Event e = serverUtils.getEvent(mainCtrl.getEvent().getInviteCode());
        mainCtrl.setEvent(e);
        for (Expense expense : mainCtrl.getEvent().getExpensesList()) {
            if (!expenseSubscriptionMap.containsKey(expense))
                subscribeToExpense(expense);
        }
        for (Tag tag : mainCtrl.getEvent().getTagsList()) {
            if (!tagSubscriptionMap.containsKey(tag))
                subscribeToTag(tag);
        }
        refresh();
    }

    /**
     * Method that subscribes to updates for an tag.
     *
     * @param tag - the tag to subscribe to.
     */
    private void subscribeToTag(Tag tag) {
        if (!tagSubscriptionMap.containsKey(tag)) {
            String dest = "/topic/events/" +
                    mainCtrl.getEvent().getInviteCode() + "/tags/"
                    + tag.getId();
            var subscription = serverUtils.registerForMessages(dest, Tag.class,
                    exp -> Platform.runLater(() -> {
                        Event e = serverUtils.getEvent(mainCtrl.getEvent().getInviteCode());
                        mainCtrl.setEvent(e);
                        setStatistics();
                    }));
            tagSubscriptionMap.put(tag, subscription);
        }
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
     * Refreshes the statistics
     */
    public void refresh() {
        if (mainCtrl.getEvent() != null) {
            subscription = serverUtils.registerForMessages(String.format("/topic/events/%s",
                    mainCtrl.getEvent().getInviteCode()), Event.class, q -> {
                mainCtrl.getEvent().setTitle(q.getTitle());
                Platform.runLater(() -> refresh());
            });
            if (tagSubscription == null)
                tagSubscription = serverUtils.registerForMessages("/topic/events/" +
                                mainCtrl.getEvent().getInviteCode() + "/tags", Tag.class,
                        tag -> {
                            Platform.runLater(() -> {
                                subscribeToTag(tag);
                            });
                        });
            if (expensesSubscription == null)
                expensesSubscription = serverUtils.registerForMessages("/topic/events/" +
                                mainCtrl.getEvent().getInviteCode() + "/expenses", Expense.class,
                        expense -> {
                            Platform.runLater(() -> {
                                if (!mainCtrl.getEvent().getExpensesList().contains(expense)) {
                                    mainCtrl.getEvent().getExpensesList().add(expense);
                                }

                                subscribeToExpense(expense);
                                setStatistics();
                            });
                        });
        }
        pieChart.titleProperty().set(mainCtrl.getEvent().getTitle());
        cancel.setGraphic(new ImageView(new Image("icons/arrowback.png")));
        manageTags.setGraphic(new ImageView(new Image("icons/settingswhite.png")));
        currency = config.getProperty("currency");
        if (currency == null || currency.isEmpty()) currency = "EUR";
        setStatistics();
    }

    /**
     * Method that subscribes to updates for an expense.
     *
     * @param expense - the expense to subscribe to.
     */
    private void subscribeToExpense(Expense expense) {
        if (!expenseSubscriptionMap.containsKey(expense)) {
            String dest = "/topic/events/" +
                    mainCtrl.getEvent().getInviteCode() + "/expenses/"
                    + expense.getId();
            var subscription = serverUtils.registerForMessages(dest, Expense.class,
                    exp -> Platform.runLater(() -> {
                        mainCtrl.getEvent().getExpensesList().remove(expense);
                        if (!"deleted".equals(exp.getDescription())) {
                            mainCtrl.getEvent().getExpensesList().add(exp);
                        }
                        setStatistics();
                    }));
            expenseSubscriptionMap.put(expense, subscription);
        }
    }

    /**
     * Back to the overview of the expenses of the Event
     */
    public void backToOverview() {
        subscription.unsubscribe();
        if (expenseSubscriptionMap != null) {
            expenseSubscriptionMap.forEach((k, v) -> v.unsubscribe());
            expenseSubscriptionMap = new HashMap<>();
        }
        if (expensesSubscription != null) {
            expensesSubscription.unsubscribe();
            expensesSubscription = null;
        }
        if (tagSubscription != null) {
            tagSubscription.unsubscribe();
            tagSubscription = null;
        }
        if (tagSubscriptionMap != null) {
            tagSubscriptionMap.forEach((k, v) -> v.unsubscribe());
            tagSubscriptionMap = new HashMap<>();
        }
        mainCtrl.showOverview();
    }

    /**
     * Manage the tags
     */
    public void showManageTagsScreen() {
        subscription.unsubscribe();
        if (expenseSubscriptionMap != null) {
            expenseSubscriptionMap.forEach((k, v) -> v.unsubscribe());
            expenseSubscriptionMap = new HashMap<>();
        }
        if (expensesSubscription != null) {
            expensesSubscription.unsubscribe();
            expensesSubscription = null;
        }
        if (tagSubscription != null) {
            tagSubscription.unsubscribe();
            tagSubscription = null;
        }
        if (tagSubscriptionMap != null) {
            tagSubscriptionMap.forEach((k, v) -> v.unsubscribe());
            tagSubscriptionMap = new HashMap<>();
        }
        mainCtrl.showManageTags();
    }


    /**
     * set the right statistics for the event
     */
    public void setStatistics() {
        pieChart.getData().clear();
        List<Pair<Tag, Double>> legendList = new ArrayList<>();
        List<Pair<Tag, List<Expense>>> stats = pairListMaker();

        double total = 0;
        for (Expense expense : mainCtrl.getEvent().getExpensesList()) {
            total += currencyConverter.convert(expense.getDate(),
                    expense.getCurrency(),
                    currency,
                    expense.getAmount());
        }

        for (Pair<Tag, List<Expense>> pair : stats) {
            if (pair.getKey() != null) {
                String catagoryName = pair.getKey().getName();
                double value = 0;
                for (Expense expense : pair.getValue()) {
                    value += currencyConverter.convert(expense.getDate(),
                            expense.getCurrency(),
                            currency,
                            expense.getAmount());
                }
                PieChart.Data slice = new PieChart.Data(catagoryName, value);
                pieChart.getData().add(slice);
                try {
                    slice.getNode().setStyle("-fx-pie-color: " + pair.getKey().getColor() + ";");
                } catch (Exception e) {
                    System.out.println(e);
                }
                legendList.add(new Pair<>(pair.getKey(), value));
            }
        }
        String legendStyle = "-fx-background-color: white; -fx-border-color: black;";
        pieChart.getData().get(0).getNode().getParent().getParent()
                .lookup(".chart-legend").setStyle(legendStyle);

        updateOwnLegend(legendList, total);
        StringBinding test = languageManager.bind("statistics.chartTitle");
        pieChart.setTitle(mainCtrl.getEvent().getTitle() +
                "\n" + test.getValue() + " " + String.format("%.2f %s", total, currency));
    }

    /**
     * Generate a list with Pairs with the right stats
     *
     * @return a list of Tag and the associate expenses
     */
    private List<Pair<Tag, List<Expense>>> pairListMaker() {
        Event event = mainCtrl.getEvent();
        List<Pair<Tag, List<Expense>>> stats = new ArrayList<>();
        List<Tag> eventTagList = getTagsFromExpenses();
        for (Tag tag : eventTagList) {
            List<Expense> expensesWithTag = new ArrayList<>();
            for (Expense expense : event.getExpensesList()) {
                if (expense.getTag() != null && expense.getTag().equals(tag)) {
                    expensesWithTag.add(expense);
                }
            }
            stats.add(new Pair<>(tag, expensesWithTag));
        }
        List<Expense> expensesWithTag = new ArrayList<>();
        for (Expense expense : event.getExpensesList()) {
            if (expense.getTag() == null) {
                expensesWithTag.add(expense);
            }
        }
        if (eventTagList.contains(null)) {
            stats.add(new Pair<>(new Tag("NO TAG", "#000000"), expensesWithTag));
        }
        return stats;
    }

    /**
     * get the tags that are in use from the expenses
     *
     * @return returns the list of tags that are in use
     */
    private List<Tag> getTagsFromExpenses() {
        List<Tag> res = new ArrayList<>();
        for (Expense expense : mainCtrl.getEvent().getExpensesList()) {
            if (!res.contains(expense.getTag())) {
                res.add(expense.getTag());
            }
        }
        return res;
    }

    /**
     * Update own legend version
     */
    private void updateOwnLegend(List<Pair<Tag, Double>> stats, double total) {
        ownLegend.getChildren().clear();
        for (Pair<Tag, Double> data : stats) {
            double precentage = (data.getValue() / total * 100);
            String withRightDigits = String.format("%.1f", precentage);
            Rectangle rectangle = new Rectangle(15, 15);
            rectangle.setStroke(Color.BLACK);
            rectangle.setStrokeWidth(1);
            rectangle.setFill(Color.web(data.getKey().getColor()));
            Label item = new Label(" " + data.getKey().getName() + ": "
                    + String.format("%.2f %s", data.getValue(), currency)
                    + " (" + withRightDigits + "%)");
            HBox test = new HBox(rectangle, item);
            ownLegend.getChildren().add(test);
        }
    }

    /**
     * When the shortcut is used it goes back to the startmenu.
     */
    public void startMenu() {
        subscription.unsubscribe();
        if (expenseSubscriptionMap != null) {
            expenseSubscriptionMap.forEach((k, v) -> v.unsubscribe());
            expenseSubscriptionMap = new HashMap<>();
        }
        if (expensesSubscription != null) {
            expensesSubscription.unsubscribe();
            expensesSubscription = null;
        }
        if (tagSubscription != null) {
            tagSubscription.unsubscribe();
            tagSubscription = null;
        }
        if (tagSubscriptionMap != null) {
            tagSubscriptionMap.forEach((k, v) -> v.unsubscribe());
            tagSubscriptionMap = new HashMap<>();
        }
        mainCtrl.showStartMenu();
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     * - if ESCAPE is pressed, then it cancels and returns to the overview.
     * - if Ctrl + m is pressed, then it returns to the startscreen.
     * - if Ctrl + m is pressed, then it returns to the overview.
     *
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ESCAPE:
                backToOverview();
                break;
            case M:
                if (e.isControlDown()) {
                    startMenu();
                    break;
                }
            case O:
                if (e.isControlDown()) {
                    backToOverview();
                    break;
                }
            case T:
                if (e.isControlDown()) {
                    showManageTagsScreen();
                    break;
                }
            default:
                break;
        }
    }
    /**
     * set the manageTags
     * @param manageTags set the manageTags of the controller
     */
    public void setManageTags(Button manageTags) {
        this.manageTags = manageTags;
    }
    /**
     * set the subscription
     * @param subscription set the subscription of the controller
     */
    public void setSubscription(StompSession.Subscription subscription) {
        this.subscription = subscription;
    }
    /**
     * set the pieChart
     * @param pieChart set the pieChart of the controller
     */
    public void setPieChart(PieChart pieChart) {
        this.pieChart = pieChart;
    }
    /**
     * set the cancel
     * @param cancel set the cancel of the controller
     */
    public void setCancel(Button cancel) {
        this.cancel = cancel;
    }
    /**
     * set the ownLegend
     * @param ownLegend set the ownLegend of the controller
     */
    public void setOwnLegend(VBox ownLegend) {
        this.ownLegend = ownLegend;
    }
    /**
     * set the currency
     * @param currency set the currency of the controller
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    /**
     * set the tagSubscription
     * @param tagSubscription set the tagSubscription of the controller
     */
    public void setTagSubscription(StompSession.Subscription tagSubscription) {
        this.tagSubscription = tagSubscription;
    }
    /**
     * set the tagSubscriptionMap
     * @param tagSubscriptionMap set the tagSubscriptionMap of the controller
     */
    public void setTagSubscriptionMap(Map<Tag, StompSession.Subscription> tagSubscriptionMap) {
        this.tagSubscriptionMap = tagSubscriptionMap;
    }

    /**
     * set the ExpenseSubscriptionMap
     * @param expensesSubscription set the expensesSubscription of the controller
     */
    public void setExpensesSubscription(StompSession.Subscription expensesSubscription) {
        this.expensesSubscription = expensesSubscription;
    }

    /**
     * return the ExpenseSubscriptionMap
     * @param expenseSubscriptionMap the expenseSubscriptionMap of the controller
     */
    public void setExpenseSubscriptionMap(Map<Expense, StompSession.Subscription> expenseSubscriptionMap) {
        this.expenseSubscriptionMap = expenseSubscriptionMap;
    }

}
