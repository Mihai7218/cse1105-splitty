package client.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigTest {

    private ByteArrayInputStream bais = new ByteArrayInputStream("language=fr".getBytes());
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private Config config;
    @BeforeEach
    void setup() {
        config = new Config(bais, baos);
    }

    /**
     * Tests the getter.
     */
    @Test
    void getProperty() {
        assertEquals("fr", config.getProperty("language"));
    }

    /**
     * Tests the setter.
     */
    @Test
    void setProperty() {
        config.setProperty("language", "en");
        assertEquals("en", config.getProperty("language"));
    }

    /**
     * Tests the save properties method.
     * @throws IOException thrown by the saveProperties() method.
     */
    @Test
    void saveProperties() throws IOException {
        config.saveProperties();
        assertEquals("language=fr", baos.toString().lines().toList().get(2));
    }
}