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

import com.google.inject.Inject;

import client.utils.ServerUtils;
import commons.Person;
import commons.Quote;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

public class AddQuoteCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField quote;

    /**
     * Constructs a new AddQuoteCtrl object.
     * @param server ServerUtils object
     * @param mainCtrl MainCtrl object
     */
    @Inject
    public AddQuoteCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;

    }

    /**
     * Clears fields and returns to the overview
     */
    public void cancel() {
        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * Adds the new quote, then returns to the overview.
     */
    public void ok() {
        try {
            server.addQuote(getQuote());
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * Returns a new Quote object with the provided details.
     * @return a Quote object with the details.
     */
    private Quote getQuote() {
        var p = new Person(firstName.getText(), lastName.getText());
        var q = quote.getText();
        return new Quote(p, q);
    }

    /**
     * Clears the fields.
     */
    private void clearFields() {
        firstName.clear();
        lastName.clear();
        quote.clear();
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     *  - if ENTER is pressed, then it submits the quote.
     *  - if ESCAPE is pressed, then it cancels and returns to the overview.
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                ok();
                break;
            case ESCAPE:
                cancel();
                break;
            default:
                break;
        }
    }
}