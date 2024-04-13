package server.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CurrencyControllerTest {

    @Test
    void getCurrency() {
        CurrencyService currencyService = mock(CurrencyService.class);
        CurrencyController sut = new CurrencyController(currencyService);

        sut.getCurrency("2024-04-01", "EUR", "USD");

        verify(currencyService).getCurrency("2024-04-01", "EUR", "USD");
    }
}