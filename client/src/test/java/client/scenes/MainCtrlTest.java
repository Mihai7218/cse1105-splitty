package client.scenes;

import client.utils.LanguageManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class MainCtrlTest {

    //Needed for the tests to run headless.
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    LanguageManager languageManager;
    MainCtrl sut;
    Stage stage;
    String stageTitle;
    String titleKey;
    StringProperty sp;
    StringBinding sb;

    @Start
    void setUp(Stage ignored) {
        languageManager = mock(LanguageManager.class);
        sut = new MainCtrl(languageManager);
        stage = mock(Stage.class);
        sb = Bindings.createStringBinding(() -> "Test");
        sp = new SimpleStringProperty();
        when(stage.getTitle()).thenReturn(stageTitle);
        doAnswer(mock -> {
            stageTitle = (String) mock.getArguments()[0];
            return null;
        }).when(stage).setTitle(anyString());
        doAnswer(mock -> {
            titleKey = (String) mock.getArguments()[0];
            return sb;
        }).when(languageManager).bind((String) any());
        when(stage.titleProperty()).thenReturn(sp);
        sut.setPrimaryStage(stage);
    }

    /**
     * Tests that the title of the stage was changed and that there was a scene set.
     */
    @Test
    void showOverview() {
        sut.showOverview();
        assertEquals("Quotes: Overview", stageTitle);
        verify(stage).setScene(any());
    }

    /**
     * Tests that the title of the stage was bound to "startScreen.windowTitle",
     * the value of that stringBinding is "Test", there was a call to setScene on the stage,
     * and that there was a call to stage.titleProperty().
     */
    @Test
    void showStartMenu() {
        sut.showStartMenu();
        assertEquals("startScreen.windowTitle", titleKey);
        assertEquals("Test", sb.get());
        verify(stage).setScene(any());
        verify(stage).titleProperty();
    }

    /**
     * Tests that the title of the stage was changed and that there was a scene set.
     */
    @Test
    void showAdd() {
        sut.showAdd();
        assertEquals("Quotes: Adding Quote", stageTitle);
        verify(stage).setScene(any());
    }

    /**
     * Tests that the title of the stage was changed and that there was a scene set.
     */
    @Test
    void showParticipant() {
        sut.showParticipant();
        assertEquals("Add Participant", stageTitle);
        verify(stage).setScene(any());
    }

    /**
     * Tests that the stage that was set is returned when calling the getter for it.
     */
    @Test
    void getPrimaryStage() {
        assertEquals(stage, sut.getPrimaryStage());
    }

    /**
     * First, this method sets up mocks and spies of the parameters of the initialize method.
     * Then it calls that method, followed by testing whether the values were set in the MainCtrl.
     */
    @Test
    void initialize() {
        QuoteOverviewCtrl quoteOverviewCtrl = mock(QuoteOverviewCtrl.class);
        Parent quoteOverview = spy(Parent.class);
        AddQuoteCtrl addQuoteCtrl = mock(AddQuoteCtrl.class);
        Parent addQuote = spy(Parent.class);
        StartScreenCtrl startScreenCtrl = mock(StartScreenCtrl.class);
        Parent startScreen = spy(Parent.class);
        ParticipantCtrl participantCtrl = mock(ParticipantCtrl.class);
        Parent participant = spy(Parent.class);
        OverviewCtrl overviewCtrl = mock(OverviewCtrl.class);
        Parent overview = spy(Parent.class);

        AddExpenseCtrl addExpenseCtrl = mock(AddExpenseCtrl.class);
        Parent addExpense = spy(Parent.class);


        sut.initialize(stage,
                new Pair<>(quoteOverviewCtrl, quoteOverview),
                new Pair<>(addQuoteCtrl, addQuote),
                new Pair<>(startScreenCtrl, startScreen),
                new Pair<>(participantCtrl, participant),
                new Pair<>(overviewCtrl, overview),
                new Pair<>(addExpenseCtrl, addExpense));
        assertEquals(stage, sut.getPrimaryStage());
        assertEquals(quoteOverviewCtrl, sut.getQuoteOverviewCtrl());
        assertEquals(quoteOverview, sut.getQuoteOverview().getRoot());
        assertEquals(addQuoteCtrl, sut.getAddCtrl());
        assertEquals(addQuote, sut.getAdd().getRoot());
        assertEquals(startScreenCtrl, sut.getStartScreenCtrl());
        assertEquals(startScreen, sut.getStartScreen().getRoot());
        assertEquals(participantCtrl, sut.getParticipantCtrl());
        assertEquals(participant, sut.getParticipant().getRoot());
        assertEquals(overviewCtrl, sut.getOverviewCtrl());
        assertEquals(overview, sut.getOverview().getRoot());
    }
}