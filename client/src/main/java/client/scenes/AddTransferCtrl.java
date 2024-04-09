package client.scenes;

import client.utils.ConfigInterface;
import client.utils.CurrencyConverter;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import commons.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AddTransferCtrl extends ExpenseCtrl implements Initializable  {


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


    @Inject
    public AddTransferCtrl(MainCtrl mainCtrl,
                           ConfigInterface config,
                           LanguageManager languageManager,
                           ServerUtils serverUtils,
                           Alert alert,
                           CurrencyConverter currencyConverter) {
        super(mainCtrl, config, languageManager, serverUtils, alert, currencyConverter);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cancel != null){
            cancel.setGraphic(new ImageView(new Image("icons/cancelwhite.png")));
        }
        if(confirm != null){
            confirm.setGraphic(new ImageView(new Image("icons/savewhite.png")));
        }
        currencyVal.getItems().addAll(currencyConverter.getCurrencies());

        from.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Call your method here
            populateToBox();
        });
        to.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Call your method here
            populateFromBox();
        });
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
        refresh();
    }

    @Override
    public void refresh(){
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null) {
            from.getItems().clear();
            from.getItems().addAll(mainCtrl.getEvent().getParticipantsList());
            to.getItems().clear();
            to.getItems().addAll(mainCtrl.getEvent().getParticipantsList());
        }
        date.setValue(LocalDate.now());

    }

    public void doneTransfer(){
        String title = "Transfer";
        LocalDate transferDate = date.getValue();
        String price = amount.getText();

        if(!validate(price, transferDate)){
            throwAlert("transfer.missingFields", "transfer.missingFieldsBody");
        }
        Expense newExpense;
        try{
            double currPrice = Double.parseDouble(price);
            boolean fail = (BigDecimal.valueOf(currPrice).scale() > 2);
            if (fail || currPrice <= 0) throw new NumberFormatException();
            int expensePrice = (int) (currPrice * 100);

            newExpense = createExpense(title, expensePrice,transferDate);
            clearFields();

        }catch(NumberFormatException e){
            throwAlert("transfer.formatAlert", "transfer.formatAlertBody");
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
        clearFields();
    }

    public Expense createExpense(String title, int price, LocalDate date){
        Participant payee = from.getValue();
        Date actualDate = java.sql.Date.valueOf(date);
        double priceVal = ((double)price)/100;

        ParticipantPayment onlySplit = new ParticipantPayment(to.getValue(), priceVal);
        ParticipantPayment secondSplit = new ParticipantPayment(from.getValue(), priceVal);

        return new Expense(priceVal, currencyVal.getValue(), title, "transfer", actualDate,List.of(onlySplit, secondSplit),
                null, payee);
    }

    @Override
    public void abort(){
        clearFields();
        mainCtrl.showOverview();
    }

    @Override
    public void clearFields(){
        from.setValue(null);
        to.setValue(null);
        amount.clear();
        currencyVal.setValue(null);
        date.setValue(null);
    }

    public void populateToBox(){
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null
        && to.getValue() == null) {
            to.getItems().clear();
            for (Participant participant : mainCtrl.getEvent().getParticipantsList()) {
                if (participant == from.getValue()) continue;
                else {
                    to.getItems().add(participant);
                }
            }
        }
    }

    public void populateFromBox(){
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null
        && from.getValue() == null) {
            from.getItems().clear();
            for (Participant participant : mainCtrl.getEvent().getParticipantsList()) {
                if (participant == to.getValue()) continue;
                else {
                    from.getItems().add(participant);
                }
            }
        }
    }

    public boolean validate(String expensePriceText, LocalDate expenseDate){
        // Perform validation
        return !expensePriceText.isEmpty() && expenseDate != null
                && currencyVal.getValue() != null && to.getValue() != null
                && from.getValue() != null;
    }

}
