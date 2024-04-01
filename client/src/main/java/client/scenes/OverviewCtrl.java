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
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.springframework.messaging.simp.stomp.StompSession;

import java.net.URL;
import java.util.*;

public class OverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final ConfigInterface config;
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
    @FXML
    private Label sumExpense;
    @FXML
    private Label sumLabel;


    /**
     * Constructs a new OverviewCtrl object.
     *
     * @param languageManager LanguageManager object
     * @param config          Config object
     * @param server          ServerUtils object
     * @param mainCtrl        MainCtrl object
     */
    @Inject
    public OverviewCtrl(LanguageManager languageManager, ConfigInterface config,
                        ServerUtils server, MainCtrl mainCtrl) {
        this.languageManager = languageManager;
        this.config = config;
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Refreshes all shown items in the overview.
     */
    public void refresh() {
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getExpensesList() != null) {
            all.getItems().clear();
            List<Expense> expenses = new ArrayList<>();
            try {
                expenses = server.getAllExpenses(mainCtrl.getEvent().getInviteCode());
            } catch (WebApplicationException e) {
                e.printStackTrace();
            }
            for (Expense expense : expenses) {
                if (!expenseSubscriptionMap.containsKey(expense)) {
                    String dest = "/topic/events/" +
                            mainCtrl.getEvent().getInviteCode() + "/expenses/"
                            + expense.getId();
                    var subscription = server.registerForMessages(dest, Expense.class,
                            exp -> {
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
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Platform.runLater(() -> {
                                    participants.refresh();
                                });
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Platform.runLater(() ->
                                        sumExpense.setText(String.format("%.2f", getSum())));
                            });
                    expenseSubscriptionMap.put(expense, subscription);
                }
            }
            all.getItems().addAll(expenses);
            all.getItems().sort((o1, o2) -> -o1.getDate().compareTo(o2.getDate()));
            all.refresh();
        }
        sumExpense.setText(String.format("%.2f", getSum()));
        addparticipant.setGraphic(new ImageView(new Image("icons/addParticipant.png")));
        settleDebts.setGraphic(new ImageView(new Image("icons/checkwhite.png")));
        addExpenseButton.setGraphic(new ImageView(new Image("icons/plus.png")));
        cancel.setGraphic(new ImageView(new Image("icons/cancelwhite.png")));
        Event event = mainCtrl.getEvent();
        if (event != null) {
            title.setText(event.getTitle());
            for (Participant p : event.getParticipantsList()) {
                if (!participants.getItems().contains(p))
                    participants.getItems().add(p);
                if (!expenseparticipants.getItems().contains(p))
                    expenseparticipants.getItems().add(p);
                participants.getItems().sort(Comparator.comparing(Participant::getName));
                expenseparticipants.getItems().sort(Comparator.comparing(Participant::getName));
            }
            if (expensesSubscription == null)
                expensesSubscription = server.registerForMessages("/topic/events/" +
                                mainCtrl.getEvent().getInviteCode() + "/expenses", Expense.class,
                        expense -> {
                            all.getItems().add(expense);
                            mainCtrl.getEvent().getExpensesList().add(expense);
                            all.getItems().sort((o1, o2) -> -o1.getDate().compareTo(o2.getDate()));
                            filterViews();
                            all.refresh();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Platform.runLater(() -> {
                                participants.refresh();
                            });
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Platform.runLater(() ->
                                    sumExpense.setText(String.format("%.2f", getSum())));
                        });
        }
    }


    /**
     * Opens the addparticipant scene to be able to add participants to the event.
     */
    public void addParticipant() {
        mainCtrl.showParticipant();
    }

    /**
     * Opens the addExpense scene to be able to add Expenses to the event.
     */
    public void addExpense() {
        mainCtrl.showAddExpense();
        refresh();
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
     * method to display a confirmation message for the expense added
     * this message disappears
     */
    public void showConfirmationExpense() {
        expenseAdded.textProperty().bind(languageManager.bind("overview.confirmExpenseAdd"));
        expenseAdded.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), expenseAdded);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(event -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), expenseAdded);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(f -> expenseAdded.setVisible(false));
                fadeOut.play();
            });
            delay.play();
        });

        fadeIn.play();
    }

    /**
     * method to display a confirmation message for participant added
     * this message disappears
     */
    public void showConfirmationParticipant() {
        expenseAdded.textProperty().bind(languageManager.bind("overview.confirmParticipantAdd"));
        expenseAdded.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), expenseAdded);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(event -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), expenseAdded);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(f -> expenseAdded.setVisible(false));
                fadeOut.play();
            });
            delay.play();
        });

        fadeIn.play();
    }

    /**
     * General method to show a confirmation message for any edits
     */
    public void showEditConfirmation() {
        expenseAdded.textProperty().bind(languageManager.bind("overview.confirmEdits"));
        expenseAdded.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), expenseAdded);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(event -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), expenseAdded);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(f -> expenseAdded.setVisible(false));
                fadeOut.play();
            });
            delay.play();
        });

        fadeIn.play();
    }


    /**
     * Settles the debts of the event.
     */
    public void settleDebts() {
        //Should show the sendInvites scene
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
                title.setText(changeable.getText());
                mainCtrl.getEvent().setTitle(changeable.getText());
                server.send("/app/events", mainCtrl.getEvent());
            }
        });
    }

    /**
     * Clears all the fields
     */
    private void clearFields() {
        if (participants != null) {
            participants.getItems().clear();
        }
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
        String language = config.getProperty("language");
        if (languages != null) languages.setValue(language);
        this.refreshLanguage();
        all.setCellFactory(x -> new ExpenseListCell(mainCtrl, server, languageManager));
        from.setCellFactory(x -> new ExpenseListCell(mainCtrl, server, languageManager));
        including.setCellFactory(x -> new ExpenseListCell(mainCtrl, server, languageManager));
        Label fromLabel = new Label();
        Label includingLabel = new Label();
        participantFrom = new Label();
        participantIncluding = new Label();
        sumLabel.textProperty().bind(languageManager.bind("overview.totalSum"));
        fromLabel.textProperty().bind(languageManager.bind("overview.fromTab"));
        includingLabel.textProperty().bind(languageManager.bind("overview.includingTab"));
        fromTab.setGraphic(new HBox(fromLabel, participantFrom));
        includingTab.setGraphic(new HBox(includingLabel, participantIncluding));
        participants.setCellFactory(x -> new ParticipantCell(mainCtrl, languageManager));
        participants.getItems().addAll(getParticipants());
        participants.getItems().sort(Comparator.comparing(Participant::getName));
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
        refresh();
        server.registerForMessages("/topic/events", Event.class, q -> {
            mainCtrl.setEvent(q);
            Platform.runLater(() -> refresh());
        });
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
        for (Expense e : expenses) {
            if (!e.getSplit().stream()
                    .filter(item -> item.getParticipant()
                            .equals(participant)).toList().isEmpty()
                    || e.getPayee().equals(participant)) {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "");
                confirmation.contentTextProperty().bind(
                        languageManager.bind("overview.removeParticipant"));
                confirmation.titleProperty().bind(languageManager.bind("commons.warning"));
                confirmation.headerTextProperty().bind(languageManager.bind("commons.warning"));
                Optional<ButtonType> result = confirmation.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    participants.getItems().remove(participant);
                    participants.refresh();
                    return;
                } else {
                    return;
                }
            }
        }

        participants.getItems().remove(participant);
        participants.refresh();

    }

    /**
     * method to calculate the sum of all expenses in the event
     *
     * @return double for the event total
     */
    public double getSum() {
        double sum = 0;
        if (mainCtrl.getEvent() == null) return sum;
        List<Expense> expenses = mainCtrl.getEvent().getExpensesList();
        for (Expense e : expenses) {
            sum += e.getAmount();
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
     * Getter for the language manager property.
     *
     * @return - the language manager property.
     */
    public LanguageManager languageManagerProperty() {
        return languageManager;
    }


    /**
     * Changes language
     */
    public void changeLanguage() {
        String language = "";
        if (languages != null) language = languages.getValue();
        config.setProperty("language", language);
        if (mainCtrl != null && mainCtrl.getOverviewCtrl() != null
                && mainCtrl.getStartScreenCtrl() != null) {
            mainCtrl.getStartScreenCtrl().updateLanguageComboBox(languages.getValue());
            mainCtrl.getOverviewCtrl().updateLanguageComboBox(languages.getValue());
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
        updateLanguageComboBox(language);
        languageManager.changeLanguage(Locale.of(language));
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
     * Getter for the expense subscription map.
     *
     * @return - the expense subscription map of the overview controller.
     */
    public Map<Expense, StompSession.Subscription> getExpenseSubscriptionMap() {
        return expenseSubscriptionMap;
    }
}