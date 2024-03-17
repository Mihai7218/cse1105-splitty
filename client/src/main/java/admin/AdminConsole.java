package admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import commons.Event;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;
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
                adminConsole.getDump(userInput);
                showOptions(userInput, adminConsole);
                break;
            case 3:
                Event event = adminConsole.importWithJson(new Scanner(System.in));
                if(event == null) break;
                adminConsole.setNewEvents(event);
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
        updateEvents();
        JSONArray jsonArray = new JSONArray(events);
        return jsonArray.toString();
    }


    /**
     * Genereate a json String with the event List
     * @return json string with event list
     */
    public List<String> eventToJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<String> eventsAsJson = new ArrayList<>();

//        StringBuilder output = new StringBuilder();
//        output.append("[");
//        int counter = 0;
        for(Event e : events){
            String s = mapper.writeValueAsString(e);
            eventsAsJson.add(s);
//            Event temp = e;
//            JSONObject jo = new JSONObject(temp);
//            output.append(jo.toString());
//            if(!(counter == events.size() - 1))
//                output.append(",");
//            counter++;
        }
        return eventsAsJson;
//        output.append("]");
//        return output.toString();
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
     * Imports JSON Event data
     * @param userInput scanner containing the JSON event data
     */
    public Event importWithJson(Scanner userInput){
        System.out.println("Enter your json text for the event you would like to add: ");
        String json;
        try{
            json = userInput.nextLine();
        } catch ( NoSuchElementException e ){
            System.out.println("Unable to import empty event. ");
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new StdDateFormat());
        if(json.isEmpty()){
            System.out.println("Unable to import empty event. ");
            return null;
        }
        try {
            Event event = objectMapper.readValue(json, Event.class);
            return event;
        } catch (JsonProcessingException | JSONException e) {
            System.out.println("Unable to import event. ");
            return null;
        }
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
