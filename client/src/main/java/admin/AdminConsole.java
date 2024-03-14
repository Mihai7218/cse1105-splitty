package admin;

import commons.Event;
import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminConsole {

    private List<Event> events;

    private ServerUtils serverUtils;

    private String password;

    /**
     * Costructor for the AdminConsole class
     */
    public AdminConsole() {
        this.events = new ArrayList<>();
        serverUtils = new ServerUtils("");
        password = "";
    }

    /**
     * a setter to change the password value
     *
     * @param password value to set the password to
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Main methode with the console application
     *
     * @param args starting arguments
     */
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        System.out.println("Welcome to the admin console");
        AdminConsole adminConsole = new AdminConsole();
        setServerAddress(userInput, adminConsole);
        //AdminConsole adminConsole = new AdminConsole("http://localhost:8080");
        System.out.println("Succes :D");
        showOptions(userInput, adminConsole);


    }

    /**
     * show menu with options for user
     *
     * @param userInput    scanner to read user input
     * @param adminConsole the currently running admin console
     */
    private static void showOptions(Scanner userInput, AdminConsole adminConsole) {
        System.out.println("What would you like to do?");
        System.out.println("\t 1 - Show all events");
        System.out.println("\t 2 - Dump database to json file");
        System.out.println("\t 3 - exit");
        switch (userInput.nextInt()) {
            case 1:
                adminConsole.printEvents();
                showOptions(userInput, adminConsole);
                break;
            case 2:
                adminConsole.getDump(userInput);
                showOptions(userInput, adminConsole);
                break;
            default:
                exit();
        }
    }

    /**
     * Methode to update/download the event list from the server
     */
    public void updateEvents() {
        events = serverUtils.getEvents(password);
    }

    /**
     * Method to dump the database to a json as backup
     * @param userInput input from the user
     */
    private void getDump(Scanner userInput) {
        System.out.println("Where do you want to save the dump? Give the folder");
        String path = userInput.next();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String formattedDateTime = now.format(timeFormat);
        path += "\\Splitty-Dump-" + formattedDateTime + ".json";
        FileWriter ewa = null;
        try {
            ewa = new FileWriter(path);
            ewa.append(eventsToJson());
            ewa.flush();
            System.out.println("Fill has been succesfully saved to " + path);
        } catch (IOException e) {
            System.out.println("Something went wrong");
            throw new RuntimeException(e);
        }
    }

    /**
     * Genereate a json String with the event List
     * @return json string with event list
     */
    public String eventsToJson() {
        updateEvents();
        JSONArray jsonArray = new JSONArray(events);
        return jsonArray.toString();
    }

    /**
     * Print all the events that are currently on the server
     */
    private void printEvents() {
        updateEvents();
        for (Event event : events) {
            System.out.println(event.toString());
        }
    }

    /**
     * Sign in to the server
     *
     * @param userInput    scanner to read user input
     * @param adminConsole the currently running admin console
     */
    private static void signIn(Scanner userInput, AdminConsole adminConsole) {
        System.out.println("what is the password?");
        String password = userInput.next();
        //password = "902087";
        try {
            adminConsole.events = adminConsole.serverUtils.getEvents(password);
            adminConsole.setPassword(password);
        } catch (Exception e) {
            System.out.println("Password incorrect");
            System.out.println("1. for retry password");
            System.out.println("2. change server address");
            System.out.println("3. to quit");
            switch (userInput.nextInt()) {
                case 1:
                    System.out.println("retry password");
                    signIn(userInput, adminConsole);
                    break;
                case 2:
                    setServerAddress(userInput, adminConsole);
                    break;
                default:
                    exit();
                    break;
            }
        }
    }

    /**
     * Set the serverAddress of the adminConsole
     *
     * @param userInput    scanner to read user input
     * @param adminConsole the current running admin console
     */
    private static void setServerAddress(Scanner userInput, AdminConsole adminConsole) {
        System.out.println("What is the address of the server?");
        adminConsole.serverUtils.setServer(userInput.next());
        //adminConsole.serverUtils.setServer("http://localhost:8080");
        signIn(userInput, adminConsole);
    }

    /**
     * exit the console Application
     */
    private static void exit() {
        System.exit(0);
    }

}
