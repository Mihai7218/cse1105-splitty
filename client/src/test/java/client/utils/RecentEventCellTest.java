package client.utils;

import client.scenes.MainCtrl;
import client.scenes.StartScreenCtrl;
import commons.Event;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class RecentEventCellTest {

    RecentEventCell sut;
    MainCtrl mainCtrl;
    StartScreenCtrl startScreenCtrl;
    Event event;

    @BeforeEach
    void setUp() {
        mainCtrl = mock(MainCtrl.class);
        startScreenCtrl = mock(StartScreenCtrl.class);
        when(mainCtrl.getStartScreenCtrl()).thenReturn(startScreenCtrl);
        sut = new RecentEventCell(mainCtrl);
    }

    @Test
    void updateItemNull() {
        sut.updateItem(null, false);

        assertNull(sut.getGraphic());
        assertNull(sut.getText());
    }

    @Test
    void updateItemWithEvent() {
        event = new Event("Hello World!", new Date(), new Date());
        sut.updateItem(event, false);

        assertNotNull(sut.getGraphic());

        HBox hBox = (HBox) sut.getGraphic();
        Label label = (Label) hBox.getChildren().stream().filter(x -> x instanceof Label).toList().getFirst();
        Button open = hBox.getChildren().stream().filter(x -> x instanceof Button).map(x -> (Button) x).filter(x -> x.getId().equals("openRecent")).toList().getFirst();
        Button close = hBox.getChildren().stream().filter(x -> x instanceof Button).map(x -> (Button) x).filter(x -> x.getId().equals("closeButton")).toList().getFirst();

        assertNotNull(open);
        assertNotNull(close);
        assertEquals("Hello World!", label.getText());
    }

    @Test
    void openButtonFunctionality() {
        event = new Event("Hello World!", new Date(), new Date());
        sut.updateItem(event, false);

        assertNotNull(sut.getGraphic());

        HBox hBox = (HBox) sut.getGraphic();
        Button open = hBox.getChildren().stream().filter(x -> x instanceof Button).map(x -> (Button) x).filter(x -> x.getId().equals("openRecent")).toList().getFirst();
        Button close = hBox.getChildren().stream().filter(x -> x instanceof Button).map(x -> (Button) x).filter(x -> x.getId().equals("closeButton")).toList().getFirst();

        assertNotNull(open);
        assertNotNull(close);

        open.getOnAction().handle(new ActionEvent());

        verify(startScreenCtrl).addRecentEvent(event);
        verify(mainCtrl).setEvent(event);
        verify(mainCtrl).showOverview();
    }

    @Test
    void closeButtonFunctionality() {
        event = new Event("Hello World!", new Date(), new Date());
        sut.updateItem(event, false);

        assertNotNull(sut.getGraphic());

        HBox hBox = (HBox) sut.getGraphic();
        Button open = hBox.getChildren().stream().filter(x -> x instanceof Button).map(x -> (Button) x).filter(x -> x.getId().equals("openRecent")).toList().getFirst();
        Button close = hBox.getChildren().stream().filter(x -> x instanceof Button).map(x -> (Button) x).filter(x -> x.getId().equals("closeButton")).toList().getFirst();

        assertNotNull(open);
        assertNotNull(close);

        close.getOnAction().handle(new ActionEvent());

        verify(startScreenCtrl).removeRecentEvent(event);
    }
}