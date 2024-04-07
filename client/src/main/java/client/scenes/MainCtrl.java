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
import client.utils.LanguageManager;
import com.google.inject.Inject;
import commons.Event;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

public class MainCtrl {

    private Stage primaryStage;

    private ConnectToServerCtrl connectCtrl;
    private Scene connectToServer;

    private final ConfigInterface config;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    private StartScreenCtrl startScreenCtrl;
    private Scene startScreen;

    private LanguageManager languageManager;

    private ParticipantCtrl participantCtrl;
    private Scene participant;

    private EditParticipantCtrl editparticipantCtrl;
    private Scene editparticipant;
    private EditExpenseCtrl editExpenseCtrl;
    private Scene editExpense;
    private OverviewCtrl overviewCtrl;
    private Scene overview;
    private AddExpenseCtrl addExpenseCtrl;
    private Scene addExpense;
    private InvitationCtrl invitationCtrl;
    private StatisticsCtrl statisticsCtrl;
    private Scene invitation;
    private SettingsCtrl settingsCtrl;
    private Scene settings;
    private Scene statistics;
    private DebtsCtrl debtsCtrl;
    private Scene debts;

    private Event event;

    /**
     * Constructor for the MainCtrl
     * @param languageManager - language manager
     */
    @Inject
    public MainCtrl(ConfigInterface config, LanguageManager languageManager) {
        this.config = config;
        this.languageManager = languageManager;
    }

    /**
     * Initialize the main controller with the primary stage,
     *
     * @param primaryStage    primary stage of the controller.
     * @param startScreen     start screen controller and scene
     * @param participant     participant controller and scene
     * @param overview        overview controller and scene
     * @param addExpense      addExpense controller and scene
     * @param invitation      invitation controller and scene
     * @param editparticipant edit participant controller and scene
     * @param settings        settings controller and scene
     * @param statistics      statistics scene
     * @param connectToServer connecting to server scene
     */
    public void initialize(Stage primaryStage,
                           Pair<AddQuoteCtrl, Parent> add,
                           Pair<StartScreenCtrl, Parent> startScreen,
                           Pair<ParticipantCtrl, Parent> participant,
                           Pair<OverviewCtrl, Parent> overview,
                           Pair<AddExpenseCtrl, Parent> addExpense,
                           Pair<InvitationCtrl, Parent> invitation,
                           Pair<EditParticipantCtrl, Parent> editparticipant,
                           Pair<SettingsCtrl, Parent> settings,
                           Pair<StatisticsCtrl, Parent> statistics,
                           Pair<EditExpenseCtrl, Parent> editExpense,
                           Pair<ConnectToServerCtrl, Parent> connectToServer,
                           Pair<DebtsCtrl, Parent> debts ) {
        this.primaryStage = primaryStage;

        this.addCtrl = add.getKey();
        this.add = new Scene(add.getValue());

        this.startScreenCtrl = startScreen.getKey();
        this.startScreen = new Scene(startScreen.getValue());

        this.participantCtrl = participant.getKey();
        this.participant = new Scene(participant.getValue());

        this.editparticipantCtrl = editparticipant.getKey();
        this.editparticipant = new Scene(editparticipant.getValue());

        this.overviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        this.addExpenseCtrl = addExpense.getKey();
        this.addExpense = new Scene(addExpense.getValue());

        this.invitationCtrl = invitation.getKey();
        this.invitation = new Scene(invitation.getValue());

        this.settingsCtrl = settings.getKey();
        this.settings = new Scene(settings.getValue());

        this.editExpenseCtrl = editExpense.getKey();
        this.editExpense = new Scene(editExpense.getValue());

        this.connectCtrl = connectToServer.getKey();
        this.connectToServer = new Scene(connectToServer.getValue());

        this.statisticsCtrl = statistics.getKey();
        this.statistics = new Scene(statistics.getValue());

        this.debtsCtrl = debts.getKey();
        this.debts = new Scene(debts.getValue());

        showConnectToServer();
        primaryStage.show();
    }

