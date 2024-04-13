package client.utils;

import client.scenes.MainCtrl;
import client.scenes.TestConfig;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import javafx.beans.binding.StringBinding;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class TagCellTest {

    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    private MainCtrl mainCtrl;
    private LanguageManager languageManager;
    private ConfigInterface config;
    private ServerUtils server;
    private Label tagColor;
    private Label tagName;
    private Button edit;
    private Button remove;
    private FlowPane details;
    private HBox hBox;

    private VBox vBox;
    private Region autogrowRight;

    private Rectangle rectangle;
    Tag item;

    TagListCell sut;

    @Start
    void setUp(Stage stage) {
        item = new Tag("test","#00ff00");
        mainCtrl = mock(MainCtrl.class);
        languageManager = mock(LanguageManager.class);
        edit = mock(Button.class);
        remove = mock(Button.class);
        hBox = mock(HBox.class);
        vBox = mock(VBox.class);
        tagColor = new Label();
        tagName = new Label();
        details = mock(FlowPane.class);
        autogrowRight = mock(Region.class);
        rectangle = mock(Rectangle.class);
        server = mock(ServerUtils.class);

        doNothing().when(edit).setText(anyString());
        doNothing().when(edit).setOnAction(any());


        StringBinding stringBinding = mock(StringBinding.class);
        when(languageManager.bind(anyString())).thenReturn(stringBinding);
        // doNothing().when(shareLabel).textProperty();
        Event e = new Event("title", null, null);
        Expense expense = new Expense(15, "USD", "test",
                "desc", null, new ArrayList<>(), new Tag("a", "a"),
                new Participant("name", null, null, null));
        e.getExpensesList().add(expense);
        when(mainCtrl.getEvent()).thenReturn(e);
        doNothing().when(remove).setText(anyString());
        doNothing().when(remove).setId(anyString());
        doNothing().when(remove).setOnAction(any());

        sut = new TagListCell(mainCtrl, languageManager, new TestConfig(), server);

        sut.setAutogrowRight(autogrowRight);
        sut.sethBox(hBox);
        sut.setEdit(edit);
        sut.setDetails(details);
        sut.setvBox(vBox);
        sut.setRectangle(rectangle);
        sut.setRemove(remove);
        sut.setLanguageManager(languageManager);
        sut.setTagName(tagName);
    }

    @Test
    public void update() {
        boolean empty = false;
        sut.updateItem(item, empty);
        //assertEquals(tagName.getText(), item.getName());
        String ewa = tagName.getText();
    }
    @Test
    public void updateEmpty() {
        boolean empty = true;
        sut.updateItem(item, empty);
        //assertEquals(tagName.getText(), item.getName());
        var ewa = tagName.getText();
    }
}