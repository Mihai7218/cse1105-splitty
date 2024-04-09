package client.scenes;

import client.utils.ConfigInterface;
import client.utils.CurrencyConverter;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddTransferCtrl implements Initializable {

    protected final ServerUtils serverUtils;
    protected final ConfigInterface config;
    protected final MainCtrl mainCtrl;
    protected final LanguageManager languageManager;
    protected final Alert alert;
    protected final CurrencyConverter currencyConverter;

    @FXML
    private Label title;
    @FXML
    private Label transferFrom;
    @FXML
    private ChoiceBox from;
    @FXML
    private Label transferTo;
    @FXML
    private ChoiceBox to;
    @FXML
    private Label transferAmount;
    @FXML
    private TextField amount;
    @FXML
    private ChoiceBox currencyVal;
    @FXML
    private Label dateLabel;
    @FXML
    private DatePicker date;
    @FXML
    private Button cancel;
    @FXML
    private Button confirm;

    public AddTransferCtrl(ServerUtils serverUtils,
                           ConfigInterface config,
                           MainCtrl mainCtrl,
                           LanguageManager languageManager,
                           Alert alert,
                           CurrencyConverter currencyConverter) {
        this.serverUtils = serverUtils;
        this.config = config;
        this.mainCtrl = mainCtrl;
        this.languageManager = languageManager;
        this.alert = alert;
        this.currencyConverter = currencyConverter;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null) {
            from.getItems().clear();
            from.getItems().addAll(mainCtrl.getEvent().getParticipantsList());
            to.getItems().clear();
            to.getItems().addAll(mainCtrl.getEvent().getParticipantsList());
        }
    }

    public void refresh(){

    }

    public void load(){

    }

    public void abort(){
        clearFields();
        mainCtrl.showOverview();
    }

    public void clearFields(){
        from.setValue(null);
        to.setValue(null);
        amount.clear();
        currencyVal.setValue(null);
        date.setValue(null);
    }

    public void populateToBox(){
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null) {
            to.getItems().clear();
            for (Participant participant : mainCtrl.getEvent().getParticipantsList()) {
                if (participant == from.getValue()) continue;
                else {
                    to.getItems().add(participant);
                }
            }
        }
    }

    public boolean validate(String expensePriceText, LocalDate expenseDate){
        // Perform validation
        if (expensePriceText.isEmpty() || expenseDate == null
                || currencyVal.getValue() == null || to.getValue() == null
        || from.getValue() == null) {
//            throwAlert("addExpense.incompleteHeader", "addExpense.incompleteBody");
//            //removeHighlight();
//            highlightMissing(expenseTitle.isEmpty(), expensePriceText.isEmpty(), expenseDate ==null,
//                    currency.getValue()==null, payee.getValue() == null);
            return false;
        }
        return true;
    }

}
