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
import commons.Participant;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class ParticipantCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Participant participant;

    @FXML
    private TextField name;

    @FXML
    private TextField email;

    @FXML
    private TextField iban;

    @FXML
    private TextField bic;


    /**
     * Constructs a new AddQuoteCtrl object.
     * @param server ServerUtils object
     * @param mainCtrl MainCtrl object
     */
    @Inject
    public ParticipantCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.participant = new Participant("","","","");
    }

    /**
     * Returns a new Participant object with the provided details.
     * @return a participant object with the details.
     */
    private Participant getParticipant() {
        var p = new Participant(name.getText(),email.getText(),iban.getText(),bic.getText());
        return p;
    }

    /**
     * When the ok button is pressed the new Participant is stored on the server.
     */
    public void ok() {
//        try {
//            server.addQuote(getQuote());
//        } catch (WebApplicationException e) {
//
//            var alert = new Alert(Alert.AlertType.ERROR);
//            alert.initModality(Modality.APPLICATION_MODAL);
//            alert.setContentText(e.getMessage());
//            alert.showAndWait();
//            return;
//        }

        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * When the abort button is pressed it goes back to the overview
     */
    public void abort() {
        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * Clears all the text fields
     */
    private void clearFields() {
        name.clear();
        email.clear();
        iban.clear();
        bic.clear();
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
                abort();
                break;
            default:
                break;
        }
    }

}