package server.api;

import commons.Event;
import commons.Expense;
import commons.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.TagRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class TagService {
    private final EventRepository eventRepo;
    private final TagRepository tagRepo;

    /**
     * Constructor of the tagservice
     * @param eventRepo The repository in which the events are stored
     */
    @Autowired
    public TagService(EventRepository eventRepo, TagRepository tagRepo){
        this.eventRepo = eventRepo;
        this.tagRepo = tagRepo;
    }

    /**
     * Returns all expenses within an event which have been tagged with
     * a certain tag
     * @param inviteCode of the event which contains the expenses
     * @param tagName the name of the tag to search for
     * @return the list of all expenses with this tag
     */
    public ResponseEntity<List<Expense>> getAllExpensesWithTag(long inviteCode, String tagName){
        if (inviteCode < 0 || !eventRepo.existsById(inviteCode) ||
                eventRepo.findById(inviteCode).get().getExpensesList() == null) {
            return ResponseEntity.notFound().build();
        }
        Event tmpEvent = eventRepo.findById(inviteCode).get();
        List<Expense> res = new ArrayList<>();
        for (Expense expense : tmpEvent.getExpensesList()) {
            if (expense.getTag().getName().equals(tagName)) {
                res.add(expense);
            }
        }
        return ResponseEntity.ok(res);
    }

    /**
     * Adds a new tag to an event
     * @param inviteCode the invite code of the event to add the tag to
     * @param tag the tag to be added
     * @return whether the tag could be added to the event
     * TODO: add a check for duplicates
     */
    public ResponseEntity<Tag> addNewToEvent(long inviteCode, Tag tag) {
        if (inviteCode < 0 || !eventRepo.existsById(inviteCode) ||
                eventRepo.findById(inviteCode).get().getExpensesList() == null) {
            return ResponseEntity.notFound().build();
        }
        if (tag == null || tag.getName() == null ||
                Objects.equals(tag.getName(), "")) {
            return ResponseEntity.badRequest().build();
        }
        tagRepo.save(tag);
        return ResponseEntity.ok(tag);
    }

    /**
     * Changes the name of a tag
     * @param inviteCode the inviteCode of the event with which the tag is associated
     * @param tagId the id of the tag itself
     * @param newName the new name of the tag after change
     * @return whether the tagname was changed
     */
    public ResponseEntity<Tag> changeName(long inviteCode, long tagId, String newName) {
        if (inviteCode < 0 || !eventRepo.existsById(inviteCode) ||
                eventRepo.findById(inviteCode).get().getExpensesList() == null) {
            return ResponseEntity.notFound().build();
        }
        if (tagId < 0 || !tagRepo.existsById(tagId)) {
            return ResponseEntity.notFound().build();
        }
        if (newName == null || Objects.equals(newName, "")){
            return ResponseEntity.badRequest().build();
        }
        Tag change = tagRepo.findById(tagId).get();
        change.setName(newName);
        tagRepo.save(change);
        return ResponseEntity.ok(null);
    }

    /**
     * Changes the colorcode of a tag
     * @param inviteCode the invitecode of the event with which the tag is associated
     * @param tagId the id of the tag itself
     * @param newColor the new colorcode of the tag
     * @return whether the color of the tag was updated
     */
    public ResponseEntity<Tag> changeColor(long inviteCode, long tagId, String newColor) {
        if (inviteCode < 0 || !eventRepo.existsById(inviteCode) ||
                eventRepo.findById(inviteCode).get().getExpensesList() == null) {
            return ResponseEntity.notFound().build();
        }
        if (tagId < 0 || !tagRepo.existsById(tagId)) {
            return ResponseEntity.notFound().build();
        }
        if (newColor == null || Objects.equals(newColor, "")){
            return ResponseEntity.badRequest().build();
        }
        Tag change = tagRepo.findById(tagId).get();
        change.setColor(newColor);
        tagRepo.save(change);
        return ResponseEntity.ok(null);
    }

    /**
     * Deletes a tag from an event
     * @param inviteCode the inv.code of the event to delete the tag from
     * @param tagId the id of the tag to be deleted
     * @return whether the tag was successfully deleted
     * TODO: check if we need to delete tag specifically from event or from repo is fine
     */
    public ResponseEntity<Tag> deleteTagFromEvent(long inviteCode, long tagId) {
        if (inviteCode < 0 || !eventRepo.existsById(inviteCode) ||
                eventRepo.findById(inviteCode).get().getExpensesList() == null) {
            return ResponseEntity.notFound().build();
        }
        if (tagId < 0 || !tagRepo.existsById(tagId)) {
            return ResponseEntity.notFound().build();
        }
        tagRepo.deleteAllById(Collections.singleton(tagId));
        return ResponseEntity.ok(null);
    }
}
