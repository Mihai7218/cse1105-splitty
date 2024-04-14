package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import jakarta.mail.MessagingException;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class DebtsCtrl implements Initializable, NotificationSender {
    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final Alert alert;
    private final CurrencyConverter currencyConverter;
    private final MailSender mailSender;
    @FXML
    private Label confirmation;
    private boolean canRemind;
    @FXML
    private Accordion menu;
    @FXML
    private Button back;
    @FXML
    private Button remind;
//    @FXML
//    private Label noDebts;

    /**
     * @param mainCtrl
     * @param config
     * @param languageManager
     * @param serverUtils
     * @param alert
     */
    @Inject
    public DebtsCtrl(MainCtrl mainCtrl,
                     ConfigInterface config,
                     LanguageManager languageManager,
                     ServerUtils serverUtils,
                     CurrencyConverter currencyConverter,
                     Alert alert,
                     MailSender mailSender) {
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageManager = languageManager;
        this.serverUtils = serverUtils;
        this.currencyConverter = currencyConverter;
        this.alert = alert;
        this.mailSender = mailSender;
    }

    /**
     * trying to get the mark received button into a green checkstyle, throws errors for now
     *
     * @param button
     */
    public static void animation(Button button) {
        Label checkMark = new Label("\u2714"); // Unicode for checkmark symbol
        checkMark.setStyle("-fx-text-fill: green; " +
                "-fx-font-size: 24px; -fx-opacity: 0;"); // Initially invisible
        Timeline timeline = new Timeline();

        // Animate button text opacity to 0
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5),
                new KeyValue(button.opacityProperty(), 0)));

        // Animate check mark opacity to 1
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5),
                new KeyValue(checkMark.opacityProperty(), 1)));

        // Change the button text after animation completes
        timeline.setOnFinished(e -> button.setText("\u2714"));

        // Play the animation
        timeline.play();
    }

    /**
     * @param url            The location used to resolve relative paths for the root object, or
     *                       {@code null} if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or {@code null} if
     *                       the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        back.textProperty().bind(languageManager.bind("debts.setButton"));
    }

    /**
     * loads the data for the scene
     */
    public void refresh() {
        String expanded = null;
        var expandedPane = menu.getExpandedPane();
        if (expandedPane != null) {
            expanded = expandedPane.getText();
        }
        menu.getPanes().clear();
        String host = config.getProperty("mail.host");
        String port = config.getProperty("mail.port");
        String user = config.getProperty("mail.user");
        String email = config.getProperty("mail.email");
        canRemind = host != null && !host.isEmpty()
                && port != null && !port.isEmpty()
                && user != null && !user.isEmpty()
                && email != null && !email.isEmpty();
        setTitles(mainCtrl.getEvent());
        for (var pane : menu.getPanes()) {
            if (expanded != null && pane.getText().equals(expanded)) {
                menu.setExpandedPane(pane);
                break;
            }
        }
    }

    /**
     * returns the serverutils object
     * @return serverUtils
     */
    public ServerUtils getServerUtils() {
        return serverUtils;
    }

    /**
     * returns the accordion of the debts
     * @return menu
     */
    public Accordion getMenu() {
        return menu;
    }

    /**
     * sets the accordion of the debts
     * @param menu
     */
    public void setMenu(Accordion menu) {
        this.menu = menu;
    }

    /**
     * returns the back button
     * @return back
     */
    public Button getBack() {
        return back;
    }

    /**
     * sets the back button
     * @param back
     */
    public void setBack(Button back) {
        this.back = back;
    }

    /**
     * return to overview
     */
    public void goBack() {
        mainCtrl.showOverview();
    }

    /**
     * goes through all participants payments that have to be paid
     *
     * @param event to search for money splits
     */
    public void setTitles(Event event) {
        Map<Participant, Double> shares = new HashMap<>();
        for (Participant participant : event.getParticipantsList()) {
            shares.put(participant, calculateShare(participant));
        }
        PriorityQueue<DebtPair> positive = new PriorityQueue<>();
        positive.addAll(shares.entrySet().stream().filter(x -> x.getValue() > 0)
                .map(pair -> new DebtPair(pair.getKey(), pair.getValue())).toList());
        PriorityQueue<DebtPair> negative = new PriorityQueue<>();
        negative.addAll(shares.entrySet().stream().filter(x -> x.getValue() < 0)
                .map(pair -> new DebtPair(pair.getKey(), Math.abs(pair.getValue()))).toList());
        List<Debt> debts = new ArrayList<>();
        while (!positive.isEmpty() && !negative.isEmpty()) {
            DebtPair owed = positive.poll();
            DebtPair owes = negative.poll();
            if (owed.getValue() > owes.getValue()) {
                debts.add(new Debt(owes.getKey(), owed.getKey(), owes.getValue()));
                owed.setValue(owed.getValue() - owes.getValue());
                positive.add(owed);
            } else if (owed.getValue() < owes.getValue()) {
                debts.add(new Debt(owes.getKey(), owed.getKey(), owed.getValue()));
                owes.setValue(owes.getValue() - owed.getValue());
                negative.add(owes);
            } else {
                debts.add(new Debt(owes.getKey(), owed.getKey(), owed.getValue()));
            }
        }
        for (Debt debt : debts) {
            populateAccordion(event, debt);
        }
    }

    /**
     * Setter for canRemind boolean.
     * @param canRemind - boolean for if reminders should be available.
     */
    public void setCanRemind(boolean canRemind) {
        this.canRemind = canRemind;
    }

    /**
     * Populates the accordion with the information in the debt.
     * @param event - the event
     * @param debt - the debt.
     */
    private void populateAccordion(Event event, Debt debt) {
        if (!debt.getDebtor().getName().equals(debt.getCreditor().getName())) {
            String title = String.format("%s: %.2f %s => %s",
                    debt.getDebtor().getName(),
                    debt.getSum(),
                    getCurrency(),
                    debt.getCreditor().getName());
            TitledPane tp = new TitledPane(title, null);
            menu.getPanes().add(tp);
            AnchorPane anchorPane = new AnchorPane();
            Label info = new Label();
            Button mark = new Button();
            remind = new Button();
            mark.setVisible(true);
            mark.textProperty().bind(languageManager.bind("debts.send"));
            mark.setOnAction(x ->
            {
                createExpense(debt);
                mark.textProperty().bind(languageManager.bind("debts.check"));
            });
            if (debt.getCreditor().getBic().equals("\u2714") ||
                    debt.getCreditor().getIban().equals("")) {
                info.textProperty().bind(languageManager.bind("debts.unavailable"));
            } else {
                String data = debt.getCreditor().getName() + "\nIBAN: " +
                        debt.getCreditor().getIban() + "\nBIC: " +
                        debt.getCreditor().getBic();

                info.setText(data);

            }
            remind.textProperty().bind(languageManager.bind("debts.remind"));
            if (!canRemind) {
                Tooltip tooltip = new Tooltip();
                tooltip.textProperty().bind(languageManager
                        .bind("debts.unavailableReminder"));
                remind.setTooltip(tooltip);
                remind.setId("disabledButton");
            } else if (debt.getDebtor().getEmail() == null
                    || debt.getDebtor().getEmail().isEmpty()) {
                Tooltip tooltip = new Tooltip();
                tooltip.textProperty().bind(languageManager
                        .bind("debts.missingEmail"));
                remind.setTooltip(tooltip);
                remind.setId("disabledButton");
            } else {
                remind.setId(null);
                remind.setTooltip(null);
                remind.setOnAction(x -> {
                    String address = config.getProperty("server");
                    if (address == null || address.isEmpty())
                        address = "http://localhost:8080";
                    String host = config.getProperty("mail.host");
                    String port = config.getProperty("mail.port");
                    String username = config.getProperty("mail.user");
                    String email = config.getProperty("mail.email");
                    try {
                        showNotification("mail.sending");
                        mailSender.sendReminder(address, event.getInviteCode(),
                                debt.getDebtor(), debt.getCreditor(),
                                String.format("%.2f %s",
                                        debt.getSum(), getCurrency()),
                                host, port, username, email);
                        showNotification("debts.reminderConfirmation");
                    } catch (MessagingException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.initModality(Modality.APPLICATION_MODAL);
                        if (e.getClass().equals(MissingPasswordException.class)) {
                            alert.contentTextProperty().bind(
                                    languageManager.bind("mail.noPassword"));
                        } else {
                            alert.contentTextProperty().unbind();
                            alert.setContentText(e.getMessage());
                        }
                        alert.showAndWait();
                        return;
                    }
                });
            }
            anchorPane.getChildren().add(info);
            anchorPane.getChildren().add(mark);
            anchorPane.getChildren().add(remind);
            anchorPane.setTopAnchor(info, 10.0);
            anchorPane.setLeftAnchor(info, 10.0);

            // Position the button relative to the label
            anchorPane.setTopAnchor(mark,
                    AnchorPane.getTopAnchor(info) + info.getPrefHeight() + 30.0);
            anchorPane.setLeftAnchor(mark,
                    AnchorPane.getLeftAnchor(info) + info.getPrefWidth() + 300.0);
            anchorPane.setTopAnchor(remind,
                    AnchorPane.getTopAnchor(info) + info.getPrefHeight() + 30.0);
            anchorPane.setLeftAnchor(remind,
                    AnchorPane.getLeftAnchor(info) + info.getPrefWidth() + 200.0);
            tp.setContent(anchorPane);
        }
    }

    /**
     * Creates the debt settlement expense and adds
     * @param debt the debt to settle
     */
    public void createExpense(Debt debt) {
        ParticipantPayment from = new ParticipantPayment(debt.getDebtor(), debt.getSum());
        ParticipantPayment to = new ParticipantPayment(debt.getCreditor(), debt.getSum());
        List<ParticipantPayment> split = List.of(from, to);
        Expense settlement = new Expense(debt.getSum(), getCurrency(), "Transfer", "settlement",
                java.sql.Date.valueOf(LocalDate.now()),
                split, null, debt.getDebtor());
        try{
            serverUtils.addExpense(mainCtrl.getEvent().getInviteCode(), settlement);
        }catch (WebApplicationException e) {
            switch (e.getResponse().getStatus()) {
                case 400 -> {
                    throwAlert("addExpense.badReqHeader", "addExpense.badReqBody");
                }
                case 404 -> {
                    throwAlert("addExpense.notFoundHeader", "addExpense.notFoundBody");
                }
            }
        }

    }

    /**
     * Method that throws an alert.
     * @param header - property associated with the header.
     * @param body - property associated with the body.
     */
    protected void throwAlert(String header, String body) {
        alert.titleProperty().bind(languageManager.bind("commons.warning"));
        alert.headerTextProperty().bind(languageManager.bind(header));
        alert.contentTextProperty().bind(languageManager.bind(body));
        alert.showAndWait();
        alert.headerTextProperty().bind(languageManager.bind("commons.warning"));
    }

    /**
     * Method that calculates the share per person and returns it as a double.
     *
     * @param current - the participant to calculate the share for.
     * @return - the share of that participant.
     */
    private double calculateShare(Participant current) {
        double participantShare = 0;
        Event curr = mainCtrl.getEvent();
        List<Expense> expenses = curr.getExpensesList();
        for (Expense expense : expenses) {
            String currency = expense.getCurrency();
            Date date = expense.getDate();
            String base = getCurrency();
            for (ParticipantPayment p : expense.getSplit()) {
                if (p.getParticipant().equals(current)
                        && !expense.getPayee().equals(current)) {
                    participantShare -= currencyConverter.convert(date,
                            currency, base, p.getPaymentAmount());
                } else if (expense.getPayee().equals(current)
                        && !p.getParticipant().equals(current)) {
                    participantShare += currencyConverter.convert(date,
                            currency, base, p.getPaymentAmount());
                }
            }
        }
        return Math.round(participantShare * 100.0) / 100.0;
    }


    /**
     * Method that gets the currency from the config.
     *
     * @return - the correct currency from the config.
     */
    private String getCurrency() {
        String currencyString = config.getProperty("currency");
        if (currencyString == null || currencyString.isEmpty()) currencyString = "EUR";
        return currencyString;
    }

    /**
     * Getter for the language manager map.
     *
     * @return - the language manager observable map.
     */
    public ObservableMap<String, Object> getLanguageManager() {
        return languageManager.get();
    }

    /**
     * Gets the notification label.
     *
     * @return - the notification label.
     */
    @Override
    public Label getNotificationLabel() {
        return confirmation;
    }

    /**
     * Sets the notification label.
     * @param notificationLabel - the notification label.
     */
    public void setNotificationLabel(Label notificationLabel){
        this.confirmation = notificationLabel;
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
     * When the shortcut is used it goes back to the startmenu.
     */
    public void startMenu() {
        mainCtrl.showStartMenu();
    }

    /**
     * Back to the overview of the expenses of the Event
     */
    public void backToOverview() {
        mainCtrl.showOverview();
    }

    /**
     * Getter for the remind button.
     *
     * @return - the button.
     */
    public Button getRemind() {
        return remind;
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     *  - if ESCAPE is pressed, then it cancels and returns to the tags scene.
     *  - if Ctrl + m is pressed, then it returns to the startscreen.
     *  - if Ctrl + o is pressed, then it returns to the overview.
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ESCAPE:
                goBack();
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
}
