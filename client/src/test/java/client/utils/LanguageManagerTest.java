package client.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageManagerTest {

    LanguageManager sut = new LanguageManager();

    @AfterEach
    void resetLocale() {
        sut.changeLanguage(Locale.ENGLISH);
    }

    @Test
    void changeLanguage() {
        sut.changeLanguage(Locale.CANADA_FRENCH);
        assertEquals(Locale.CANADA_FRENCH, Locale.getDefault());
    }

    @Test
    void refresh() {
        sut.refresh();
        LanguageManager comparison = new LanguageManager();
        comparison.put("hello", "world");
        comparison.put("value","test");
        comparison.put("why","not");
        assertEquals(comparison, sut);
    }

    @Test
    void bind() {
        sut.put("hello", "world");
        sut.put("value","test");
        sut.put("why","not");
        StringBinding sb = sut.bind("hello");
        StringBinding cp = Bindings.valueAt(sut, "hello").asString();
        assertEquals(cp.get(), sb.get());
    }
}