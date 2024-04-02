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
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;


public class AddExpenseCtrl extends ExpenseCtrl {

    @FXML
    private Button addExpense;

    /**
     *
     * @param mainCtrl
     * @param config
     * @param languageManager
     * @param serverUtils
     * @param alert
     */
    @Inject
    public AddExpenseCtrl(MainCtrl mainCtrl,
                          ConfigInterface config,
                          LanguageManager languageManager,
                          ServerUtils serverUtils,
                          Alert alert) {
        super(mainCtrl, config, languageManager, serverUtils, alert);
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
        refresh();
    }

    /**
     * Refreshes the available participants.
     */
    public void refresh() {
        super.refresh();
        populateParticipantCheckBoxes();
        date.setValue(LocalDate.now());
        everyone.setSelected(true);
    }

    /**
     * Add method for handling "Add" button click event
     */
    @FXML
    public void addExpense() {
        // Gather data from input fields
        String expenseTitle = title.getText();
        String expensePriceText = price.getText();
        LocalDate expenseDate = date.getValue();

        if (!validate(expenseTitle, expensePriceText, expenseDate)) return;
        Expense newExpense;
        try {
            // Parse price to double
            double currPrice = Double.parseDouble(expensePriceText);
            boolean fail = (BigDecimal.valueOf(currPrice).scale() > 2);
            if(fail || currPrice <= 0) throw new NumberFormatException();
            int expensePrice = (int)(currPrice * 100);

            if (expensePrice <= 0) {
                throwAlert("addExpense.smallHeader", "addExpense.smallBody");
                highlightMissing(false, true, false, false, false);
                return;
            }

            // Create a new Expense object
            newExpense = createExpense(expenseTitle, expensePrice, expenseDate);

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
            serverUtils.addExpense(mainCtrl.getEvent().getInviteCode(), newExpense);
        } catch (WebApplicationException e) {
            switch (e.getResponse().getStatus()) {
                case 400 -> {
                    throwAlert("addExpense.badReqHeader", "addExpense.badReqBody");
                }
                case 404 -> {
                    throwAlert("addExpense.notFoundHeader", "addExpense.notFoundBody");
                }
            }
        }
        mainCtrl.showOverview();
        mainCtrl.showExpenseConfirmation();

        // Optionally, clear input fields after adding the expense
        clearFields();
    }

    /**
     * Method to create a new Expense object
     */
    public Expense createExpense(String title, int price, LocalDate date) {
        Tag tag = expenseType.getValue();
        Participant actualPayee = payee.getValue();
        double priceVal = ((double)price)/100;

        List<ParticipantPayment> participantPayments = getParticipantPayments(price, actualPayee);

        return new Expense(priceVal, currency.getValue(), title, "testing",
                java.sql.Date.valueOf(date), participantPayments, tag, actualPayee);
    }

    /**
     * getter for button for testing
     * @param addExpense
     */
    public void setAddExpense(Button addExpense) {
        this.addExpense = addExpense;
    }
}
