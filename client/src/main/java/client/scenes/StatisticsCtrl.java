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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.springframework.messaging.simp.stomp.StompSession;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class StatisticsCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final CurrencyConverter currencyConverter;
    private StompSession.Subscription subscription;
    @FXML
    public javafx.scene.chart.PieChart pieChart;
    @FXML
    public Button cancel;
    @FXML
    public VBox ownLegend;
    private String currency;


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
        if (mainCtrl.getEvent() != null) {
            Event e = serverUtils.getEvent(mainCtrl.getEvent().getInviteCode());
            mainCtrl.setEvent(e);
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
        }
        pieChart.titleProperty().set(mainCtrl.getEvent().getTitle());
        cancel.setGraphic(new ImageView(new Image("icons/arrowback.png")));
        currency = config.getProperty("currency");
        if (currency == null || currency.isEmpty()) currency = "EUR";
        setStatistics();
    }

    /**
     * Back to the overview of the expenses of the Event
     */
    public void backToOverview() {
        subscription.unsubscribe();
        mainCtrl.showOverview();
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
            legendList.add(new Pair<>(pair.getKey(),value));
        }


        String legendStyle = "-fx-background-color: white; -fx-border-color: black;";
        pieChart.getData().get(0).getNode().getParent().getParent()
                .lookup(".chart-legend").setStyle(legendStyle);

        updateOwnLegend(legendList,total);


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
        for (Tag tag : event.getTagsList()) {
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
        stats.add(new Pair<>(new Tag("NO TAG", "#000000"), expensesWithTag));
        return stats;
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
    }/**
     * Update own legend version
     */
    private void updateOwnLegend(List<Pair<Tag, Double>> stats, double total) {
        ownLegend.getChildren().clear();
        for (Pair<Tag, Double> data : stats) {
            double precentage = (data.getValue()/total*100);
            String withRightDigits = String.format("%.1f",precentage);
            Label item = new Label(data.getKey().getName() + ": "
                    + String.format("%.2f %s", data.getValue(), currency)
                    + " (" + withRightDigits + "%)");
            item.setTextFill(Color.web(data.getKey().getColor()));
            ownLegend.getChildren().add(item);
        }
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     *  - if ESCAPE is pressed, then it cancels and returns to the overview.
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ESCAPE:
                backToOverview();
                break;
            default:
                break;
        }
    }

}
