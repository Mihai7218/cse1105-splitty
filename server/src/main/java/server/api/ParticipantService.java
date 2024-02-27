package server.api;

import commons.Participant;
import jakarta.servlet.http.Part;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.ParticipantRepository;

import java.beans.Transient;
import java.util.List;
import java.util.Objects;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    @Autowired
    public ParticipantService(ParticipantRepository participantRepository){
        this.participantRepository = participantRepository;
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public ResponseEntity<Participant> addParticipants(Participant participant) {
        if(participant.getName() == null){
            throw new IllegalStateException("participant needs a non-null name");
        }
        Participant saved = participantRepository.save(participant);
        return ResponseEntity.ok(saved);
    }

    @Transactional
    public ResponseEntity<Participant> updateParticipant(Long id, String name, String email, String iban, String bic) {
        Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "The participant with id " + id + " does not exist."
                ));
        if(name != null && !name.isEmpty()
                && !Objects.equals(participant.getName(), name)){
            participant.setName(name);
        }
        Participant saved = participantRepository.save(participant);
        return ResponseEntity.ok(saved);
    }

    public void deleteParticipant(Long id) {
        boolean inRepo = participantRepository.existsById(id);
        if(!inRepo){
            throw new IllegalStateException("Participant with id " + id +
                    " does not exist.");
        }
        participantRepository.deleteById(id);
    }
}
