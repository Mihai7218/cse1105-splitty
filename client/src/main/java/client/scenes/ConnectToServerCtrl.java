package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

//These imports are for testing if the connection works
import java.net.HttpURLConnection;
import java.net.URL;
public class ConnectToServerCtrl {

    @FXML
    private TextField serverAddressField;

    @FXML
    private Button connectButton;

    private MainCtrl mainCtrl;
    protected final ConfigInterface configInterface;
    private final ServerUtils server;
    private LanguageManager languageManager;

    /**
     * @param mainCtrl
     * @param configInterface
     * @param languageManager
     */
    @Inject
    public ConnectToServerCtrl (MainCtrl mainCtrl, ConfigInterface configInterface,
                                ServerUtils server, LanguageManager languageManager){
        this.mainCtrl = mainCtrl;
        this.configInterface = configInterface;
        this.server = server;
        this.languageManager = languageManager;

    }


    /**
     * The handler when the connect button is pressed.
     * The server address is retrieved from the responsible text field and
     * the config file is updated accordingly.
     */
    @FXML
    private void connectButtonHandler() {
        /*
            If the user leaves the server field empty, we break out of the method so the user
            can enter a new address
         */
        String serverAddress = serverAddressField.getText();
        if (emptyServer(serverAddress)) {
            showAlert("Server Address Required", "Please enter a server address.");
            return;
        }

        /*
            Test whether the server is available for connection
            Alert the user if not and break out of the method to enter a new server
         */
        if (!isServerAvailable(serverAddress)) {
            showAlert("Connection Error", "Could not establish a connection with the server.");
            return;
        }
        // Update config if everything is fine and redirect to start screen
        updateConfigFile(serverAddress);
        server.connectToServer();
        mainCtrl.showStartMenu();
    }

    /**
     * Writes to the config file with the server address
     * @param serverAddress the server address to be replaced in the config file
     */
    private void updateConfigFile(String serverAddress) {
        configInterface.setProperty("server", serverAddress);
    }

    /**
     * @param title descriptive title of the alert
     * @param message a short message with details of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * @param serverAddress the address of the server, to test whether available or not
     * @return whether the responseCode was OK
     * In the case of an IOException, it also returns that the server is not available
     * This may be a TODO to handle some more descriptive handling?
     */
    private boolean isServerAvailable(String serverAddress) {
        try {
            URL url = new URL(serverAddress);
            HttpURLConnection testConnection = (HttpURLConnection) url.openConnection();
            testConnection.setRequestMethod("HEAD");
            int responseCode = testConnection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * @param serverAddr the server address String
     * @return whether the field was left empty by the user
     */
    private boolean emptyServer(String serverAddr) {
        return serverAddr.isEmpty() || serverAddr.equals("");
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     *  - if ENTER is pressed, then it edits the participant with the current values.
     *  - if ESCAPE is pressed, then it cancels and returns to the overview.
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                connectButtonHandler();
                break;
            default:
                break;
        }
    }
}
