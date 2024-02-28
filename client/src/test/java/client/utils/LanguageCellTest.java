package client.utils;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LanguageCellTest {

    LanguageCell lc;

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {}
    }
    @BeforeEach
    void start() {
        lc = new LanguageCell();
    }
    @Test
    void testEmptyStringUpdate() {
        lc.updateItem(null, false);
        assertNull(lc.getText());
        assertNull(lc.getGraphic());
    }
    @Test
    void testEmptyTrue() {
        lc.updateItem("ignored", true);
        assertNull(lc.getText());
        assertNull(lc.getGraphic());
    }
    @Test
    void testUpdate() {
        lc.updateItem("test", false);
        assertEquals("Language not found", lc.getText());
        assertNull(lc.getGraphic());
    }
}