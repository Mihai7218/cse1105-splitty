package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;


public class EditExpenseCtrl extends ExpenseCtrl {
    @FXML
    private Button done;

    /**
     * @param mainCtrl
     * @param config
     * @param languageManager
     * @param serverUtils
     * @param alert
     */
    @Inject
    public EditExpenseCtrl(MainCtrl mainCtrl,
                           ConfigInterface config,
                           LanguageManager languageManager,
                           ServerUtils serverUtils,
                           Alert alert) {
        super(mainCtrl, config, languageManager, serverUtils, alert);
    }

    /**
     * Setter for the expense.
     * @param expense - the new value of the expense.
     */
    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    /**
     * loads the fields with data from the expense to be modified
     * yet to be finished, choiceboxes do not work as intended
     */
    public void loadFields() {
        scrollNames.setVisible(false);
        //System.out.println(expense);
        payee.setValue(expense.getPayee());
        populateParticipantCheckBoxes();
        title.setText(expense.getTitle());
        price.setText(String.valueOf(expense.getAmount()));
        date.setValue(LocalDate.ofInstant(expense.getDate().toInstant(), ZoneId.systemDefault()));
        if (!checkAllSelected()) {
            only.setSelected(true);
            onlyCheck();
        } else {
            everyoneCheck();
        }
        expenseType.getItems().clear();
        expenseType.getItems().addAll(mainCtrl.getEvent().getTagsList());
        expenseType.setValue(expense.getTag());
        currency.setValue(expense.getCurrency());
    }

    /**
     * @param url            The location used to resolve relative paths for the root object, or
     *                       {@code null} if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or {@code null} if
     *                       the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        //refresh();
    }

    /**
     * Creates the view for the expense type.
     */

    /**
     * Refreshes the available participants.
     */
    public void refresh() {
        super.refresh();
        loadFields();
    }

    /**
     * Add method for handling "Add" button click event
     */
    @FXML
    public void doneExpense() {
        // Gather data from input fields
        String expenseTitle = title.getText();
        String expensePriceText = price.getText();
        LocalDate expenseDate = date.getValue();

        if (!validate(expenseTitle, expensePriceText, expenseDate)) return;
        try {
            // Parse price to double
            double currPrice = Double.parseDouble(expensePriceText);
            boolean fail = (BigDecimal.valueOf(currPrice).scale() > 2);
            if (fail || currPrice <= 0) throw new NumberFormatException();
            int expensePrice = (int) (currPrice * 100);

            if (expensePrice <= 0) {
                throwAlert("addExpense.smallHeader", "addExpense.smallBody");
                highlightMissing(false, true, false, false, false);
                return;
            }

            // Create a new Expense object
            modifyExpense(expenseTitle, expensePrice, expenseDate, expense);

            // Optionally, clear input fields after adding the expense
            removeHighlight();
            clearFields();
        } catch (NumberFormatException e) {
            // Display an alert informing the user about incorrect price format
            throwAlert("addExpense.invalidHeader", "addExpense.invalidBody");
            highlightMissing(false, true, false, false, false);
            return;
        }
        try {
            serverUtils.updateExpense(mainCtrl.getEvent().getInviteCode(), expense);
        } catch (WebApplicationException e) {
            switch (e.getResponse().getStatus()) {
                case 400 -> {
                    throwAlert("addExpense.badReqHeader",
                            "addExpense.badReqBody");
                }
                case 404 -> {
                    throwAlert("addExpense.notFoundHeader",
                            "addExpense.notFoundBody");
                }
            }
        }
        mainCtrl.showOverview();
        mainCtrl.showEditConfirmation();

        // Optionally, clear input fields after adding the expense
        clearFields();
    }

    /**
     * Method to create a new Expense object
     */

    public void modifyExpense(String title, int price, LocalDate date, Expense expense) {
        Tag tag = expenseType.getValue();
        Participant actualPayee = payee.getValue();

        List<ParticipantPayment> participantPayments = getParticipantPayments(price, actualPayee);

//       new Expense(price, currency.getValue(), title, "testing",
//                java.sql.Date.valueOf(date), participantPayments, tag, actualPayee);

        expense.setTitle(title);
        expense.setAmount(price / 100.0);
        expense.setCurrency(currency.getValue());
        expense.setDate(java.sql.Date.valueOf(date));
        expense.setSplit(participantPayments);
        expense.setTag(tag);
        expense.setPayee(actualPayee);
    }

    /**
     * setter for the add button (testing)
     *
     * @param done button for adding expense
     */
    public void setDone(Button done) {
        this.done = done;
    }
}

