package client.scenes;

import client.commands.EditExpenseCommand;
import client.commands.ICommand;
import client.utils.*;
import com.google.inject.Inject;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import org.springframework.messaging.simp.stomp.StompSession;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;


public class EditExpenseCtrl extends ExpenseCtrl {
    @FXML
    private Button done;
    private StompSession.Subscription expenseSubscription;

    /**
     * Constructor for editExpenseController
     * @param mainCtrl main controller
     * @param config client config
     * @param languageManager language manager for language switch
     * @param serverUtils server utils
     * @param alert alerts to throw
     * @param currencyConverter currency converter for foreign currency
     */
    @Inject
    public EditExpenseCtrl(MainCtrl mainCtrl,
                           ConfigInterface config,
                           LanguageManager languageManager,
                           ServerUtils serverUtils,
                           Alert alert,
                           CurrencyConverter currencyConverter) {
        super(mainCtrl, config, languageManager, serverUtils, alert, currencyConverter);
    }

    /**
     * Setter for the expense.
     *
     * @param expense - the new value of the expense.
     */
    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    /**
     * loads page and adds websocket subscriptions
     */
    @Override
    public void load() {
        super.load();
        String dest = "/topic/events/" +
                mainCtrl.getEvent().getInviteCode() + "/expenses/"
                + expense.getId();
        expenseSubscription = serverUtils.registerForMessages(dest, Expense.class,
                exp -> Platform.runLater(() -> {
                    if ("deleted".equals(exp.getDescription())) {
                        throwAlert("editExpense.removedExpenseHeader",
                                "editExpense.removedExpenseBody");
                        exit();
                    }
                }));
    }

    /**
     * loads the fields with data from the expense to be modified
     * yet to be finished, choiceboxes do not work as intended
     */
    public void loadFields() {
        ignoreQuestion = true;
        scrollNames.setVisible(false);
        //System.out.println(expense);
        payee.setValue(expense.getPayee());
        populateParticipantCheckBoxes();
        title.setText(expense.getTitle());
        price.setText(String.valueOf(expense.getAmount()));
        try {
            date.setValue(LocalDate.ofInstant(expense.getDate()
                    .toInstant(), ZoneId.systemDefault()));
        }catch(UnsupportedOperationException e){
            System.out.println("Unsupported Operation.");
        }
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
        ignoreQuestion = false;
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
        } catch (EmptySplitException e) {
            throwAlert("commons.warning", "addExpense.emptyShare");
            return;
        }
        exit();
        mainCtrl.showEditConfirmation();
    }

    /**
     * Method to create a new Expense object
     */
    public void modifyExpense(String title, int price, LocalDate date, Expense expense) {
        Tag tag = expenseType.getValue();
        Participant actualPayee = payee.getValue();

        List<ParticipantPayment> participantPayments = getParticipantPayments(price, actualPayee);

        if (participantPayments != null && participantPayments.stream()
                .allMatch(x -> x.getParticipant().equals(actualPayee))) {
            throw new EmptySplitException();
        }

        ICommand editExpense = new EditExpenseCommand(price / 100.0, currency.getValue(), title,
                java.sql.Date.valueOf(date), participantPayments,
                tag, actualPayee, expense, serverUtils, mainCtrl);
        try {
            editExpense.execute();
            mainCtrl.getOverviewCtrl().addToHistory(editExpense);
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
    }

    /**
     * Undoes the change to the expense and deals with exceptions
     *
     * @param undoCommand the edits to undo
     */
    public void undo(ICommand undoCommand) {
        try {
            undoCommand.undo();
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
    }

    /**
     * When the shortcut is used it goes back to the startmenu.
     */
    public void startMenu() {
        clearFields();
        mainCtrl.showStartMenu();
    }

    /**
     * Exit method. Returns to the overview and unsubscribes.
     */
    @Override
    protected void exit() {
        if (expenseSubscription != null) {
            expenseSubscription.unsubscribe();
            expenseSubscription = null;
        }
        super.exit();
    }

    /**
     * setter for the add button (testing)
     *
     * @param done button for adding expense
     */
    public void setDone(Button done) {
        this.done = done;
    }

    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     * - if ENTER is pressed, then it edits the expense with the current values.
     * - if ESCAPE is pressed, then it cancels and returns to the overview.
     * - if Ctrl + m is pressed, then it returns to the startscreen.
     * - if Ctrl + o is pressed, then it returns to the overview.
     *
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                doneExpense();
                break;
            case ESCAPE:
                abort();
                break;
            case M:
                if (e.isControlDown()) {
                    startMenu();
                    break;
                }
            case O:
                if (e.isControlDown()) {
                    abort();
                    break;
                }
            default:
                break;
        }
    }


}

