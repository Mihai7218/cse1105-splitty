package client.utils;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public class Config implements ConfigInterface {

    private final Properties prop = new Properties();

    private OutputStream outputStream = null;

    /**
     * Constructor for the config with a default file path.
     */
    public Config() {
        this(new File(String.valueOf(Path.of("client", "config.properties"))));
    }

    /**
     * Constructor for the config with specified file
     * @param file - file that needs to be written to
     */
    public Config(File file) {
        this(ignoreFileNotFoundException(file), null);
        try {
            this.outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            file.getParentFile().mkdirs();
            try {
                this.outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Constructor for the config.
     * Creates the config file if it does not exist yet.
     */
    public Config(InputStream inputStream, OutputStream outputStream) {
        try {
            prop.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (outputStream != null) this.outputStream = outputStream;
    }

    /**
     * Method that returns null if the FileInputStream throws a FileNotFoundException.
     * @param file - file that needs to be read
     * @return - the FileInputStream of the file or null if the file is not found
     */
    private static InputStream ignoreFileNotFoundException(File file) {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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
    public void setProperty(String key, String value) {
        prop.setProperty(key, value);
    }

    /**
     * Saves the config to the output stream.
     * @throws IOException  when outputStream cannot write the config.
     */
    public void saveProperties() throws IOException {
        prop.store(outputStream, "Splitty Config File");
    }

    /**
     * Removes a property from the config.
     * @param key - the key of that property.
     */
    public void removeProperty(String key) {
        prop.remove(key);
    }
}
