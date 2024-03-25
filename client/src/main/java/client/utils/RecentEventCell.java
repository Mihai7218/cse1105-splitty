package client.utils;

import client.scenes.MainCtrl;
import commons.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

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
        eventName.setFont(new Font(15));
        open = new Button();
        open.setId("openRecent");
        open.setGraphic(new ImageView(new Image("icons/openarrow.png")));
        open.setOnAction(param -> {
            mainCtrl.getStartScreenCtrl().addRecentEvent(item);
            mainCtrl.setEvent(item);
            mainCtrl.showOverview();
        });
        close = new Button();
        close.setText("\uD83D\uDDD1");
        close.setId("closeButton");
        close.setOnAction(param -> {
            mainCtrl.getStartScreenCtrl().removeRecentEvent(item);
        });
        autogrow = new Region();
        hBox = new HBox(eventName, open, autogrow, close);
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(autogrow, Priority.ALWAYS);
    }
}
