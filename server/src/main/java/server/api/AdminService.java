package server.api;

import commons.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.OK;

@Service
public class AdminService {

    private final EventRepository eventRepository;
    private final TagRepository tagRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantPaymentRepository participantPaymentRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * Constructor for de EventService
     * @param eventRepository the event repository
     * @param tagRepository the tag repository
     */
    @Autowired
    public AdminService(EventRepository eventRepository,
                        TagRepository tagRepository,
                        ParticipantRepository participantRepository,
                        ParticipantPaymentRepository participantPaymentRepository,
                        ExpenseRepository expenseRepository) {
        this.eventRepository = eventRepository;
        this.tagRepository = tagRepository;
        this.participantRepository = participantRepository;
        this.participantPaymentRepository = participantPaymentRepository;
        this.expenseRepository = expenseRepository;
    }

    /**
     * Get methode to get all the events on the server
     * @return returns a list of all events on the server
     */
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventRepository.findAll());
    }

    /**
     * Method to add an event to the repository from a JSON import
     * @param event event to be added to the eventRepository
     * @return the event in a ResponseEntity
     */
    public ResponseEntity<Event> addCreatedEvent(Event event) {
        if(validateEvent(event).getStatusCode().equals(OK)){
            try {
                Event eventToSave = new Event(event.getTitle(),event.getCreationDate(),event.getLastActivity());
                eventRepository.save(eventToSave);
                HashMap<Tag, Tag> tagTagHashMap = tagHashMapMaker(event, eventToSave);
                HashMap<Participant, Participant> ppHashMap = participantHashMapMaker(event, eventToSave);
                eventRepository.save(eventToSave);
                for (Expense e : event.getExpensesList()) {
                    Expense expenseToSave = new Expense(e.getAmount(),
                            e.getCurrency(),
                            e.getTitle(),
                            e.getDescription(),
                            e.getDate(),
                            new ArrayList<>(),
                            null,null);
                    if (e.getPayee() != null) {
                        expenseToSave.setPayee(ppHashMap.get(e.getPayee()));
                    }if (e.getTag() != null) {
                        expenseToSave.setTag(tagTagHashMap.get(e.getTag()));
                    }
                    for (ParticipantPayment pp : e.getSplit()) {
                        ParticipantPayment ppToSave = new ParticipantPayment(ppHashMap.get(pp.getParticipant()),pp.getPaymentAmount());
                        participantPaymentRepository.save(ppToSave);
                        expenseToSave.getSplit().add(ppToSave);
                    }
                    expenseRepository.save(expenseToSave);
                    eventToSave.getExpensesList().add(expenseToSave);
                }
                eventRepository.save(eventToSave);
                return ResponseEntity.ok(eventToSave);
            } catch (Exception e) {
                return ResponseEntity
                        .badRequest()
                        .body(new Event("Something went wrong",null,null));
            }
        }else{
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    private HashMap<Participant, Participant> participantHashMapMaker(Event event, Event eventToSave) {
        HashMap<Participant,Participant> ppHashMap = new HashMap<>();
        for (Participant p : event.getParticipantsList()) {
            Participant participantToSave = new Participant(p.getName(),p.getEmail(), p.getIban(), p.getBic());
            ppHashMap.put(p,participantToSave);
            participantRepository.save(participantToSave);
            eventToSave.getParticipantsList().add(participantToSave);
        }
        return ppHashMap;
    }

    private HashMap<Tag, Tag> tagHashMapMaker(Event event, Event eventToSave) {
        HashMap<Tag,Tag> tagTagHashMap = new HashMap<>();
        for (Tag tag : event.getTagsList()) {
            Tag tagToSave = new Tag(tag.getName(), tag.getColor());
            tagTagHashMap.put(tag,tagToSave);
            tagRepository.save(tagToSave);
            eventToSave.getTagsList().add(tagToSave);
        }
        return tagTagHashMap;
    }

    /**
     * Method to check if the imported event is valid
     * @param event event being imported
     * @return event if it is valid or error code if not
     */
    public ResponseEntity<Event> validateEvent(Event event) {
        if(event == null
                || Objects.equals(event.getTitle(), "")
                || event.getTitle() == null){
            return ResponseEntity.badRequest().build();
        }
        List<Event> allEvents = eventRepository.findAll();
        for(Event e: allEvents){
            event.setInviteCode(e.getInviteCode());
            boolean test = e.fullEquals(event);
            if(e.fullEquals(event)){
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok(event);
    }

    public boolean eventEquals(Event a, Event b) {
        return a.getInviteCode() == b.getInviteCode() &&
                a.getLastActivity().equals(b.getLastActivity()) &&
                a.getCreationDate().equals(b.getCreationDate()) &&
                a.getTitle().equals(b.getTitle());
    }
}
