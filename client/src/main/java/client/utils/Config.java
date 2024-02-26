package client.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class Config {

    private static final Properties prop = new Properties();

    public Config() {
        try {
            File config = new File("client/src/main/resources/config.properties");
            prop.load(new FileInputStream(config));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter for a property.
     * @param key - key of the property
     * @return - value of the property
     */
    public String getProperty(String key) {
        return prop.getProperty(key);
    }

    /**
     * Setter for a property.
     * @param key - key of the property
     * @param value - value of the property
     * @throws IOException - if the file is not found
     */
    public void setProperty(String key, String value) throws IOException {
        prop.setProperty(key, value);
        prop.store(new PrintWriter("client/src/main/resources/config.properties"),
                "Splitty Configuration File");
    }
}
