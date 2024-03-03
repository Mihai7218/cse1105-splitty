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
    public Config() throws FileNotFoundException {
        this(new File(String.valueOf(Path.of("client", "config.properties"))));
    }

    /**
     * Constructor for the config with specified file
     * @param file - file that needs to be written to
     * @throws FileNotFoundException - in case the file is not found
     */
    public Config(File file) throws FileNotFoundException {
        this(new FileInputStream(file), null);
        this.outputStream = new FileOutputStream(file);
    }

    /**
     * Constructor for the config.
     * Creates the config file if it does not exist yet.
     */
    public Config(InputStream inputStream, OutputStream outputStream) {
        try {
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (outputStream != null) this.outputStream = outputStream;
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
}
