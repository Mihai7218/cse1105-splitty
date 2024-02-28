package client.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(ApplicationExtension.class)
class LanguageCellTest {

    LanguageCell lc;
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