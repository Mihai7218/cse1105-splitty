package admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Event;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminConsole {

    private List<Event> events;

    private ServerUtils utils;

    private String password;

    /**
     * setter for the event variable
     * @param events the list of events to set the events to set the variable to
     */
    public void setEvents(List<Event> events) {
        this.events = events;
    }

    /**
     * return the events variable
     * @return return the events that are currently in the system
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Return the ServerUtils that are stored in the adminconsole
     * @return the stored ServerUtils
     */
    public ServerUtils getUtils() {
        return utils;
    }

    /**
     * Return the password that is saved on the adminConsole
     * @return the saved password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Costructor for the AdminConsole class
     */
    public AdminConsole() {
        this.events = new ArrayList<>();
        utils = new ServerUtils("");
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
     * Prints the options of the adminConsole
     */
    public static void printOptions(){
        System.out.println("What would you like to do?");
        System.out.println("\t 1 - Show events in the database");
        System.out.println("\t 2 - Dump database to json file");
        System.out.println("\t 3 - Import events from json file");
        System.out.println("\t 4 - Delete an event from the database"); //TODO
        System.out.println("\t 5 - exit");
    }
    /**
     * show menu with options for user
     *
     * @param userInput    scanner to read user input
     * @param adminConsole the currently running admin console
     */
    public static void showOptions(Scanner userInput, AdminConsole adminConsole) {
        boolean running = true;
        while (running){
            printOptions();
            int choseOption = 5;
            try{
                choseOption = Integer.parseInt(userInput.next());
            } catch (Exception e) {
                System.out.println("This is not a number. Please enter a number");
                showOptions(userInput,adminConsole);
            }
            switch (choseOption) {
                case 1:
                    printerMenu(userInput, adminConsole);
                    break;
                case 2:
                    adminConsole.updateEvents();
                    adminConsole.getDump(userInput);
                    showOptions(userInput, adminConsole);
                    break;
                case 3:
                    List<Event> importedEvents = adminConsole.readFromFile(new Scanner(System.in));
                    if(importedEvents == null || importedEvents.isEmpty()) break;
                    int succes = 0;
                    int existed = 0;
                    int error = 0;
                    for(Event e: importedEvents) {
                        Response response = adminConsole.setNewEvents(e);
                        if (response.getStatus() == 200) {
                            succes++;
                        } else if (response.getStatus() == 404) {
                            existed++;
                        } else {
                            error++;
                        }
                    }
                    System.out.println(succes + " events where successfully imported");
                    System.out.println(existed + " events already existed");
                    System.out.println(error + " events encountered an error");
                    break;
                case 4:
                    adminConsole.deleteEventMenu(userInput, adminConsole);
                    break;
                default:
                    running = false;
                    exit();
            }
        }

    }

    /**
     * @param userInput scanner to read user selected option from
     * @param adminConsole the current admin console
     */
    public static void printerMenu(Scanner userInput, AdminConsole adminConsole){
        System.out.println("What would you like to do?");
        printerMenuPrintText();
        int choseOption = 0;
        while (choseOption == 0) {
            try{
                choseOption = Integer.parseInt(userInput.next());
            } catch (Exception exception) {
                System.out.println("This is not a number. Please enter a number");
                printerMenuPrintText();
            }
        }
        switch (choseOption){
            case 1:
                adminConsole.updateEvents();
                adminConsole.printEvents();
                showOptions(userInput, adminConsole);
                break;
            case 2:
                adminConsole.updateEvents();
                adminConsole.orderByTitleAsc();
                showOptions(userInput, adminConsole);
                break;
            case 3:
                adminConsole.updateEvents();
                adminConsole.orderByTitleDesc();
                showOptions(userInput, adminConsole);
                break;
            case 4:
                adminConsole.updateEvents();
                adminConsole.orderByCreationRecent();
                showOptions(userInput, adminConsole);
                break;
            case 5:
                adminConsole.updateEvents();
                adminConsole.orderByCreationOld();
                showOptions(userInput, adminConsole);
                break;
            case 6:
                adminConsole.updateEvents();
                adminConsole.orderByActivityRecent();
                showOptions(userInput, adminConsole);
                break;
            case 7:
                adminConsole.updateEvents();
                adminConsole.orderByActivityOld();
                showOptions(userInput, adminConsole);
                break;
            case 8:
                showOptions(userInput, adminConsole);
            case 9:
                exit();
        }


    }

    /**
     * Just prints the text for the printerMenu
     * Otherwise the method is too long
     */
    public static void printerMenuPrintText(){
        System.out.println("""
                        \t 1 - Show all events in the database
                        \t 2 - Show all events ordered by title (ASC)
                        \t 3 - Show all events ordered by title (DESC)
                        \t 4 - Show all events ordered by creation date (NEWEST)
                        \t 5 - Show all events ordered by creation date (OLDEST)
                        \t 6 - Show all events ordered by activity (RECENT)
                        \t 7 - Show all events ordered by activity (LAST)
                        \t 8 - Return to main menu
                        \t 9 - exit""");
    }

    /**
     * @param userInput the scanner for the userInput
     * @param adminConsole the currently running adminConsole
     */
    public void deleteEventMenu(Scanner userInput, AdminConsole adminConsole){
        System.out.println("Please enter the inviteCode of the " +
                "Event you would like to delete from the database (or type 'cancel' to cancel):");
        String nextInput = userInput.next();
        int invCode;
        if(nextInput.equals("cancel")){
            return;
        }else{
            try {
                invCode = Integer.parseInt(nextInput);
            }catch (Exception e){
                System.out.println("Invalid Event ID.");
                return;
            }
        }
        boolean deletion = confirmationMenu(userInput, invCode);
        if (deletion){
            Response event = delete(adminConsole, invCode);
            if (event.getStatus() == 200) {
                System.out.println("Event " +
                        invCode +
                        " deleted successfully");
            } else if (event.getStatus() == 404) {
                System.out.println("Event was not found on the server");
            } else {
                System.out.println("Something went wrong on the server");
            }

        } else {
            System.out.println("Event remains in the database");
        }
    }

    /**
     * Split for testing purposes
     * @param invCode the invite code of the event to delete
     * @return  the deleted event
     */
    public Response delete(AdminConsole adminConsole, int invCode){
        return utils.deleteEvent(invCode);
    }


    /**
     * @param userInput the userInput scanner
     * @param invCode the invite code of the event to be deleted from the system
     * @return whether the deletion was confirmed
     */
    public boolean confirmationMenu(Scanner userInput, int invCode) {
        System.out.println("Please confirm you want to delete event " + invCode + "\n" +
                "Y/N");
        String choice = userInput.next();
        switch (choice) {
            case "Y":
                return true;
            case "N":
                return false;
            default:
                return false;
        }
    }
    /**
     * Methode to update/download the event list from the server
     */
    public void updateEvents() {
        events = utils.getEvents(password);
    }

    /**
     * Method to dump the database to a json as backup
     * @param userInput input from the user
     */
    public void getDump(Scanner userInput) {
        System.out.println("Where do you want to save the dump? " +
                "Give the folder or type 'cancel' to cancel: ");
        String path = userInput.next();

        if(path.equals("cancel")){
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String formattedDateTime = now.format(timeFormat);
        path = Path.of(path, "Splitty-Dump-" + formattedDateTime + ".json").toString();
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
        JSONArray jsonArray = new JSONArray(events);
        return jsonArray.toString();
    }


    /**
     * Genereate a json String with the event List
     * @return json string with event list
     */
    public List<String> exportListOfEvents() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<String> eventsAsJson = new ArrayList<>();

        for(Event e : events){
            String s = mapper.writeValueAsString(e);
            eventsAsJson.add(s);
        }
        return eventsAsJson;
    }

    /**
     * Prints all events on the server
     */
    public void printEvents() {
        System.out.println("InviteCode\t\tTitle\t\tNr. Participants" +
                "\t\tNr. Expenses\t\tLast Activity\n");
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
    public static void signIn(Scanner userInput, AdminConsole adminConsole) {
        System.out.println("what is the password?");
        String password = userInput.next();
        //password = "902087";
        try {
            adminConsole.events = adminConsole.utils.getEvents(password);
            adminConsole.setPassword(password);
        } catch (Exception e) {
            System.out.println("Password incorrect");
            System.out.println("1. for retry password");
            System.out.println("2. change server address");
            System.out.println("3. to quit");
            int choseOption = 0;
            while (choseOption == 0) {
                try{
                    choseOption = Integer.parseInt(userInput.next());
                } catch (Exception exception) {
                    System.out.println("This is not a number. Please enter a number");
                    System.out.println("Password incorrect");
                    System.out.println("1. for retry password");
                    System.out.println("2. change server address");
                    System.out.println("3. to quit");
                }
            }
            switch (choseOption) {
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
     * Method to accept a filepath from a user to import JSON data
     */
    public List<Event> readFromFile(Scanner inputScanner) {
        System.out.println("Enter the filepath containing" +
                " the JSON for the event you would like to add (Or type 'cancel' to escape): ");
        Scanner fileScan = inputScanner;
        String line;
        try {
            line = fileScan.nextLine();
        }catch(NoSuchElementException n){
            System.out.println("Unable to locate the requested file (empty filepath). ");
            return null;
        }
        if(line.equals("cancel")){
            return null;
        }
        try {
            File file = new File(line);
            Scanner textScan = new Scanner(file);
            return importWithJson(textScan);
        }catch (FileNotFoundException f){
            System.out.println("Unable to locate the requested file. ");
            return null;
        }
    }

    /**
     * Imports JSON Event data
     * @param textInput scanner containing the JSON event data
     */
    public List<Event> importWithJson(Scanner textInput){
        List<Event> events = new ArrayList<>();
        String completeJson = "";
        while(textInput.hasNext()) {
            completeJson += textInput.nextLine();
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(completeJson);
        } catch (Exception e) {
            System.out.println("Unable to import JSON events. ");
            return new ArrayList<>();
        }

        for (Object object : jsonArray) {
            try {
                JSONObject tmpEvent = (JSONObject) object;
                ObjectMapper objectMapper = new ObjectMapper();
                SimpleDateFormat creationDateFormat = new
                        SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                objectMapper.setDateFormat(creationDateFormat);
                try {
                    Event event = objectMapper.readValue(tmpEvent.toString(), Event.class);
                    events.add(event);
                } catch (JsonProcessingException | JSONException e) {
                    System.out.println("Unable to import event. {Error with JSON formatting.} ");
                }
            } catch (Exception e) {
                System.out.println("Error importing event. ");
            }
        }
        return events;
    }

    /**
     * Method to add imported events to the server
     *
     * @param event the event to be added/validated
     * @return
     */
    public Response setNewEvents(Event event){
        return utils.setEvents(event, password);
    }

    /**
     * Prints out all events in alphabetical order
     */
    public void orderByTitleAsc(){
        Collections.sort(events, Comparator.comparing(Event::getTitle));
        System.out.println("InviteCode\t\tTitle\t\tNr. Participants" +
                "\t\tNr. Expenses\t\tLast Activity\n");
        for (Event event : events) {
            System.out.println(event.toString());
        }
    }

    /**
     * prints out all events in reverse alphabetical order
     */
    public void orderByTitleDesc(){
        Collections.sort(events, Comparator.comparing(Event::getTitle));
        Collections.reverse(events);
        System.out.println("InviteCode\t\tTitle\t\tNr. Participants" +
                "\t\tNr. Expenses\t\tLast Activity\n");
        for (Event event : events) {
            System.out.println(event.toString());
        }
    }

    /**
     * Prints out all events by creation date, newest to oldest
     */
    public void orderByCreationRecent(){
        events.sort(Comparator.comparing(Event::getCreationDate));
        System.out.println("InviteCode\t\tTitle\t\tNr. Participants" +
                "\t\tNr. Expenses\t\tLast Activity\n");
        for (Event event : events) {
            System.out.println(event.toString());
        }
    }

    /**
     * Prints out all events by creation date, oldest to newest
     */
    public void orderByCreationOld(){
        events.sort(Comparator.comparing(Event::getCreationDate));
        Collections.reverse(events);
        System.out.println("InviteCode\t\tTitle\t\tNr. Participants" +
                "\t\tNr. Expenses\t\tLast Activity\n");
        for (Event event : events) {
            System.out.println(event.toString());
        }
    }

    /**
     * prints out all events by last activity date, newest to oldest
     */
    public void orderByActivityRecent(){
        events.sort(Comparator.comparing(Event::getLastActivity));
        System.out.println("InviteCode\t\tTitle\t\tNr. Participants" +
                "\t\tNr. Expenses\t\tLast Activity\n");
        for (Event event : events) {
            System.out.println(event.toString());
        }
    }

    /**
     * Prints out all events by activity date oldest to newest
     */
    public void orderByActivityOld(){
        events.sort(Comparator.comparing(Event::getLastActivity));
        Collections.reverse(events);
        System.out.println("InviteCode\t\tTitle\t\tNr. Participants" +
                "\t\tNr. Expenses\t\tLast Activity\n");
        for (Event event : events) {
            System.out.println(event.toString());
        }
    }

    /**
     * Set the serverAddress of the adminConsole
     *
     * @param userInput    scanner to read user input
     * @param adminConsole the current running admin console
     */
    public static void setServerAddress(Scanner userInput, AdminConsole adminConsole) {
        System.out.println("What is the address of the server?");
        adminConsole.utils.setServer(userInput.next());
        //adminConsole.utils.setServer("http://localhost:8080");
        signIn(userInput, adminConsole);
    }

    /**
     * exit the console Application
     */
    public static void exit() {
        System.exit(0);
    }

    /**
     * Mainly for testing purposes
     * @param util the util to change to
     */
    public void setUtils(ServerUtils util) {
        this.utils = util;
    }
}
