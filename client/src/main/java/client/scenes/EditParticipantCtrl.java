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
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Participant;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import org.springframework.messaging.simp.stomp.StompSession;


public class EditParticipantCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    @FXML
    private TextField name;
    @FXML
    private TextField email;
    @FXML
    private TextField iban;
    @FXML
    private TextField bic;
    //this is the participant that is being edited.
    private Participant participant;
    private StompSession.Subscription participantSubscription;


    /**
     * Constructs a new ParticipantCtrl object.
     *
     * @param server          ServerUtils object
     * @param mainCtrl        MainCtrl object
     * @param languageManager LanguageManager object
     */
    @Inject
    public EditParticipantCtrl(ServerUtils server, MainCtrl mainCtrl,
                               LanguageManager languageManager) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.languageManager = languageManager;
    }

    /**
     * Refreshes all shown items in the overview.
     */
    public void refresh() {
        name.setText(participant.getName());
        email.setText(participant.getEmail());
        iban.setText(participant.getIban());
        bic.setText(participant.getBic());
        String dest = "/topic/events/" +
                mainCtrl.getEvent().getInviteCode() + "/participants/"
                + participant.getId();
        participantSubscription = server.registerForMessages(dest, Participant.class,
                part -> Platform.runLater(() -> {
                    if ("deleted".equals(part.getIban())) {
                        var alert = new Alert(Alert.AlertType.WARNING);
                        alert.headerTextProperty().bind(languageManager
                                .bind("editParticipant.removedParticipantHeader"));
                        alert.contentTextProperty().bind(languageManager
                                .bind("editParticipant.removedParticipantBody"));
                        alert.initModality(Modality.APPLICATION_MODAL);
                        alert.showAndWait();
                        abort();
                    }
                }));
    }

    /**
     * Returns a new Participant object with the provided details.
     *
     * @return a participant object with the details.
     */
    public Participant getParticipant() {
        return new Participant(name.getText(), email.getText(), iban.getText(), bic.getText());
    }

    /**
     * sets the participant that is getting edited
     *
     * @param participant
     */
    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    /**
     * When the ok button is pressed the new Participant is stored on the server.
     */
    public void ok() {
        boolean bicPresent = bic.getText().isEmpty();
        boolean ibanPresent = iban.getText().isEmpty();
        if (name.getText().isEmpty()) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.contentTextProperty().bind(languageManager
                    .bind("editParticipant.emptyFields"));
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        if (bicPresent != ibanPresent) {
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.contentTextProperty()
                    .bind(languageManager.bind("editParticipant.invalidPayment"));
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        try {
            participant.setName(name.getText());
            participant.setEmail(email.getText());
            participant.setIban(iban.getText());
            participant.setBic(bic.getText());
            server.changeParticipant(mainCtrl.getEvent(), participant);
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        abort();
        mainCtrl.showEditConfirmation();
    }

    /**
     * When the abort button is pressed it goes back to the overview
     */
    public void abort() {
        if (participantSubscription != null) {
            participantSubscription.unsubscribe();
            participantSubscription = null;
        }
        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * Clears all the text fields
     */
    private void clearFields() {
        if (name != null) {
            name.clear();
        }
        if (email != null) {
            email.clear();
        }
        if (iban != null) {
            iban.clear();
        }
        if (bic != null) {
            bic.clear();
        }
    }

    /**
     * When the shortcut is used it goes back to the startmenu.
     */
    public void startMenu() {
        clearFields();
        mainCtrl.showStartMenu();
    }

    /**
     * Getter for the language manager observable map.
     *
     * @return - the language manager observable map.
     */
    public ObservableMap<String, Object> getLanguageManager() {
        return languageManager.get();
    }

    /**
     * Setter for the language manager observable map.
     *
     * @param languageManager - the language manager observable map.
     */
    public void setLanguageManager(ObservableMap<String, Object> languageManager) {
        this.languageManager.set(languageManager);
    }

    /**
     * Getter for the language manager property.
     *
     * @return - the language manager property.
     */
    public LanguageManager languageManagerProperty() {
        return languageManager;
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     * - if ENTER is pressed, then it edits the participant with the current values.
     * - if ESCAPE is pressed, then it cancels and returns to the overview.
     * - if Ctrl + m is pressed, then it returns to the startscreen.
     * - if Ctrl + o is pressed, then it returns to the overview.
     *
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
            case M:
                if (e.isControlDown()) {
                    startMenu();
                    break;
                }
            case O:
                if (e.isControlDown()) {
                    abort();
                    break;
                }
            default:
                break;
        }
    }

    /**
     * Sets the textifeld for the name
     * @param name textfield
     */
    public void setName(TextField name) {
        this.name = name;
    }

    /**
     * Sets the textfield for the email
     * @param email textfield
     */
    public void setEmail(TextField email) {
        this.email = email;
    }

    /**
     * Sets the textfield of the iban
     * @param iban textfield
     */
    public void setIban(TextField iban) {
        this.iban = iban;
    }

    /**
     * Sets the bic textfield
     * @param bic textfield
     */
    public void setBic(TextField bic) {
        this.bic = bic;
    }

    /**
     * Sets the websocket subscription
     * @param participantSubscription subscription
     */
    public void setParticipantSubscription(StompSession.Subscription participantSubscription) {
        this.participantSubscription = participantSubscription;
    }
}