package client.scenes;

import client.utils.ConfigInterface;
import client.utils.CurrencyConverter;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
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
        
    }
}
