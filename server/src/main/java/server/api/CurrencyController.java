package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rates")
public class CurrencyController {

    public final CurrencyService currencyService;

    /**
     * Constructor for the currency controller.
     * @param currencyService - the currency service.
     */
    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    /**
     * Method that gets the rate from the cached file or the currency API
     * @param date - date of the payment
     * @param from - currency of the payment
     * @param to - desired currency
     * @return - 200 OK with the rate, 404 Not Found if currency codes are not found,
     *           400 Bad Request if the date, from or to are not valid dates/currency codes
     */
    @GetMapping("/{date}/{from}/{to}")
    public ResponseEntity<Double> getCurrency(@PathVariable("date") String date,
                                              @PathVariable("from") String from,
                                              @PathVariable("to") String to) {
        return currencyService.getCurrency(date, from, to);
    }
}
