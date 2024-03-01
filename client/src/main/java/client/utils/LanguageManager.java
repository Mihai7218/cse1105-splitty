package client.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;

import java.util.Locale;
import java.util.ResourceBundle;


public class LanguageManager extends SimpleMapProperty<String, Object> {

    Locale locale = Locale.getDefault();

    /**
     * Constructor for the LanguageManager.
     */
    public LanguageManager() {
        super(FXCollections.observableHashMap());
    }

    /**
     * Method that changes the currently selected language.
     * @param locale - locale of the new language.
     */
    public void changeLanguage(Locale locale) {
        this.locale = locale;
        refresh();
    }

    /**
     * Method that refreshes the MapProperty with the values
     * from the ResourceBundle of the new language.
     */
    public void refresh() {
        ResourceBundle rb = ResourceBundle.getBundle("client.languages", locale);
        var keys = rb.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = rb.getString(key);
            MapProperty<String, Object> map = this;
            String[] parts = key.split("\\.");
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i];
                if (i == parts.length - 1) {
                    map.put(part, value);
                } else {
                    if (!map.containsKey(part)) {
                        map.put(part, new SimpleMapProperty<>(FXCollections.observableHashMap()));
                    }
                    map = (MapProperty<String, Object>) map.get(part);
                }
            }
        }
    }

    /**
     * Method that binds each key to the value in this map.
     * @param key - the key to bind
     * @return - a StringBinding of the key to the value in this map.
     */
    public StringBinding bind(String key) {
        MapProperty<String, Object> map = this;
        String[] parts = key.split("\\.");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == parts.length - 1) {
                return Bindings.valueAt(map, part).asString();
            } else {
                if (!map.containsKey(part)) {
                    map.put(part, new SimpleMapProperty<>(FXCollections.observableHashMap()));
                }
                map = (MapProperty<String, Object>) map.get(part);
            }
        }
        return null;
    }
}
