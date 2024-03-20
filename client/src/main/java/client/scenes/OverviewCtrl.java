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

import client.utils.ConfigInterface;
import client.utils.LanguageComboBox;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class OverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final ConfigInterface config;

    @FXML
    private LanguageComboBox languages;
    @FXML
    private Label title;
    @FXML
    private ListView participants;

    @FXML
    private ListView all;

    @FXML
    private ChoiceBox<String> expenseparticipants;

    @FXML
    private Button addparticipant;

    @FXML
    private Button editparticipant;



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
        addparticipant.setGraphic(new ImageView(new Image("icons/addparticipant.png")));
        editparticipant.setGraphic(new ImageView(new Image("icons/edit.png")));
        Event event = mainCtrl.getEvent();
        clearFields();
        if(event != null){
            title.setText(event.getTitle());
            List<String> names = event.getParticipantsList().stream()
                    .map(Participant::getName).toList();
            participants.getItems().addAll(names);
            expenseparticipants.getItems().addAll(names);
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
        //Should show the sendInvites scene
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
     * Initialize method for the Overview scene.
     * Sets the language currently in the config file as the selected one.
     * @param url - URL object
     * @param resourceBundle - ResourceBundle object
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String language = config.getProperty("language");
        if (languages != null) languages.setValue(language);
        this.refreshLanguage();
    }
}