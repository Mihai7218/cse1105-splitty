package client.scenes;

import client.utils.ConfigInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TestConfig implements ConfigInterface {

    Properties properties = new Properties();
    List<String> calls = new ArrayList<>();

    /**
     *
     * @param key - key of the property
     * @return
     */
    @Override
    public String getProperty(String key) {
        calls.add("get");
        return properties.getProperty(key);
    }

    /**
     *
     * @param key   - key of the property
     * @param value - value of the property
     */
    @Override
    public void setProperty(String key, String value) {
        calls.add("set");
        properties.setProperty(key, value);
    }
}
