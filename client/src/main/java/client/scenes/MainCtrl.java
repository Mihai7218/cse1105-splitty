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

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

public class MainCtrl {

    private Stage primaryStage;

    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;

    private AddQuoteCtrl addCtrl;
    private Scene add;

    private StartScreenCtrl startScreenCtrl;
    private Scene startScreen;

    /**
     * Initialize the main controller with the primary stage,
     * @param primaryStage primary stage of the controller.
     * @param overview overview controller and scene
     * @param add add quote controller and scene
     */
    public void initialize(Stage primaryStage, Pair<QuoteOverviewCtrl, Scene> overview,
            Pair<AddQuoteCtrl, Scene> add, Pair<StartScreenCtrl, Scene> startScreen) {
        this.primaryStage = primaryStage;
        this.overviewCtrl = overview.getKey();
        this.overview = overview.getValue();

        this.addCtrl = add.getKey();
        this.add = add.getValue();

        this.startScreenCtrl = startScreen.getKey();
        this.startScreen = startScreen.getValue();

        showStartMenu();
        primaryStage.show();
    }

    /**
     * Shows the overview scene.
     */
    public void showOverview() {
        primaryStage.setTitle("Quotes: Overview");
        primaryStage.setScene(overview);
        overviewCtrl.refresh();
    }

    /**
     * Shows the start menu scene.
     */
    public void showStartMenu() {
        primaryStage.setTitle("Splitty: Start Screen");
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
}