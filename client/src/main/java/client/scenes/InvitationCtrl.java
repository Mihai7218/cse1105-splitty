package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class InvitationCtrl implements Initializable{
    @FXML
    public Text code;
    @FXML
    private Text test;
    @FXML
    private TextArea mailSpace;
    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final Alert alert;
    /**
     *
     * @param mainCtrl
     * @param config
     * @param languageManager
     * @param serverUtils
     * @param alert
     */
    @Inject
    public InvitationCtrl(MainCtrl mainCtrl,
                          ConfigInterface config,
                          LanguageManager languageManager,
                          ServerUtils serverUtils,
                          Alert alert) {
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageManager = languageManager;
        this.serverUtils = serverUtils;
        this.alert = alert;
    }

    /**
     *
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * loads the title and code of the event
     */
    public void refresh() {
        test.setText(mainCtrl.getEvent().getTitle());
        code.setText(String.valueOf(mainCtrl.getEvent().getInviteCode()));
    }

    /**
     * sends invites to the mail in the text area
     * has to be implemented with mail functionality
     */
    public void sendInvites(){
        mainCtrl.showOverview();
    }

    /**
     * clears the text area and goes back to overview
     */
    public void goBack(){
        mainCtrl.showOverview();
        mailSpace.setText("");
    }

    /**
     * When the shortcut is used it goes back to the startmenu.
     */
    public void startMenu() {
        mainCtrl.showStartMenu();
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     *  - if ENTER is pressed, then send the invites.
     *  - if ESCAPE is pressed, then it cancels and returns to the overview.
     *  - if Ctrl + m is pressed, then it returns to the startscreen.
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                sendInvites();
                break;
            case ESCAPE:
                goBack();
                break;
            case M:
                if(e.isControlDown()){
                    startMenu();
                    break;
                }
            default:
                break;
        }
    }
}
