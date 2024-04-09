package server.api;

import commons.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    private final String adminPassword;

    /**
     * @param adminService
     */
    @Autowired
    public AdminController(AdminService adminService, @Qualifier("adminPassword") String adminPassword) {
        this.adminService = adminService;
        this.adminPassword = adminPassword;
    }

    /**
     * Get methode to get all the events on the server
     * @return returns a list of all events on the server
     */
    @GetMapping(path = { "/{password}" })
    public ResponseEntity<List<Event>> get(@PathVariable("password") String password) {
        if (adminPassword.equals(password)) {
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
        if (adminPassword.equals(password)) {
            return adminService.addCreatedEvent(event);
        }else{
            return ResponseEntity.badRequest().build();
        }
    }
}
