package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Tag;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.util.Pair;

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
    @FXML
    public javafx.scene.chart.PieChart pieChart;


    /**
     * Constructor for the StartScreenCtrl
     * @param mainCtrl - main controller
     * @param config - config
     */
    @Inject
    public StatisticsCtrl(MainCtrl mainCtrl,
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
        if (mainCtrl.getEvent() != null) {
            Event e = serverUtils.getEvent(mainCtrl.getEvent().getInviteCode());
            mainCtrl.setEvent(e);
        }
        this.refreshLanguage();
        serverUtils.registerForMessages("/topic/events", Event.class, q -> {
            mainCtrl.setEvent(q);
            Platform.runLater(() -> refresh());
        });
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
     * Refreshes the statistics
     */
    public void refresh() {
        pieChart.titleProperty().set(mainCtrl.getEvent().getTitle());
        setStatistics();
    }


    /**
     * set the right statistics for the event
     */
    public void setStatistics() {
        pieChart.getData().clear();
        List<Pair<Tag, List<Expense>>> stats = pairListMaker();
        System.out.println(stats);
        for(Pair<Tag,List<Expense>> pair : stats) {
            String catagoryName = pair.getKey().getName();
            double value = 0;
            for (Expense expense : pair.getValue()) {
                value += expense.getAmount();
            }
            PieChart.Data slice = new PieChart.Data(catagoryName, value);
            pieChart.getData().add(slice);
            try {
                slice.getNode().setStyle("-fx-pie-color: " + pair.getKey().getColor() + ";");
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }

    /**
     * Generate a list with Pairs with the right stats
     * @return a list of Tag and the associate expenses
     */
    private List<Pair<Tag, List<Expense>>> pairListMaker() {
        Event event = mainCtrl.getEvent();
        List<Pair<Tag,List<Expense>>> stats = new ArrayList<>();
        for(Tag tag : event.getTagsList()) {
            List<Expense> expensesWithTag = new ArrayList<>();
            for(Expense expense : event.getExpensesList()) {
                if (expense.getTag() != null && expense.getTag().equals(tag)) {
                    expensesWithTag.add(expense);
                }
            }
            stats.add(new Pair<>(tag,expensesWithTag));
        }
        List<Expense> expensesWithTag = new ArrayList<>();
        for(Expense expense : event.getExpensesList()) {
            if (expense.getTag() == null) {
                expensesWithTag.add(expense);
            }
        }
        stats.add(new Pair<>(new Tag("NO TAG","#000000"),expensesWithTag));
        return stats;
    }


}
