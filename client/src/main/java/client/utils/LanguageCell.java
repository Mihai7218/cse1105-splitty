package client.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class LanguageCell extends javafx.scene.control.ListCell<String> {

    private static final Properties language = new Properties();

    private final Config config;

    /**
     * Constructor for the LanguageCell.
     * @param config - config object
     */
    public LanguageCell(Config config) {
        this.config = config;
    }

    /**
     * Updates item in the list to have the flag and the name of the language
     * @param languageCode - ISO 639-1 code of the language
     * @param empty - whether the item is empty or not
     */
    @Override
    protected void updateItem(String languageCode, boolean empty) {
        super.updateItem(languageCode, empty);
        if (empty || languageCode == null) {
            setText(null);
            setGraphic(null);
        } else {
            try {
                language.load(new FileInputStream(
                        String.valueOf(Path.of("client", "src", "main",
                                "resources", "languages", languageCode+".properties"))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            setText(language.getProperty("name"));
            Image image = null;
            String flagPath = String.valueOf(Path.of("flags", languageCode+".png"));
            try {
                image = new Image(flagPath);
            }
            catch (IllegalArgumentException e) {
                try {
                    downloadFlag(languageCode, flagPath);
                    image = new Image(flagPath);
                } catch (IOException | URISyntaxException ignored1) {
                    e.printStackTrace();
                }
            }
            ImageView flag = new ImageView(image);
            setGraphic(flag);
        }
    }

    /**
     * Downloads missing flag file from flagsapi.com and saves it in the resources/flags folder.
     * @param language - ISO 639-1 code of the language
     * @param flagPath - path of the flag file
     * @throws IOException - in case the file is inaccessible
     * @throws URISyntaxException - in case the URL of the Flags API is invalid
     */
    private static void downloadFlag(String language, String flagPath)
            throws IOException, URISyntaxException {
        InputStream in = new URI(String.format("https://flagsapi.com/%s/flat/24.png",
                language.toUpperCase())).toURL().openStream();
        Files.copy(in, Paths.get("client", "src", "main",
                "resources", flagPath), StandardCopyOption.REPLACE_EXISTING);
    }
}
