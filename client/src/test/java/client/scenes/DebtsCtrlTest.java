package client.scenes;

import client.utils.*;
import commons.Event;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class DebtsCtrlTest {

    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }
    MainCtrl mainCtrl;
    LanguageManager languageManager;
    ServerUtils serverUtils;
    CurrencyConverter currencyConverter;
    Alert alert;
    MailSender mailSender;
    DebtsCtrl sut;
    ConfigInterface config;
    Stage stage;
    String stageTitle;
    String titleKey;
    StringProperty sp;
    StringBinding sb;

    ObservableList menulist = FXCollections.observableArrayList();
    Label confirmation;
    Accordion menu;
    Button back;

    @Start
    void setUp(Stage ignored) {
        mainCtrl = mock(MainCtrl.class);
        config = new TestConfig();
        languageManager = mock(LanguageManager.class);
        serverUtils = mock(ServerUtils.class);
        currencyConverter = mock(CurrencyConverter.class);
        alert = mock(Alert.class);
        mailSender = mock(MailSender.class);
        confirmation = mock(Label.class);
        menu = mock(Accordion.class);
        back = mock(Button.class);

        sp = new SimpleStringProperty("Hello");
        when(languageManager.bind(anyString())).thenReturn(Bindings.createStringBinding(() -> ""));
        when(alert.contentTextProperty()).thenReturn(sp);
        when(alert.titleProperty()).thenReturn(sp);
        when(alert.headerTextProperty()).thenReturn(sp);

        sut = new DebtsCtrl(mainCtrl,config,languageManager,serverUtils,currencyConverter,alert,mailSender);
        sut.setBack(back);
        sut.setConfirmation(confirmation);
        sut.setMenu(menu);

        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
        when(menu.getPanes()).thenReturn(menulist);
    }

    @Test
    void animation() {
    }

    @Test
    void initialize() {
        assertEquals(menu.getPanes().size(),0);
    }

    @Test
    void refresh() {
    }

    @Test
    void goBack() {
    }

    @Test
    void setTitles() {
    }

    @Test
    void getLanguageManager() {
    }

    @Test
    void getNotificationLabel() {
    }

    @Test
    void languageManagerProperty() {
    }

    @Test
    void startMenu() {
    }

    @Test
    void backToOverview() {
    }

    @Test
    void keyPressed() {
    }
}