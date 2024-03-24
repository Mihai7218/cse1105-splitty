package client.utils;

import client.scenes.MainCtrl;
import commons.Participant;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class ParticipantCell extends ListCell<Participant> {
    private Label participant;
    private Button edit;
    private Region autogrow;
    private Button remove;
    private HBox hBox;
    private MainCtrl mainCtrl;
    /**
     * Constructor for the ParticipantCell.
     */
    public ParticipantCell(MainCtrl mainCtrl) {
        super();
        this.mainCtrl = mainCtrl;
        participant = new Label();
        edit = new Button();
        edit.setText("\uD83D\uDD89");
        edit.setOnAction(param -> {
            mainCtrl.getEditparticipantCtrl().setParticipant(this.getItem());
            mainCtrl.showEditParticipant();
        });
        remove = new Button();
        remove.setText("\uD83D\uDDD1");
        remove.setOnAction(param -> {
            mainCtrl.getOverviewCtrl().removeParticipant(this.getItem());
        });
        autogrow = new Region();
        hBox = new HBox(participant, autogrow,edit, remove);
        hBox.setSpacing(5);
        HBox.setHgrow(autogrow, Priority.ALWAYS);
    }

    /**
     * Updates the item in the list to have the event name and the open and close buttons.
     * @param item - item in the list
     * @param empty - whether the item is empty or not
     */
    @Override
    protected void updateItem(Participant item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            participant.setText(item.getName());
            setGraphic(hBox);
        }
    }
}
