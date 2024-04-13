package server.api;

import commons.Event;
import commons.Expense;
import commons.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.TagRepository;

import java.util.*;

@Service
public class TagService {
    private final EventRepository eventRepo;
    private final TagRepository tagRepo;

    /**
     * Constructor of the tagservice
     *
     * @param eventRepo The repository in which the events are stored
     */
    @Autowired
    public TagService(EventRepository eventRepo, TagRepository tagRepo) {
        this.eventRepo = eventRepo;
        this.tagRepo = tagRepo;
    }

    /**
     * Returns all expenses within an event which have been tagged with
     * a certain tag
     *
     * @param inviteCode of the event which contains the expenses
     * @param tagName the name of the tag to search for
     * @return the list of all expenses with this tag
     */
    public ResponseEntity<List<Expense>> getAllExpensesWithTag(long inviteCode, String tagName) {
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
     * Returns all the tags associated with an event
     * @param inviteCode the invite code of the event
     * @return the list of tags or status code
     */
    public ResponseEntity<List<Tag>> getTagsFromEvent(long inviteCode){
        if (inviteCode < 0 || !eventRepo.existsById(inviteCode) ||
                eventRepo.findById(inviteCode).get().getExpensesList() == null) {
            return ResponseEntity.notFound().build();
        }
        List<Tag> res = eventRepo.findById(inviteCode).get().getTagsList();
        return ResponseEntity.ok(res);
    }

    /**
     * Adds a new tag to an event
     *
     * @param inviteCode the invite code of the event to add the tag to
     * @param tag        the tag to be added
     * @param serverUtil
     * @return whether the tag could be added to the event
     */
    public ResponseEntity<Tag> addNewToEvent(long inviteCode, Tag tag,
                                             GerneralServerUtil serverUtil) {
        if (inviteCode < 0 || !eventRepo.existsById(inviteCode) ||
                eventRepo.findById(inviteCode).get().getExpensesList() == null) {
            return ResponseEntity.notFound().build();
        }
        if (tag == null || tag.getName() == null ||
                Objects.equals(tag.getName(), "")) {
            return ResponseEntity.badRequest().build();
        }
        Event event = eventRepo.findById(inviteCode).get();
        //Add tag to the list of tags of the event
        List<Tag> tagList = event.getTagsList();
        tagList.add(tag);
        event.setTagsList(tagList);
        //Save both in their respective repos
        tagRepo.save(tag);
        serverUtil.updateDate(eventRepo,inviteCode);
        eventRepo.save(event);

        return ResponseEntity.ok(tag);
    }

    /**
     * Changes the name of a tag
     *
     * @param inviteCode the inviteCode of the event with which the tag is associated
     * @param tagId      the id of the tag itself
     * @param tag        the new tag which the old one should be changed to
     * @param serverUtil
     * @return whether the tagName was changed
     */
    public ResponseEntity<Tag> changeTag(long inviteCode, long tagId, Tag tag,
                                         GerneralServerUtil serverUtil) {
        if (inviteCode < 0 || !eventRepo.existsById(inviteCode) ||
                eventRepo.findById(inviteCode).get().getExpensesList() == null) {
            return ResponseEntity.notFound().build();
        }
        if (tagId < 0 || !tagRepo.existsById(tagId)) {
            return ResponseEntity.notFound().build();
        }
        if (tag == null || Objects.equals(tag.getName(), "")) {
            return ResponseEntity.badRequest().build();
        }
        Event event = eventRepo.findById(inviteCode).get();
        serverUtil.updateDate(eventRepo,inviteCode);
        Tag change = tagRepo.findById(tagId).get();
        change.setColor(tag.getColor());
        change.setName(tag.getName());
        tagRepo.save(change);
        return ResponseEntity.ok(change);
    }

    /**
     * Deletes a tag from an event
     *
     * @param inviteCode the inviteCode of the event to delete the tag from
     * @param tagId      the id of the tag to be deleted
     * @param serverUtil
     * @return whether the tag was successfully deleted
     */
    public ResponseEntity<Tag> deleteTagFromEvent(long inviteCode, long tagId,
                                                  GerneralServerUtil serverUtil) {
        if (inviteCode < 0 || !eventRepo.existsById(inviteCode) ||
                eventRepo.findById(inviteCode).get().getExpensesList() == null) {
            return ResponseEntity.notFound().build();
        }
        if (tagId < 0 || !tagRepo.existsById(tagId)) {
            return ResponseEntity.notFound().build();
        }
        Event event = eventRepo.findById(inviteCode).get();
        serverUtil.updateDate(eventRepo,inviteCode);
        Tag test = tagRepo.findById(tagId).get();
        for(Expense expense : event.getExpensesList()) {
            if (test.equals(expense.getTag())) {
                expense.setTag(null);
            }
        }
        event.getTagsList().remove(test);
        eventRepo.save(event);
        tagRepo.deleteAllById(Collections.singleton(tagId));
        return ResponseEntity.ok(test);
    }



    /**
     * Method to check if the imported tag is valid
     * @param tag tag being imported
     * @return tag if it is valid or error code if not
     */
    public ResponseEntity<Tag> validateTag(Tag tag) {
        if(tag == null || tag.getName() == null
                || tag.getColor() == null
                || Objects.equals(tag.getName(), "")
                || Objects.equals(tag.getColor(), "")){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(tag);
    }


}