    /**
     * shows the Open Debts scene
     */
    public void showDebts(){
        primaryStage.titleProperty().bind(languageManager.bind("debts.sceneTitle"));
        try {
            debts.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(debts);
        if (debtsCtrl != null) debtsCtrl.refresh();
    }

    /**
     * shows scene to send invitations
     */
    public void showInvitation(){
        primaryStage.titleProperty().bind(languageManager.bind("invitation.windowTitle"));
        try {
            invitation.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(invitation);
        if (invitationCtrl != null) invitationCtrl.refresh();
    }
    /**
     * shows scene for statistics
     */
    public void showStatistics(){
        primaryStage.titleProperty().bind(languageManager.bind("statistics.sceneTitle"));
        try {
            statistics.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(statistics);
        if (statisticsCtrl != null) statisticsCtrl.refresh();
    }

    /**
     * shows the edit expense scene
     */
    public void showEditExpense(){
        primaryStage.titleProperty().bind(languageManager.bind("editExpense.windowTitle"));
        try {
            editExpense.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(editExpense);
        if(editExpenseCtrl!=null) editExpenseCtrl.refresh();
    }

    /**
     *
     * @return controller for Open Debts
     */
    public DebtsCtrl getDebtsCtrl() {
        return debtsCtrl;
    }

    /**
     *
     * @return the scene with Open Debts
     */
    public Scene getDebts() {
        return debts;
    }

    /**
     *
     * @return the controller for editing an expense
     */
    public EditExpenseCtrl getEditExpenseCtrl() {
        return editExpenseCtrl;
    }

    /**
     *
     * @return the scene for editing and expense
     */
    public Scene getEditExpense() {
        return editExpense;
    }

    /**
     * shows AddExpense scene
     */
    public void showAddExpense(){
        primaryStage.titleProperty().bind(languageManager.bind("addExpense.windowTitle"));
        try {
            addExpense.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(addExpense);
        addExpenseCtrl.refresh();
    }

    /**
     * Shows the overview scene.
     */
    public void showOverview() {
        primaryStage.titleProperty().bind(languageManager.bind("overview.windowTitle"));
        try {
            overview.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(overview);
        if (overviewCtrl != null) overviewCtrl.refresh();
    }

    /**
     * Calls the method to display successful expense added message
     */
    public void showExpenseConfirmation(){
        overviewCtrl.showConfirmationExpense();
    }

    /**
     * calls the method to display a participant being added successfully
     */
    public void showParticipantConfirmation(){
        overviewCtrl.showConfirmationParticipant();
    }

    /**
     * calls the method to display an edit being made successfully
     */
    public void showEditConfirmation(){
        overviewCtrl.showEditConfirmation();
    }

    /**
     * Shows the start menu scene.
     */
    public void showStartMenu() {
        primaryStage.titleProperty().bind(languageManager.bind("startScreen.windowTitle"));
        try {
            startScreen.getStylesheets().add(getClass().getResource("stylesheet.css")
                    .toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        if (startScreenCtrl != null) startScreenCtrl.refresh();
        primaryStage.setScene(startScreen);
    }

    /**
     * Shows the add quote scene.
     */
    public void showAdd() {
        primaryStage.setTitle("Quotes: Adding Quote");
        primaryStage.setScene(add);
        if (add != null) add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    /**
     * Shows the add participant scene.
     */
    public void showParticipant() {
        primaryStage.titleProperty().bind(languageManager.bind("addParticipant.windowTitle"));
        try {
            participant.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(participant);
    }

    /**
     * Shows the edit participant scene.
     */
    public void showEditParticipant() {
        primaryStage.titleProperty().bind(languageManager.bind("editParticipant.windowTitle"));
        try {
            editparticipant.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(editparticipant);
        if (overviewCtrl != null) editparticipantCtrl.refresh();
    }

    /**
     * Displays the scene for connecting to a server
     */
    public void showConnectToServer() {
        primaryStage.setTitle("Splitty: Connect to a server");
        try {
            connectToServer.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        } catch (NullPointerException e) {
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(connectToServer);
    }

    /**
     * Shows the settings scene.
     */
    public void showSettings() {
        primaryStage.titleProperty().bind(languageManager.bind("settings.windowTitle"));
        try {
            settings.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(settings);
        if (settingsCtrl != null) settingsCtrl.refresh();
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
    public OverviewCtrl getOverviewCtrl() {
        return overviewCtrl;
    }

    /**
     * Getter for the overview scene.
     * Package-access getter for testing purposes.
     * @return - overview scene.
     */
    Scene getOverview() {
        return overview;
    }

    /**
     * Getter for the editParticipant controller.
     * Package-access getter for testing purposes.
     * @return - editParticipant controller.
     */
    public EditParticipantCtrl getEditparticipantCtrl() {
        return editparticipantCtrl;
    }

    /**
     * Getter for the editParticipant scene.
     * Package-access getter for testing purposes.
     * @return - editParticipant scene.
     */
    public Scene getEditparticipant() {
        return editparticipant;
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
    public StartScreenCtrl getStartScreenCtrl() {
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
     * Getter for the add participant controller.
     * Package-access getter for testing purposes.
     * @return - add participant controller.
     */
    ParticipantCtrl getParticipantCtrl() {
        return participantCtrl;
    }

    /**
     * Getter for the add participant scene.
     * Package-access getter for testing purposes.
     * @return - add participant scene.
     */
    Scene getParticipant() {
        return participant;
    }

    /**
     * Setter for the primary stage.
     * @param primaryStage - the primary stage
     */
    void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Getter for the event.
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Setter for the event.
     * @param event - the event
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * Getter for expense controller
     * @return the ExpenseCtrl
     */
    public AddExpenseCtrl getAddExpenseCtrl() {
        return addExpenseCtrl;
    }

    /**
     * Getter for AddExpense scene
     * @return the scene
     */
    public Scene getAddExpense() {
        return addExpense;
    }

    /**
     * Getter for the invitation controller
     * @return the InvitationCtrl
     */
    public InvitationCtrl getInvitationCtrl() {
        return invitationCtrl;
    }

    /**
     * Getter for Invitation scene
     * @return the scene
     */
    public Scene getInvitation() {
        return invitation;
    }

    /**
     * Getter for the settings controller.
     * @return - the settings controller.
     */
    public SettingsCtrl getSettingsCtrl() {
        return settingsCtrl;
    }

    /**
     * Getter for the settings scene.
     * @return - the settings scene.
     */
    Scene getSettings() {
        return settings;
    }

    /**
     * Method that returns an optional password.
     * @return - the optional password.
     */
    public Optional<String> getPassword() {
        String configPassword = config.getProperty("mail.password");
        if (configPassword != null && !configPassword.isEmpty())
            return Optional.of(configPassword);
        Dialog<String> dialog = new Dialog<>();
        dialog.titleProperty().bind(languageManager.bind("mail.passwordTitle"));
        dialog.headerTextProperty().bind(languageManager.bind("mail.passwordHeader"));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField passwordField = new PasswordField();
        HBox hBox = new HBox();
        Label passwordLabel = new Label();
        passwordLabel.textProperty().bind(languageManager.bind("mail.passwordLabel"));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(passwordLabel, passwordField);
        dialog.getDialogPane().setContent(hBox);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });

        return dialog.showAndWait();
    }
}