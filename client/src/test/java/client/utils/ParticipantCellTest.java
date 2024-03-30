package client.utils;

import client.scenes.MainCtrl;
import com.sun.javafx.binding.SelectBinding;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.Tag;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class ParticipantCellTest {

    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    private Label participant;
    private Button edit;
    private Region autogrow;
    private Button remove;
    private HBox hBox1;
    private HBox hBox2;
    private VBox vBox;
    private MainCtrl mainCtrl;
    private LanguageManager languageManager;
    private Label shareLabel;
    private Label share;
    private Region autogrow2;
    private Region autogrow3;
    private Label oweLabel;
    private Label owe;
    private Label owedLabel;
    private Label owed;
    ParticipantCell sut;
    boolean removed = false;
    boolean edited = false;
    Participant item;

    @Start
    void setUp(Stage stage) {
        item = new Participant("test", null, null, null);
        mainCtrl = mock(MainCtrl.class);
        languageManager = mock(LanguageManager.class);
        participant = new Label();
        edit = mock(Button.class);
        autogrow = mock(Region.class);
        remove = mock(Button.class);
        hBox1 = mock(HBox.class);
        hBox2 = mock(HBox.class);
        vBox = mock(VBox.class);
        shareLabel = mock(Label.class);
        share = new Label();
        autogrow2 = mock(Region.class);
        autogrow3 = mock(Region.class);
        oweLabel = mock(Label.class);
        owe = new Label();
        owedLabel = mock(Label.class);
        owed =new Label();

        doNothing().when(edit).setText(anyString());
        doNothing().when(edit).setOnAction(any());

        StringProperty textProperty = new SimpleStringProperty();
        when(shareLabel.textProperty()).thenReturn(textProperty);
        when(owedLabel.textProperty()).thenReturn(textProperty);
        when(oweLabel.textProperty()).thenReturn(textProperty);

        StringBinding stringBinding = mock(StringBinding.class);
        when(languageManager.bind(anyString())).thenReturn(stringBinding);
       // doNothing().when(shareLabel).textProperty();
        Event e = new Event("title", null, null);
        Expense expense = new Expense(15, "USD", "test",
                "desc", null, new ArrayList<>(), new Tag("a","a"), new Participant("name", null, null, null));
        e.getExpensesList().add(expense);
        when(mainCtrl.getEvent()).thenReturn(e);
        //doNothing().when(oweLabel).textProperty();
       // doNothing().when(owedLabel).textProperty();
        doNothing().when(remove).setText(anyString());
        doNothing().when(remove).setId(anyString());
        doNothing().when(remove).setOnAction(any());
        doNothing().when(autogrow2).setPrefWidth(anyDouble());
        doNothing().when(autogrow3).setPrefWidth(anyDouble());
        doNothing().when(hBox1).setSpacing(anyDouble());
        doNothing().when(hBox1).setAlignment(any());
        doNothing().when(hBox2).setSpacing(anyDouble());
        doNothing().when(hBox2).setAlignment(any());
        doNothing().when(shareLabel).setStyle(any());
        doNothing().when(oweLabel).setStyle(any());
        doNothing().when(owedLabel).setStyle(any());

        sut = new ParticipantCell(mainCtrl, languageManager);

        sut.setParticipant(participant);
        sut.setEdit(edit);
        sut.setAutogrow(autogrow);
        sut.setRemove(remove);
        sut.sethBox1(hBox1);
        sut.sethBox2(hBox2);
        sut.setvBox(vBox);
        sut.setShareLabel(shareLabel);
        sut.setShare(share);
        sut.setAutogrow2(autogrow2);
        sut.setAutogrow3(autogrow3);
        sut.setOweLabel(oweLabel);
        sut.setOwe(owe);
        sut.setOwedLabel(owedLabel);
        sut.setOwed(owed);
    }

    @Test
    public void update(){
        boolean empty = false;
        sut.updateItem(item, empty);
        assertEquals(participant.getText(), item.getName());
        assertEquals(share.getText(), "0.00");
        assertEquals(owe.getText(), "0.00");
        assertEquals(owed.getText(), "0.00");
    }
}