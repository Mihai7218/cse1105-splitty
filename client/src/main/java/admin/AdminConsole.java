package admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Event;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminConsole {

    private List<Event> events;

    private ServerUtils serverUtils;

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
    public ServerUtils getServerUtils() {
        return serverUtils;
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
    public static void showOptions(Scanner userInput, AdminConsole adminConsole) {
        System.out.println("What would you like to do?");
        System.out.println("\t 1 - Show all events");
        System.out.println("\t 2 - Dump database to json file");
        System.out.println("\t 3 - Import events from json file");
        System.out.println("\t 4 - exit");
        switch (userInput.nextInt()) {
            case 1:
                adminConsole.updateEvents();
                System.out.println(adminConsole.printEvents());
                showOptions(userInput, adminConsole);
                break;
            case 2:
                adminConsole.updateEvents();
                adminConsole.getDump(userInput);
                showOptions(userInput, adminConsole);
                break;
            case 3:
                List<Event> importedEvents = adminConsole.readFromFile(new Scanner(System.in));
                if(importedEvents == null || importedEvents.isEmpty()) break;
                for(Event e: importedEvents) adminConsole.setNewEvents(e);
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
    public void getDump(Scanner userInput) {
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
     * Print all the events that are currently on the server
     */
    public String printEvents() {
        String res = "";

        for (Event event : events) {
            res += event.toString();
        }
        return res;
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
     * Method to accept a filepath from a user to import JSON data
     */
    public List<Event> readFromFile(Scanner inputScanner) {
        System.out.println("Enter the filepath containing" +
                " the JSON for the event you would like to add: ");
        Scanner fileScan = inputScanner;
        try {
            File file = new File(fileScan.nextLine());
            Scanner textScan = new Scanner(file);
            return importWithJson(textScan);
        }catch (FileNotFoundException f){
            System.out.println("Unable to locate the requested file. ");
            return null;
        }catch (NoSuchElementException e){
            System.out.println("Unable to locate the requested file (empty filepath). ");
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
     * @param event the event to be added/validated
     */
    public void setNewEvents(Event event){
        serverUtils.setEvents(event, password);
    }

    /**
     * Prints out all events in alphabetical order
     */
    public void orderByTitleAsc(){
        Collections.sort(events, Comparator.comparing(Event::getTitle));
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
        for (Event event : events) {
            System.out.println(event.toString());
        }
    }

    /**
     * Prints out all events by creation date, newest to oldest
     */
    public void orderByCreationRecent(){
        events.sort(Comparator.comparing(Event::getCreationDate));
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
        for (Event event : events) {
            System.out.println(event.toString());
        }
    }

    /**
     * prints out all events by last activity date, newest to oldest
     */
    public void orderByActivityRecent(){
        events.sort(Comparator.comparing(Event::getLastActivity));
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
        adminConsole.serverUtils.setServer(userInput.next());
        //adminConsole.serverUtils.setServer("http://localhost:8080");
        signIn(userInput, adminConsole);
    }

    /**
     * exit the console Application
     */
    public static void exit() {
        System.exit(0);
    }

}
