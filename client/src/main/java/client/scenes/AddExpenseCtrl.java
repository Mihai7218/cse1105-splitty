package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


public class AddExpenseCtrl extends ExpenseCtrl {


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
                          Alert alert,
                          CurrencyConverter currencyConverter) {

        super(mainCtrl, config, languageManager, serverUtils, alert, currencyConverter);
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
        Date actualDate = java.sql.Date.valueOf(date);
        String currencyString = currency.getValue();
        double priceVal = ((double)price)/100;

        List<ParticipantPayment> participantPayments = getParticipantPayments(price, actualPayee);

        return new Expense(priceVal, currencyString, title, "testing",
                actualDate, participantPayments, tag, actualPayee);
    }

    /**
     * When the shortcut is used it goes back to the startmenu.
     */
    public void startMenu() {
        clearFields();
        mainCtrl.showStartMenu();
    }

    /**
     * getter for button for testing
     * @param addExpense
     */
    public void setAddExpense(Button addExpense) {
        this.addExpense = addExpense;
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     *  - if ENTER is pressed, then it adds the expense.
     *  - if ESCAPE is pressed, then it cancels and returns to the overview.
     *  - if Ctrl + m is pressed, then it returns to the startscreen.
     *  - if Ctrl + o is pressed, then it returns to the overview.
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                addExpense();
                break;
            case ESCAPE:
                abort();
                break;
            case M:
                if(e.isControlDown()){
                    startMenu();
                    break;
                }
            case O:
                if(e.isControlDown()){
                    abort();
                    break;
                }
            default:
                break;
        }
    }
}
