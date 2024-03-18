package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import commons.Tag;
//import javafx.animation.FadeTransition;
//import javafx.animation.PauseTransition;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
//import javafx.util.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
//import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AddExpenseCtrl implements Initializable {

    // Include this in the anchor of the fxml file after overwriting using Scene Builder:
    // fx:controller="com.example.tutorial.addCtrl"
    @FXML
    private ChoiceBox<String> payee;
    private String[] names = {"John", "Chris", "Anna", "David"};
    @FXML
    private ChoiceBox<String> currency;
    private String[] currencies = {"USD", "EUR", "CHF"};
    private Tag[] tags = new Tag[3];
    @FXML
    private ComboBox<String> expenseType;
    @FXML
    private CheckBox everyone;
    @FXML
    private CheckBox only;
    @FXML
    private VBox namesContainer;
    @FXML
    private Label question;

    /**
     * @param url            The location used to resolve relative paths for the root object, or
     *                       {@code null} if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or {@code null} if
     *                       the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        question.setVisible(false);
        payee.getItems().addAll(names);
        currency.getItems().addAll(currencies);
        Tag food = new Tag("food", "yellow");
        Tag transport = new Tag("transport", "green");
        Tag admissionFees = new Tag("admission fees", "blue");
        tags[0] = food;
        tags[1] = transport;
        tags[2] = admissionFees;
        for (Tag tag : tags) {
            expenseType.getItems().add(tag.toString());
        }
        chooseOne();
    }

    /**
     * makes only one of the two checkboxes regarding the split be selected
     * not both at once
     */
    public void chooseOne() {
        everyone.setOnAction(event -> {
            if (everyone.isSelected()) {
                only.setSelected(false);
                namesContainer.getChildren().clear();
                // Clear the checkboxes if "everyone" is selected
            }
        });

        only.setOnAction(event -> {
            if (only.isSelected()) {
                everyone.setSelected(false);
                namesContainer.getChildren().clear(); // Clear the checkboxes before adding new ones
                for (String name : names) {
                    CheckBox checkBox = new CheckBox(name);
                    namesContainer.getChildren().add(checkBox);
                }
            } else {
                namesContainer.getChildren().clear();
                // Clear the checkboxes if "only" is deselected
            }
        });
    }

    /**
     * checks if all boxes are selected
     */
    public void checkAllSelected() {
        boolean allSelected = true;
        boolean atLeastOneSelected = false; // Flag to check if at least one checkbox is selected

        for (Node node : namesContainer.getChildren()) {
            CheckBox checkBox = (CheckBox) node;
            if (checkBox.isSelected()) {
                atLeastOneSelected = true;
            } else {
                allSelected = false;
            }
        }

        if (!atLeastOneSelected) {
            allSelected = false; // No checkbox is selected
        }

        // If all names are selected, clear the VBox and auto-select the "everyone" checkbox
        if (allSelected) {
            namesContainer.getChildren().clear();
            everyone.setSelected(true);
            only.setSelected(false);
            showQuestion();
        } else if (atLeastOneSelected){
            everyone.setSelected(false); // Deselect "everyone" if not all names are selected
        }
    }

    /**
     *
     */
    public void showQuestion() {
        question.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), question);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(event -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), question);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(f -> question.setVisible(false));
                fadeOut.play();
            });
            delay.play();
        });

        fadeIn.play();
    }


    @FXML
    private TextField title;
    @FXML
    private TextField price;
    @FXML
    private DatePicker date;

    /**
     * Add method for handling "Add" button click event
     */

    @FXML
    public void addExpense() {
        // Gather data from input fields
        String expenseTitle = title.getText();
        String expensePriceText = price.getText();
        LocalDate expenseDate = date.getValue();

        // Perform validation
        if (expenseTitle.isEmpty() || expensePriceText.isEmpty() || expenseDate == null) {
            // Display an alert informing the user about missing input
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Incomplete Data");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        try {
            // Parse price to double
            double expensePrice = Double.parseDouble(expensePriceText);

            // Create a new Expense object
            Expense newExpense = createExpense(expenseTitle, expensePrice, expenseDate);

            // Optionally, clear input fields after adding the expense
            clearFields();
        } catch (NumberFormatException e) {
            // Display an alert informing the user about incorrect price format
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Incorrect Data Format");
            alert.setContentText("Please enter a valid price.");
            alert.showAndWait();
        }
    }

    /**
     * Method to create a new Expense object
     */

    public Expense createExpense(String title, double price, LocalDate date) {
        List<ParticipantPayment> participantPayments = new ArrayList<>();
        Tag tag = new Tag(expenseType.getValue(), "default");
        Participant payee = new Participant(this.payee.getValue(), "mail.com", "23", "24");

        Expense expense = new Expense(price, currency.getValue(), title, "testing",
                java.sql.Date.valueOf(date), participantPayments, tag, payee);

        return expense;
    }

    /**
     * Method to clear input fields
     */
    public void clearFields() {
        title.clear();
        price.clear();
        date.setValue(null);
    }

    /**
     *
     * @return
     */
    public ChoiceBox<String> getPayee() {
        return payee;
    }

    /**
     *
     * @return
     */
    public String[] getNames() {
        return names;
    }

    /**
     *
     * @return
     */
    public ChoiceBox<String> getCurrency() {
        return currency;
    }

    /**
     *
     * @return
     */
    public String[] getCurrencies() {
        return currencies;
    }

    /**
     *
     * @return
     */
    public Tag[] getTags() {
        return tags;
    }

    /**
     *
     * @return
     */
    public ComboBox<String> getExpenseType() {
        return expenseType;
    }

    /**
     *
     * @return
     */
    public CheckBox getEveryone() {
        return everyone;
    }

    /**
     *
     * @return
     */
    public CheckBox getOnly() {
        return only;
    }

    /**
     *
     * @return
     */
    public VBox getNamesContainer() {
        return namesContainer;
    }

    /**
     *
     * @return
     */
    public Label getQuestion() {
        return question;
    }

    /**
     *
     * @return
     */
    public TextField getTitle() {
        return title;
    }

    /**
     *
     * @return
     */
    public TextField getPrice() {
        return price;
    }

    /**
     *
     * @return
     */
    public DatePicker getDate() {
        return date;
    }

    /**
     *
     * @param namesContainer
     */
    public void setNamesContainer(VBox namesContainer) {
        this.namesContainer = namesContainer;
    }

    /**
     *
     * @param question
     */
    public void setQuestion(Label question) {
        this.question = question;
    }

    /**
     *
     * @param title
     */
    public void setTitle(TextField title) {
        this.title = title;
    }

    /**
     *
     * @param price
     */
    public void setPrice(TextField price) {
        this.price = price;
    }

    /**
     *
     * @param date
     */
    public void setDate(DatePicker date) {
        this.date = date;
    }

    /**
     *
     * @param payee
     */
    public void setPayee(ChoiceBox<String> payee) {
        this.payee = payee;
    }

    /**
     *
     * @param names
     */
    public void setNames(String[] names) {
        this.names = names;
    }

    /**
     *
     * @param currency
     */
    public void setCurrency(ChoiceBox<String> currency) {
        this.currency = currency;
    }

    /**
     *
     * @param currencies
     */
    public void setCurrencies(String[] currencies) {
        this.currencies = currencies;
    }

    /**
     *
     * @param tags
     */
    public void setTags(Tag[] tags) {
        this.tags = tags;
    }

    /**
     *
     * @param expenseType
     */
    public void setExpenseType(ComboBox<String> expenseType) {
        this.expenseType = expenseType;
    }

    /**
     *
     * @param everyone
     */
    public void setEveryone(CheckBox everyone) {
        this.everyone = everyone;
    }

    /**
     *
     * @param only
     */
    public void setOnly(CheckBox only) {
        this.only = only;
    }
}
