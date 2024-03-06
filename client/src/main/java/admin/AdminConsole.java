package admin;

import commons.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminConsole {

    private List<Event> events;

    private ServerUtils serverUtils;

    /**
     * Costructor for the AdminConsole class
     */
    public AdminConsole(String serverAddress) {
        this.events = new ArrayList<>();
        serverUtils = new ServerUtils(serverAddress);
    }

    /**
     * Main methode with the console application
     * @param args starting arguments
     */
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        System.out.println("Welcome to the admin console");
        System.out.println("What is the address of the server?");
        AdminConsole adminConsole = new AdminConsole(userInput.nextLine());
        //AdminConsole adminConsole = new AdminConsole("http://localhost:8080");
        signIn(userInput, adminConsole);



    }

    /**
     * a
     * @param userInput a
     * @param adminConsole a
     */
    private static void signIn(Scanner userInput, AdminConsole adminConsole) {
        String password = passwordCheck(userInput);
        try {
            adminConsole.events = adminConsole.serverUtils.getEvents(password);
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
                    setServerAddress(userInput);
                    break;
                case 3:
                    exit();
                    break;
            }
        }
    }

    /**
     *
     * @param userInput
     */
    private static void setServerAddress(Scanner userInput) {
    }

    /**
     *
     */
    private static void exit() {
        System.exit(0);
    }

    /**
     * setup meny to ask for password
     * @return user provided password
     */
    private static String passwordCheck(Scanner userInput) {
        System.out.println("what is the password?");
        return userInput.nextLine();
    }
}
