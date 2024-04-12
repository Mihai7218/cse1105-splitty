package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class EditTagCtrlTest {

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
    EditTagCtrl sut;


    Alert alert;

    Button cancelButton;
    TextField name;
    ColorPicker colorPicker;
    Button changeTag;

    Tag editTag;

    StringProperty sp;


    @Start
    void setUp(Stage stage) {
        mainCtrl = mock(MainCtrl.class);
        serverUtils = mock(ServerUtils.class);
        languageManager = mock(LanguageManager.class);
        config = new TestConfig();
        alert = mock(Alert.class);
        sp = new SimpleStringProperty("Hello");
        when(alert.contentTextProperty()).thenReturn(sp);
        when(alert.titleProperty()).thenReturn(sp);
        when(alert.headerTextProperty()).thenReturn(sp);
        when(languageManager.bind(anyString())).thenReturn(Bindings.createStringBinding(() -> ""));
        sut = new EditTagCtrl(mainCtrl, config, languageManager,
                serverUtils,alert);

        name = mock(TextField.class);
        colorPicker = mock(ColorPicker.class);
        cancelButton = mock(Button.class);
        changeTag = mock(Button.class);

        sut.setCancelButton(cancelButton);
        sut.setName(name);
        sut.setColorPicker(colorPicker);
        sut.setChangeTag(changeTag);
        sut.setTag(new Tag("test","blue"));

        doNothing().when(cancelButton).setGraphic(any(Node.class));
        doNothing().when(changeTag).setGraphic(any(Node.class));
        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
    }

    @Test
    void initialize() {
        assertFalse(cancelButton.isVisible());
        assertFalse(name.isVisible());
    }
//    @Test
//    void onTagReceiveTest() {
//        Event test = new Event("test", null, null);
//        Tag newTag = new Tag("TAG", "blue");
//        newTag.setId(1);
//        test.addTag(newTag);
//        test.addExpense(new Expense(1, "EUR", "Title", "Desc", null, null, null, null));
//        test.addExpense(new Expense(1, "EUR", "Title", "Desc", null, null, newTag, null));
//        test.setInviteCode(1);
//        when(serverUtils.getEvent(1)).thenReturn(test);
//        when(mainCtrl.getEvent()).thenReturn(test);
//
//        ManageTagsCtrl testSut = spy(sut);
//        testSut.setTagSubscription(null);
//        testSut.setup();
//
//        Event testEvent = testSut.getMainCtrl().getEvent();
//        assertEquals(testEvent.getTagsList().size(),1);
//
//
//        Tag newTag2 = new Tag("TAG2", "blue");
//        newTag2.setId(2);
//        testSut.onTagReceive(newTag2);
//
//        verify(testSut, atLeastOnce()).subscribeToTag(newTag2);
//        assertEquals(testEvent.getTagsList().size(),2);
//        assertTrue(testEvent.getTagsList().contains(newTag2));
//    }


    @Test
    void keyTestEscape() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.ESCAPE);
        EditTagCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, atLeastOnce()).abort();
    }

    @Test
    void keyTestM() {
        KeyEvent ke = mock(KeyEvent.class);

        when(ke.getCode()).thenReturn(KeyCode.M);
        when(ke.isControlDown()).thenReturn(true);
        EditTagCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, atLeastOnce()).startMenu();
    }
    @Test
    void keyTestEnter() {
        when(name.getText()).thenReturn("test");
        when(colorPicker.getValue()).thenReturn(Color.valueOf("#ffffff"));
        Event testEvent = new Event();
        testEvent.setInviteCode(1);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
        Tag testTag = new Tag();
        testTag.setId(0);
        KeyEvent ke = mock(KeyEvent.class);
        when(serverUtils.updateTag(1, testTag)).thenReturn(testTag);
        when(ke.getCode()).thenReturn(KeyCode.ENTER);
        sut.keyPressed(ke);
    }
    @Test
    void refreshTest() {
        EditTagCtrl test = spy(sut);
        test.refresh();
        verify(test, atLeastOnce()).clearFields();
        verify(test, atLeastOnce()).loadFields();
    }

    @Test
    void emptyNameTest() {
        EditTagCtrl test = spy(sut);
        when(name.getText()).thenReturn("");
        test.submitTagChanges();
        verify(test, atLeastOnce()).throwAlert(any(),any());
    }

    @Test
    void submitChanges404ErrorTest() {
        EditTagCtrl test = spy(sut);
        when(name.getText()).thenReturn("test");
        when(colorPicker.getValue()).thenReturn(Color.valueOf("#ffffff"));
        WebApplicationException ex = new WebApplicationException(Response.status(404).build());
        Event testEvent = new Event();
        testEvent.setInviteCode(1);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
        Tag testTag = new Tag();
        testTag.setId(0);
        when(serverUtils.updateTag(1, testTag)).thenThrow(ex);
        test.submitTagChanges();
        verify(test, atLeastOnce()).throwAlert(any(),any());
    }

    @Test
    void submitChanges400ErrorTest() {
        EditTagCtrl test = spy(sut);
        when(name.getText()).thenReturn("test");
        when(colorPicker.getValue()).thenReturn(Color.valueOf("#ffffff"));
        WebApplicationException ex = new WebApplicationException(Response.status(400).build());
        Event testEvent = new Event();
        testEvent.setInviteCode(1);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
        Tag testTag = new Tag();
        testTag.setId(0);
        when(serverUtils.updateTag(1, testTag)).thenThrow(ex);
        test.submitTagChanges();
        verify(test, atLeastOnce()).throwAlert(any(),any());
    }
    @Test
    void submitChangesSuccessTest() {
        EditTagCtrl test = spy(sut);
        when(name.getText()).thenReturn("test");
        when(colorPicker.getValue()).thenReturn(Color.valueOf("#ffffff"));
        Event testEvent = new Event();
        testEvent.setInviteCode(1);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
        Tag testTag = new Tag();
        testTag.setId(0);
        when(serverUtils.updateTag(1, testTag)).thenReturn(testTag);
        test.submitTagChanges();
        verify(test, atLeastOnce()).clearFields();
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
        EditTagCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, atLeastOnce()).backToOverview();
    }

    @Test
    void keyTestAnyThingElse() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.G);
        EditTagCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, never()).backToOverview();
        verify(test, never()).startMenu();
    }
}
