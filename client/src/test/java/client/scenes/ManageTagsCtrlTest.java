package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class ManageTagsCtrlTest {

    //Needed for the tests to run headless.
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    MainCtrl mainCtrl;
    ServerUtils serverUtils;
    ConfigInterface config;
    LanguageManager languageManager;
    ManageTagsCtrl sut;


    StompSession.Subscription tagSubscription;
    Map<Tag, StompSession.Subscription> tagSubscriptionMap;

    Button cancel;
    ListView tagsListView;




    @Start
    void setUp(Stage stage) {

        mainCtrl = mock(MainCtrl.class);
        serverUtils = mock(ServerUtils.class);
        languageManager = mock(LanguageManager.class);
        config = new TestConfig();
        sut = new ManageTagsCtrl(mainCtrl, config, languageManager,
                serverUtils);

        cancel = mock(Button.class);
        tagSubscription = mock(StompSession.Subscription.class);
        tagSubscriptionMap = mock(HashMap.class);
        tagsListView = mock(ListView.class);

        ObservableList<Tag> tagObservableList = FXCollections.observableArrayList();
        when(tagsListView.getItems()).thenReturn(tagObservableList);

        sut.setCancel(cancel);
        sut.setTagSubscription(tagSubscription);
        sut.setTagSubscriptionMap(tagSubscriptionMap);
        sut.setTagsListView(tagsListView);

        doNothing().when(cancel).setGraphic(any(Node.class));
        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
    }

    @Test
    void initialize() {
        assertFalse(cancel.isVisible());
        assertFalse(tagsListView.isVisible());
    }
    @Test
    void onTagUpdateTest() {
        Event test = new Event("test", null, null);
        Tag newTag = new Tag("TAG", "blue");
        newTag.setId(1);
        test.addTag(newTag);
        test.addExpense(new Expense(1, "EUR", "Title", "Desc", null, null, null, null));
        test.addExpense(new Expense(1, "EUR", "Title", "Desc", null, null, newTag, null));
        test.setInviteCode(1);
        when(serverUtils.getEvent(1)).thenReturn(test);
        when(mainCtrl.getEvent()).thenReturn(test);

        ManageTagsCtrl testSut = spy(sut);
        testSut.setTagSubscription(null);
        testSut.setup();

        Event testEvent = testSut.getMainCtrl().getEvent();
        assertEquals(testEvent.getTagsList().size(),1);


        Tag newTag2 = new Tag("TAG2", "blue");
        newTag2.setId(1);
        testSut.onTagUpdate(newTag2);

        assertEquals(testEvent.getTagsList().size(),1);
        assertTrue(testEvent.getTagsList().contains(newTag2));
    }
    @Test
    void onTagReceiveTest() {
        Event test = new Event("test", null, null);
        Tag newTag = new Tag("TAG", "blue");
        newTag.setId(1);
        test.addTag(newTag);
        test.addExpense(new Expense(1, "EUR", "Title", "Desc", null, null, null, null));
        test.addExpense(new Expense(1, "EUR", "Title", "Desc", null, null, newTag, null));
        test.setInviteCode(1);
        when(serverUtils.getEvent(1)).thenReturn(test);
        when(mainCtrl.getEvent()).thenReturn(test);

        ManageTagsCtrl testSut = spy(sut);
        testSut.setTagSubscription(null);
        testSut.setup();

        Event testEvent = testSut.getMainCtrl().getEvent();
        assertEquals(testEvent.getTagsList().size(),1);


        Tag newTag2 = new Tag("TAG2", "blue");
        newTag2.setId(2);
        testSut.onTagReceive(newTag2);

        verify(testSut, atLeastOnce()).subscribeToTag(newTag2);
        assertEquals(testEvent.getTagsList().size(),2);
        assertTrue(testEvent.getTagsList().contains(newTag2));
    }


    @Test
    void keyTestEscape() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.ESCAPE);
        ManageTagsCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, atLeastOnce()).backToStatistics();
    }

    @Test
    void keyTestM() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.M);
        when(ke.isControlDown()).thenReturn(true);
        ManageTagsCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, atLeastOnce()).startMenu();
    }


    @Test
    void languageManagerTest() {
        sut.setLanguageManager(null);
        assertEquals(sut.getLanguageManager(), null);
        assertEquals(sut.languageManagerProperty(), languageManager);
    }


    @Test
    void keyTestO() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.O);
        when(ke.isControlDown()).thenReturn(true);
        ManageTagsCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, atLeastOnce()).backToOverview();
    }

    @Test
    void keyTestAnyThingElse() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.G);
        ManageTagsCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, never()).backToOverview();
        verify(test, never()).startMenu();
    }
}
