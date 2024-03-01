package server.api;

import java.sql.Timestamp;
import java.util.*;


import commons.Event;

import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.database.EventRepository;
import server.database.TagRepository;

@RestController
@RequestMapping("/api/events")

public class EventController {

    private final EventRepository repo;
    private final TagRepository tagRepo;


    /**
     * EventController constructor
     * @param repo a Event Repository to store the events
     */
    public EventController(EventRepository repo, TagRepository tagRepo) {
        this.repo = repo;
        this.tagRepo = tagRepo;
    }

    /**
     * Get methode to get all the events on the server
     * @return returns a list of all events on the server
     */
    @GetMapping(path = { "/{inviteCode}" })
    public ResponseEntity<Event> get(@PathVariable("inviteCode") long inviteCode) {
        if (inviteCode < 0 || !repo.existsById(inviteCode)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(inviteCode).get());
    }

    /**
     * A post method to add and event to the repository
     * @param event an event in the requestBody to add to the repository
     * @return the event if succesfully made
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Event> add(@RequestBody Event event) {

        if (event == null || event.getTitle() == null ||Objects.equals(event.getTitle(), "")) {
            return ResponseEntity.badRequest().build();
        }
        Tag tag1 = new Tag("food", "green");
        Tag tag2 = new Tag("entrance fees", "blue");
        Tag tag3 = new Tag("travel", "red");
        tagRepo.save(tag1);
        tagRepo.save(tag2);
        tagRepo.save(tag3);

        List<Tag> savedTags;
        if (event.getTagsList() == null) {
            savedTags = new ArrayList<>();
        } else {
            savedTags = event.getTagsList();
        }
        savedTags.add(tag1);
        savedTags.add(tag2);
        savedTags.add(tag3);
        event.setTagsList(savedTags);
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        event.setCreationDate(timestamp2);
        event.setLastActivity(timestamp2);
        repo.save(event);
        return ResponseEntity.ok(event);
    }

    /**
     * Change an existing event
     * @param inviteCode the invite code of the event to change
     * @param event the data that the event should have
     * @return the new changed event
     */
    @PutMapping(path = {"/{inviteCode}" })
    public ResponseEntity<Event> change(@PathVariable("inviteCode") long inviteCode,
                                        @RequestBody Event event) {

        if (Objects.equals(event.getTitle(), "") || inviteCode < 0
                || !repo.existsById(inviteCode)) {
            return ResponseEntity.badRequest().build();
        }

        Event saved = repo.findById(inviteCode).get();
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        saved.setLastActivity(timestamp2);
        saved.setTitle(event.getTitle());
        repo.save(saved);
        return ResponseEntity.ok(saved);
    }

    /**
     * Delete an existing event
     * @param inviteCode the invitecode of the event
     * @return the event that was deleted
     */
    @DeleteMapping(path = {"/{inviteCode}" })
    public ResponseEntity<Event> delete(@PathVariable("inviteCode") long inviteCode) {

        if (inviteCode < 0 || !repo.existsById(inviteCode)) {
            return ResponseEntity.badRequest().build();
        }

        Event saved = repo.findById(inviteCode).get();

        List<Tag> tmpTags = new ArrayList<>();
        for(Tag tag : saved.getTagsList()) {
            tmpTags.add(tag);
        }
        saved.getTagsList().removeAll(saved.getTagsList());
        repo.save(saved);
        for(Tag tag : tmpTags) {
            tagRepo.deleteById(tag.getId());
        }
        repo.deleteAllById(Collections.singleton(inviteCode));
        return ResponseEntity.ok(null);
    }



}
