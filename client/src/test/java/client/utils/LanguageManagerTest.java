package client.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class LanguageManagerTest {

    ConfigInterface config = mock(Config.class);
    LanguageManager sut = new LanguageManager(config);

    @AfterEach
    void resetLocale() {
        sut.changeLanguage(Locale.ENGLISH);
    }

    /**
     * Tests that the locale is changed.
     */
    @Test
    void changeLanguage() {
        sut.changeLanguage(Locale.CANADA_FRENCH);
        assertEquals(Locale.CANADA_FRENCH, Locale.getDefault());
    }

    /**
     * Tests that the config is read properly.
     */
    @Test
    void refresh() {
        sut.refresh();
        LanguageManager comparison = new LanguageManager(config);
        comparison.put("hello", "world");
        comparison.put("big", new SimpleMapProperty<>(FXCollections.observableHashMap()));
        ((Map<String, Object>) comparison.get("big")).put("small", "test");
        comparison.put("why","not");
        assertEquals(comparison, sut);
    }

    /**
     * Tests that a string is bound when the method is called and that its value is correct.
     */
    @Test
    void bind() {
        sut.put("hello", "world");
        sut.put("why","not");
        StringBinding sb = sut.bind("big.small");
        StringBinding cp = Bindings.valueAt((ObservableMap<String, Object>) sut.get("big"), "small").asString();
        assertEquals(cp.get(), sb.get());
    }
}