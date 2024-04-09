package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditTagCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;

    protected final Alert alert;

    @FXML
    public Button cancel;
    @FXML
    public TextField name;
    @FXML
    public ColorPicker colorPicker;
    @FXML
    public Button cancelButton;
    @FXML
    public Button changeTag;

    public Tag editTag;


    /**
     * Constructor for the EditTagCtrl
     *
     * @param mainCtrl - main controller
     * @param config   - config
     */
    @Inject
    public EditTagCtrl(MainCtrl mainCtrl,
                       ConfigInterface config,
                       LanguageManager languageManager,
                       ServerUtils serverUtils,
                       Alert alert) {
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageManager = languageManager;
        this.serverUtils = serverUtils;
        this.alert = alert;
        editTag = null;
    }

    /**
     * Initializes the start screen view
     *
     * @param url            - URL of the FXML file
     * @param resourceBundle - resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String language = config.getProperty("language");
        if (language == null) {
            language = "en";
        }

        this.refreshLanguage();
        if (cancelButton != null)
            cancelButton.setGraphic(new ImageView(new Image("icons/cancelwhite.png")));
        if (changeTag != null)
            changeTag.setGraphic(new ImageView(new Image("icons/savewhite.png")));
    }

    /**
     * Method to clear input fields
     */
    public void clearFields() {
        name.clear();
        colorPicker.setValue(Color.WHITE);

    }

    /**
     * Method that refreshes the language.
     */
    private void refreshLanguage() {
        String language = config.getProperty("language");
        if (language == null) {
            language = "en";
        }
        languageManager.changeLanguage(Locale.of(language));
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
     * Refreshes the list of Tags
     */
    public void refresh() {
        clearFields();
        loadFields();
    }

    /**
     * set the taf to edit in the edit screen
     * @param item the tag to edit
     */
    public void setTag(Tag item) {
        editTag = item;
    }

    /**
     * Abort back to the manage Tags sceen
     */
    public void abort() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "");
        confirmation.contentTextProperty().bind(languageManager.bind("editTag.abortAlert"));
        confirmation.titleProperty().bind(languageManager.bind("commons.warning"));
        confirmation.headerTextProperty().bind(languageManager.bind("commons.warning"));
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            clearFields();
            mainCtrl.showManageTags();
        }
    }

    /**
     * Function for submitting the changes for the tag
     */
    public void submitTagChanges() {
        String nameString = name.getText();
        name.setStyle("-fx-border-color: none;");
        if(nameString.isEmpty()) {
            throwAlert("editTag.incompleteHeader", "editTag.incompleteBody");
            name.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
            return;
        }
        editTag.setName(nameString);
        editTag.setColor("#" +
                colorPicker.getValue().toString().substring(2,8));
        try {
            serverUtils.updateTag(mainCtrl.getEvent().getInviteCode(), editTag);
        } catch (WebApplicationException e) {
            switch (e.getResponse().getStatus()) {
                case 400 -> {
                    throwAlert("editTag.badReqHeader",
                            "editTag.badReqBody");
                }
                case 404 -> {
                    throwAlert("editTag.notFoundHeader",
                            "editTag.notFoundBody");
                }
            }
        }
        mainCtrl.showManageTags();
        mainCtrl.showEditConfirmation();
        clearFields();
    }

    /**
     * Method that throws an alert.
     * @param header - property associated with the header.
     * @param body - property associated with the body.
     */
    protected void throwAlert(String header, String body) {
        alert.titleProperty().bind(languageManager.bind("commons.warning"));
        alert.headerTextProperty().bind(languageManager.bind(header));
        alert.contentTextProperty().bind(languageManager.bind(body));
        alert.showAndWait();
    }

    /**
     * loads the fields with data from the tag to be modified
     */
    public void loadFields() {
        name.setText(editTag.getName());
        colorPicker.setValue(Color.web(editTag.getColor()));
    }

    /**
     * When the shortcut is used it goes back to the startmenu.
     */
    public void startMenu() {
        mainCtrl.showStartMenu();
    }

    /**
     * Back to the overview of the expenses of the Event
     */
    public void backToOverview() {
        mainCtrl.showOverview();
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     *  - if ENTER is pressed, then it edits the tag.
     *  - if ESCAPE is pressed, then it cancels and returns to the tags scene.
     *  - if Ctrl + m is pressed, then it returns to the startscreen.
     *  - if Ctrl + o is pressed, then it returns to the overview.
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                submitTagChanges();
                break;
            case ESCAPE:
                abort();
                break;
            case M:
                if(e.isControlDown()){
                    startMenu();
                    break;
                }
            case O:
                if(e.isControlDown()){
                    backToOverview();
                    break;
                }
            default:
                break;
        }
    }
}
