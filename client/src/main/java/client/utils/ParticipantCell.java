package client.utils;

import client.scenes.MainCtrl;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class ParticipantCell extends ListCell<Participant> {
    private Label participant;
    private Button edit;
    private Region autogrow;
    private Button remove;
    private HBox hBox1;
    private HBox hBox2;
    private VBox vBox;
    private MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private Label shareLabel;
    private Label share;
    private Region autogrow2;
    private Region autogrow3;
    private Label oweLabel;
    private Label owe;
    private Label owedLabel;
    private Label owed;


    /**
     * Constructor for the ParticipantCell.
     */
    public ParticipantCell(MainCtrl mainCtrl, LanguageManager languageManager) {
        super();
        this.mainCtrl = mainCtrl;
        this.languageManager = languageManager;
        shareLabel = new Label();
        owe = new Label();
        owed = new Label();
        owedLabel = new Label();
        oweLabel = new Label();
        share = new Label();
        participant = new Label();
        edit = new Button();
        edit.setText("\uD83D\uDD89");
        edit.setOnAction(param -> {
            mainCtrl.getEditparticipantCtrl().setParticipant(this.getItem());
            mainCtrl.showEditParticipant();
        });
        shareLabel.textProperty().bind(this.languageManager.bind("participantCell.shareLabel"));
        oweLabel.textProperty().bind(languageManager.bind("participantCell.owes"));
        owedLabel.textProperty().bind(languageManager.bind("participantCell.owed"));
        remove = new Button();
        remove.setText("\uD83D\uDDD1");
        remove.setId("cancel");
        remove.setOnAction(param -> mainCtrl.getOverviewCtrl().removeParticipant(this.getItem()));
        autogrow = new Region();
        autogrow2 = new Region();
        autogrow3 = new Region();
        autogrow2.setPrefWidth(8);
        autogrow3.setPrefWidth(8);
        hBox1 = new HBox(participant, autogrow, edit, remove);
        hBox1.setSpacing(5);
        hBox1.setAlignment(Pos.CENTER);
        hBox2 = new HBox(shareLabel, share, autogrow3, oweLabel, owe, autogrow2, owedLabel, owed);
        hBox2.setSpacing(5);
        hBox2.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(autogrow, Priority.ALWAYS);
        vBox = new VBox(hBox1, hBox2);

    }

    /**
     * Updates the item in the list to have the event name and the open and close buttons.
     *
     * @param item  - item in the list
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
            participant.setStyle("-fx-font-weight: bold");
            double itemShare = calculateShare(item);
            double itemOwes = calculateOwe(item);
            double itemOwed = calulateOwed(item);
            share.setText(String.format("%.2f", itemShare));
            shareLabel.setStyle("-fx-font-style: italic");
            owe.setText(String.format("%.2f", itemOwes));
            oweLabel.setStyle("-fx-font-style: italic");
            owed.setText(String.format("%.2f", itemOwed));
            owedLabel.setStyle("-fx-font-style: italic");
            if (itemShare < 0) share.setStyle("-fx-text-fill: red");
            else if (itemShare > 0) share.setStyle("-fx-text-fill: green");
            else share.setStyle("-fx-text-fill: black");
            setGraphic(vBox);

        }
    }

    /**
     * Calculates the amount of money a participant owes
     * @param current the participant to calculate for
     * @return double amount owed
     */
    protected double calculateOwe(Participant current){
        double debt = 0;
        List<Expense> expenses = mainCtrl.getEvent().getExpensesList();

        for(Expense expense: expenses){
            if(!expense.getPayee().equals(current)){
                for(ParticipantPayment p: expense.getSplit()){
                    if(p.getParticipant().equals(current)){
                        debt += p.getPaymentAmount();
                    }
                }
            }
        }
        return debt;
    }



    /**
     * calculates the amount a participant is owed
     * @param current participant to calculate the value for
     * @return double value for amount owed
     */
    protected double calulateOwed(Participant current){
        double owed = 0;
        List<Expense> expenses = mainCtrl.getEvent().getExpensesList();
        for(Expense expense: expenses){
            if(expense.getPayee().equals(current)){
                for(ParticipantPayment p : expense.getSplit()){
                    if(!p.getParticipant().equals(current)) {
                        owed += p.getPaymentAmount();
                    }
                }
            }
        }
        return owed;
    }


    /**
     * Calculate the share of the expense that the participant paid
     *
     * @param current participant to calculate the share for
     * @return double of the share of the expense
     */
    protected double calculateShare(Participant current) {
        double participantShare = 0;
        Event curr = mainCtrl.getEvent();
        List<Expense> expenses = curr.getExpensesList();
        for (Expense expense : expenses) {

            for (ParticipantPayment p : expense.getSplit()) {
                if (p.getParticipant().equals(current)
                        && !expense.getPayee().equals(current)) {
                    participantShare -= p.getPaymentAmount();
                } else if (expense.getPayee().equals(current)
                        && !p.getParticipant().equals(current)) {
                    participantShare += p.getPaymentAmount();
                }
            }
        }
        return participantShare;

    }

    /**
     * Setter for tests
     * @param participant gets participant label
     */
    public void setParticipant(Label participant) {
        this.participant = participant;
    }

    /**
     * setter for edit button
     * @param edit button
     */
    public void setEdit(Button edit) {
        this.edit = edit;
    }

    /**
     * setter for autogrow region
     * @param autogrow region
     */
    public void setAutogrow(Region autogrow) {
        this.autogrow = autogrow;
    }

    /**
     * setter for remove button
     * @param remove button
     */
    public void setRemove(Button remove) {
        this.remove = remove;
    }

    /**
     * setter for hbox1
     * @param hBox1 hbox
     */
    public void sethBox1(HBox hBox1) {
        this.hBox1 = hBox1;
    }

    /**
     * setter for hbox2
     * @param hBox2 hbox
     */
    public void sethBox2(HBox hBox2) {
        this.hBox2 = hBox2;
    }

    /**
     * setter for vbox
     * @param vBox vbox
     */
    public void setvBox(VBox vBox) {
        this.vBox = vBox;
    }


    /**
     * setter for share label
     * @param shareLabel label
     */
    public void setShareLabel(Label shareLabel) {
        this.shareLabel = shareLabel;
    }

    /**
     * setter for setShare
     * @param share label
     */
    public void setShare(Label share) {
        this.share = share;
    }

    /**
     * setter for region
     * @param autogrow2 region
     */
    public void setAutogrow2(Region autogrow2) {
        this.autogrow2 = autogrow2;
    }

    /**
     * setter for region
     * @param autogrow3 region
     */
    public void setAutogrow3(Region autogrow3) {
        this.autogrow3 = autogrow3;
    }

    /**
     * setter for owelabel
     * @param oweLabel label
     */
    public void setOweLabel(Label oweLabel) {
        this.oweLabel = oweLabel;
    }

    /**
     * setter for owe label
     * @param owe label
     */
    public void setOwe(Label owe) {
        this.owe = owe;
    }

    /**
     * setter for setOwed label
     * @param owedLabel label
     */
    public void setOwedLabel(Label owedLabel) {
        this.owedLabel = owedLabel;
    }

    /**
     * setter for setowed
     * @param owed label
     */
    public void setOwed(Label owed) {
        this.owed = owed;
    }
}
