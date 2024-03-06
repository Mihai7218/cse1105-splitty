package client.scenes;

import client.utils.LanguageManager;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MainCtrlTest {

    LanguageManager languageManager;
    MainCtrl sut;
    Stage stage;
    String stageTitle;
    String titleKey;
    StringProperty sp;
    StringBinding sb;

    @BeforeEach
    void setUp() {
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
}