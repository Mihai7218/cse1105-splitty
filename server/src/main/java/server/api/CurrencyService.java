package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.NoSuchElementException;
import java.util.Scanner;

@Service
public class CurrencyService {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<Double> getCurrency(String date, String from, String to) {
        if (from.length() != 3 && to.length() != 3)
            return ResponseEntity.badRequest().build();
        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().build();
        }
        File file = new File(String.valueOf(Path.of("server","src", "main", "resources", "rates", date, from, to + ".txt")));
        System.out.println(file);
        try {
            if (file.equals(new File("null"))) throw new FileNotFoundException();
            return ResponseEntity.ok(new Scanner(file).nextDouble());
        } catch (FileNotFoundException | NoSuchElementException e) {
            try {
                InputStream in = new URI(String.format("http://data.fixer.io/api/%s?access" +
                                "_key=95d3ec8d229b0de3605a0ec223f24c41&base=%s&symbols=%s",
                        date, from, to)).toURL().openStream();
                var obj = objectMapper.readTree(in);
                double rate = obj.get("rates").get(to).asDouble();
                new File(String.valueOf(Path.of("server", "src", "main", "resources", "rates", date, from))).mkdirs();
                PrintWriter pw = new PrintWriter(file);
                pw.print(rate);
                pw.flush();
                return ResponseEntity.ok(rate);
            } catch (IOException | URISyntaxException ex) {
                return ResponseEntity.internalServerError().build();
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
