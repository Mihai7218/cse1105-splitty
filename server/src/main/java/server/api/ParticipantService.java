package server.api;

import commons.Event;
import commons.Participant;
import jakarta.servlet.http.Part;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.beans.Transient;
import java.util.List;
import java.util.Objects;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;


    @Autowired
    public ParticipantService(ParticipantRepository participantRepository,
                              EventRepository eventRepository){
        this.participantRepository = participantRepository;
        this.eventRepository = eventRepository;
    }

    public List<Participant> getAllParticipants(long eventId) {
        if(eventId < 0 || !eventRepository.existsById(eventId)){
            throw new IllegalStateException("The event with id " + eventId + " does not exist.");
        }
        // TODO
        return eventRepository.findById(eventId).get().getParticipantsList();
    }

    public Participant getParticipant(long eventId, long id) {
        List<Participant> participants = getAllParticipants(eventId);
        if(id < 0 || !participantRepository.existsById(id)){
            throw new IllegalStateException("The participant with id " + id + " does not exist.");

        }
        int indexPos = participants.indexOf(participantRepository.getReferenceById(id));
        if(indexPos == -1){
            throw new IllegalStateException("The participant with id " + id + " is not in this event.");
        }
        return participants.get(indexPos);

    }

    public ResponseEntity<Participant> addParticipant(long eventId, Participant participant) {
        List<Participant> currentParticipants = getAllParticipants(eventId);

        if(participant == null || participant.getName()==null ||
                participant.getName().isEmpty()){
            throw new IllegalStateException("Participant name should not be empty");
        }
        if(currentParticipants.contains(participant)){
            throw new IllegalStateException("This participant is already a member of this event");
        }
        currentParticipants.add(participant);
        Participant saved = participantRepository.save(participant);
        return ResponseEntity.ok(saved);
    }

    @Transactional
    public ResponseEntity<Participant> updateParticipant(long eventId, long id, String name, String email, String iban, String bic) {
        Participant participant = getParticipant(eventId,id);

        if(name != null && !name.isEmpty()
                && !Objects.equals(participant.getName(), name)){
            participant.setName(name);
        }
        // TODO add more logic to update other fields
        Participant saved = participantRepository.save(participant);
        return ResponseEntity.ok(saved);
    }

    public void deleteParticipant(long eventId, long id) {
        List<Participant> participants = getAllParticipants(eventId);
        boolean inRepo = participantRepository.existsById(id);
        if(!inRepo){
            throw new IllegalStateException("Participant with id " + id +
                    " does not exist.");
        }
        boolean inEvent = participants.contains(participantRepository.getReferenceById(id));
        if(!inEvent){
            throw new IllegalStateException("Participant with id " + id + " is not in this event.");
        }
        int index = participants.indexOf(participantRepository.getReferenceById(id));
        participants.remove(index);
    }
}
