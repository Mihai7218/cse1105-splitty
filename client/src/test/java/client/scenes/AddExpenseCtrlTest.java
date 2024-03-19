package client.scenes;

import client.utils.ServerUtils;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(ApplicationExtension.class)
public class AddExpenseCtrlTest {

    //Needed for the tests to run headless.
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless","true");
    }

    MainCtrl mainCtrl;
    ServerUtils serverUtils;
    AddExpenseCtrl sut;
    ChoiceBox<String> payee;
    ChoiceBox<String> currency;
    ComboBox<String> expenseType;
    CheckBox everyone;
    CheckBox only;
    VBox namesContainer;
    Label question;
    TextField title;
    TextField price;
    DatePicker date;
    ObservableList op = FXCollections.observableArrayList();
    ObservableList oc = FXCollections.observableArrayList();
    ObservableList ot = FXCollections.observableArrayList();
    ObservableList noc = FXCollections.observableArrayList();


    @Start
    void setUp(Stage stage) {
        mainCtrl = mock(MainCtrl.class);
        serverUtils = mock(ServerUtils.class);
        sut = new AddExpenseCtrl(serverUtils,mainCtrl);
        payee = mock(ChoiceBox.class);
        currency = mock(ChoiceBox.class);
        expenseType = mock(ComboBox.class);
        everyone = mock(CheckBox.class);
        only = mock(CheckBox.class);
        namesContainer = mock(VBox.class);
        question = mock(Label.class);
        title = mock(TextField.class);
        price = mock(TextField.class);
        date = mock(DatePicker.class);

        sut.setPayee(payee);
        sut.setCurrency(currency);
        sut.setExpenseType(expenseType);
        sut.setEveryone(everyone);
        sut.setOnly(only);
        sut.setNamesContainer(namesContainer);
        sut.setQuestion(question);
        sut.setTitle(title);
        sut.setPrice(price);
        sut.setDate(date);

        when(payee.getItems()).thenReturn(op);
        when(currency.getItems()).thenReturn(oc);
        when(expenseType.getItems()).thenReturn(ot);
        when(namesContainer.getChildren()).thenReturn(noc);

        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
    }
    public void addPeople(ObservableList noc){
        noc.add(0, "John");
        noc.add(1, "Heather");
    }
    
    @Test
    void initialize() {
        assertFalse(question.isVisible());
        assertNotNull(sut.getNames());
        assertNotNull(sut.getCurrencies());
        assertNotNull(sut.getTags());
        assertEquals(0, namesContainer.getChildren().size());
    }

    @Test
    void chooseOne() {
        everyone.setSelected(true);
        sut.chooseOne();
        assertFalse(only.isSelected());
        assertTrue(namesContainer.getChildren().isEmpty());

        everyone.setSelected(false);
        only.setSelected(true);
        sut.chooseOne();
        assertFalse(everyone.isSelected());
        if(everyone.isSelected()) noc.clear();
        else addPeople(noc);
        assertFalse(namesContainer.getChildren().isEmpty());
    }
}
