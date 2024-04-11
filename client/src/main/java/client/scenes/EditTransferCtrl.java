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
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import org.springframework.messaging.simp.stomp.StompSession;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    protected StompSession.Subscription participantSubscription;
    protected Map<Participant, StompSession.Subscription> participantSubscriptionMap;
    protected StompSession.Subscription transferSubscription;

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
        participantSubscriptionMap = new HashMap<>();
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
        try {
            date.setValue(LocalDate.ofInstant(expense.getDate().toInstant(),
                    ZoneId.systemDefault()));
        }catch(UnsupportedOperationException e){
            System.out.println("oops");
        }
        load();
    }


    /**
     * Adds websocket subscriptions to participants and adds new participants
     * to dropdown options
     */
    @Override
    public void load(){
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null){
            for(Participant p: mainCtrl.getEvent().getParticipantsList()){
                subscribeToParticipant(p);
            }
            if(participantSubscription == null){
                participantSubscription = serverUtils.registerForMessages("/topic/events/" +
                                mainCtrl.getEvent().getInviteCode()
                                + "/participants", Participant.class,
                        participant -> Platform.runLater(() ->{
                            to.getItems().add(participant);
                            from.getItems().add(participant);
                            subscribeToParticipant(participant);
                        }));
            }
            String dest = "/topic/events/" +
                    mainCtrl.getEvent().getInviteCode() + "/expenses/"
                    + expense.getId();
            transferSubscription = serverUtils.registerForMessages(dest, Expense.class,
                    exp -> Platform.runLater(() -> {
                        if ("deleted".equals(exp.getDescription())){
                            throwAlert("transfer.removedTransfer", "transfer.removedTransferBody");
                            exit();
                        }
                    }));
        }
    }

    /**
     * Adds subscriptions to participants
     * if a participant is deleted it is removed from the options,
     * or if previously selected an alert is thrown.
     * @param participant participant to subscribe to
     */
    public void subscribeToParticipant(Participant participant) {
        if(!participantSubscriptionMap.containsKey(participant)){
            String dest = "/topic/events/" +
                    mainCtrl.getEvent().getInviteCode() + "/participants/"
                    + participant.getId();
            var subscription = serverUtils.registerForMessages(dest, Participant.class,
                    part -> Platform.runLater(() -> {
                        if("deleted".equals(part.getIban())){
                            if(to.getValue().equals(part)){
                                throwAlert("transfer.participantDeleted",
                                        "transfer.participantDeletedBody");
                                to.setValue(null);
                            }
                            if(from.getValue().equals(part)){
                                throwAlert("transfer.participantDeleted",
                                        "transfer.participantDeletedBody");
                                from.setValue(null);
                            }
                            to.getItems().remove(part);
                            from.getItems().remove(part);
                        }
                    }));
            participantSubscriptionMap.put(participant, subscription);
        }
    }

    /**
     * Unsubscribes from websocket connections and returns to overview
     */
    @Override
    public void exit(){
        participantSubscription.unsubscribe();
        participantSubscriptionMap.forEach((k,v) -> v.unsubscribe());
        clearFields();
        mainCtrl.showOverview();
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
        removeHighlight();
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
        removeHighlight();
        exit();
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
        if( expensePriceText.isEmpty() || expenseDate == null
                || currencyVal.getValue() == null || to.getValue() == null
                || from.getValue() == null){
            removeHighlight();
            highlightMissing(to.getValue()==null, from.getValue()==null,
                    expensePriceText.isEmpty(), expenseDate==null, currencyVal.getValue() == null);
            return false;
        }
        return true;
    }

    /**
     * Insert highlight for missing fields in the addexpense scene
     * @param toBool boolean if recipient is present
     * @param fromBool boolean if transferee for price is present
     * @param priceBool boolean for amount being present
     * @param dateBool boolean for date being present
     * @param currencyBool boolean for currency selected
     */

    public void highlightMissing(boolean toBool, boolean fromBool,
                                 boolean priceBool, boolean dateBool, boolean currencyBool){
        if(toBool) to
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if(fromBool) from
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if(dateBool) date
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if(currencyBool) currencyVal
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if(priceBool) amount
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
    }

    /**
     * removes any prior highlighting of required fields
     */
    public void removeHighlight() {
        to.setStyle("-fx-border-color: none;");
        from.setStyle("-fx-border-color: none; ");
        date.setStyle("-fx-border-color: none; ");
        currencyVal.setStyle("-fx-border-color: none;");
        amount.setStyle("-fx-border-color: none");
    }


    /**
     * Checks whether a key is pressed and performs a certain action depending on that:
     *  - if ESCAPE is pressed, then it cancels and returns to the tags scene.
     *  - if Ctrl + m is pressed, then it returns to the startscreen.
     *  - if Ctrl + o is pressed, then it returns to the overview.
     * @param e KeyEvent
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ESCAPE:
                cancel();
                break;
            case M:
                if(e.isControlDown()){
                    startMenu();
                    break;
                }
            case O:
                if(e.isControlDown()){
                    backToOverview();
                    break;
                }
            default:
                break;
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
     * Back to the overview of the expenses of the Event
     */
    public void backToOverview() {
        clearFields();
        mainCtrl.showOverview();
    }

    /**
     * Setter for header label (testing)
     * @param header label
     */
    public void setHeader(Label header) {
        this.header = header;
    }

    /**
     * SEtter for transfer label (testing)
     * @param transferFrom label
     */
    public void setTransferFrom(Label transferFrom) {
        this.transferFrom = transferFrom;
    }

    /**
     * Setter for from choicebox (testing)
     * @param from choicebox
     */
    public void setFrom(ChoiceBox<Participant> from) {
        this.from = from;
    }

    /**
     * Setter for transferTO label (testing)
     * @param transferTo label
     */
    public void setTransferTo(Label transferTo) {
        this.transferTo = transferTo;
    }

    /**
     * setter for choicebox to (testing)
     * @param to choicebox
     */
    public void setTo(ChoiceBox<Participant> to) {
        this.to = to;
    }

    /**
     * Setter for transferAmount label
     * @param transferAmount label
     */
    public void setTransferAmount(Label transferAmount) {
        this.transferAmount = transferAmount;
    }

    /**
     * Setter for amount field (testing)
     * @param amount textfield
     */
    public void setAmount(TextField amount) {
        this.amount = amount;
    }

    /**
     * Setter for currency val choicebox (testing)
     * @param currencyVal choicebox
     */
    public void setCurrencyVal(ChoiceBox<String> currencyVal) {
        this.currencyVal = currencyVal;
    }

    /**
     * Setter for the date label (testing)
     * @param dateLabel label
     */
    public void setDateLabel(Label dateLabel) {
        this.dateLabel = dateLabel;
    }

    /**
     * Setter for the datepicker (testing)
     * @param date datepicker
     */
    @Override
    public void setDate(DatePicker date) {
        this.date = date;
    }

    /**
     * Setter for the cancel button (Testing)
     * @param cancel cancel button
     */
    public void setCancel(Button cancel) {
        this.cancel = cancel;
    }

    /**
     * Setter for confirm button (testing)
     * @param confirm button
     */
    public void setConfirm(Button confirm) {
        this.confirm = confirm;
    }

    /**
     * Setter for participant subscriptions (testing)
     * @param participantSubscription Stompsession subscription
     */
    public void setParticipantSubscription(StompSession.Subscription participantSubscription) {
        this.participantSubscription = participantSubscription;
    }

    /**
     * Setter for the websocket subscription map (testing)
     * @param participantSubscriptionMap map
     */
    public void setParticipantSubscriptionMap(Map<Participant,
            StompSession.Subscription> participantSubscriptionMap) {
        this.participantSubscriptionMap = participantSubscriptionMap;
    }

}
