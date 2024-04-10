/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.*;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.springframework.messaging.simp.stomp.StompSession;

import java.net.URL;
import java.util.*;

public class OverviewCtrl implements Initializable, LanguageSwitcher, NotificationSender {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final CurrencyConverter currencyConverter;
    private final ConfigInterface config;
    @FXML
    public Button showStatisticsButton;
    @FXML
    public Button addTransferButton;
    @FXML
    private Tab fromTab;
    @FXML
    private Tab includingTab;
    @FXML
    private LanguageComboBox languages;
    @FXML
    private Label title;
    @FXML
    private ListView<Participant> participants;

    private Label participantFrom;
    private Label participantIncluding;

    @FXML
    private ListView<Expense> all;
    @FXML
    private ListView<Expense> from;
    @FXML
    private ListView<Expense> including;

    @FXML
    private ChoiceBox<Participant> expenseparticipants;
    @FXML
    private Button settings;
    @FXML
    private Button addparticipant;
    @FXML
    private Button editparticipant;

    @FXML
    private Button sendMail;
    @FXML
    private Button cancel;
    @FXML
    private Label expenseAdded;
    @FXML
    private Button settleDebts;
    @FXML
    private Button addExpenseButton;
    private StompSession.Subscription expensesSubscription;
    private Map<Expense, StompSession.Subscription> expenseSubscriptionMap;
    private Map<Tag, StompSession.Subscription> tagSubscriptionMap;
    private StompSession.Subscription tagSubscription;
    private StompSession.Subscription participantSubscription;

    private Map<Participant, StompSession.Subscription> participantSubscriptionMap;

    @FXML
    private Label sumExpense;
    @FXML
    private Label sumLabel;
    @FXML
    private Label code;
    @FXML
    private Label inviteLang;


    /**
     * Constructs a new OverviewCtrl object.
     *
     * @param languageManager LanguageManager object
     * @param config Config object
     * @param server ServerUtils object
     * @param mainCtrl MainCtrl object
     * @param currencyConverter CurrencyConverter object
     */
    @Inject
    public OverviewCtrl(LanguageManager languageManager,
                        ConfigInterface config,
                        ServerUtils server,
                        MainCtrl mainCtrl,
                        CurrencyConverter currencyConverter) {
        this.languageManager = languageManager;
        this.config = config;
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.currencyConverter = currencyConverter;
    }

    /**
     * Getter for the tag subscription map.
     *
     * @return - the tag subscription map of the overview controller.
     */
    public Map<Tag, StompSession.Subscription> getTagSubscriptionMap() {
        return tagSubscriptionMap;
    }

