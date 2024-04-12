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

import client.commands.ICommand;
import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import com.google.inject.Inject;
import commons.Event;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.util.Optional;
import java.util.Stack;

public class MainCtrl {

    private Stage primaryStage;

    private ConnectToServerCtrl connectCtrl;
    private Scene connectToServer;

    private final ConfigInterface config;

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

    private Scene invitation;
    private StatisticsCtrl statisticsCtrl;
    private Scene statistics;
    private SettingsCtrl settingsCtrl;
    private Scene settings;

    private EditTagCtrl editTagCtrl;
    private Scene editTag;

    private ManageTagsCtrl manageTagsCtrl;
    private Scene manageTags;

    private DebtsCtrl debtsCtrl;
    private Scene debts;

    private AddTransferCtrl transferCtrl;
    private Scene transfer;

    private EditTransferCtrl editTransferCtrl;
    private Scene editTransfer;

    private FileChooser fileChooser = new FileChooser();

    private Event event;


    Stack<ICommand> history;

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
     * @param manageTags      Manage tag controller and scene
     * @param editTag         editTag controller and scene
     * @param statistics      statistics scene
     * @param connectToServer connecting to server scene
     * @param transfer        transfer controller and scene
     */
    public void initialize(Stage primaryStage,
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
                           Pair<DebtsCtrl, Parent> debts,
                           Pair<ManageTagsCtrl, Parent> manageTags,
                           Pair<EditTagCtrl, Parent> editTag,
                           Pair<AddTransferCtrl, Parent> transfer,
                           Pair<EditTransferCtrl, Parent> editTransfer){
        this.primaryStage = primaryStage;

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

        this.manageTagsCtrl = manageTags.getKey();
        this.manageTags = new Scene(manageTags.getValue());

        this.editTagCtrl = editTag.getKey();
        this.editTag = new Scene(editTag.getValue());

        this.debtsCtrl = debts.getKey();
        this.debts = new Scene(debts.getValue());

        history = new Stack<>();

        this.transferCtrl = transfer.getKey();
        this.transfer = new Scene(transfer.getValue());

        this.editTransferCtrl = editTransfer.getKey();
        this.editTransfer = new Scene(editTransfer.getValue());


        showConnectToServer();
        primaryStage.show();
    }

