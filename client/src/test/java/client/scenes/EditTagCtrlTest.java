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
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.Optional;
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
    Alert confirmation;

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
        confirmation = mock(Alert.class);

        sut.setCancelButton(cancelButton);
        sut.setName(name);
        sut.setColorPicker(colorPicker);
        sut.setChangeTag(changeTag);
        sut.setConfirmation(confirmation);
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


    @Test
    void keyTestEscape() {
        Optional<ButtonType> buttonType = Optional.of(ButtonType.OK);
        when(confirmation.showAndWait()).thenReturn(buttonType);
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
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.ENTER);
        test.keyPressed(ke);
        verify(test, atLeastOnce()).submitTagChanges();
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
