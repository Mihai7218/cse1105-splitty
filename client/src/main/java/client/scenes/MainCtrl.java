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

import client.utils.LanguageManager;
import com.google.inject.Inject;
import commons.Event;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private QuoteOverviewCtrl qouteoverviewCtrl;
    private Scene qouteoverview;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    private StartScreenCtrl startScreenCtrl;
    private Scene startScreen;

    private LanguageManager languageManager;

    private ParticipantCtrl participantCtrl;
    private Scene participant;
    private OverviewCtrl overviewCtrl;
    private Scene overview;
    private AddExpenseCtrl addExpenseCtrl;
    private Scene addExpense;

    private Event event;

    /**
     * Constructor for the MainCtrl
     * @param languageManager - language manager
     */
    @Inject
    public MainCtrl(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    /**
     * Initialize the main controller with the primary stage,
     *
     * @param primaryStage primary stage of the controller.
     * @param qouteoverview qoute overview controller and scene
     * @param add          add quote controller and scene
     * @param startScreen  start screen controller and scene
     * @param participant participant controller and scene
     * @param overview     overview controller and scene
     * @param addExpense addExpense controller and scene
     */
    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Parent> qouteoverview,
                           Pair<AddQuoteCtrl, Parent> add, Pair<StartScreenCtrl,
                           Parent> startScreen, Pair<ParticipantCtrl, Parent> participant
            , Pair<OverviewCtrl, Parent> overview, Pair<AddExpenseCtrl, Parent> addExpense) {
        this.primaryStage = primaryStage;
        this.qouteoverviewCtrl = qouteoverview.getKey();
        this.qouteoverview = new Scene(qouteoverview.getValue());

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.startScreenCtrl = startScreen.getKey();
        this.startScreen = new Scene(startScreen.getValue());

        this.participantCtrl = participant.getKey();
        this.participant = new Scene(participant.getValue());

        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addExpenseCtrl = addExpense.getKey();
        this.addExpense = new Scene(addExpense.getValue());

        showStartMenu();
        primaryStage.show();
    }

    /**
     * Shows the overview scene.
     */
    public void showOverview() {
        primaryStage.titleProperty().bind(languageManager.bind("startScreen.windowTitle"));
        primaryStage.setScene(overview);
        overviewCtrl.refresh();

    }

    /**
     * Shows the start menu scene.
     */
    public void showStartMenu() {
        primaryStage.titleProperty().bind(languageManager.bind("startScreen.windowTitle"));
        primaryStage.setScene(startScreen);
    }

    /**
     * Shows the add quote scene.
     */
    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    /**
     * Shows the add participant scene.
     */
    public void showParticipant() {
        primaryStage.titleProperty().bind(languageManager.bind("startScreen.windowTitle"));
        primaryStage.setScene(participant);
    }

    /**
     * Shows the add expense scene.
     */
    public void showAddExpense(){
        primaryStage.titleProperty().bind(languageManager.bind("startScreen.windowTitle"));
        primaryStage.setScene(addExpense);
    }

    /**
     * Getter for the primary stage.
     * Package-access getter for testing purposes.
     * @return - primary stage.
     */
    Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Getter for the overview controller.
     * Package-access getter for testing purposes.
     * @return - overview controller.
     */
    QuoteOverviewCtrl getOverviewCtrl() {
        return qouteoverviewCtrl;
    }

    /**
     * Getter for the overview scene.
     * Package-access getter for testing purposes.
     * @return - overview scene.
     */
    Scene getOverview() {
        return qouteoverview;
    }

    /**
     * Getter for the add quote controller.
     * Package-access getter for testing purposes.
     * @return - add quote controller.
     */
    AddQuoteCtrl getAddCtrl() {
        return addCtrl;
    }

    /**
     * Getter for the add quote scene.
     * Package-access getter for testing purposes.
     * @return - add quote scene.
     */
    Scene getAdd() {
        return add;
    }

    /**
     * Getter for the start screen controller.
     * Package-access getter for testing purposes.
     * @return - start screen controller.
     */
    StartScreenCtrl getStartScreenCtrl() {
        return startScreenCtrl;
    }

    /**
     * Getter for the start screen scene.
     * Package-access getter for testing purposes.
     * @return - start screen scene.
     */
    Scene getStartScreen() {
        return startScreen;
    }

    /**
     * Returns the current event of mainCtrl
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Sets the current event of mainCtrl
     */
    public void setEvent(Event event) {
        this.event = event;
    }
}