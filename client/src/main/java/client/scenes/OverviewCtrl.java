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

import client.utils.ExpenseListCell;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class OverviewCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Label title;
    @FXML
    private ListView participants;

    @FXML
    private ListView<Expense> all;

    @FXML
    private ChoiceBox<String> expenseparticipants;

    @FXML
    private Button addparticipant;

    @FXML
    private Button editparticipant;


    /**
     * Constructs a new OverviewCtrl object.
     * @param server ServerUtils object
     * @param mainCtrl MainCtrl object
     */
    @Inject
    public OverviewCtrl(ServerUtils server, MainCtrl mainCtrl) {
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
     * Initialize method for the overview controller.
     * @param url - URL
     * @param resourceBundle - ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        all.setCellFactory(x -> new ExpenseListCell(mainCtrl));
    }
}