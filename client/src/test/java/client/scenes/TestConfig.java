package client.scenes;

import client.utils.ConfigInterface;

import java.util.HashMap;

public class TestConfig implements ConfigInterface {
    public HashMap<String, String> config = new HashMap<>();

    /**
     * Getter for a property.
     * @param key - key of the property
     * @return - value of the property
     */
    @Override
    public String getProperty(String key) {
        return config.get(key);
    }

    /**
     * Setter for a property.
     * @param key   - key of the property
     * @param value - value of the property
     */
    @Override
    public void setProperty(String key, String value) {
        config.put(key, value);
    }
}
