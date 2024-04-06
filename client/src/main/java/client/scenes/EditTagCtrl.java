package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Tag;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Locale;
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
    public Button addExpense;

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
        if (addExpense != null)
            addExpense.setGraphic(new ImageView(new Image("icons/savewhite.png")));
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
    }

    /**
     * Function for submitting the changes for the tag
     */
    public void submitTagChanges() {
    }

    /**
     * loads the fields with data from the tag to be modified
     */
    public void loadFields() {
        name.setText(editTag.getName());
        colorPicker.setValue(Color.web(editTag.getColor()));
    }
}
