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

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/{date}/{from}/{to}")
    public ResponseEntity<Double> getCurrency(@PathVariable("date") String date,
                                              @PathVariable("from") String from,
                                              @PathVariable("to") String to) {
        return currencyService.getCurrency(date, from, to);
    }
}
