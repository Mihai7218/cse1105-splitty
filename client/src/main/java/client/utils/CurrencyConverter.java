package client.utils;

import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class CurrencyConverter {

    private final ServerUtils serverUtils;

    private final List<String> currencies = List.of("EUR", "USD", "CHF");

    /**
     * Constructor for the currency converter.
     * @param serverUtils - the server utils.
     */
    @Inject
    public CurrencyConverter(ServerUtils serverUtils) {
        this.serverUtils = serverUtils;
    }

    /**
     * Method that converts
     * @param date
     * @param from
     * @param to
     * @param sum
     * @return
     */
    public double convert(Date date, String from, String to, double sum) {
        if (from.equals(to))
            return sum;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(date);
        try {
            return sum * serverUtils.getRate(dateString, from, to);
        } catch (WebApplicationException e) {
            switch (e.getResponse().getStatus()) {
                case 400 -> {
                    System.err.println("Date or currencies are invalid.");
                }
                case 404 -> {
                    System.err.println("Rate not found. Trying again with yesterday's rate.");
                    try {
                        Date yesterday = java.sql.Date.valueOf(LocalDate.now().minusDays(1));
                        return sum * serverUtils.getRate(dateFormat.format(yesterday), from, to);
                    }
                    catch (WebApplicationException ex) {
                        switch (ex.getResponse().getStatus()) {
                            case 400 -> System.err.println("Date or currencies are invalid.");
                            case 404 -> System.err.println("Rate not found.");
                            case 500 -> System.err.println("Internal server error.");
                        }
                        System.err.printf("date: %s\nfrom: %s\nto: %s\n", date, from, to);
                    }
                }
                case 500 -> System.err.println("Internal server error.");
            }
            System.err.printf("date: %s\nfrom: %s\nto: %s\n", date, from, to);
            throw new CouldNotConvertException(e.getResponse().getStatus());
        }
    }

    /**
     * Getter for the list of available currencies.
     * @return - the list of currencies.
     */
    public List<String> getCurrencies() {
        return currencies;
    }
}
