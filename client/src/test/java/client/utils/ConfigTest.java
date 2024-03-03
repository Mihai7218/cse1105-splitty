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
    @Test
    void getProperty() {
        assertEquals("fr", config.getProperty("language"));
    }

    @Test
    void setProperty() {
        config.setProperty("language", "en");
        assertEquals("en", config.getProperty("language"));
    }

    @Test
    void saveProperties() throws IOException {
        config.saveProperties();
        assertEquals("language=fr", baos.toString().lines().toList().get(2));
    }
}