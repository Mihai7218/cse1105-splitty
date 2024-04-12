package server.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrencyServiceTest {

    CurrencyService sut = new CurrencyService();
    @Test
    void getCurrencyFromNot3() {
        var response = sut.getCurrency("2024-04-01", "A", "USD");

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
    @Test
    void getCurrencyFromNull() {
        var response = sut.getCurrency("2024-04-01", null, "USD");

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
    @Test
    void getCurrencyToNot3() {
        var response = sut.getCurrency("2024-04-01", "EUR", "B");

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
    @Test
    void getCurrencyToNull() {
        var response = sut.getCurrency("2024-04-01", "USD", null);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
    @Test
    void getCurrencyWrongDateFormat() {
        var response = sut.getCurrency("20240401", "USD", "EUR");

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
    @Test
    void getCurrencyNullDate() {
        var response = sut.getCurrency(null, "USD", "EUR");

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
    @Test
    void getCurrencySuccessCached() {
        var response = sut.getCurrency("2024-04-01", "EUR", "EUR");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1.0, response.getBody());
    }
    @Test
    void getCurrencySuccessNotCached() {
        File file = new File(String.valueOf(Path.of("server", "src",
                "main", "resources", "rates", "2024-04-01", "EUR", "EUR" + ".txt")));
        file.delete();

        var response = sut.getCurrency("2024-04-01", "EUR", "EUR");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1.0, response.getBody());
        assertTrue(file.exists());
    }
    @Test
    void getCurrencyNotFound() {
        var response = sut.getCurrency("2024-04-01", "AAA", "AAA");

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }
    @Test
    void getCurrency500() {
        var response = sut.getCurrency("2024-04-01", "/\\\\", "AAA");

        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
    }
}