package client.scenes;

import client.utils.ConfigInterface;
import client.utils.CurrencyConverter;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;

public class EditTransferCtrl extends ExpenseCtrl implements Initializable {

    @FXML
    private Label header;
    @FXML
    private Label transferFrom;
    @FXML
    private ChoiceBox<Participant> from;
    @FXML
    private Label transferTo;
    @FXML
    private ChoiceBox<Participant> to;
    @FXML
    private Label transferAmount;
    @FXML
    private TextField amount;
    @FXML
    private ChoiceBox<String> currencyVal;
    @FXML
    private Label dateLabel;
    @FXML
    private DatePicker date;
    @FXML
    private Button cancel;
    @FXML
    private Button confirm;

    /**
     * @param mainCtrl
     * @param config
     * @param languageManager
     * @param serverUtils
     * @param alert
     * @param currencyConverter
     */
    @Inject
    public EditTransferCtrl(MainCtrl mainCtrl,
                            ConfigInterface config,
                            LanguageManager languageManager,
                            ServerUtils serverUtils, Alert alert,
                            CurrencyConverter currencyConverter) {
        super(mainCtrl, config, languageManager, serverUtils, alert, currencyConverter);
    }

    /**
     * Initalizes the transfer ctrl
     * @param url
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (cancel != null){
            cancel.setGraphic(new ImageView(new Image("icons/cancelwhite.png")));
        }
        if(confirm != null){
            confirm.setGraphic(new ImageView(new Image("icons/savewhite.png")));
        }
        currencyVal.getItems().addAll(currencyConverter.getCurrencies());

        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null) {
            for (Participant p : mainCtrl.getEvent().getParticipantsList()) {
                if (p.equals(to.getValue())) {
                    to.getItems().add(p);
                } else if (p.equals(from.getValue())) {
                    from.getItems().add(p);
                } else {
                    to.getItems().add(p);
                    from.getItems().add(p);
                }
            }
        }
        from.setConverter(new StringConverter<>() {
            @Override
            public String toString(Participant participant) {
                if (participant == null) return "";
                return participant.getName();
            }

            @Override
            public Participant fromString(String s) {
                return null;
            }
        });

        to.setConverter(new StringConverter<>() {
            @Override
            public String toString(Participant participant) {
                if (participant == null) return "";
                return participant.getName();
            }

            @Override
            public Participant fromString(String s) {
                return null;
            }
        });
    }

    /**
     * Setter for the expense.
     * @param expense - the new value of the expense.
     */
    public void setExpense(Expense expense) {
        this.expense = expense;
    }


    /**
     * Refreshes the scene
     */
    @Override
    public void refresh(){
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null) {
            from.getItems().clear();
            from.getItems().addAll(mainCtrl.getEvent().getParticipantsList());
            to.getItems().clear();
            to.getItems().addAll(mainCtrl.getEvent().getParticipantsList());
        }
        from.setValue(expense.getPayee());
        to.setValue(expense.getSplit().stream().filter(item ->
                !item.getParticipant().equals(expense.getPayee()))
                .toList().getFirst().getParticipant());
        amount.setText(String.valueOf(expense.getAmount()));
        currencyVal.setValue(expense.getCurrency());
        date.setValue(LocalDate.ofInstant(expense.getDate().toInstant(), ZoneId.systemDefault()));
    }

    /**
     * Attempt to add the transfer to the expenses
     */
    public void doneTransfer(){
        String expensePriceText = amount.getText();
        LocalDate expenseDate = date.getValue();

        if(!validate(expensePriceText,expenseDate)) {
            throwAlert("transfer.missingFields", "transfer.missingFieldsBody");
            return;
        }else if(from.getValue().equals(to.getValue())){
            throwAlert("transfer.sameFields","transfer.sameFieldsBody");
            return;
        }

        try{
            double currPrice = Double.parseDouble(expensePriceText);
            boolean fail = (BigDecimal.valueOf(currPrice).scale() > 2);
            if (fail || currPrice <= 0) throw new NumberFormatException();
            int expensePrice = (int) (currPrice * 100);

            ParticipantPayment onlySplit = new ParticipantPayment(to.getValue(), currPrice);
            ParticipantPayment secondSplit = new ParticipantPayment(from.getValue(), currPrice);

            modifyTransfer((double) expensePrice, expenseDate, onlySplit, secondSplit);

            clearFields();

        }catch(NumberFormatException e){
            throwAlert("transfer.formatAlert", "transfer.formatAlertBody");
            return;
        }

        try {
            serverUtils.updateExpense(mainCtrl.getEvent().getInviteCode(), expense);
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
        clearFields();
    }

    /**
     * Modifies the transfer
     * @param expensePrice
     * @param expenseDate
     * @param onlySplit
     * @param secondSplit
     */
    public void modifyTransfer(double expensePrice, LocalDate expenseDate,
                               ParticipantPayment onlySplit, ParticipantPayment secondSplit) {
        expense.setPayee(from.getValue());
        expense.setAmount(expensePrice /100);
        expense.setCurrency(currencyVal.getValue());
        expense.setDate(java.sql.Date.valueOf(expenseDate));
        expense.setSplit(List.of(onlySplit, secondSplit));
    }

    /**
     * Cancels adding the transfer
     */
    public void cancel(){
        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * removes all values in the fields
     */
    @Override
    public void clearFields(){
        from.setValue(null);
        to.setValue(null);
        amount.clear();
        currencyVal.setValue(null);
        date.setValue(null);
    }

    /**
     * Validation method to check that the transfer is correctly filled in
     * @param expensePriceText amount to transfer
     * @param expenseDate date of transfer
     * @return true of transfer is valid
     */
    public boolean validate(String expensePriceText, LocalDate expenseDate){
        // Perform validation
        return !expensePriceText.isEmpty() && expenseDate != null
                && currencyVal.getValue() != null && to.getValue() != null
                && from.getValue() != null;
    }
}
