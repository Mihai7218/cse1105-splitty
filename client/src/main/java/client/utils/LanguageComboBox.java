package client.utils;

import javafx.scene.control.ComboBox;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LanguageComboBox extends ComboBox<String> {

    /**
     * Constructor for a LanguageComboBox.
     */
    public LanguageComboBox() {
        super();
        List<String> languageCodes = getLanguageCodes();
        this.getItems().addAll(languageCodes);
        this.getItems().add("template");
    }

    /**
     * Method that gets the ISO 639-1 codes from the file names in resources/languages.
     * @return the list of language codes.
     */
    private List<String> getLanguageCodes() {
        System.out.println(getClass().getResource("/client"));
        File languagesFolder = new File(getClass().getResource(
                "/client/languages_en.properties").getFile()).getParentFile();
        File[] languageFiles = languagesFolder.listFiles();
        if (languageFiles == null)
            return new ArrayList<>();
        return Arrays.stream(languageFiles)
                .filter(File::isFile)
                .map(File::getName)
                .filter(name -> !name.equals("template.properties"))
                .map(filename -> filename.substring(10, 12)).sorted().toList();
    }

    /**
     * Sets the cell factory with the corresponding language manager.
     * @param languageManager - the language manager.
     */
    public void setCellFactory(LanguageManager languageManager) {
        this.setCellFactory(param -> new LanguageCell(languageManager));
        this.setButtonCell(new LanguageCell(languageManager));
    }
}
