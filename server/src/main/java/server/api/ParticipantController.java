package server.api;

import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ParticipantRepository;

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

    @Autowired
    public ParticipantController(ParticipantService participantService){
        this.participantService = participantService;
    }

    @GetMapping(path = { "/{eventId}/participants", "/{eventId}/participants/"})
    public List<Participant> getParticipants(
            @PathVariable("eventId") long eventId) {
        return participantService.getAllParticipants(eventId);
    }

    @GetMapping(path = {"/{eventId}/participants/{id}", "/{eventId}/participants/{id}/"})
    public Participant getParticipant(
            @PathVariable("eventId") long eventId,
            @PathVariable("id") long id){
        return participantService.getParticipant(eventId, id);
    }


    @PostMapping(path = {"/{eventId}/participants", "/{eventId}/participants/"})
    public ResponseEntity<Participant> addParticipant(
            @PathVariable("eventId") long eventId,
            @RequestBody Participant participant){
        return participantService.addParticipant(eventId, participant);
    }

    @PutMapping(path = {"/{eventId}/participants/{id}"})
    public ResponseEntity<Participant> updateParticipant(
            @PathVariable("eventId") long eventId,
            @PathVariable("id") long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String iban,
            @RequestParam(required = false) String bic){
        return participantService.updateParticipant(eventId, id, name, email, iban, bic);
    }

    @DeleteMapping(path = {"/{eventId}/participants/{id}"})
    public void deleteParticipant(
            @PathVariable("eventId") long eventId,
            @PathVariable("id") long id){
        participantService.deleteParticipant(eventId, id);
    }





}
