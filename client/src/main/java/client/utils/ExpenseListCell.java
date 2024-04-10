package client.utils;

import client.scenes.MainCtrl;
import commons.Expense;
import commons.ParticipantPayment;
import jakarta.ws.rs.WebApplicationException;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.text.DateFormat;

public class ExpenseListCell extends ListCell<Expense> {
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final CurrencyConverter currencyConverter;
    private final ConfigInterface config;
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
    private VBox vBox2;
    private HBox hBox;
    private Region autogrowLeft;
    private Region autogrowRight;
    private Text tagName;
    private javafx.scene.shape.Rectangle rectangle2;

    /**
     * Constructor for the RecentEventCell.
     */
    public ExpenseListCell(MainCtrl mainCtrl,
                           LanguageManager languageManager,
                           CurrencyConverter currencyConverter,
                           ConfigInterface config,
                           ServerUtils server) {
        super();
        this.mainCtrl = mainCtrl;
        this.server = server;
        this.languageManager = languageManager;
        this.currencyConverter = currencyConverter;
        this.config = config;
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
        rectangle2 = new javafx.scene.shape.Rectangle(100, 20);

        tagName = new Text();
        rectangle2.setStroke(Color.BLACK);
        rectangle2.setStrokeWidth(1);
        StackPane stackPane = new StackPane(rectangle2, tagName);
        vBox = new VBox(details, payers);
        vBox2 = new VBox(date,stackPane);
        hBox = new HBox(vBox2, autogrowLeft, vBox, autogrowRight, edit, remove);
        hBox.setSpacing(5);
        vBox.setSpacing(5);
        details.setHgap(3);
        details.setOrientation(Orientation.HORIZONTAL);
        details.setPrefWidth(350);
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
            if(this.getItem().getDescription().equals("transfer")){
                createTransfer(item);
                updateTransfer();
            }else {
                createGraphic(item);
                update();
            }
        }
    }

    /**
     * Method to style the transfer in the expense cell
     * @param item the transfer
     */
    private void createTransfer(Expense item) {
        expenseName = new Label();
        paidLabel = new Label();
        paidLabel.textProperty().bind(languageManager.bind("overview.transfered"));
        payeeName = new Label();
        forLabel = new Label();
        forLabel.textProperty().bind(languageManager.bind("overview.toLabel"));
        price = new Label();
        currency = new Label();
        date = new Label();
        payers = new Label();
        edit = new Button();
        edit.setText("\uD83D\uDD89");
        edit.setOnAction(param -> {
            if(item.getDescription().equals("transfer")){
                mainCtrl.getEditTransferCtrl().setExpense(item);
                mainCtrl.showEditTransfer();
            }else {
                mainCtrl.getEditExpenseCtrl().setExpense(item);
                mainCtrl.showEditExpense();
            }
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

        tagName = new Text();

        vBox = new VBox(details, payers);
        vBox2 = new VBox(date,tagName);
        vBox2.setPrefWidth(100);
        hBox = new HBox(vBox2, autogrowLeft, vBox, autogrowRight, edit, remove);
        hBox.setSpacing(5);
        vBox.setSpacing(5);
        details.setHgap(3);
        details.setOrientation(Orientation.HORIZONTAL);
        details.setPrefWidth(350);
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
     * Updates the cell according to transfer specific styling
     */
    private void updateTransfer(){
        boolean setCurrency = false;
        try {
            for(ParticipantPayment p : this.getItem().getSplit()){
                if(!p.getParticipant().equals(this.getItem().getPayee())){
                    expenseName.setText(p.getParticipant().getName());
                }
            }
        } catch (NullPointerException e) {
            expenseName.setText("<no receiver>");
        }
        try {
            payeeName.setText(this.getItem().getPayee().getName());
        } catch (NullPointerException e) {
            payeeName.setText("<no payee>");
        }

        tagName.textProperty().bind(languageManager.bind("overview.transfer"));
        tagName.setFill(Paint.valueOf("black"));
        tagName.setStyle("-fx-font-weight: bold");

        String currencyString = config.getProperty("currency");
        if (currencyString == null || currencyString.isEmpty()) currencyString = "EUR";
        try {
            double amountInEUR = this.getItem().getAmount();
            double converted = currencyConverter.convert(
                    this.getItem().getDate(),
                    this.getItem().getCurrency(),
                    currencyString,
                    amountInEUR);
            price.setText(String.format("%.2f", converted));
        }
        catch (NullPointerException e) {
            price.setText("<no price>");
        } catch (NumberFormatException e) {
            price.setText("<invalid price>");
        }
        catch (CouldNotConvertException e) {
            price.setText(Double.toString(this.getItem().getAmount()));
            setCurrency = true;
        }
        try {
            if (setCurrency) {
                currency.setText(this.getItem().getCurrency());
            } else {
                currency.setText(currencyString);
            }
        }
        catch (NullPointerException e) {
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

    /**
     * Updates the labels and sets the graphic to the HBox.
     */
    private void update() {
        boolean setCurrency = false;
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
            tagName.setText(this.getItem().getTag().getName());
            Color tagColor = Color.web(this.getItem().getTag().getColor());
            if (0.2126 * tagColor.getRed() + 0.7152 * tagColor.getGreen()
                    + 0.0722* tagColor.getBlue()<0.5) {
                tagName.setFill(Color.color(1.0,1.0,1.0));
            } else {
                tagName.setFill(Color.color(0.0,0.0,0.0));
            }
            rectangle2.setFill(Paint.valueOf(this.getItem().getTag().getColor()));
        } catch (NullPointerException e) {
            rectangle2.setFill(Paint.valueOf("#ffffff"));
            tagName.setText("<no tag>");
        }
        String currencyString = config.getProperty("currency");
        if (currencyString == null || currencyString.isEmpty()) currencyString = "EUR";
        try {
            double amountInEUR = this.getItem().getAmount();
            double converted = currencyConverter.convert(
                    this.getItem().getDate(),
                    this.getItem().getCurrency(),
                    currencyString,
                    amountInEUR);
            price.setText(String.format("%.2f", converted));
        }
        catch (NullPointerException e) {
            price.setText("<no price>");
        } catch (NumberFormatException e) {
            price.setText("<invalid price>");
        }
        catch (CouldNotConvertException e) {
            price.setText(Double.toString(this.getItem().getAmount()));
            setCurrency = true;
        }
        try {
            if (setCurrency) {
                currency.setText(this.getItem().getCurrency());
            } else {
                currency.setText(currencyString);
            }
        }
        catch (NullPointerException e) {
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
