package admin;

import commons.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminConsole {

    private List<Event> events;

    /**
     * Costructor for the AdminConsole class
     */
    public AdminConsole() {
        this.events = new ArrayList<>();
    }

    /**
     * Main methode with the console application
     * @param args starting arguments
     */
    public static void main(String[] args) {
        String password = setup();
    }

    /**
     * setup meny to ask for password
     * @return user provided password
     */
    private static String setup() {
        System.out.println("Welcome to the admin console");
        System.out.println("what is the password?");
        Scanner userInput = new Scanner(System.in);
        return userInput.nextLine();
    }
}
