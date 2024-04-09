package client.utils;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ApplicationExtension.class)
class LanguageComboBoxTest {

    LanguageComboBox sut;

    @Start
    void setUp(Stage stage) {
        sut = new LanguageComboBox();
    }

    /**
     * Tests that the language combo box is initialized as an empty list
     * when the language files are not found.
     */
    @Test
    void testLanguageCodesNullList() {
        assertEquals(List.of("en", "template"), sut.getItems().stream().sorted().toList());
    }

}