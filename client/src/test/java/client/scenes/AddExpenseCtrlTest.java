package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Participant;
import commons.Tag;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;

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
    ConfigInterface config;
    LanguageManager languageManager;
    Alert alert;
    AddExpenseCtrl sut;
    ChoiceBox<Participant> payee;
    ChoiceBox<String> currency;
    ComboBox<Tag> expenseType;
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
    ScrollPane scrollNames;
    Button addTag;
    TextField newTag;
    Label instructions;
    Button cancelButton;
    Button add;


    @Start
    void setUp(Stage stage) {
        mainCtrl = mock(MainCtrl.class);
        serverUtils = mock(ServerUtils.class);
        languageManager = mock(LanguageManager.class);
        sut = new AddExpenseCtrl(mainCtrl, config, languageManager, serverUtils, alert);
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
        scrollNames = mock(ScrollPane.class);
        addTag = mock(Button.class);
        newTag = mock(TextField.class);
        instructions = mock(Label.class);
        cancelButton=mock(Button.class);
        add = mock(Button.class);

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
        sut.setScrollNames(scrollNames);
        sut.setAddTag(addTag);
        sut.setNewTag(newTag);
        sut.setInstructions(instructions);
        sut.setAdd(add);
        sut.setCancelButton(cancelButton);

        doNothing().when(add).setGraphic(any(Node.class));
        doNothing().when(cancelButton).setGraphic(any(Node.class));
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
        assertNotNull(sut.getNamesContainer());
        assertNotNull(sut.getCurrencies());
        assertNotNull(sut.getTags());
        assertEquals(0, namesContainer.getChildren().size());
    }

    @Test
    void chooseOne() {
        everyone.setSelected(true);
        sut.everyoneCheck();
        assertFalse(only.isSelected());
        assertTrue(namesContainer.getChildren().isEmpty());

        everyone.setSelected(false);
        only.setSelected(true);
        sut.onlyCheck();
        assertFalse(everyone.isSelected());
        if(everyone.isSelected()) noc.clear();
        else addPeople(noc);
        assertFalse(namesContainer.getChildren().isEmpty());
    }
}
