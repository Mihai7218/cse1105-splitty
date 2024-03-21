package server.api;

import commons.Expense;
import commons.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

//TODO: add endpoints for specific expense within an event if necessary

@RestController
@RequestMapping("/api/events")

public class TagController {

    private final TagService tagService;

    /**
     * Constructor of the tag controller
     * @param tagService the service which will be called
     */
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    /**
     * Returns a list of expenses with a certain tag
     * @param inviteCode the invite code of the event which contains the expenses
     * @param tagName the tag to be checked for
     * @return the list of expenses with this tag
     */
    @GetMapping(path = { "/{inviteCode}/tags/{name}/expenses" })
    public ResponseEntity<List<Expense>> getAllExpensesWithTag(
            @PathVariable("inviteCode") long inviteCode, @PathVariable("name") String tagName) {
        return tagService.getAllExpensesWithTag(inviteCode, tagName);
    }

    /**
     * Gets all tags associated with an Event
     * @param inviteCode the inviteCode of the event to get the tag from
     * @return whether the tags could be retrieved or not
     */
    @GetMapping(path = {"/{inviteCode}/tags"})
    public ResponseEntity<List<Tag>> getTagsFromEvent(@PathVariable("inviteCode") long inviteCode){
        return tagService.getTagsFromEvent(inviteCode);
    }


    /**
     * Adds a new tag to an event
     * @param inviteCode the inviteCode of the event to add the tag to
     * @param tag the tag to be added
     * @return whether the tag could be added to the event
     */
    @PostMapping(path = {"/{inviteCode}/tags"})
    public ResponseEntity<Tag> addNewToEvent(@PathVariable("inviteCode") long inviteCode,
                                      @RequestBody Tag tag){
        return tagService.addNewToEvent(inviteCode, tag);
    }

    /**
     * Changes the name of a tag
     * @param inviteCode the inviteCode of the event with which the tag is associated
     * @param tagId the id of the tag itself
     * @param tag the new tag to be changed to
     * @return whether the tagname was changed
     */
    @PutMapping(path = {"/{inviteCode}/tags/{tagId}"})
    public ResponseEntity<Tag> changeTag(@PathVariable("inviteCode") long inviteCode,
                                          @PathVariable("tagId") long tagId,
                                          @RequestBody Tag tag){
        return tagService.changeTag(inviteCode, tagId, tag);
    }



    /***
     * Deletes a tag from an event/the repo
     * @param inviteCode the event to delete from
     * @param tagId the id of the tag
     * @return whether the tag was deleted or not
     */
    @DeleteMapping(path = {"/{inviteCode}/tags/{tagId}"})
    public ResponseEntity<Tag> deleteTagFromEvent(@PathVariable("inviteCode") long inviteCode,
                                                  @PathVariable("tagId") long tagId){
        return tagService.deleteTagFromEvent(inviteCode, tagId);
    }



    /**
     * Post method to allow an admin to upload new tags
     * @param password string password
     * @param tag the list of tags to be added
     * @return the list of tags if succesfully added
     */
    @PostMapping(path = {"/admin/tag/{password}"})
    public ResponseEntity <Tag> addJsonImport(@PathVariable("password") String password,
                                              @RequestBody Tag tag){
        if (PasswordService.getPassword().equals(password)) {

            if(tagService.validateTag(tag).getStatusCode().equals(OK)){
                tagService.addCreatedTag(tag);
                return ResponseEntity.ok(tag);

            }else{
                return ResponseEntity.badRequest().build();

            }

        } else {
            return ResponseEntity.badRequest().build();
        }
    }


}
