package client.scenes;

import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import commons.Tag;
//import javafx.animation.FadeTransition;
//import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
    private String[] currencies = {"USD","EUR","CHF"};
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
     *
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
        question.setVisible(false);
        payee.getItems().addAll(names);
        currency.getItems().addAll(currencies);
        Tag food = new Tag("food", "yellow");
        Tag transport = new Tag("transport", "green");
        Tag admissionFees = new Tag("admission fees", "blue");
        tags[0]= food;
        tags[1]= transport;
        tags[2]= admissionFees;
        for(Tag tag: tags){
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
    public void checkAllSelected(){
        // Check if all individual name checkboxes are selected
        boolean allSelected = true;
        for (Node node : namesContainer.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                if (!checkBox.isSelected()) {
                    allSelected = false;
                    break;
                }
            }
        }

        // If all names are selected, clear the VBox and auto-select the "everyone" checkbox
        if (allSelected) {
            namesContainer.getChildren().clear();
            everyone.setSelected(true);
            only.setSelected(false);
            //showQuestion();
        } else {
            everyone.setSelected(false); // Deselect "everyone" if not all names are selected
        }
    }
//    public void showQuestion(){
//        question.setVisible(true);
//        PauseTransition delay = new PauseTransition(Duration.seconds(3));
//        delay.setOnFinished(event -> {
//            // Start the fade-out animation after 3 seconds
//            fadeOUT.setNode(question);
//            fadeOUT.setFromValue(1.0);
//            fadeOUT.setToValue(0.0);
//            fadeOUT.setCycleCount(1);
//            fadeOUT.setAutoReverse(false);
//            fadeOUT.play(); // Start the FadeTransition animation
//        });
//        delay.play(); // Start the PauseTransition
//    }
//    private FadeTransition fadeOUT = new FadeTransition(
//            Duration.seconds(3)
//    );
    // have to fix this, not necessary but adds a little bit of fun

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
    private void addExpense() {
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
     *  Method to create a new Expense object
     */

    private Expense createExpense(String title, double price, LocalDate date) {
        List<ParticipantPayment> participantPayments = new ArrayList<>();
        Tag tag = new Tag(expenseType.getValue(), "default");
        Participant payee = new Participant(this.payee.getValue(), "mail.com", "23", "24");

        Expense expense = new Expense(price, currency.getValue(), title, "testing",
                java.sql.Date.valueOf(date), participantPayments, tag, payee);

        return expense;
    }

    /**
     *  Method to clear input fields
     */
    private void clearFields() {
        title.clear();
        price.clear();
        date.setValue(null);
    }

}
