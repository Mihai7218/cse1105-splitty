package server.api;

import commons.Participant;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class ParticipantService {
    private final EventRepository eventRepository;


    /**
     * ParticipantService Constructor
     * @param eventRepository the event repository to retrieve events from
     */
    @Autowired
    public ParticipantService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    /**
     * Get method that checks if the id is valid and retrieves the list of participants
     * @param eventId id of the event to get participants from
     * @return list of participants if successfully retrieved
     */
    public ResponseEntity<List<Participant>> getAllParticipants(long eventId) {
        if(eventId < 0 || !eventRepository.existsById(eventId)){
            return ResponseEntity.badRequest().build();
        }else if(eventRepository.findById(eventId).isPresent()) {
            return ResponseEntity.ok(
                    eventRepository.findById(eventId).get().getParticipantsList());
        }else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get method that validates the event and participant id and retrieves the participant
     * @param eventId the id of the event to get the participant from
     * @param id of the participant to retrieve
     * @return participant if succesfully retrieved
     */
    public ResponseEntity<Participant> getParticipant(long eventId, long id) {
        List<Participant> participants = getAllParticipants(eventId).getBody();
        if(id < 0 || participants==null){
            return ResponseEntity.badRequest().build();
        }
        participants = participants.stream().filter(item -> item.getId()==id).toList();
        if(participants.size() != 1 || participants.get(0) == null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(participants.get(0));

    }

    /**
     * Post method that validates the participant to be added and adds it
     * @param eventId the event the participant is added to
     * @param participant the participant being added
     * @return participant if successfully added to event
     */
    public ResponseEntity<Participant> addParticipant(long eventId, Participant participant) {
        List<Participant> currentParticipants = getAllParticipants(eventId).getBody();
        if(participant == null || currentParticipants == null){
            return ResponseEntity.badRequest().build();
        }
        if(currentParticipants.contains(participant)
            || currentParticipants.stream().anyMatch(item -> item.getId()==participant.getId())){
            return ResponseEntity.badRequest().build();
        }

        if(!validateBic(participant.getBic()) || !validateName(participant.getName()) ||
                !validateEmail(participant.getEmail()) || !validateIban(participant.getIban())){
            return ResponseEntity.badRequest().build();
        }

        currentParticipants.add(participant);
        return ResponseEntity.ok(participant);
    }

    /**
     * Put method that validates the values in the participant to be changed
     * @param eventId id of the event the participant is in
     * @param id id of the participant to be retrieved
     * @param name the new name to replace the participants name with
     * @param email the new email to replace the participants email with
     * @param iban the new iban to replace the participants iban with
     * @param bic the new bic to replace the participants bic with
     * @return participant if successfully added to event
     */
    @Transactional
    public ResponseEntity<Participant> updateParticipant(long eventId, long id, String name,
                                                         String email, String iban, String bic) {
        Participant participant = getParticipant(eventId,id).getBody();
        if(participant == null){
            return ResponseEntity.badRequest().build();
        }
        if(validateName(name)){
            participant.setName(name);
        }
        if(validateEmail(email)){
            participant.setEmail(email);
        }
        if(validateIban(iban)){
            participant.setIban(iban);
        }
        if(validateBic(bic)){
            participant.setBic(bic);
        }
        return ResponseEntity.ok(participant);
    }

    /**
     * Delete method to remove a participant from the event
     * @param eventId id of event participant is in
     * @param id id of the participant to delete
     * @return participant if successfully removed
     */
    public ResponseEntity<Participant> deleteParticipant(long eventId, long id) {
        List<Participant> participantList = getAllParticipants(eventId).getBody();
        Participant participant = getParticipant(eventId, id).getBody();
        if(participant == null){
            return ResponseEntity.badRequest().build();
        }
        participantList.remove(participant);
        return ResponseEntity.ok(participant);
    }

    /**
     * Method to validate a String is in bic format
     * @param bic the submitted bic string
     * @return boolean value if the string is a valid bic number
     */
    private static boolean validateBic(String bic) {
        String bicRegex = "^[A-Z]{4}[-]{0,1}[A-Z]{2}[-]{0,1}[A-Z0-9]{2}[-]{0,1}[0-9]{3}$";
        return bic != null && Pattern.compile(bicRegex).matcher(bic).matches();
    }

    private static boolean validateName(String name){
        return name != null && !name.isEmpty();
    }

    /**
     * Method to validate a String is in iban format
     * @param iban the submitted iban string
     * @return boolean value if the string is a valid iban number
     */
    private static boolean validateIban(String iban) {
        String ibanRegex = "^[A-Z]{2}[0-9]{2}(?:[ ]?[0-9]{4}){4}(?:[ ]?[0-9]{1,2})?$";
        return iban != null && Pattern.compile(ibanRegex).matcher(iban).matches();
    }

    /**
     * Method to validate a String is in email format
     * @param email the submitted email string
     * @return boolean value if the string is a valid email
     */
    private static boolean validateEmail(String email) {
        String emailRegex = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+" +
                "(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$";
        return email != null && Pattern.compile(emailRegex).matcher(email).matches();

    }
}
