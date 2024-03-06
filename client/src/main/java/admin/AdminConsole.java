package admin;

import commons.Event;

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
     * a
     * @param password a
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Main methode with the console application
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
     * adwa
     */
    private static void showOptions(Scanner userInput, AdminConsole adminConsole) {
        System.out.println("What would you like to do?");
        System.out.println("\t 1 - Show all events");
        System.out.println("\t 2 - exit");
        switch (userInput.nextInt()) {
            case 1:
                adminConsole.printEvents();
                showOptions(userInput, adminConsole);
                break;
            default:
                exit();
        }
    }

    /**
     *
     */
    private void printEvents() {
        events = serverUtils.getEvents(password);
        for (Event event : events) {
            System.out.println(event.toString());
        }
    }

    /**
     * a
     * @param userInput a
     * @param adminConsole a
     */
    private static void signIn(Scanner userInput, AdminConsole adminConsole) {
        System.out.println("what is the password?");
        //String password = userInput.next();
        String password = "703788";
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
                    signIn(userInput,adminConsole);
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
     * @param userInput
     * @return a
     */
    private static void setServerAddress(Scanner userInput, AdminConsole adminConsole) {
        System.out.println("What is the address of the server?");
        //adminConsole.serverUtils.setServer(userInput.next());
        adminConsole.serverUtils.setServer("http://localhost:8080");
        signIn(userInput, adminConsole);
    }

    /**
     *
     */
    private static void exit() {
        System.exit(0);
    }

}
