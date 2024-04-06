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

    private QuoteOverviewCtrl quoteOverviewCtrl;
    private Scene quoteOverview;

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

    private Scene invitation;
    private StatisticsCtrl statisticsCtrl;
    private Scene statistics;
    private SettingsCtrl settingsCtrl;
    private Scene settings;

    private EditTagCtrl editTagCtrl;
    private Scene editTag;

    private ManageTagsCtrl manageTagsCtrl;
    private Scene manageTags;


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
     * @param primaryStage    primary stage of the controller.
     * @param quoteOverview   quote overview controller and scene
     * @param add             add quote controller and scene
     * @param startScreen     start screen controller and scene
     * @param participant     participant controller and scene
     * @param overview        overview controller and scene
     * @param addExpense      addExpense controller and scene
     * @param invitation      invitation controller and scene
     * @param editparticipant edit participant controller and scene
     * @param settings        settings controller and scene
     * @param statistics
     * @param manageTags
     * @param editTag
     */
    public void initialize(Stage primaryStage,
                           Pair<QuoteOverviewCtrl, Parent> quoteOverview,
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
                           Pair<ManageTagsCtrl, Parent> manageTags,
                           Pair<EditTagCtrl, Parent> editTag) {
        this.primaryStage = primaryStage;
        this.quoteOverviewCtrl = quoteOverview.getKey();
        this.quoteOverview = new Scene(quoteOverview.getValue());

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

        this.statisticsCtrl = statistics.getKey();
        this.statistics = new Scene(statistics.getValue());

        this.manageTagsCtrl = manageTags.getKey();
        this.manageTags = new Scene(manageTags.getValue());

        this.editTagCtrl = editTag.getKey();
        this.editTag = new Scene(editTag.getValue());


        showStartMenu();
        primaryStage.show();
    }

    /**
     * shows scene to send invitations
     */
    public void showInvitation(){
        primaryStage.titleProperty().bind(languageManager.bind("sendInvitations.windowTitle"));
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
        if (statisticsCtrl != null) statisticsCtrl.setup();
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
    public void showEditTags(){
        primaryStage.titleProperty().bind(languageManager.bind("editTags.sceneTitle"));
        try {
            editTag.getStylesheets().add(getClass()
                    .getResource("stylesheet.css").toExternalForm());
        }catch(NullPointerException e){
            System.out.println("exception caught: Null Pointer Exception");
        }
        primaryStage.setScene(editTag);
        if (editTagCtrl != null) editTagCtrl.refresh();
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
    QuoteOverviewCtrl getQuoteOverviewCtrl() {
        return quoteOverviewCtrl;
    }

    /**
     * Getter for the overview scene.
     * Package-access getter for testing purposes.
     * @return - overview scene.
     */
    Scene getQuoteOverview() {
        return quoteOverview;
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
}