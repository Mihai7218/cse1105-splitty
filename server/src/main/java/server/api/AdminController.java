package server.api;

import commons.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    /**
     * Constructor for the AdminController
     * @param adminService an instance of a adminService with all the required functions
     */
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Get methode to get all the events on the server
     * @return returns a list of all events on the server
     */
    @GetMapping(path = { "/{password}" })
    public ResponseEntity<List<Event>> get(@PathVariable("password") String password) {
        if (PasswordService.getPassword().equals(password)) {
            return adminService.getAllEvents();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Post method to allow an admin to upload new events
     * @param password string password
     * @param event the list of events to be added
     * @return the list of events if succesfully added
     */
    @PostMapping(path = {"/{password}"})
    public ResponseEntity<Event> addJsonImport(@PathVariable("password") String password,
                                               @RequestBody  Event event){
        if (PasswordService.getPassword().equals(password)) {
            return adminService.addCreatedEvent(event);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }
}
