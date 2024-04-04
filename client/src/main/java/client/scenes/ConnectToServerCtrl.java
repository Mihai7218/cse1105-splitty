package client.scenes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ConnectToServerCtrl {

    @FXML
    private TextField serverAddressField;

    @FXML
    private Button connectButton;

    /**
     *
     */
    @FXML
    private void connectButtonHandler() {
        String serverAddress = serverAddressField.getText();
        updateConfigFile(serverAddress);

        // Redirect to the next scene (StartScreen.fxml)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/scenes/StartScreen.fxml"));
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
     * @param serverAddress
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
}
