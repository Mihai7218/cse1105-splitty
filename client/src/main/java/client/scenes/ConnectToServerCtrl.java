package client.scenes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//These imports are for testing if the connection works
import java.net.HttpURLConnection;
import java.net.URL;
public class ConnectToServerCtrl {

    @FXML
    private TextField serverAddressField;

    @FXML
    private Button connectButton;

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
        if (emptyServer(serverAddressField)) {
            showAlert("Server Address Required", "Please enter a server address.");
            return;
        }

        String serverAddress = serverAddressField.getText();

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/client/scenes/StartScreen.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) connectButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes to the config file with the server address
     * @param serverAddress the server address to be replaced in the config file
     */
    private void updateConfigFile(String serverAddress) {
        String filePath = "client/config.properties"; // Path relative to the project root
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("#Splitty Config File");
            writer.println("#" + java.time.LocalDateTime.now());
            writer.println("server=" + serverAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * @param textField the server address textField
     * @return whether the field was left empty by the user
     */
    private boolean emptyServer(TextField textField) {
        return textField.getText().trim().isEmpty();
    }
}
