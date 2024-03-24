package client.utils;

import client.scenes.MainCtrl;
import commons.Event;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class RecentEventCell extends ListCell<Event> {
    private Label eventName;
    private Button open;
    private Region autogrow;
    private Button close;
    private HBox hBox;
    private MainCtrl mainCtrl;
    /**
     * Constructor for the RecentEventCell.
     */
    public RecentEventCell(MainCtrl mainCtrl) {
        super();
        this.mainCtrl = mainCtrl;
    }

    /**
     * Updates the item in the list to have the event name and the open and close buttons.
     * @param item - item in the list
     * @param empty - whether the item is empty or not
     */
    @Override
    protected void updateItem(Event item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            createGraphic(item);
            eventName.setText(item.getTitle());
            setGraphic(hBox);
        }
    }

    /**
     * Creates the graphic for the recent event.
     * @param item - the event
     */
    private void createGraphic(Event item) {
        eventName = new Label();
        open = new Button();
        open.setText("↗");
        open.setOnAction(param -> {
            mainCtrl.getStartScreenCtrl().addRecentEvent(item);
            mainCtrl.setEvent(item);
            mainCtrl.showOverview();
        });
        close = new Button();
        close.setText("\uD83D\uDDD1");
        close.setOnAction(param -> {
            mainCtrl.getStartScreenCtrl().removeRecentEvent(item);
        });
        autogrow = new Region();
        hBox = new HBox(eventName, open, autogrow, close);
        hBox.setSpacing(5);
        HBox.setHgrow(autogrow, Priority.ALWAYS);
    }
}
