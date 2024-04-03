package client.utils;

import client.scenes.MainCtrl;
import commons.Expense;
import jakarta.ws.rs.WebApplicationException;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;

import java.text.DateFormat;

public class ExpenseListCell extends ListCell<Expense> {
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final ServerUtils server;
    private Label expenseName;
    private Label paidLabel;
    private Label payeeName;
    private Label forLabel;
    private Label price;
    private Label currency;
    private Label date;
    private Label payers;
    private Button edit;
    private Button remove;
    private FlowPane details;
    private VBox vBox;
    private HBox hBox;
    private Region autogrowLeft;
    private Region autogrowRight;

    /**
     * Constructor for the RecentEventCell.
     */
    public ExpenseListCell(MainCtrl mainCtrl,
                           ServerUtils server,
                           LanguageManager languageManager) {
        super();
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.languageManager = languageManager;
    }

    /**
     * Creates the graphic for the expense.
     *
     * @param item - expense item
     */
    private void createGraphic(Expense item) {
        expenseName = new Label();
        paidLabel = new Label();
        paidLabel.textProperty().bind(languageManager.bind("overview.paidLabel"));
        payeeName = new Label();
        forLabel = new Label();
        forLabel.textProperty().bind(languageManager.bind("overview.forLabel"));
        price = new Label();
        currency = new Label();
        date = new Label();
        payers = new Label();
        edit = new Button();
        edit.setText("\uD83D\uDD89");
        edit.setOnAction(param -> {
            mainCtrl.getEditExpenseCtrl().setExpense(item);
            mainCtrl.showEditExpense();
            //System.out.println(item);
        });
        remove = new Button();
        remove.setText("\uD83D\uDDD1");
        remove.setId("cancel");
        remove.setOnAction(param -> {
            try {
                server.removeExpense(mainCtrl.getEvent().getInviteCode(), item.getId());
            } catch (WebApplicationException e) {
                if (mainCtrl.getOverviewCtrl() == null
                        || mainCtrl.getOverviewCtrl().getExpenseSubscriptionMap() == null)
                    return;
                var sub = mainCtrl.getOverviewCtrl().getExpenseSubscriptionMap().get(item);
                if (sub != null)
                    sub.notify();
            }
        });

        autogrowLeft = new Region();
        autogrowRight = new Region();
        details = new FlowPane(payeeName, paidLabel, price, currency, forLabel, expenseName);
        vBox = new VBox(details, payers);
        hBox = new HBox(date, autogrowLeft, vBox, autogrowRight, edit, remove);
        hBox.setSpacing(5);
        vBox.setSpacing(5);
        details.setHgap(3);
        details.setOrientation(Orientation.HORIZONTAL);
        details.setPrefWidth(200);
        payeeName.setStyle("-fx-font-weight: 700;");
        expenseName.setStyle("-fx-font-weight: 700;");
        price.setStyle("-fx-font-weight: 700;");
        currency.setStyle("-fx-font-weight: 700;");
        date.setAlignment(Pos.CENTER);
        edit.setAlignment(Pos.CENTER);
        HBox.setHgrow(autogrowLeft, Priority.ALWAYS);
        HBox.setHgrow(autogrowRight, Priority.ALWAYS);
    }

    /**
     * Updates the item in the list to have the event name and the open and close buttons.
     *
     * @param item  - item in the list
     * @param empty - whether the item is empty or not
     */
    @Override
    protected void updateItem(Expense item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            createGraphic(item);
            update();
        }
    }

    /**
     * Updates the labels and sets the graphic to the HBox.
     */
    private void update() {
        try {
            expenseName.setText(this.getItem().getTitle());
        } catch (NullPointerException e) {
            expenseName.setText("<no title>");
        }
        try {
            payeeName.setText(this.getItem().getPayee().getName());
        } catch (NullPointerException e) {
            payeeName.setText("<no payee>");
        }
        try {
            price.setText(String.format("%.2f", this.getItem().getAmount()));
        } catch (NullPointerException e) {
            price.setText("<no price>");
        } catch (NumberFormatException e) {
            price.setText("<invalid price>");
        }
        try {
            currency.setText(this.getItem().getCurrency());
        } catch (NullPointerException e) {
            currency.setText("<no currency>");
        }
        StringBuilder sb = new StringBuilder();
        if (!this.getItem().getSplit().isEmpty()) {
            sb.append("(");
            for (int i = 0; i < this.getItem().getSplit().size() - 1; i++) {
                sb.append(this.getItem().getSplit().get(i).getParticipant().getName()).append(", ");
            }
            sb.append(this.getItem().getSplit()
                    .get(this.getItem().getSplit().size() - 1).getParticipant().getName());
            sb.append(")");
        } else sb.append("none");
        payers.setText(sb.toString());
        try {
            var dateObj = this.getItem().getDate();
            date.setText(DateFormat.getDateInstance().format(dateObj));
        } catch (NullPointerException e) {
            date.setText("<no date>");
        }
        setGraphic(hBox);
    }
}
