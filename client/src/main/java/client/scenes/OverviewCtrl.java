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
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

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
    private Button settleDebts;
    @FXML
    private Button addExpenseButton;



    /**
     * Constructs a new OverviewCtrl object.
     * @param languageManager LanguageManager object
     * @param config Config object
     * @param server ServerUtils object
     * @param mainCtrl MainCtrl object
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
            all.getItems().addAll(mainCtrl.getEvent().getExpensesList());
            all.getItems().sort((o1, o2) -> -o1.getDate().compareTo(o2.getDate()));
            all.refresh();
        }
        addparticipant.setGraphic(new ImageView(new Image("icons/addParticipant.png")));
        settleDebts.setGraphic(new ImageView(new Image("icons/checkwhite.png")));
        addExpenseButton.setGraphic(new ImageView(new Image("icons/whiteplus.png")));
        cancel.setGraphic(new ImageView(new Image("icons/cancelwhite.png")));
        Event event = mainCtrl.getEvent();
        clearFields();
        if(event != null){
            title.setText(event.getTitle());
            participants.getItems().addAll(event.getParticipantsList());
            expenseparticipants.getItems().addAll(event.getParticipantsList());
        }
    }



    /**
     * Opens the addparticipant scene to be able to add participants to the event.
     */
    public void addParticipant(){
        mainCtrl.showParticipant();
    }

    /**
     * Opens the addExpense scene to be able to add Expenses to the event.
     */
    public void addExpense(){
        mainCtrl.showAddExpense();
    }

    /**
     * Goes back to the startMenu.
     */
    public void startMenu(){
        mainCtrl.showStartMenu();
    }

    /**
     * Opens the sendInvites scene to be able to send Invites to the other people.
     */
    public void sendInvites(){
        mainCtrl.showInvitation();
    }

    /**
     * Settles the debts of the event.
     */
    public void settleDebts(){
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
                server.send("/app/events",mainCtrl.getEvent());
            }
        });
    }

    /**
     * Clears all the fields
     */
    private void clearFields() {
        if(participants!=null){
            participants.getItems().clear();
        }
        if(expenseparticipants!=null){
            expenseparticipants.getItems().clear();
        }

    }

    /**
<<<<<<< HEAD
     * Initialize method for the Overview scene.
     * Sets the language currently in the config file as the selected one.
     * Sets the cell factories for all ListViews.
     * @param url - URL
     * @param resourceBundle - ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String language = config.getProperty("language");
        if (languages != null) languages.setValue(language);
        this.refreshLanguage();
        all.setCellFactory(x -> new ExpenseListCell(mainCtrl, languageManager));
        from.setCellFactory(x -> new ExpenseListCell(mainCtrl, languageManager));
        including.setCellFactory(x -> new ExpenseListCell(mainCtrl, languageManager));
        Label fromLabel = new Label();
        Label includingLabel = new Label();
        participantFrom = new Label();
        participantIncluding = new Label();
        fromLabel.textProperty().bind(languageManager.bind("overview.fromTab"));
        includingLabel.textProperty().bind(languageManager.bind("overview.includingTab"));
        fromTab.setGraphic(new HBox(fromLabel, participantFrom));
        includingTab.setGraphic(new HBox(includingLabel, participantIncluding));
        participants.setCellFactory(x -> new ParticipantCell(mainCtrl));
        participants.getItems().addAll(getParticipants());
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
        refresh();
        server.registerForMessages("/topic/events", Event.class,q -> {
            mainCtrl.setEvent(q);
            Platform.runLater(() -> refresh());
        });
    }

    /**
     * Method that filters the views to a specific participant.
     */
    public void filterViews() {
        Participant participant = expenseparticipants.getValue();
        if (participant == null) return;
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
=======
     * Removes a participant from the list
     */
    public void removeParticipant(Participant participant) {
        participants.getItems().remove(participant);
        participants.refresh();
    }

    /**
>>>>>>> c605a63e9bea86de7055e63fa987bc6f19703c64
     * Getter for the language manager observable map.
     * @return - the language manager observable map.
     */
    public ObservableMap<String, Object> getLanguageManager() {
        return languageManager.get();
    }

    /**
     * Setter for the language manager observable map.
     * @param languageManager - the language manager observable map.
     */
    public void setLanguageManager(ObservableMap<String, Object> languageManager) {
        this.languageManager.set(languageManager);
    }

    /**
     * Getter for the language manager property.
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
     * Gets the List of participants of the event
     */
    private List<Participant> getParticipants() {
        if (mainCtrl.getEvent() == null) return new ArrayList<>();
        List<Participant> participantsList = mainCtrl.getEvent().getParticipantsList();
        return participantsList;
    }

    /**
     * Method that updates the language combo box with the correct flag.
     * @param language - code of the new language
     */
    public void updateLanguageComboBox(String language) {
        if (languages != null) languages.setValue(language);
    }
}