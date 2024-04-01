package server.api;

import commons.Event;
import commons.Expense;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;


//TODO: debt and owed endpoints
@RestController
@RequestMapping("/api/events")

public class EventController {

    private final EventService eventService;

    private final GerneralServerUtil serverUtil;


    /**
     * constructor for the EventController
     * @param eventService the service with all the necessary functions for the api
     */
    public EventController(EventService eventService,
                           @Qualifier("serverUtilImpl") GerneralServerUtil serverUtil) {
        this.eventService = eventService;
        this.serverUtil = serverUtil;
    }

    /**
     * Get method to get a specific event from the database
     * @param inviteCode the invite code of that specific event
     * @return the requested event
     */
    @GetMapping(path = { "/{inviteCode}/updates" })
    public DeferredResult<ResponseEntity<Event>> getPolling(
            @PathVariable("inviteCode") long inviteCode) {

        return eventService.getPolling(inviteCode);
    }

    /**
     * Websocket implemmentation of the add
     * @param event new values of event
     */
    @MessageMapping("/events")
    @SendTo("/topic/events")
    public Event changeEvent(Event event) {
        change(event.getInviteCode(),event).getBody();
        return event;
    }

    /***
     * @param id the event of which we want to sum the total of expenses
     * @return the sum of all expenses
     */
    @GetMapping(path = { "/{id}/total" })
    public ResponseEntity<Double> getTotal(@PathVariable("id") long id) {
        return eventService.getTotal(id);
    }


    /**
     * Get method to get a specific event from the database
     * @param inviteCode the invite code of that specific event
     * @return the requested event
     */
    @GetMapping(path = { "/{inviteCode}" })
    public ResponseEntity<Event> get(@PathVariable("inviteCode") long inviteCode) {
        return eventService.getEvent(inviteCode);
    }

    /**
     * Get methode to get all the events on the server
     * @return returns a list of all events on the server
     */
    @GetMapping(path = { "/admin/{password}" })
    public ResponseEntity<List<Event>> get(@PathVariable("password") String password) {
        if (PasswordService.getPassword().equals(password)) {
            return eventService.getAllEvents();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * @param inviteCode the invite code of the event to check in
     * @param payeeId the id of the participant to check if they're
     *                the payee in the expenses
     * @return the list of expenses the participant was the payee in
     */
    @GetMapping(path = {"/{inviteCode}/payee/{payeeId}"})
    public ResponseEntity<List<Expense>> getInvolvingPayee(@PathVariable("inviteCode")
                                                               long inviteCode,
                                             @PathVariable("payeeId") long payeeId){
        return eventService.getExpensesInvolvingPayee(inviteCode, payeeId);
    }

    /**
     * @param inviteCode the invite code of the event to check in
     * @param partId the id of the participant to check if they're
     *               involved in the expenses
     * @return the list of expenses the participant was involved in
     */
    @GetMapping(path = {"/{inviteCode}/participant/{partId}"})
    public ResponseEntity<List<Expense>> getInvolvingPart(@PathVariable("inviteCode")
                                                              long inviteCode,
                                             @PathVariable("partId") long partId){
        return eventService.getExpensesInvolvingParticipant(inviteCode, partId);
    }

    /**
     * A post method to add and event to the repository
     * @param event an event in the requestBody to add to the repository
     * @return the event if succesfully made
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Event> add(@RequestBody Event event) {
        return eventService.addEvent(event);
    }

    /**
     * Change an existing event
     * @param inviteCode the invite code of the event to change
     * @param event the data that the event should have
     * @return the new changed event
     */
    @PutMapping(path = {"/{inviteCode}" })
    public ResponseEntity<Event> change(@PathVariable("inviteCode") long inviteCode,
                                        @RequestBody Event event) {
        return eventService.changeEvent(inviteCode,event,serverUtil);
    }

    /**
     * Delete an existing event
     * @param inviteCode the invitecode of the event
     * @return the event that was deleted
     */
    @DeleteMapping(path = {"/{inviteCode}" })
    public ResponseEntity<Event> delete(@PathVariable("inviteCode") long inviteCode) {
        return eventService.deleteEvent(inviteCode);
    }

    /**
     * Post method to allow an admin to upload new events
     * @param password string password
     * @param event the list of events to be added
     * @return the list of events if succesfully added
     */
    @PostMapping(path = {"/admin/{password}"})
    public ResponseEntity<Event> addJsonImport(@PathVariable("password") String password,
                                               @RequestBody  Event event){
        if (PasswordService.getPassword().equals(password)) {
            return eventService.addCreatedEvent(event);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint to access and calculated the share for a certain participant
     * @param eventId id of the event the participant is in
     * @param participantId id of the participant
     * @return amount that is owed if calculable
     */
    @GetMapping(path = {"/{invitecode}/share/{participantId}"})
    public ResponseEntity<Double> getShare(@PathVariable("invitecode") Long eventId,
                                           @PathVariable("participantId") Long participantId){
        return eventService.getShare(eventId, participantId);
    }

    /**
     * @param eventId the event to get the debt from
     * @param participantId the participant to calculate the debt of
     * @return the response entity containing the debt as a double
     */
    //TODO
    @GetMapping(path = {"/{invitecode}/debt/{participantId}"})
    public ResponseEntity<Double> getDebt(@PathVariable("invitecode") Long eventId,
                                           @PathVariable("participantId") Long participantId){
        return eventService.getDebt(eventId, participantId);
    }

    /**
     * @param eventId the event to calculate how much the part. is owed
     * @param participantId the participant to calculate the owed of
     * @return the response entity containing how much the part. is owed
     */
    @GetMapping(path = {"/{invitecode}/owed/{participantId}"})
    public ResponseEntity<Double> getOwed(@PathVariable("invitecode") Long eventId,
                                          @PathVariable("participantId") Long participantId){
        return eventService.getOwed(eventId, participantId);
    }

}
