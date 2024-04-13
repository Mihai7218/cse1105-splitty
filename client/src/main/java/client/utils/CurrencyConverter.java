package client.utils;

import com.google.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

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
     * Method that converts between currencies
     * @param date date of the conversion
     * @param from currency to convert from
     * @param to currency to convert to
     * @param sum value converted
     * @return converted amount
     */
    public double convert(Date date, String from, String to, double sum) {
        if (from.equals(to))
            return sum;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(date);
        try {
            if (date.compareTo(new Date()) > 0)
                throw new WebApplicationException(404);
            return sum * getRate(dateString, from, to);
        } catch (WebApplicationException e) {
            switch (e.getResponse().getStatus()) {
                case 400 -> {
                    System.err.println("Date or currencies are invalid.");
                }
                case 404 -> {
                    System.err.println("Rate not found. Trying again with yesterday's rate.");
                    try {
                        Date yesterday = java.sql.Date.valueOf(LocalDate.now().minusDays(1));
                        return sum * getRate(dateFormat.format(yesterday), from, to);
                    } catch (WebApplicationException ex) {
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
     * Method that gets the rate from the cached file or from the server.
     * @param date - the date as a string
     * @param from - the currency to convert from
     * @param to - the currency to convert to
     * @return - the rate for the specified date and currency pair.
     */
    private double getRate(String date, String from, String to) {
        File file = new File(String.valueOf(Path.of("client", "src",
                "main", "resources", "rates", date, from, to + ".txt")));
        try {
            if (file.equals(new File("null"))) throw new FileNotFoundException();
            return new Scanner(file).nextDouble();
        } catch (FileNotFoundException | NoSuchElementException e) {
            double rate = serverUtils.getRate(date, from, to);
            new File(String.valueOf(Path.of("client", "src",
                    "main", "resources", "rates", date, from))).mkdirs();
            PrintWriter pw;
            try {
                pw = new PrintWriter(file);
            } catch (FileNotFoundException ex) {
                System.err.println("Couldn't print the rate to a file!");
                return rate;
            }
            pw.print(rate);
            pw.flush();
            return rate;
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
