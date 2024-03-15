package server.api;

import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class ParticipantController {

    /*
    Endpoints:
    /api/events/{id}/participants (GET, POST)
    /api/events/{id}/participants/{id} (PUT, DELETE)
     */
    private final ParticipantService participantService;

    /**
     * Participant Controller Constructor
     * @param participantService participant service used for this controller
     */
    @Autowired
    public ParticipantController(ParticipantService participantService){
        this.participantService = participantService;
    }

    /**
     * Get method to return list of participants associated with an event
     * @param eventId the id of the event to get participants from
     * @return the list of participants if the event is found
     */
    @GetMapping(path = { "/{eventId}/participants", "/{eventId}/participants/"})
    public ResponseEntity<List<Participant>> getParticipants(
            @PathVariable("eventId") long eventId) {
        return participantService.getAllParticipants(eventId);
    }

    /**
     * Get method to return a specific participant associated with an event
     * @param eventId id of the event being looked at
     * @param id id of the participant in the event
     * @return the requested participant if found
     */
    @GetMapping(path = {"/{eventId}/participants/{id}", "/{eventId}/participants/{id}/"})
    public ResponseEntity<Participant> getParticipant(
            @PathVariable("eventId") long eventId,
            @PathVariable("id") long id){
        return participantService.getParticipant(eventId, id);
    }

    /**
     * Post method to add a new participant to an event
     * @param eventId id of the event that the participant is added to
     * @param participant the participant to be added to the event
     * @return the participant if successfully made or badrequest
     */
    @PostMapping(path = {"/{eventId}/participants", "/{eventId}/participants/"})
    public ResponseEntity<Participant> addParticipant(
            @PathVariable("eventId") long eventId,
            @RequestBody Participant participant){
        return participantService.addParticipant(eventId, participant);
    }

    /**
     * Put method to update a participant in an event
     * @param eventId id of the event the participant is in
     * @param id id of participant to update
     * @param participant the participant to change to
     * @return the participant after it is updated or badrequest
     */
    @PutMapping(path = {"/{eventId}/participants/{id}"})
    public ResponseEntity<Participant> updateParticipant(
            @PathVariable("eventId") long eventId,
            @PathVariable("id") long id,
            @RequestBody Participant participant){
        return participantService.updateParticipant(eventId, id,participant);
    }

    /**
     * Delete method to remove a participant from an event
     * @param eventId id of the event the participant was in
     * @param id id of the participant to be removed
     */
    @DeleteMapping(path = {"/{eventId}/participants/{id}"})
    public ResponseEntity<Participant> deleteParticipant(
            @PathVariable("eventId") long eventId,
            @PathVariable("id") long id){
        return participantService.deleteParticipant(eventId, id);
    }



    /**
     * Endpoint to add participant from json import as admin
     * @param password password for admin access
     * @param participant the participant to add
     * @return participant if added successfully
     */
    @PostMapping(path = {"/admin/{password}"})
    public ResponseEntity<Participant> addPriorParticipant(
            @PathVariable("password") String password,
            @RequestBody Participant participant){
        if (PasswordService.getPassword().equals(password)) {
            return participantService.addPriorParticipant(participant);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


}
