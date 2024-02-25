package client.utils;

import client.scenes.StartScreenCtrl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LanguageCell extends javafx.scene.control.ListCell<String> {

    private static final Properties language = new Properties();

    /**
     * Updates item in the list to have the flag and the name of the language
     * @param item - ISO 639-1 code of the language
     * @param empty - whether the item is empty or not
     */
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            try {
                language.load(new FileInputStream(
                        String.format("client/src/main/resources/languages/%s.properties", item)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            setText(language.getProperty("name"));
            ImageView flag = new ImageView(new Image(String.format("flags/%s.png", item)));
            setGraphic(flag);
            try {
                StartScreenCtrl.setProperty("language", item);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
