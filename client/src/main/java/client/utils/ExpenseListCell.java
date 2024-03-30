package client.utils;

import client.scenes.MainCtrl;
import commons.Expense;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;

public class ExpenseListCell extends ListCell<Expense> {
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final CurrencyConverter currencyConverter;
    private final ConfigInterface config;
    private Label expenseName;
    private Label paidLabel;
    private Label payeeName;
    private Label forLabel;
    private Label price;
    private Label currency;
    private Label date;
    private Label payers;
    private Button edit;
    private FlowPane details;
    private VBox vBox;
    private HBox hBox;
    private Region autogrowLeft;
    private Region autogrowRight;

    /**
     * Constructor for the RecentEventCell.
     */
    public ExpenseListCell(MainCtrl mainCtrl,
                           LanguageManager languageManager,
                           CurrencyConverter currencyConverter,
                           ConfigInterface config) {
        super();
        this.mainCtrl = mainCtrl;
        this.languageManager = languageManager;
        this.currencyConverter = currencyConverter;
        this.config = config;
    }

    /**
     * Creates the graphic for the expense.
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
        edit.setText("✎");
        edit.setOnAction(param -> {
            //TODO: add edit expense functionality
        });
        autogrowLeft = new Region();
        autogrowRight = new Region();
        details = new FlowPane(payeeName, paidLabel, price, currency, forLabel, expenseName);
        vBox = new VBox(details, payers);
        hBox = new HBox(date, autogrowLeft, vBox, autogrowRight, edit);
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
     * @param item - item in the list
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
        boolean setCurrency = false;
        try {
            expenseName.setText(this.getItem().getTitle());
        }
        catch (NullPointerException e) {
            expenseName.setText("<no title>");
        }
        try {
            payeeName.setText(this.getItem().getPayee().getName());
        }
        catch (NullPointerException e) {
            payeeName.setText("<no payee>");
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
        }
        catch (NumberFormatException e) {
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
        }
        else {
            sb.append("none");
        }
        payers.setText(sb.toString());
        try {
            date.setText(String.valueOf(this.getItem().getDate()));
        }
        catch (NullPointerException e) {
            date.setText("<no date>");
        }
        setGraphic(hBox);
    }
}
