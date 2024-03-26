package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import commons.Tag;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;


public class AddExpenseCtrl implements Initializable {

    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final Alert alert;
    // Include this in the anchor of the fxml file after overwriting using Scene Builder:
    // fx:controller="com.example.tutorial.addCtrl"
    @FXML
    private ChoiceBox<Participant> payee;
    private List<Participant> participantsList;
    @FXML
    private ChoiceBox<String> currency;
    private String[] currencies = {"USD", "EUR", "CHF"};
    private ArrayList<Tag> tags = new ArrayList<>();
    private Map<CheckBox, Participant> participantCheckBoxMap;
    @FXML
    private ComboBox<Tag> expenseType;
    @FXML
    private CheckBox everyone;
    @FXML
    private Button addTag;
    @FXML
    private CheckBox only;
    @FXML
    private VBox namesContainer;
    @FXML
    private Label question;
    @FXML
    private ScrollPane scrollNames;
    @FXML
    private TextField newTag;
    @FXML
    private Label instructions;
    @FXML
    private TextField title;
    @FXML
    private TextField price;
    @FXML
    private DatePicker date;
    private Button add;
    @FXML
    private Button cancelButton;
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
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageManager = languageManager;
        this.serverUtils = serverUtils;
        this.alert = alert;
        participantCheckBoxMap = new HashMap<>();
        //System.out.println(mainCtrl.getEvent());
    }

    /**
     * @param url            The location used to resolve relative paths for the root object, or
     *                       {@code null} if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or {@code null} if
     *                       the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        question.setVisible(false);
        scrollNames.setVisible(false);
        instructions.setVisible(false);
        newTag.setVisible(false);
        cancelButton.setGraphic(new ImageView(new Image("icons/cancelwhite.png")));
        addExpense.setGraphic(new ImageView(new Image("icons/checkwhite.png")));
        addTag.setGraphic(new ImageView(new Image("icons/plus.png")));
        currency.getItems().addAll(currencies);
        Tag food = new Tag("food", "green");
        Tag entranceFees = new Tag("entrance fees", "red");
        Tag travel = new Tag("travel", "blue");
        tags.add(food);
        tags.add(entranceFees);
        tags.add(travel);
        for (Tag tag : tags) {
            expenseType.getItems().add(tag);
        }
        payee.setConverter(new StringConverter<>() {
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
        setExpenseTypeView();
        refresh();
    }

    /**
     * Creates the view for the expense type.
     */
    private void setExpenseTypeView() {
        expenseType.setCellFactory(param -> new ListCell<>() {
            private final Rectangle rectangle = new Rectangle(100, 20);
            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    rectangle.setFill(javafx.scene.paint.Paint.valueOf(item.getColor()));
                    //setText(item.getName());
                    StackPane stackPane = new StackPane(rectangle, new Text(item.getName()));
                    setGraphic(stackPane);
                }
            }
        });
        expenseType.setConverter(new StringConverter<Tag>() {
            @Override
            public String toString(Tag tag) {
                if (tag == null) {
                    return null;
                } else {
                    return tag.getName();
                }
            }
            @Override
            public Tag fromString(String string) {
                // Not needed for ComboBox
                return null;
            }
        });
    }

    /**
     * Refreshes the available participants.
     */
    public void refresh() {
        if (add != null)
            add.setGraphic(new ImageView(new Image("icons/checkwhite.png")));
        loadParticipants();
        populateParticipantCheckBoxes();
    }

    /**
     *
     */
    public void loadParticipants() {
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null) {
            payee.getItems().clear();
            participantsList = mainCtrl.getEvent().getParticipantsList();
            for (Participant participant : participantsList) {
                payee.getItems().add(participant);
            }
        }
    }

    /**
     * Handler for the "only" checkbox.
     */
    public void onlyCheck() {
        if (only.isSelected()) {
            populateParticipantCheckBoxes();
            everyone.setSelected(false);
            scrollNames.setVisible(true);
        } else {
            scrollNames.setVisible(false);
            namesContainer.getChildren().clear();
            // Clear the checkboxes if "only" is deselected
        }
    }

    /**
     * Handler for the "everyone" checkbox.
     */
    public void everyoneCheck() {
        if (everyone.isSelected()) {
            scrollNames.setVisible(false);
            only.setSelected(false);
            namesContainer.getChildren().clear();
            // Clear the checkboxes if "everyone" is selected
        }
    }

    /**
     * Creates the checkboxes for each participant.
     */
    private void populateParticipantCheckBoxes() {
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null) {
            namesContainer.getChildren().clear(); // Clear the checkboxes before adding new ones
            participantCheckBoxMap.clear();
            for (Participant participant : mainCtrl.getEvent().getParticipantsList()) {
                if (participant == payee.getValue()) continue;
                String name = participant.getName();
                CheckBox checkBox = new CheckBox(name);
                participantCheckBoxMap.put(checkBox, participant);
                namesContainer.getChildren().add(checkBox);
            }
        }
    }

    /**
     * lets the user add a new tag to the already existing list
     */
    public void addTag() {
        showInstructions();
        newTag.setVisible(true);
        newTag.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String tag = newTag.getText().trim();
                // Add text to the ArrayList if it's not empty
                if (!tag.isEmpty()) {
                    //tags.add(new Tag(tag, "black"));
                    expenseType.getItems().add(new Tag(tag, "yellow"));
                }
                // Clear the text field
                newTag.clear();
                newTag.setVisible(false);
            }
        });
    }

    /**
     * tell the user how to add the new tag
     */
    public void showInstructions() {
        instructions.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), instructions);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(event -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), instructions);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(f -> instructions.setVisible(false));
                fadeOut.play();
            });
            delay.play();
        });

        fadeIn.play();
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
            scrollNames.setVisible(false);
            showQuestion();
        } else if (atLeastOneSelected) {
            scrollNames.setVisible(true);
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
            alert.titleProperty().bind(languageManager.bind("addExpense.alertTitle"));
            alert.headerTextProperty().bind(languageManager.bind("addExpense.incompleteHeader"));
            alert.contentTextProperty().bind(languageManager.bind("addExpense.incompleteBody"));
            alert.showAndWait();
            return;
        }

        try {
            // Parse price to double
            double expensePrice = Double.parseDouble(expensePriceText);

            // Create a new Expense object
            Expense newExpense = createExpense(expenseTitle, expensePrice, expenseDate);
            mainCtrl.getEvent().addExpense(newExpense);
            mainCtrl.showOverview();

            // Optionally, clear input fields after adding the expense
            clearFields();
        } catch (NumberFormatException e) {
            // Display an alert informing the user about incorrect price format
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.titleProperty().bind(languageManager.bind("addExpense.alertTitle"));
            alert.headerTextProperty().bind(languageManager.bind("addExpense.invalidHeader"));
            alert.contentTextProperty().bind(languageManager.bind("addExpense.invalidBody"));
            alert.showAndWait();
        }
    }

    /**
     * Method to create a new Expense object
     */

    public Expense createExpense(String title, double price, LocalDate date) {
        Tag tag = expenseType.getValue();
        Participant actualPayee = payee.getValue();

        List<ParticipantPayment> participantPayments = getParticipantPayments(price, actualPayee);

        Expense expense = new Expense(price, currency.getValue(), title, "testing",
                java.sql.Date.valueOf(date), participantPayments, tag, actualPayee);

        return expense;
    }

    /**
     * Method that gets the list of participant payments.
     * @param price - price of the expense
     * @param actualPayee - payee of the expense.
     * @return  - list of participant payments.
     */
    private List<ParticipantPayment> getParticipantPayments(double price, Participant actualPayee) {
        List<ParticipantPayment> participantPayments = new ArrayList<>();
        if (everyone.isSelected()) {
            everyoneCase(price, actualPayee, participantPayments);
        } else if (only.isSelected()) {
            onlyCase(price, participantPayments);
        }
        return participantPayments;
    }

    /**
     * Updates the participant payments list in the case that only some people are selected.
     * @param price - price of the expense
     * @param participantPayments - list of participant payments.
     */
    private void onlyCase(double price, List<ParticipantPayment> participantPayments) {
        int numberOfParticipants = 1;
        for (var pair : participantCheckBoxMap.entrySet()) {
            if (pair.getKey().isSelected()) {
                numberOfParticipants++;
            }
        }
        for (var pair : participantCheckBoxMap.entrySet()) {
            if (pair.getKey().isSelected()) {
                participantPayments.add(new ParticipantPayment(pair.getValue(),
                        price / numberOfParticipants));
            }
        }
    }

    /**
     * Updates the participant payments list in the case that everyone is selected.
     * @param price - price of the expense
     * @param actualPayee - payee of the expense.
     * @param participantPayments - list of participant payments.
     */
    private void everyoneCase(double price, Participant actualPayee,
                              List<ParticipantPayment> participantPayments) {
        for (Participant p : mainCtrl.getEvent().getParticipantsList()) {
            if (!p.equals(actualPayee)) {
                participantPayments.add(new ParticipantPayment(p,
                        price / (mainCtrl.getEvent().getParticipantsList().size())));
            }
        }
    }

    /**
     * Method to clear input fields
     */
    public void clearFields() {
        title.clear();
        price.clear();
        newTag.clear();
        date.setValue(null);
        currency.setValue(null);
        payee.setValue(null);
        everyone.setSelected(false);
        only.setSelected(false);
        expenseType.setValue(null);
        question.setVisible(false);
        scrollNames.setVisible(false);
        instructions.setVisible(false);
        newTag.setVisible(false);
    }

    /**
     * Getter for the language manager observable map.
     * @return - the language manager observable map.
     */
    public ObservableMap<String, Object> getLanguageManager() {
        return languageManager.get();
    }

    /**
     * Setter for the language manager observable map.
     * @param languageManager - the language manager observable map.
     */
    public void setLanguageManager(ObservableMap<String, Object> languageManager) {
        this.languageManager.set(languageManager);
    }

    /**
     * Getter for the language manager property.
     * @return - the language manager property.
     */
    public LanguageManager languageManagerProperty() {
        return languageManager;
    }

    /**
     * When the abort button is pressed it goes back to the overview
     */
    public void abort() {
        //alert.contentTextProperty().bind(languageManager.bind("startScreen.createEventEmpty"));

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "");
        confirmation.contentTextProperty().bind(languageManager.bind("addExpense.abortAlert"));
        confirmation.titleProperty().bind(languageManager.bind("commons.warning"));
        confirmation.headerTextProperty().bind(languageManager.bind("commons.warning"));
        Optional<ButtonType> result = confirmation.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            clearFields();
            mainCtrl.showOverview();
        }
    }

    /**
     *
     * @return
     */
    public ChoiceBox<Participant> getPayee() {
        return payee;
    }

    /**
     *
     * @param payee
     */
    public void setPayee(ChoiceBox<Participant> payee) {
        this.payee = payee;
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
     * @param currency
     */
    public void setCurrency(ChoiceBox<String> currency) {
        this.currency = currency;
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
     * @param currencies
     */
    public void setCurrencies(String[] currencies) {
        this.currencies = currencies;
    }

    /**
     *
     * @return
     */
    public ArrayList<Tag> getTags() {
        return tags;
    }

    /**
     *
     * @param tags
     */
    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    /**
     *
     * @return
     */
    public ComboBox<Tag> getExpenseType() {
        return expenseType;
    }

    /**
     *
     * @param expenseType
     */
    public void setExpenseType(ComboBox<Tag> expenseType) {
        this.expenseType = expenseType;
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
     * @param everyone
     */
    public void setEveryone(CheckBox everyone) {
        this.everyone = everyone;
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
     * @param only
     */
    public void setOnly(CheckBox only) {
        this.only = only;
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
     * @param namesContainer
     */
    public void setNamesContainer(VBox namesContainer) {
        this.namesContainer = namesContainer;
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
     * @param question
     */
    public void setQuestion(Label question) {
        this.question = question;
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
     * @param title
     */
    public void setTitle(TextField title) {
        this.title = title;
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
     * @param price
     */
    public void setPrice(TextField price) {
        this.price = price;
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
     * @param date
     */
    public void setDate(DatePicker date) {
        this.date = date;
    }

    /**
     *
     * @param scrollNames
     */
    public void setScrollNames(ScrollPane scrollNames) {
        this.scrollNames = scrollNames;
    }

    /**
     *
     * @param addTag
     */
    public void setAddTag(Button addTag) {
        this.addTag = addTag;
    }

    /**
     *
     * @param newTag
     */
    public void setNewTag(TextField newTag) {
        this.newTag = newTag;
    }

    /**
     *
     * @param instructions
     */
    public void setInstructions(Label instructions) {
        this.instructions = instructions;
    }

    /**
     * setter for the add button (testing)
     * @param add button for adding expense
     */
    public void setAdd(Button add) {
        this.add = add;
    }

    /**
     * setter for cancel button (testing)
     * @param cancelButton button to cancel adding expense
     */
    public void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }
}
