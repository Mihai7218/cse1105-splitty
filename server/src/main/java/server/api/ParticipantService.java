package server.api;

import commons.Participant;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.*;

@Service
public class ParticipantService {
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    /**
     * ParticipantService Constructor
     * @param eventRepository the event repository to retrieve events from
     */
    @Autowired
    public ParticipantService(EventRepository eventRepository,
                              ParticipantRepository participantRepository){
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
    }

    /**
     * Get method that checks if the id is valid and retrieves the list of participants
     * @param eventId id of the event to get participants from
     * @return list of participants if successfully retrieved
     */
    public ResponseEntity<List<Participant>> getAllParticipants(long eventId) {
        if(eventId < 0){
            return ResponseEntity.badRequest().build();
        }else if (!eventRepository.existsById(eventId)){
            return ResponseEntity.notFound().build();
        } else if(eventRepository.findById(eventId).isPresent()) {
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
        if(!getAllParticipants(eventId).getStatusCode().equals(OK)){
            return ResponseEntity.badRequest().build();
        }
        List<Participant> participants = getAllParticipants(eventId).getBody();
        if(id < 0 || participants==null){
            return ResponseEntity.badRequest().build();
        }
        List<Participant> filtered =
                participants.stream().filter(item -> item.getId()==id).toList();
        if(filtered.size() != 1 || filtered.get(0) == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filtered.get(0));
    }

    /**
     * Post method that validates the participant to be added and adds it
     * @param eventId the event the participant is added to
     * @param participant the participant being added
     * @return participant if successfully added to event
     */
    public ResponseEntity<Participant> addParticipant(long eventId, Participant participant) {
        if(getAllParticipants(eventId).getStatusCode().equals(BAD_REQUEST)){
            return ResponseEntity.badRequest().build();
        }else if(getAllParticipants(eventId).getStatusCode().equals(NOT_FOUND)){
            return ResponseEntity.notFound().build();
        }
        List<Participant> currentParticipants = getAllParticipants(eventId).getBody();
        if(participant == null || currentParticipants == null
            || currentParticipants.contains(participant)){
            return ResponseEntity.badRequest().build();
        }

        if(!validateBic(participant.getBic()) || !validateName(participant.getName()) ||
                !validateEmail(participant.getEmail()) || !validateIban(participant.getIban())){
            return ResponseEntity.badRequest().build();
        }

        participantRepository.save(participant);
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
        if(!getParticipant(eventId, id).getStatusCode().equals(OK)
            || getParticipant(eventId,id).getBody() == null){
            return getParticipant(eventId,id);
        }
        Participant participant = getParticipant(eventId,id).getBody();
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
        participantRepository.save(participant);
        return ResponseEntity.ok(participant);
    }

    /**
     * Delete method to remove a participant from the event
     * @param eventId id of event participant is in
     * @param id id of the participant to delete
     * @return participant if successfully removed
     */
    public ResponseEntity<Participant> deleteParticipant(long eventId, long id) {
        if(getAllParticipants(eventId).getStatusCode().equals(BAD_REQUEST)
            || getParticipant(eventId, id).getStatusCode().equals(NOT_FOUND)){
            return ResponseEntity.badRequest().build();
        }
        List<Participant> participantList = getAllParticipants(eventId).getBody();
        Participant participant = getParticipant(eventId, id).getBody();
        if(participant == null || participantList == null){
            return ResponseEntity.badRequest().build();
        }
        participantList.remove(participant);
        participantRepository.deleteById(id);
        eventRepository.getReferenceById(eventId).setParticipantsList(participantList);
        return ResponseEntity.ok(participant);
    }

    /**
     * Method to validate a String is in bic format
     * @param bic the submitted bic string
     * @return boolean value if the string is a valid bic number
     */
    public static boolean validateBic(String bic) {
        String bicRegex = "^[A-Za-z]{6}[0-9A-Za-z]{2}([0-9A-Za-z]{3})?$";
        return bic != null && Pattern.compile(bicRegex).matcher(bic).matches();
    }

    /**
     * Method to validate the name of the participant
     * @param name string name to validate
     * @return true if string is valid, false if not
     */
    public static boolean validateName(String name){
        return name != null && !name.isEmpty();
    }

    /**
     * Method to validate a String is in iban format
     * @param iban the submitted iban string
     * @return boolean value if the string is a valid iban number
     */
    public static boolean validateIban(String iban) {
        String ibanRegex = "^[A-Z]{2}[0-9]{2}[A-Za-z0-9]{11,30}$";
        return iban != null && Pattern.compile(ibanRegex).matcher(iban).matches();
    }

    /**
     * Method to validate a String is in email format
     * @param email the submitted email string
     * @return boolean value if the string is a valid email
     */
    public static boolean validateEmail(String email) {
        Pattern basic = Pattern.compile("^[\\w!#$%&’*+/=?{|}~^-]+(?:\\." +
                "[\\w!#$%&’*+/=?{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
        return email != null && basic.matcher(email).matches();

    }
}
