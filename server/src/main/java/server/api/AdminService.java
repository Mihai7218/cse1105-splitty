package server.api;

import commons.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.TagRepository;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.OK;

@Service
public class AdminService {

    private final EventRepository eventRepository;
    private final TagRepository tagRepository;
    private final ParticipantPaymentService participantPaymentService;
    private final ParticipantService participantService;

    /**
     * Constructor for de EventService
     * @param eventRepository the event repository
     * @param tagRepository the tag repository
     */
    @Autowired
    public AdminService(EventRepository eventRepository,
                        TagRepository tagRepository,
                        ParticipantPaymentService participantPaymentService,
                        ParticipantService participantService) {
        this.eventRepository = eventRepository;
        this.tagRepository = tagRepository;
        this.participantPaymentService = participantPaymentService;
        this.participantService = participantService;
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
            Event eventToSave = new Event(event.getTitle(),event.getCreationDate(),event.getLastActivity());

            return ResponseEntity.ok(eventRepository.save(event));
        }else{
            return ResponseEntity.badRequest().build();
        }
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
            if(e.fullEquals(event)){
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok(event);
    }
}
