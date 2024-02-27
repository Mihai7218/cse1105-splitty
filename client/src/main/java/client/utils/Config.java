package client.utils;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

public class Config implements ConfigInterface {

    private static final Properties prop = new Properties();

    private final OutputStream outputStream;

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
        this(new FileInputStream(file), new FileOutputStream(file));
    }

    /**
     * Constructor for the config.
     * Creates the config file if it does not exist yet.
     */
    public Config(InputStream inputStream, OutputStream outputStream) {
        this.outputStream = outputStream;
        try {
            prop.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
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
        prop.store(this.outputStream, "Splitty Configuration File");
    }
}
