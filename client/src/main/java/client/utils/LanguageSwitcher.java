package client.utils;

import client.scenes.MainCtrl;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.stage.Modality;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

public interface LanguageSwitcher {

    /**
     * Changes language
     */
    default void changeLanguage() {
        String language = "";
        if (getLanguages() != null) language = getLanguages().getValue();
        if (language.equals("template")) {
            getLanguages().setValue(getConfig().getProperty("language"));
            try {
                File savedByUser = getMainCtrl().pickLocation(
                        "languages_<ISO639-1 Code>.properties");
                var template = getClass().getResourceAsStream(
                        String.format("/client/languages_%s.properties",getLanguages().getValue()));
                assert template != null;
                Files.copy(template, savedByUser.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.contentTextProperty().bind(
                        languageManagerProperty().bind("template.instructions"));
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.show();
            } catch (IOException | AssertionError e) {
                e.printStackTrace();
            }
        }
        else {
            getConfig().setProperty("language", language);
            if (getMainCtrl() != null && getMainCtrl().getOverviewCtrl() != null
                    && getMainCtrl().getStartScreenCtrl() != null) {
                getMainCtrl().getStartScreenCtrl().
                        updateLanguageComboBox(getLanguages().getValue());
                getMainCtrl().getOverviewCtrl().
                        updateLanguageComboBox(getLanguages().getValue());
            }
            this.refreshLanguage();
        }
    }

    /**
     * Method that refreshes the language.
     */
    default void refreshLanguage() {
        String language = getConfig().getProperty("language");
        if (language == null) {
            language = "en";
        }
        updateLanguageComboBox(language);
        languageManagerProperty().changeLanguage(Locale.of(language));
    }

    /**
     * Getter for the main controller
     * @return MainCtrl object
     */
    MainCtrl getMainCtrl();

    /**
     * Getter for the languages combo box.
     * @return LanguageComboBox object
     */
    LanguageComboBox getLanguages();

    /**
     * Getter for the config.
     * @return - the config
     */
    ConfigInterface getConfig();

    /**
     * Getter for the language manager
     * @return - the language manager
     */
    LanguageManager languageManagerProperty();

    /**
     * Method that updates the language combo box.
     * @param language - the new language value.
     */
    void updateLanguageComboBox(String language);
}
