package server.api;

import commons.Event;
import commons.Participant;
import commons.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ParticipantRepository;
import server.database.TagRepository;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

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
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventRepository.findAll());

    }


    public ResponseEntity<Event> addEvent(Event event) {
        if (event == null || event.getTitle() == null || Objects.equals(event.getTitle(), "")) {
            return ResponseEntity.badRequest().build();
        }
        Tag tag1 = new Tag("food", "green");
        Tag tag2 = new Tag("entrance fees", "blue");
        Tag tag3 = new Tag("travel", "red");
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
        event.setCreationDate(timestamp2);
        event.setLastActivity(timestamp2);
        eventRepository.save(event);

        return ResponseEntity.ok(event);
    }

    @Transactional
    public ResponseEntity<Event> changeEvent(long inviteCode, Event event) {
        if (inviteCode < 0) {
            return ResponseEntity.badRequest().build();
        }if (!eventRepository.existsById(inviteCode)) {
            return ResponseEntity.notFound().build();
        }
        if (event == null || Objects.equals(event.getTitle(), "") || event.getTitle() == null) {
            return ResponseEntity.badRequest().build();
        }

        Event saved = eventRepository.findById(inviteCode).get();
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        saved.setLastActivity(timestamp2);
        saved.setTitle(event.getTitle());
        eventRepository.save(saved);
        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<Event> deleteEvent(long inviteCode, long id) {
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
}
