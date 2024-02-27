package client.utils;

import com.google.inject.ImplementedBy;

import java.io.IOException;

@ImplementedBy(Config.class)
public interface ConfigInterface {

    /**
     * Getter for a property.
     *
     * @param key - key of the property
     * @return - value of the property
     */
    String getProperty(String key);

    /**
     * Setter for a property.
     *
     * @param key   - key of the property
     * @param value - value of the property
     * @throws IOException - if the file is not found
     */
    void setProperty(String key, String value) throws IOException;
}
