package client.scenes;

import client.utils.Config;
import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class ConnectToServerCtrlTest {


    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    private TextField serverAddressField;
    private Button connectButton;

    private MainCtrl mainCtrl;
    ConfigInterface configInterface;
    ServerUtils server;
    private LanguageManager languageManager;

    ConnectToServerCtrl sut;
    StringProperty contentSp;
    StringProperty titleSp;
    StringProperty headerSp;
    Alert alert;

    @Start
    public void setUp(Stage stage){
        server = mock(ServerUtils.class);
        serverAddressField = mock(TextField.class);
        connectButton = mock(Button.class);
        mainCtrl = mock(MainCtrl.class);
        configInterface = mock(Config.class);
        languageManager = mock(LanguageManager.class);
        alert = mock(Alert.class);
        sut = new ConnectToServerCtrl(mainCtrl, configInterface, server, languageManager);
        sut.setConnectButton(connectButton);
        sut.setMainCtrl(mainCtrl);
        sut.setLanguageManager(languageManager);
        sut.setServerAddressField(serverAddressField);
        contentSp = new SimpleStringProperty("Hello");
        titleSp = new SimpleStringProperty("Hello");
        headerSp = new SimpleStringProperty("Hello");
        when(languageManager.bind(anyString())).thenReturn(Bindings.createStringBinding(() -> ""));
        when(alert.contentTextProperty()).thenReturn(contentSp);
        when(alert.titleProperty()).thenReturn(titleSp);
        when(alert.headerTextProperty()).thenReturn(headerSp);
        doNothing().when(alert).setAlertType(any());


    }

    @Test
    public void connectButtonTester(){
        doNothing().when(configInterface).setProperty(anyString(), anyString());

        when(serverAddressField.getText()).thenReturn("value");
        ConnectToServerCtrl spy = spy(sut);
        when(spy.isServerAvailable(anyString())).thenReturn(true);
        doNothing().when(mainCtrl).showStartMenu();
        doNothing().when(server).connectToServer();
        spy.connectButtonHandler();

        verify(server).connectToServer();
        verify(mainCtrl).showStartMenu();
    }

    @Test
    public void testAddConfig(){
        doNothing().when(configInterface).setProperty(anyString(),anyString());
        sut.updateConfigFile("test");
        verify(configInterface).setProperty(anyString(),anyString());
    }

    @Test
    public void testEmptyServer(){
        assertFalse(sut.isServerAvailable(""));
    }

    KeyEvent mockEvent = mock(KeyEvent.class);
    @Test
    void testKeyPressedDefault() {
        ConnectToServerCtrl spies = spy(sut);
        when(mockEvent.getCode()).thenReturn(KeyCode.P);

        spies.keyPressed(mockEvent);

        verify(spies, never()).connectButtonHandler();
    }


}