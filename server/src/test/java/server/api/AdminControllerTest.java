package server.api;

import commons.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import server.database.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static server.api.PasswordService.setPassword;

public class AdminControllerTest {

    private AdminService serviceStub;
    private AdminController sutStubbed;
    private PasswordService ps;

    public class AdminServiceStub extends AdminService {

        public AdminServiceStub(EventRepository eventRepository,
                                TagRepository tagRepository,
                                ParticipantRepository participantRepository,
                                ParticipantPaymentRepository participantPaymentRepository,
                                ExpenseRepository expenseRepository) {
            super(eventRepository,
                    tagRepository,participantRepository,
                    participantPaymentRepository,expenseRepository);
        }

        public ResponseEntity<List<Event>> getAllEvents() {
            return ResponseEntity.ok().build();
        }

        public ResponseEntity<Event> addCreatedEvent(Event event) {
            return ResponseEntity.ok().build();
        }

    }

    public TestEventRepository repo = new TestEventRepository();

    @BeforeEach
    public void setup() {
        TestEventRepository eventRepo = new TestEventRepository();
        TestTagRepository tagRepo = new TestTagRepository();
        TestParticipantRepository partRepo = new TestParticipantRepository();
        TestParticipantPaymentRepository ppRepo = new TestParticipantPaymentRepository();
        TestExpenseRepository expRepo = new TestExpenseRepository();

        serviceStub = new AdminServiceStub(eventRepo, tagRepo,partRepo,ppRepo,expRepo);
        sutStubbed = new AdminController(serviceStub);

        ps = new PasswordService();
        setPassword("password");
    }

    @Test
    public void getAllPasswordTestWrong() {
        assertEquals(ResponseEntity.badRequest().build(), sutStubbed.get("kip"));
    }

    @Test
    public void getAllPasswordTestCorrect() {
        assertEquals(ResponseEntity.ok().build(), sutStubbed.get("password"));
    }

    @Test
    public void addJsonImportPasswordTestWrong() {
        assertEquals(ResponseEntity.badRequest().build(), sutStubbed.addJsonImport("kip",null));
    }

    @Test
    public void addJsonImportPasswordTestCorrect() {
        assertEquals(ResponseEntity.ok().build(), sutStubbed.addJsonImport("password",null));
    }

}
