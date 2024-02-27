package client.utils;

import javafx.scene.control.ComboBox;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LanguageComboBox extends ComboBox<String> {

    private List<String> languageCodes;

    /**
     * Constructor for a LanguageComboBox.
     */
    public LanguageComboBox() {
        super();
        languageCodes = getLanguageCodes();
        this.getItems().addAll(languageCodes);
        this.setCellFactory(param -> new LanguageCell());
        this.setButtonCell(new LanguageCell());
    }

    /**
     * Method that gets the ISO 639-1 codes from the file names in resources/languages.
     * @return the list of language codes.
     */
    private static List<String> getLanguageCodes() {
        File languagesFolder = new File(String.valueOf(Path.of("client",
                "src", "main", "resources", "languages")));
        return Arrays.stream(Objects
                        .requireNonNull(languagesFolder.listFiles()))
                .map(File::getName)
                .filter(name -> !name.equals("template.properties"))
                .map(filename -> filename.substring(0, 2)).sorted().toList();
    }
}
