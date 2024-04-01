package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Tag;
import javafx.application.Platform;
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
    @FXML
    public Button cancel;
    @FXML
    public VBox ownLegend;


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
        pieChart.titleProperty().set(mainCtrl.getEvent().getTitle());
        cancel.setGraphic(new ImageView(new Image("icons/cancelwhite.png")));
        setStatistics();
    }

    /**
     * Back to the overview of the expenses of the Event
     */
    public void backToOverview() {
        mainCtrl.showOverview();
    }


    /**
     * set the right statistics for the event
     */
    public void setStatistics() {
        pieChart.getData().clear();
        List<Pair<Tag, List<Expense>>> stats = pairListMaker();
        System.out.println(stats);

        for (Pair<Tag, List<Expense>> pair : stats) {
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


        String legendStyle = "-fx-background-color: white; -fx-border-color: black;";
        pieChart.getData().get(0).getNode().getParent().getParent()
                .lookup(".chart-legend").setStyle(legendStyle);

        updateOwnLegend();

        double total = 0;
        for (Expense expense : mainCtrl.getEvent().getExpensesList()) {
            total += expense.getAmount();
        }
        pieChart.titleProperty().set(mainCtrl.getEvent().getTitle() +
                "\nThe total sum is: " + total);
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
    private void updateOwnLegend() {
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

}
