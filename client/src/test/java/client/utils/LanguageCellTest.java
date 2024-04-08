package client.utils;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(ApplicationExtension.class)
class LanguageCellTest {

    LanguageCell languageCell;

    @Start
    void setUp(Stage stage) {
        languageCell = new LanguageCell(mock(LanguageManager.class));
    }

    /**
     * Tests that the language cell is updated with the text "Language not found"
     * and that there is no flag as graphic when the language code is not found.
     */
    @Test
    void updateItemNonEmptyLanguageFlagNotFound() {
        languageCell.updateItem("test", false);
        assertEquals("Language not found", languageCell.getText());
        assertNull(languageCell.getGraphic());
    }

    /**
     * Tests that the graphic is updated when the flag is found.
     */
    @Test
    void updateItemNonEmptyLanguageFlagFound() {
        languageCell.updateItem("en", false);
        assertEquals("English", languageCell.getText());
        assertNotNull(languageCell.getGraphic());
    }

    /**
     * Tests that the text of the cell is null when the language code passed is also null.
     */
    @Test
    void updateItemEmptyLanguage() {
        languageCell.updateItem(null, false);
        assertNull(languageCell.getText());
    }
}