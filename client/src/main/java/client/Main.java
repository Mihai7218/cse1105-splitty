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
package client;

import client.scenes.*;
import client.utils.Config;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * Main method of the client.
     * @param args array of arguments passed to the method.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Starts the primary stage.
     * @param primaryStage primary stage
     */
    @Override
    public void start(Stage primaryStage) {

        var qouteoverview = FXML.load(QuoteOverviewCtrl.class, "client"
                , "scenes", "QuoteOverview.fxml");
        var add = FXML.load(AddQuoteCtrl.class, "client", "scenes", "AddQuote.fxml");
        var startScreen = FXML.load(StartScreenCtrl.class, "client", "scenes", "StartScreen.fxml");
        var participant = FXML.load(ParticipantCtrl.class, "client", "scenes", "Participant.fxml");
        var overview = FXML.load(OverviewCtrl.class, "client", "scenes", "Overview.fxml");
        var addExpense = FXML.load(AddExpenseCtrl.class, "client", "scenes", "AddExpense.fxml");
        var editparticipant =
                FXML.load(EditParticipantCtrl.class, "client", "scenes", "EditParticipant.fxml");
        var invitation = FXML.load(InvitationCtrl.class, "client", "scenes", "Invitation.fxml");


        var mainCtrl = INJECTOR.getInstance(MainCtrl.class);
        mainCtrl.initialize(primaryStage, qouteoverview, add, startScreen,
                participant, overview, addExpense, invitation, editparticipant);
        primaryStage.setOnCloseRequest(e -> {
            startScreen.getKey().stop();
        });

    }

    /**
     * Method that runs when the application is terminated.
     * Saves the config to file.
     * @throws Exception -
     */
    @Override
    public void stop() throws Exception {
        var config = INJECTOR.getInstance(Config.class);
        config.saveProperties();
        super.stop();
    }
}