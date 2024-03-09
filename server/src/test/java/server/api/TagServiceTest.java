package server.api;

import commons.Event;
import commons.Expense;
import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.EventRepository;
import server.database.TagRepository;

public class TagServiceTest {
    public TestEventRepository eventRepo;
    public TestTagRepository tagRepo;
    public TagService tagService;
    public Event event1;
    public Event event2;
    public Tag tag1;
    public Tag tag2;

    @BeforeEach
    public void setup() {
        eventRepo = new TestEventRepository();
        tagRepo = new TestTagRepository();
        tagService = new TagService(eventRepo, tagRepo);

        event1 = new Event("bowling", null, null);
        event2 = new Event("picnic", null, null);
        eventRepo.save(event1);
        eventRepo.save(event2);

        tag1 = new Tag("food", "#ffffff");
        tag2 = new Tag("drinks", "#ffd1dc");
        tagRepo.save(tag1);
        tagRepo.save(tag2);
    }
}
}
