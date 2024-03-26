package server.api;

import commons.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.EventRepository;
import server.database.TagRepository;

import java.sql.Timestamp;
import java.util.*;

import static org.springframework.http.HttpStatus.*;


@Service
public class EventService {
    private final EventRepository eventRepository;
    private final TagRepository tagRepository;

    /**
     * Constructor for de EventService
     * @param eventRepository the event repository
     * @param tagRepository the tag repository
     */
    @Autowired
    public EventService(EventRepository eventRepository, TagRepository tagRepository) {
        this.eventRepository = eventRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * endpoint for longPolling
     *
     * @param inviteCode the event to longPoll
     * @return the Long Polled event
     */
    public DeferredResult<ResponseEntity<Event>> getPolling(long inviteCode) {

        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<Event>>(5000L);

        try {
            Thread.sleep(1000);
            Event tmp = eventRepository.findById(inviteCode).get();
            res.setResult(ResponseEntity.ok(tmp));

        } catch (Exception e) {
            res.setErrorResult("Something went wrong");
        }
        res.onTimeout(() -> res.setErrorResult("The server did not respond in time"));
        return res;
    }

    /**
     * Get method to get a specific event from the database
     * @param inviteCode the invite code of that specific event
     * @return the requested event
     */
    public ResponseEntity<Event> getEvent(long inviteCode) {
        if(inviteCode < 0){
            return ResponseEntity.badRequest().build();
        }else if (!eventRepository.existsById(inviteCode)){
            return ResponseEntity.notFound().build();
        } else if(eventRepository.findById(inviteCode).isPresent()) {
            return ResponseEntity.ok(eventRepository.findById(inviteCode).get());
        }else {
            return ResponseEntity.badRequest().build();
        }
    }
    /**
     * Get methode to get all the events on the server
     * @return returns a list of all events on the server
     */
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventRepository.findAll());

    }




    /**
     * A post method to add and event to the repository
     * @param event an event in the requestBody to add to the repository
     * @return the event if successfully made
     */
    public ResponseEntity<Event> addEvent(Event event) {
        if (event == null || event.getTitle() == null || Objects.equals(event.getTitle(), "")) {
            return ResponseEntity.badRequest().build();
        }
        Tag tag1 = new Tag("food", "#93c47d");
        Tag tag2 = new Tag("entrance fees", "#4a86e8");
        Tag tag3 = new Tag("travel", "#e06666");
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        tagRepository.save(tag3);

        List<Tag> savedTags;
        savedTags = new ArrayList<>();
        savedTags.add(tag1);
        savedTags.add(tag2);
        savedTags.add(tag3);
        event.setTagsList(savedTags);
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        event.setLastActivity(timestamp2);
        event.setCreationDate(timestamp2);
        eventRepository.save(event);

        return ResponseEntity.ok(event);
    }

    /**
     * Change an existing event
     * @param inviteCode the invite code of the event to change
     * @param event the data that the event should have
     * @return the new changed event
     */
    @Transactional
    public ResponseEntity<Event> changeEvent(long inviteCode, Event event,
                                             GerneralServerUtil serverUtil) {
        if (inviteCode < 0) {
            return ResponseEntity.badRequest().build();
        }if (!eventRepository.existsById(inviteCode)) {
            return ResponseEntity.notFound().build();
        }
        if (event == null || Objects.equals(event.getTitle(), "") || event.getTitle() == null) {
            return ResponseEntity.badRequest().build();
        }

        Event saved = eventRepository.findById(inviteCode).get();
        serverUtil.updateDate(eventRepository,inviteCode);
        saved.setTitle(event.getTitle());
        eventRepository.save(saved);
        return ResponseEntity.ok(saved);
    }

    /**
     * Delete an existing event
     * @param inviteCode the invitecode of the event
     * @return the event that was deleted
     */
    public ResponseEntity<Event> deleteEvent(long inviteCode) {
        if (inviteCode < 0) {
            return ResponseEntity.badRequest().build();
        }if (!eventRepository.existsById(inviteCode)) {
            return ResponseEntity.notFound().build();
        }

        Event saved = eventRepository.findById(inviteCode).get();

        List<Tag> tmpTags = new ArrayList<>();
        for(Tag tag : saved.getTagsList()) {
            tmpTags.add(tag);
        }
        saved.getTagsList().removeAll(saved.getTagsList());
        eventRepository.save(saved);
        for(Tag tag : tmpTags) {
            tagRepository.deleteById(tag.getId());
        }
        eventRepository.deleteAllById(Collections.singleton(inviteCode));
        return ResponseEntity.ok(saved);
    }


    /**
     * Method to check if the imported event is valid
     * @param event event being imported
     * @return event if it is valid or error code if not
     */
    public ResponseEntity<Event> validateEvent(Event event) {
        if(event == null || event.getInviteCode()<0
                || Objects.equals(event.getTitle(), "")
                || event.getTitle() == null){
            return ResponseEntity.badRequest().build();
        }
        List<Event> allEvents = eventRepository.findAll();
        for(Event e: allEvents){
            if(e.equals(event)){
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok(event);
    }

    /**
     * Method to add an event to the repository from a JSON import
     * @param event event to be added to the eventRepository
     * @return the event in a ResponseEntity
     */
    public ResponseEntity<Event> addCreatedEvent(Event event) {
        return ResponseEntity.ok(eventRepository.save(event));
    }


    /**
     * Endpoint to calculate the debts that a certain participant owes/is owed
     * @param eventId the event that the participant is in
     * @param participantId the id of the participant whose debt is to be calculated
     * @return double of debt amount (negative if they owe, positive if they are owed)
     */
    public ResponseEntity<Double> getDebts(Long eventId, Long participantId) {
        if(validateDebt(eventId, participantId).getStatusCode() != OK){
            return validateDebt(eventId, participantId);
        }
        Event e= eventRepository.findById(eventId).get();
        Participant current = e.getParticipantsList()
                .stream()
                .filter(item -> item.getId()==participantId)
                .toList().getFirst();
        List<Expense> expenses = e.getExpensesList();
        double balance = 0;
        for(Expense expense: expenses){
            if(expense.getPayee().equals(current)){
                balance += expense.getAmount();
            }else{
                for(ParticipantPayment p: expense.getSplit()){
                    if(p.getParticipant().equals(current)){
                        balance -= p.getPaymentAmount();
                    }
                }
            }
        }
        return ResponseEntity.ok(balance);
    }

    /**
     * Validates that the event and participant exist together
     * @param eventId id of the event to check
     * @param participantId id of the participant to locate
     * @return responseentity indicating if the event and participant could be
     * verified.
     */
    private ResponseEntity<Double> validateDebt(Long eventId, Long participantId) {
        if(eventId<0 || participantId<0){
            return ResponseEntity.badRequest().build();
        }else if(!eventRepository.existsById(eventId)){
            return ResponseEntity.notFound().build();
        }
        if(eventRepository.findById(eventId).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Event e = eventRepository.findById(eventId).get();
        Participant current = e.getParticipantsList()
                .stream()
                .filter(item -> item.getId()==participantId)
                .toList().getFirst();
        if(current == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(0.0);
    }
}
