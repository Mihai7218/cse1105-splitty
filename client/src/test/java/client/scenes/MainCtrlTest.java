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

    LanguageManager languageManager;
    MainCtrl sut;
    Stage stage;
    String stageTitle;
    String titleKey;
    StringProperty sp;
    StringBinding sb;
    Stage actualStage;

    @Start
    void setUp(Stage actualStage) {
        this.actualStage = actualStage;
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

    @Test
    void showOverview() {
        sut.showOverview();
        assertEquals("Quotes: Overview", stageTitle);
        verify(stage).setScene(any());
    }

    @Test
    void showStartMenu() {
        sut.showStartMenu();
        assertEquals("startScreen.windowTitle", titleKey);
        assertEquals("Test", sb.get());
        verify(stage).setScene(any());
        verify(stage).titleProperty();
    }

    @Test
    void showAdd() {
        sut.showAdd();
        assertEquals("Quotes: Adding Quote", stageTitle);
        verify(stage).setScene(any());
    }

    @Test
    void showParticipant() {
        sut.showParticipant();
        assertEquals("Add Participant", stageTitle);
        verify(stage).setScene(any());
    }

    @Test
    void getPrimaryStage() {
        assertEquals(stage, sut.getPrimaryStage());
    }

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
        sut.initialize(stage,
                new Pair<>(quoteOverviewCtrl, quoteOverview),
                new Pair<>(addQuoteCtrl, addQuote),
                new Pair<>(startScreenCtrl, startScreen),
                new Pair<>(participantCtrl, participant),
                new Pair<>(overviewCtrl, overview));
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