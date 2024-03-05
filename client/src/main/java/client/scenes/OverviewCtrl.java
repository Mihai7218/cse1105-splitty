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

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.stream.Collectors;

public class OverviewCtrl{

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Event event;

    @FXML
    private Label title;
    @FXML
    private ListView participants;

    @FXML
    private ListView all;

    @FXML
    private ChoiceBox<String> expenseparticipants;


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
         this.event = mainCtrl.getEvent();
         if(this.event != null){
             title.setText(event.getTitle());
             List<String> names = event.getParticipantsList().stream()
                     .map(Participant::getName).collect(Collectors.toList());
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
     * Opens the sendInvites scene to be able to send Invites to the other people.
     */
    public void sendInvites(){
        //Should show the sendInvites scene
    }

    /**
     * Change the title.
     */
    public void changeTitle() {
       //Should update the title of the Event that is being added.
    }

}