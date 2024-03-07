package client.utils;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class LanguageCellTest {

    LanguageCell languageCell;

    @Start
    void setUp(Stage stage) {
        languageCell = new LanguageCell();
    }
    @Test
    void updateItemNonEmptyLanguageFlagNotFound() {
        languageCell.updateItem("test", false);
        assertEquals("Language not found", languageCell.getText());
        assertNull(languageCell.getGraphic());
    }
    @Test
    void updateItemNonEmptyLanguageFlagFound() {
        languageCell.updateItem("en", false);
        assertEquals("Language not found", languageCell.getText());
        assertNotNull(languageCell.getGraphic());
    }
    @Test
    void updateItemEmptyLanguage() {
        languageCell.updateItem(null, false);
        assertNull(languageCell.getText());
    }
}