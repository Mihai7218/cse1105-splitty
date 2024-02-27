package client.scenes;

import client.utils.ConfigInterface;

import java.util.HashMap;

public class TestConfig implements ConfigInterface {
    public HashMap<String, String> config = new HashMap<>();

    @Override
    public String getProperty(String key) {
        return config.get(key);
    }

    @Override
    public void setProperty(String key, String value) {
        config.put(key, value);
    }
}
