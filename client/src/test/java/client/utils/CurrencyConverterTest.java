package client.utils;

import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrencyConverterTest {

    CurrencyConverter sut;
    ServerUtils serverUtils;

    @BeforeEach
    void setup() {
        serverUtils = mock(ServerUtils.class);
        sut = new CurrencyConverter(serverUtils);
    }

    @Test
    void convertSameCurrency() {
        double result = sut.convert(new Date(), "EUR", "EUR", 42);
        assertEquals(42, result);
    }

    @Test
    void convertSuccessCached() throws FileNotFoundException {
        File file = new File(String.valueOf(Path.of("client", "src",
                "main", "resources", "rates", "2024-04-01", "EUR", "USD" + ".txt")));
        file.getParentFile().mkdirs();
        PrintWriter pw = new PrintWriter(file);
        pw.print(2.0);
        pw.flush();
        double result = sut.convert(java.sql.Date.valueOf("2024-04-01"), "EUR", "USD", 42);
        assertEquals(84.0, result);
    }

    @Test
    void convertSuccessFutureDate() throws FileNotFoundException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(java.sql.Date.valueOf(LocalDate.now().plusYears(1)));
        String yesterday = dateFormat.format(java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
        File yesterdayFile = new File(String.valueOf(Path.of("client", "src",
                "main", "resources", "rates", yesterday, "EUR", "USD" + ".txt")));
        File dateFile = new File(String.valueOf(Path.of("client", "src",
                "main", "resources", "rates", date, "EUR", "USD" + ".txt")));
        yesterdayFile.getParentFile().mkdirs();
        PrintWriter pw = new PrintWriter(yesterdayFile);
        pw.print(2.0);
        pw.flush();
        double result = sut.convert(java.sql.Date.valueOf(date), "EUR", "USD", 42);
        assertEquals(84.0, result);
        assertFalse(dateFile.exists());
    }

    @Test
    void getCurrencySuccessNotCached() {
        File file = new File(String.valueOf(Path.of("client", "src",
                "main", "resources", "rates", "2024-04-01", "EUR", "USD" + ".txt")));
        file.delete();
        when(serverUtils.getRate("2024-04-01", "EUR", "USD")).thenReturn(2.0);

        var response = sut.convert(java.sql.Date.valueOf("2024-04-01"), "EUR", "USD", 42);

        assertEquals(84.0, response);
        assertTrue(file.exists());
    }

    @Test
    void convert400FutureDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = dateFormat.format(java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
        File yesterdayFile = new File(String.valueOf(Path.of("client", "src",
                "main", "resources", "rates", yesterday, "EUR", "USD" + ".txt")));
        yesterdayFile.delete();
        String date = dateFormat.format(java.sql.Date.valueOf(LocalDate.now().plusYears(1)));
        when(serverUtils.getRate(any(), any(), any())).then(mock -> {
            throw new WebApplicationException(400);
        });
        assertThrows(CouldNotConvertException.class, () -> {
            sut.convert(java.sql.Date.valueOf(date), "EUR", "USD", 1);
        });
    }

    @Test
    void convert404FutureDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = dateFormat.format(java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
        File yesterdayFile = new File(String.valueOf(Path.of("client", "src",
                "main", "resources", "rates", yesterday, "EUR", "USD" + ".txt")));
        yesterdayFile.delete();
        String date = dateFormat.format(java.sql.Date.valueOf(LocalDate.now().plusYears(1)));
        when(serverUtils.getRate(any(), any(), any())).then(mock -> {
            throw new WebApplicationException(404);
        });
        assertThrows(CouldNotConvertException.class, () -> {
            sut.convert(java.sql.Date.valueOf(date), "EUR", "USD", 1);
        });
    }

    @Test
    void convert404FutureDateCorrectCode() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = dateFormat.format(java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
        File yesterdayFile = new File(String.valueOf(Path.of("client", "src",
                "main", "resources", "rates", yesterday, "EUR", "USD" + ".txt")));
        yesterdayFile.delete();
        String date = dateFormat.format(java.sql.Date.valueOf(LocalDate.now().plusYears(1)));
        when(serverUtils.getRate(any(), any(), any())).then(mock -> {
            throw new WebApplicationException(404);
        });
        int status = 0;
        try {
            sut.convert(java.sql.Date.valueOf(date), "EUR", "USD", 1);
        }
        catch (CouldNotConvertException e) {
            status = e.getStatus();
        }
        assertEquals(404, status);
    }

    @Test
    void convert500FutureDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = dateFormat.format(java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
        File yesterdayFile = new File(String.valueOf(Path.of("client", "src",
                "main", "resources", "rates", yesterday, "EUR", "USD" + ".txt")));
        yesterdayFile.delete();
        String date = dateFormat.format(java.sql.Date.valueOf(LocalDate.now().plusYears(1)));
        when(serverUtils.getRate(any(), any(), any())).then(mock -> {
            throw new WebApplicationException(500);
        });
        assertThrows(CouldNotConvertException.class, () -> {
            sut.convert(java.sql.Date.valueOf(date), "EUR", "USD", 1);
        });
    }

    @Test
    void convert500() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = dateFormat.format(java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
        File yesterdayFile = new File(String.valueOf(Path.of("client", "src",
                "main", "resources", "rates", yesterday, "EUR", "USD" + ".txt")));
        yesterdayFile.delete();
        when(serverUtils.getRate(any(), any(), any())).then(mock -> {
            throw new WebApplicationException(500);
        });
        assertThrows(CouldNotConvertException.class, () -> {
            sut.convert(java.sql.Date.valueOf(yesterday), "EUR", "USD", 1);
        });
    }

    @Test
    void convert400() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterday = dateFormat.format(java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
        File yesterdayFile = new File(String.valueOf(Path.of("client", "src",
                "main", "resources", "rates", yesterday, "EUR", "USD" + ".txt")));
        yesterdayFile.delete();
        when(serverUtils.getRate(any(), any(), any())).then(mock -> {
            throw new WebApplicationException(400);
        });
        assertThrows(CouldNotConvertException.class, () -> {
            sut.convert(java.sql.Date.valueOf(yesterday), "EUR", "USD", 1);
        });
    }

    @Test
    void getCurrencies() {
        assertTrue(sut.getCurrencies().containsAll(List.of("EUR", "USD", "CHF")));
    }
}