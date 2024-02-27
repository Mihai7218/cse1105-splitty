package server.api;

import commons.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ParticipantRepository;

import java.util.List;

@RestController
@RequestMapping("/api/events/{id}/participants")
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

    @GetMapping(path = { "", "/"})
    public List<Participant> getParticipants(){
        return participantService.getAllParticipants();
    }

    @PostMapping(path = {"", "/"})
    public ResponseEntity<Participant> addParticipant(@RequestBody Participant participant){
        return participantService.addParticipants(participant);
    }

    @PutMapping(path = {"/{id}"})
    public ResponseEntity<Participant> updateParticipant(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String iban,
            @RequestParam(required = false) String bic){
        return participantService.updateParticipant(id, name, email, iban, bic);
    }

    @DeleteMapping(path = {"/id"})
    public void deleteParticipant(
            @PathVariable("id") Long id){
        participantService.deleteParticipant(id);
    }





}
