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
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

import java.util.regex.Pattern;

public class ParticipantCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private TextField name;

    @FXML
    private TextField email;

    @FXML
    private TextField iban;

    @FXML
    private TextField bic;
    private final LanguageManager languageManager;



    /**
     * Constructs a new ParticipantCtrl object.
     * @param server ServerUtils object
     * @param mainCtrl MainCtrl object
     * @param languageManager LanguageManager object
     */
    @Inject
    public ParticipantCtrl(ServerUtils server, MainCtrl mainCtrl, LanguageManager languageManager) {
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.languageManager = languageManager;
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
        boolean bicPresent = bic.getText().isEmpty();
        boolean ibanPresent = iban.getText().isEmpty();
        boolean emailPresent = email.getText().isEmpty();
        String ibanRegex = "^[A-Z]{2}[0-9]{2}[A-Za-z0-9]{11,30}$";
        String bicRegex = "^[A-Za-z]{6}[0-9A-Za-z]{2}([0-9A-Za-z]{3})?$";
        String emailRegex = "^[\\w!#$%&’*+/=?{|}~^-]+(?:\\." +
                "[\\w!#$%&’*+/=?{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        //Alerts if the name is empty
        if (name.getText().isEmpty()){
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.contentTextProperty().bind(languageManager
                    .bind("addParticipant.emptyFields"));
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        //Alerts if the Email is not in the right form
        if(emailPresent == false && Pattern.compile(emailRegex)
                .matcher(email.getText()).matches() == false){
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.contentTextProperty().bind(languageManager
                    .bind("addParticipant.invalidEmail"));
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        //Alerts if there is only the bic or the iban but not both
        if(bicPresent != ibanPresent){
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.contentTextProperty().bind(languageManager.bind("addParticipant.invalidPayment"));
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        //Alerts if the Iban is not in the right form
        if(ibanPresent == false && Pattern.compile(bicRegex)
                .matcher(iban.getText()).matches() == false){
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.contentTextProperty().bind(languageManager
                    .bind("addParticipant.invalidIban"));
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        //Alerts if the Bic is not in the right form
        if(bicPresent == false && Pattern.compile(ibanRegex)
                .matcher(iban.getText()).matches() == false){
            var alert = new Alert(Alert.AlertType.WARNING);
            alert.contentTextProperty().bind(languageManager
                    .bind("addParticipant.invalidBic"));
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        try {
            server.addParticipant(mainCtrl.getEvent().getInviteCode(), getParticipant());
            mainCtrl.getEvent().getParticipantsList().add(getParticipant());
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        clearFields();
        mainCtrl.showOverview();
        mainCtrl.showParticipantConfirmation();
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
        if(name!=null){
            name.clear();
        }
        if(email!=null){
            email.clear();
        }
        if(iban!=null){
            iban.clear();
        }
        if(bic!=null){
            bic.clear();
        }
    }

    /**
     * Getter for the language manager observable map.
     * @return - the language manager observable map.
     */
    public ObservableMap<String, Object> getLanguageManager() {
        return languageManager.get();
    }

    /**
     * Setter for the language manager observable map.
     * @param languageManager - the language manager observable map.
     */
    public void setLanguageManager(ObservableMap<String, Object> languageManager) {
        this.languageManager.set(languageManager);
    }

    /**
     * Getter for the language manager property.
     * @return - the language manager property.
     */
    public LanguageManager languageManagerProperty() {
        return languageManager;
    }
}