    /**
     * Method that populates the lists related to expenses.
     */
    public void populateExpenses() {
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getExpensesList() != null) {
            all.getItems().clear();
            List<Expense> expenses = new ArrayList<>();
            List<Participant> participantsList = new ArrayList<>();
            try {
                expenses = server.getAllExpenses(mainCtrl.getEvent().getInviteCode());
                participantsList = server.getEvent(mainCtrl.getEvent().getInviteCode())
                        .getParticipantsList();
            } catch (WebApplicationException e) {
                e.printStackTrace();
            }
            for (Expense expense : expenses) {
                if (!expenseSubscriptionMap.containsKey(expense))
                    subscribeToExpense(expense);
                if (!all.getItems().contains(expense))
                    all.getItems().add(expense);
            }
            for (Participant participant : participantsList) {
                if (!participantSubscriptionMap.containsKey(participant))
                    subscribeToParticipant(participant);
                if (!participants.getItems().contains(participant))
                    participants.getItems().add(participant);
            }
            mainCtrl.getEvent().setExpensesList(expenses);
            all.getItems().sort((o1, o2) -> -o1.getDate().compareTo(o2.getDate()));
            all.refresh();
            filterViews();
        }
        String base = getCurrency();
        sumExpense.setText(String.format("%.2f %s", getSum(), base));
    }

    /**
     * Method that populates the lists related to participants.
     */
    public void populateParticipants() {
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null) {
            List<Participant> serverparticipants = new ArrayList<>();
            try {
                serverparticipants = server.getAllParticipants(mainCtrl.getEvent().getInviteCode());
            }
            catch (WebApplicationException e) {
                e.printStackTrace();
            }
            participants.getItems().clear();
            participants.getItems(). addAll(serverparticipants);
            for (Participant p : serverparticipants) {
                if (!expenseparticipants.getItems().contains(p)) {
                    expenseparticipants.getItems().add(p);
                }
            }
            List<Participant> removed = new ArrayList<>();
            for (Participant p : expenseparticipants.getItems()) {
                if (!serverparticipants.contains(p)){
                    removed.add(p);
                }
//                    expenseparticipants.getItems().remove(p);
            }
            expenseparticipants.getItems().removeAll(removed);
            participants.refresh();
        }
    }

    /**
     * Refreshes all shown items in the overview.
     */
    public void refresh() {
        addparticipant.setGraphic(new ImageView(new Image("icons/addParticipant.png")));
        settleDebts.setGraphic(new ImageView(new Image("icons/checkwhite.png")));
        settings.setGraphic(new ImageView(new Image("icons/settingswhite.png")));
        addExpenseButton.setGraphic(new ImageView(new Image("icons/plus.png")));
        addTransferButton.setGraphic(new ImageView(new Image("icons/plus.png")));
        showStatisticsButton.setGraphic(new ImageView(new Image("icons/graph.png")));
        cancel.setGraphic(new ImageView(new Image("icons/cancelwhite.png")));
        Event event = mainCtrl.getEvent();
        if (event != null) {
            title.setText(event.getTitle());
            code.setText(String.valueOf(event.getInviteCode()));
            participants.getItems().sort(Comparator.comparing(Participant::getName));
            expenseparticipants.getItems().sort(Comparator.comparing(Participant::getName));
            server.registerForMessages(String.format("/topic/events/%s",
                    mainCtrl.getEvent().getInviteCode()), Event.class, q -> Platform.runLater(() ->{
                        mainCtrl.getEvent().setTitle(q.getTitle());
                        title.setText(q.getTitle());
                    }));
            if (expensesSubscription == null)
                expensesSubscription = server.registerForMessages("/topic/events/" +
                                mainCtrl.getEvent().getInviteCode() + "/expenses", Expense.class,
                        expense -> {
                            Platform.runLater(() -> {
                                all.getItems().add(expense);
                                mainCtrl.getEvent().getExpensesList().add(expense);
                                all.getItems().sort((o1, o2) ->
                                        -o1.getDate().compareTo(o2.getDate()));
                                filterViews();
                                all.refresh();
                                populateParticipants();
                                sumExpense.setText(String.format(
                                        "%.2f %s", getSum(), getCurrency()));
                                subscribeToExpense(expense);
                                mainCtrl.getDebtsCtrl().refresh();
                            });
                        });
            if (participantSubscription == null)
                participantSubscription = server.registerForMessages("/topic/events/" +
                                mainCtrl.getEvent()
                                        .getInviteCode() + "/participants", Participant.class,
                        participant -> Platform.runLater(() -> {
                            participants.getItems().add(participant);
                            mainCtrl.getEvent().getParticipantsList().add(participant);
                            subscribeToParticipant(participant);
                            mainCtrl.getDebtsCtrl().refresh();
                            populateParticipants();
                        }));
            if (tagSubscription == null)
                tagSubscription = server.registerForMessages("/topic/events/" +
                                mainCtrl.getEvent().getInviteCode() + "/tags", Tag.class,
                        tag -> {
                            Platform.runLater(() -> {
                                subscribeToTag(tag);
                            });
                        });
            for (Tag tag : event.getTagsList()) {
                if (!tagSubscriptionMap.containsKey(tag))
                    subscribeToTag(tag);
            }
        }
    }

    /**
     * @param participant the participant to subscribe to
     */
    private void subscribeToParticipant(Participant participant) {
        if (!participantSubscriptionMap.containsKey(participant)) {
            String dest = "/topic/events/" +
                    mainCtrl.getEvent().getInviteCode() + "/participants/"
                    + participant.getId();
            var subscription = server.registerForMessages(dest, Participant.class,
                    part -> Platform.runLater(() -> {
                        mainCtrl.getEvent().getParticipantsList().remove(participant);
                        if (!"deleted".equals(part.getIban())) {
                            mainCtrl.getEvent().getParticipantsList().add(part);
                        }
                        mainCtrl.getDebtsCtrl().refresh();
                        populateExpenses();
                        populateParticipants();
                    }));
            participantSubscriptionMap.put(participant, subscription);
        }
    }

    /**
     * Method that subscribes to updates for an tag.
     * @param tag - the tag to subscribe to.
     */
    private void subscribeToTag(Tag tag) {
        if (!tagSubscriptionMap.containsKey(tag)) {
            String dest = "/topic/events/" +
                    mainCtrl.getEvent().getInviteCode() + "/tags/"
                    + tag.getId();
            var subscription = server.registerForMessages(dest, Tag.class,
                    exp -> Platform.runLater(() -> {
                        mainCtrl.getEvent().getTagsList().remove(exp);
                        if (!"deleted".equals(exp.getColor())) {
                            mainCtrl.getEvent().getTagsList().add(exp);
                        }
                        populateExpenses();
                        populateParticipants();
                    }));
            tagSubscriptionMap.put(tag, subscription);
        }
    }

    /**
     * Method that subscribes to updates for an expense.
     * @param expense - the expense to subscribe to.
     */
    private void subscribeToExpense(Expense expense) {
        if (!expenseSubscriptionMap.containsKey(expense)) {
            String dest = "/topic/events/" +
                    mainCtrl.getEvent().getInviteCode() + "/expenses/"
                    + expense.getId();
            var subscription = server.registerForMessages(dest, Expense.class,
                    exp -> Platform.runLater(() -> {
                        all.getItems().remove(expense);
                        mainCtrl.getEvent().getExpensesList().remove(expense);
                        all.refresh();
                        if (!"deleted".equals(exp.getDescription())) {
                            all.getItems().add(exp);
                            mainCtrl.getEvent().getExpensesList().add(exp);
                            all.getItems().sort((o1, o2) ->
                                    -o1.getDate().compareTo(o2.getDate()));
                        }
                        filterViews();
                        all.refresh();
                        mainCtrl.getDebtsCtrl().refresh();
                        populateParticipants();
                        String baseCurrency = getCurrency();
                        sumExpense.setText(String.format("%.2f %s", getSum(), baseCurrency));
                    }));
            expenseSubscriptionMap.put(expense, subscription);
        }
    }

    /**
     * changes language
     */
    @Override
    public void changeLanguage() {
        LanguageSwitcher.super.changeLanguage();
    }

    /**
     * Method that gets the code of the currency that is currently set.
     * @return - the currency code.
     */
    private String getCurrency() {
        String currencyString = config.getProperty("currency");
        if (currencyString == null || currencyString.isEmpty()) currencyString = "EUR";
        return currencyString;
    }


    /**
     * Opens the addparticipant scene to be able to add participants to the event.
     */
    public void addParticipant() {
        mainCtrl.showParticipant();
    }

    /**
     * Opens transfer scene to add a transfer to an event
     */
    public void addTransfer(){
        mainCtrl.showTransfer();
    }

    /**
     * Opens the addExpense scene to be able to add Expenses to the event.
     */
    public void addExpense() {
        mainCtrl.showAddExpense();
    }

    /**
     * Goes back to the startMenu.
     */
    public void startMenu() {
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
        if (participantSubscription != null) {
            participantSubscription.unsubscribe();
            participantSubscription = null;
        }
        if (participantSubscriptionMap != null) {
            participantSubscriptionMap.forEach((k, v) -> v.unsubscribe());
            participantSubscriptionMap = new HashMap<>();
        }
        clearFields();
        mainCtrl.showStartMenu();
    }

    /**
     * Opens the sendInvites scene to be able to send Invites to the other people.
     */
    public void sendInvites() {
        mainCtrl.showInvitation();
    }
    /**
     * Opens the statistics scene to be able to see the statistics.
     */
    public void statistics(){
        mainCtrl.showStatistics();
    }

    /**
     * Settles the debts of the event.
     */
    public void settleDebts() {
        mainCtrl.showDebts();
    }

    /**
     * Change the title.
     */
    public void changeTitle() {
        TextField changeable = new TextField(title.getText());
        title.setGraphic(changeable);
        title.setText("");
        changeable.requestFocus();
        changeable.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                title.setGraphic(null);
                Event event = mainCtrl.getEvent();
                event.setTitle(changeable.getText());
                server.changeEvent(event);
                server.send("/app/events", mainCtrl.getEvent());
            }
        });
    }

    /**
     * Clears all the fields
     */
    private void clearFields() {
        if (expenseparticipants != null) {
            expenseparticipants.getItems().clear();
        }

    }

    /**
     * Initialize method for the Overview scene.
     * Sets the language currently in the config file as the selected one.
     * Sets the cell factories for all ListViews.
     *
     * @param url            - URL
     * @param resourceBundle - ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        languages.setCellFactory(languageManager);
        String language = config.getProperty("language");
        if (languages != null) languages.setValue(language);
        this.refreshLanguage();
        all.setCellFactory(x ->
                new ExpenseListCell(mainCtrl, languageManager, currencyConverter, config, server));
        from.setCellFactory(x ->
                new ExpenseListCell(mainCtrl, languageManager, currencyConverter, config, server));
        including.setCellFactory(x ->
                new ExpenseListCell(mainCtrl, languageManager, currencyConverter, config, server));
        Label fromLabel = new Label();
        Label includingLabel = new Label();
        participantFrom = new Label();
        participantIncluding = new Label();
        sumLabel.textProperty().bind(languageManager.bind("overview.totalSum"));
        fromLabel.textProperty().bind(languageManager.bind("overview.fromTab"));
        includingLabel.textProperty().bind(languageManager.bind("overview.includingTab"));
        fromTab.setGraphic(new HBox(fromLabel, participantFrom));
        includingTab.setGraphic(new HBox(includingLabel, participantIncluding));
        participants.setCellFactory(x -> new ParticipantCell(mainCtrl,
                languageManager, config, currencyConverter));
        expenseparticipants.setConverter(new StringConverter<Participant>() {
            @Override
            public String toString(Participant participant) {
                if (participant == null) return "";
                return participant.getName();
            }

            @Override
            public Participant fromString(String s) {
                return null;
            }
        });
        expenseSubscriptionMap = new HashMap<>();
        tagSubscriptionMap = new HashMap<>();
        participantSubscriptionMap = new HashMap<>();
        refresh();
    }

    /**
     * Method that filters the views to a specific participant.
     */
    public void filterViews() {
        Participant participant = expenseparticipants.getValue();
        if (participant == null) {
            from.getItems().clear();
            including.getItems().clear();
            participantFrom.setText("");
            participantIncluding.setText("");
            return;
        }
        participantFrom.setText(participant.getName());
        participantIncluding.setText(participant.getName());
        from.getItems().clear();
        from.getItems().addAll(all.getItems().stream()
                .filter(x -> x.getPayee().equals(participant)).toList());
        including.getItems().clear();
        including.getItems().addAll(all.getItems().stream()
                .filter(x -> !(x.getSplit().stream()
                        .map(ParticipantPayment::getParticipant)
                        .filter(y -> y.equals(participant)).toList().isEmpty())).toList());
    }

    /**
     * Removes a participant from the list
     */
    public void removeParticipant(Participant participant) {
        List<Expense> expenses = mainCtrl.getEvent().getExpensesList();
        for(Expense e: expenses){
            if(!e.getSplit().stream()
                    .filter(item -> item.getParticipant()
                            .equals(participant)).toList().isEmpty()
                || e.getPayee().equals(participant)){
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "");
                confirmation.contentTextProperty().bind(
                        languageManager.bind("overview.removeParticipant"));
                confirmation.titleProperty().bind(languageManager.bind("commons.warning"));
                confirmation.headerTextProperty().bind(languageManager.bind("commons.warning"));
                Optional<ButtonType> result = confirmation.showAndWait();
                if(!(result.isPresent() && result.get() == ButtonType.OK)) {
                    return;
                } else {
                    break;
                }
            }
        }
        for (Expense expense : expenses) {
            if (expense.getPayee().equals(participant)) {
                try {
                    server.removeExpense(mainCtrl.getEvent().getInviteCode(), expense.getId());
                } catch (WebApplicationException e) {
                    if (mainCtrl.getOverviewCtrl() == null
                            || mainCtrl.getOverviewCtrl().getExpenseSubscriptionMap() == null)
                        return;
                    var sub = expenseSubscriptionMap.get(expense);
                    if (sub != null)
                        sub.notify();
                }
            } else if(!expense.getSplit().stream().filter(x -> x.getParticipant()
                    .equals(participant)).toList().isEmpty()) {
                recalculateSplit(expense, participant);
                try {
                    server.updateExpense(mainCtrl.getEvent().getInviteCode(), expense);
                } catch (WebApplicationException e) {
                    var sub = expenseSubscriptionMap.get(expense);
                    if (sub != null)
                        sub.notify();
                }
            }
        }
        server.removeParticipant(mainCtrl.getEvent().getInviteCode(),participant);
        participants.getItems().remove(participant);
        expenseparticipants.getItems().remove(participant);
        participants.refresh();
    }

    /**
     * Recalculate expenses on deletion of participant.
     * @param expense - the expense.
     * @param participant - the deleted participant.
     */
    private void recalculateSplit(Expense expense, Participant participant) {
        double amount = expense.getAmount();
        int amountCents = (int) (amount*100);
        List<Participant> participantsList = new ArrayList<>(expense.getSplit().stream()
                .map(ParticipantPayment::getParticipant).toList());
        participantsList.remove(participant);
        expense.getSplit().clear();
        int noParticipants = participantsList.size();

        Map<Participant, ParticipantPayment> participantSplits = new HashMap<>();
        double amtAdded = (double)(amountCents/noParticipants)/100.0;
        //System.out.println(amtAdded);
        int remainder = amountCents % noParticipants;
        for (Participant p : participantsList) {
            ParticipantPayment newP = new ParticipantPayment(p, amtAdded);
            expense.getSplit().add(newP);
            participantSplits.put(p, newP);
        }
        Collections.shuffle(participantsList);
        int counter = 0;
        while(remainder > 0){
            Participant subject = participantsList.get(counter);
            double initAmt = participantSplits.get(subject).getPaymentAmount();
            participantSplits.get(participantsList.get(counter)).setPaymentAmount(initAmt + 0.01);
            remainder--;
            counter++;
            //System.out.println(subject.toString() + " got the extra cent!");
        }
    }

    /**
     * method to calculate the sum of all expenses in the event
     * @return double for the event total
     */
    public double getSum(){
        double sum = 0;
        if (mainCtrl.getEvent() == null) return sum;
        List<Expense> expenses = mainCtrl.getEvent().getExpensesList();
        for (Expense e : expenses) {
            String currency = e.getCurrency();
            Date date = e.getDate();
            String base = getCurrency();
            sum += currencyConverter.convert(date, currency, base, e.getAmount());
        }
        return sum;
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
     * Gets the notification label.
     * @return - the notification label.
     */
    @Override
    public Label getNotificationLabel() {
        return expenseAdded;
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
     *
     */
    public ListView<Participant> getParticipantsListView() {
        return participants;
    }

    /**
     * Gets the List of participants of the event
     */
    public List<Participant> getParticipants() {
        if (mainCtrl.getEvent() == null) return new ArrayList<>();
        List<Participant> participantsList = mainCtrl.getEvent().getParticipantsList();
        return participantsList;
    }

    /**
     * Method that updates the language combo box with the correct flag.
     *
     * @param language - code of the new language
     */
    public void updateLanguageComboBox(String language) {
        if (languages != null) languages.setValue(language);
    }

    /**
     * Method that handles the settings button.
     */
    public void settings() {
        mainCtrl.getSettingsCtrl().setPrevScene(true);
        mainCtrl.showSettings();
    }
    /**
     * Getter for the expense subscription map.
     *
     * @return - the expense subscription map of the overview controller.
     */
    public Map<Expense, StompSession.Subscription> getExpenseSubscriptionMap() {
        return expenseSubscriptionMap;
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     *  - if ENTER is pressed, then it goes to settle debts.
     *  - if ESCAPE is pressed, then it cancels and returns to the startscreen.
     *  - if Ctrl + p is pressed, then it opens the add participant scene.
     *  - if Ctrl + e is pressed, then it opens the add expense scene.
     *  - if Ctrl + s is pressed, then it opens the statistics.
     *  - if Ctrl + m is pressed, then it returns to the startscreen.
     *  - if Ctrl + t is pressed, then it opens the settings.
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                settleDebts();
                break;
            case ESCAPE:
                startMenu();
                break;
            case E:
                if(e.isControlDown()){
                    addExpense();
                    break;
                }
            case P:
                if(e.isControlDown()){
                    addParticipant();
                    break;
                }
            case S:
                if(e.isControlDown()){
                    statistics();
                    break;
                }
            case M:
                if(e.isControlDown()){
                    startMenu();
                    break;
                }
            case T:
                if(e.isControlDown()){
                    settings();
                    break;
                }
            default:
                break;
        }
    }
}