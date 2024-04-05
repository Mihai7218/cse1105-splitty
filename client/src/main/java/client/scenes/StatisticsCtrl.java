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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

    private StompSession.Subscription expensesSubscription;
    private Map<Expense, StompSession.Subscription> expenseSubscriptionMap;


    /**
     * Constructor for the StartScreenCtrl
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
        mainCtrl.showOverview();
    }


    /**
     * set the right statistics for the event
     */
    public void setStatistics() {
        pieChart.getData().clear();
        List<Pair<Tag, Double>> legendList = new ArrayList<>();
        List<Pair<Tag, List<Expense>>> stats = pairListMaker();
        System.out.println(stats);

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
    private void updateOwnLegend2() {
        ownLegend.getChildren().clear();
        for (PieChart.Data data : pieChart.getData()) {
            Label item = new Label(data.getName());
            String style = data.getNode().getStyle();
            String colorString = style.substring(style.indexOf("-fx-pie-color:") + 15,
                    style.indexOf(";"));
            item.setTextFill(Color.web(colorString));
            ownLegend.getChildren().add(item);
        }
    }

    /**
     * Update own legend version
     */
    private void updateOwnLegend(List<Pair<Tag, Double>> stats, double total) {
        ownLegend.getChildren().clear();
        for (Pair<Tag, Double> data : stats) {
            double precentage = (data.getValue() / total * 100);
            String withRightDigits = String.format("%.1f", precentage);
            Label item = new Label(data.getKey().getName() + ": "
                    + String.format("%.2f %s", data.getValue(), currency)
                    + " (" + withRightDigits + "%)");
            item.setTextFill(Color.web(data.getKey().getColor()));
            ownLegend.getChildren().add(item);
        }
    }

}