    /**
     * Displays the edit transfer scene
     */
    public void showEditTransfer(){
        primaryStage.titleProperty().bind(languageManager.bind("transfer.sceneTitle"));
        try {
            editTransfer.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(editTransfer);
        if (editTransferCtrl != null) editTransferCtrl.refresh();
        if (editTransfer != null) editTransfer.setOnKeyPressed
            (e -> editTransferCtrl.keyPressed(e));

    }

    /**
     * Displays the transfer scene
     */
    public void showTransfer(){
        primaryStage.titleProperty().bind(languageManager.bind("transfer.sceneTitle"));
        try {
            transfer.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(transfer);
        if (transferCtrl != null) transferCtrl.refresh();
        if (transferCtrl != null) transfer.setOnKeyPressed
                (e -> transferCtrl.keyPressed(e));
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
        if (invitation != null) invitation.setOnKeyPressed(e -> invitationCtrl.keyPressed(e));

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
        if (statisticsCtrl != null) statisticsCtrl.setup();
        if (statistics != null) statistics.setOnKeyPressed(e -> statisticsCtrl.keyPressed(e));

    }

    /**
     * shows scene for Manage Tags Screen
     */
    public void showManageTags(){
        primaryStage.titleProperty().bind(languageManager.bind("manageTags.sceneTitle"));
        try {
            manageTags.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(manageTags);
        if (manageTagsCtrl != null) manageTagsCtrl.setup();
    }

    /**
     * shows scene for Manage Tags Screen
     */
    public void showEditTag(){
        primaryStage.titleProperty().bind(languageManager.bind("editTag.sceneTitle"));
        try {
            editTag.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(editTag);
        if (editTagCtrl != null) editTagCtrl.refresh();
        if (statisticsCtrl != null) statisticsCtrl.refresh();
        if (statistics != null) statistics.setOnKeyPressed(e -> statisticsCtrl.keyPressed(e));

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
        if (editExpense != null) editExpense.setOnKeyPressed(e -> editExpenseCtrl.keyPressed(e));

    }

    /**
     * Getter for edit transfer controller
     * @return edit transfer controller
     */
    public EditTransferCtrl getEditTransferCtrl() {
        return editTransferCtrl;
    }

    /**
     * Getter for edit transfer scene
     * @return edit transfer scene
     */
    public Scene getEditTransfer() {
        return editTransfer;
    }

    /**
     * Getter for transfer controller
     * @return transfer controller
     */
    public AddTransferCtrl getTransferCtrl() {
        return transferCtrl;
    }

    /**
     * Getter for transfer scene
     * @return transfer scene
     */
    public Scene getTransfer() {
        return transfer;
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
     * @return the controller for editing an expense
     */
    public EditExpenseCtrl getEditExpenseCtrl() {
        return editExpenseCtrl;
    }
    /**
     *
     * @return the controller for editing a tag
     */
    public EditTagCtrl getTagCtrl() {
        return editTagCtrl;
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
        if (addExpense != null) addExpense.setOnKeyPressed(e -> addExpenseCtrl.keyPressed(e));

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
        if (overview != null) overview.setOnKeyPressed(e -> overviewCtrl.keyPressed(e));
    }

    /**
     * Calls the method to display successful expense added message
     */
    public void showExpenseConfirmation(){
        overviewCtrl.showNotification("overview.confirmExpenseAdd");
    }

    /**
     * calls the method to display a participant being added successfully
     */
    public void showParticipantConfirmation(){
        overviewCtrl.showNotification("overview.confirmParticipantAdd");
    }

    /**
     * calls the method to display an edit being made successfully
     */
    public void showEditConfirmation(){
        overviewCtrl.showNotification("overview.confirmEdits");
    }

    /**
     * calls the method to display a message that invites were sent successfully
     */
    public void showInviteConfirmation(){
        overviewCtrl.showNotification("overview.confirmInvite");
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
        if (startScreen != null) startScreen
                .setOnKeyPressed(e -> startScreenCtrl.keyPressed(e));
        history.clear();
    }


    /**
     * Adds a command to application history
     * @param i command to add
     */
    public void addToHistory(ICommand i){
        history.push(i);
    }

    /**
     * Getter for the history stack in main
     * @return history stack
     */
    public Stack<ICommand> getHistory() {
        return history;
    }

    /**
     * Undoes the most recent change
     */
    public void undo(){
        if(!history.isEmpty()){
            ICommand prev = history.pop();
            editExpenseCtrl.undo(prev);
        }
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
        if (participant != null) participant.setOnKeyPressed(e -> participantCtrl.keyPressed(e));
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
        if (editparticipant != null) editparticipant
                .setOnKeyPressed(e -> editparticipantCtrl.keyPressed(e));

    }

    /**
     * Displays the scene for connecting to a server
     */
    public void showConnectToServer() {
        if (primaryStage.getTitle() == null || primaryStage.getTitle().isEmpty()) {
            primaryStage.setTitle("Splitty: Connect to a server");
        }

        try {
            connectToServer.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        } catch (NullPointerException e) {
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(connectToServer);
        if (connectToServer != null) connectToServer
                .setOnKeyPressed(e -> connectCtrl.keyPressed(e));

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
        if (settings != null) settings.setOnKeyPressed(e -> settingsCtrl.keyPressed(e));

    }

    /**
     * Shows the file picker.
     * @param defaultName - the default name of the file.
     * @return - the new file.
     */
    public File pickLocation(String defaultName) {
        fileChooser.setInitialFileName(defaultName);
        return fileChooser.showSaveDialog(primaryStage);
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