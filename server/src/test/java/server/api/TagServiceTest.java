package server.api;

import commons.Expense;
import org.junit.jupiter.api.BeforeEach;
import server.database.EventRepository;
import server.database.TagRepository;

public class TagServiceTest {
    public TestEventRepository eventRepo;
    public TestTagRepository tagRepo;
    public TagService tagService;
    public Expense expense1;
    @BeforeEach
    public void setup(){
        eventRepo = new TestEventRepository();
        tagRepo = new TestTagRepository();
        tagService = new TagService(eventRepo, tagRepo);

        //TODO: add payee and tag
        expense1 = new Expense(500, "eur", "food", null, null, null, null, null);
    }

    //TODO
}
