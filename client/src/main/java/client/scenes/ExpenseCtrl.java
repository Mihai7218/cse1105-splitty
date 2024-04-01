package client.scenes;

import client.utils.ConfigInterface;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import commons.Tag;
import jakarta.ws.rs.WebApplicationException;
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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public abstract class ExpenseCtrl implements Initializable {


    protected final ServerUtils serverUtils;
    protected final ConfigInterface config;
    protected final MainCtrl mainCtrl;
    protected final LanguageManager languageManager;
    protected final Alert alert;
    @FXML
    protected ChoiceBox<Participant> payee;
    protected List<Participant> participantsList;
    @FXML
    protected ChoiceBox<String> currency;
    protected String[] currencies = {"USD", "EUR", "CHF"};
    protected Map<CheckBox, Participant> checkBoxParticipantMap;
    protected Map<Participant, CheckBox> participantCheckBoxMap;
    @FXML
    protected ComboBox<Tag> expenseType;
    @FXML
    protected CheckBox everyone;
    @FXML
    protected Button addTag;
    @FXML
    protected CheckBox only;
    @FXML
    protected VBox namesContainer;
    @FXML
    protected Label question;
    @FXML
    protected ScrollPane scrollNames;
    @FXML
    protected TextField newTag;
    @FXML
    protected Label instructions;
    @FXML
    protected TextField title;
    @FXML
    protected TextField price;
    @FXML
    protected DatePicker date;
    protected Button add;
    @FXML
    protected Button cancelButton;
    @FXML
    protected Button addExpense;

    protected Expense expense;

    /**
     *
     * @param mainCtrl
     * @param config
     * @param languageManager
     * @param serverUtils
     * @param alert
     */
    @Inject
    public ExpenseCtrl(MainCtrl mainCtrl,
                          ConfigInterface config,
                          LanguageManager languageManager,
                          ServerUtils serverUtils,
                          Alert alert) {
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageManager = languageManager;
        this.serverUtils = serverUtils;
        this.alert = alert;
        checkBoxParticipantMap = new HashMap<>();
        participantCheckBoxMap = new HashMap<>();
        participantsList = new ArrayList<>();
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
        if (cancelButton != null)
            cancelButton.setGraphic(new ImageView(new Image("icons/cancelwhite.png")));
        if (addExpense != null)
            addExpense.setGraphic(new ImageView(new Image("icons/checkwhite.png")));
        if (addTag != null)
            addTag.setGraphic(new ImageView(new Image("icons/plus.png")));
        currency.getItems().addAll(currencies);
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
    }

    /**
     * Creates the view for the expense type.
     */
    protected void setExpenseTypeView() {
        expenseType.setCellFactory(param -> new ListCell<>() {
            private final Rectangle rectangle = new Rectangle(100, 20);

            @Override
            protected void updateItem(Tag item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    rectangle.setFill(Paint.valueOf(item.getColor()));
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
        load();
    }

    /**
     *
     */
    public void load() {
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null) {
            payee.getItems().clear();
            payee.getItems().addAll(mainCtrl.getEvent().getParticipantsList());
            expenseType.getItems().clear();
            expenseType.getItems().addAll(mainCtrl.getEvent().getTagsList());
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
    public void populateParticipantCheckBoxes() {
        if (mainCtrl != null && mainCtrl.getEvent() != null
                && mainCtrl.getEvent().getParticipantsList() != null) {
            namesContainer.getChildren().clear(); // Clear the checkboxes before adding new ones
            checkBoxParticipantMap.clear();
            participantCheckBoxMap.clear();
            List<ParticipantPayment> split = null;
            if (expense != null)
                split = expense.getSplit();
            for (Participant participant : mainCtrl.getEvent().getParticipantsList()) {
                if (participant == payee.getValue()) continue;
                String name = participant.getName();
                CheckBox checkBox = new CheckBox(name);
                if (split != null && split.stream().anyMatch(
                        pp -> pp.getParticipant().equals(participant)))
                    checkBox.setSelected(true);
                checkBoxParticipantMap.put(checkBox, participant);
                participantCheckBoxMap.put(participant, checkBox);
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
                    Tag newTag = new Tag(tag, "yellow");
                    try {
                        newTag = serverUtils.addTag(mainCtrl.getEvent().getInviteCode(), newTag);
                    }
                    catch (WebApplicationException e) {
                        switch (e.getResponse().getStatus()) {
                            case 400 -> throwAlert("addExpense.invalidTagHeader",
                                    "addExpense.invalidTagBody");
                            case 404 -> throwAlert("addExpense.notFoundHeader",
                                    "addExpense.notFoundBody");
                        }
                        return;
                    }
                    expenseType.getItems().add(newTag);
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
    public boolean checkAllSelected() {
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
        } else if (atLeastOneSelected && only.isSelected()) {
            scrollNames.setVisible(true);
            everyone.setSelected(false); // Deselect "everyone" if not all names are selected
        }
        return allSelected;
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
     * Method that throws an alert.
     * @param header - property associated with the header.
     * @param body - property associated with the body.
     */
    protected void throwAlert(String header, String body) {
        alert.titleProperty().bind(languageManager.bind("commons.warning"));
        alert.headerTextProperty().bind(languageManager.bind(header));
        alert.contentTextProperty().bind(languageManager.bind(body));
        alert.showAndWait();
    }

    /**
     * Insert highlight for missing fields in the addexpense scene
     * @param titleBool boolean if title is present
     * @param priceText boolean if text for price is present
     * @param dateBool boolean for date being present
     * @param currencyBool boolean for currency selected
     */
    public void highlightMissing(boolean titleBool,
                                 boolean priceText, boolean dateBool, boolean currencyBool,
                                 boolean payeeBool){
        if(titleBool) title
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if(priceText) price
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if(dateBool) date
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if(currencyBool) currency
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
        if(payeeBool) payee
                .setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius:2px;");
    }

    /**
     * removes any prior highlighting of required fields
     */
    public void removeHighlight() {
        title.setStyle("-fx-border-color: none;");
        price.setStyle("-fx-border-color: none; ");
        date.setStyle("-fx-border-color: none; ");
        currency.setStyle("-fx-border-color: none;");
        payee.setStyle("-fx-border-color: none");
    }

    /**
     * Method that gets the list of participant payments.
     * @param price - price of the expense
     * @param actualPayee - payee of the expense.
     * @return  - list of participant payments.
     */
    protected List<ParticipantPayment> getParticipantPayments(int price, Participant actualPayee) {
        List<ParticipantPayment> participantPayments = new ArrayList<>();
        if (only.isSelected()) {
            onlyCase(price, participantPayments, actualPayee);
        } else {
            everyoneCase(price, actualPayee, participantPayments);
        }
        return participantPayments;
    }

    /**
     * Updates the participant payments list in the case that only some people are selected.
     * @param price - price of the expense
     * @param participantPayments - list of participant payments.
     */
    protected void onlyCase(int price, List<ParticipantPayment> participantPayments,
                          Participant actualPayee) {
        int numberOfParticipants = 1;
        for (var pair : checkBoxParticipantMap.entrySet()) {
            if (pair.getKey().isSelected()) {
                numberOfParticipants++;
            }
        }

        Map<Participant, ParticipantPayment> participantSplits = new HashMap<>();
        List<Participant> currParticipants = new ArrayList<>();

        double amtAdded = (double)(price/numberOfParticipants)/100.0;
        ParticipantPayment payeePayment = new ParticipantPayment(actualPayee, amtAdded);
        participantSplits.put(actualPayee, payeePayment);
        participantPayments.add(payeePayment);
        currParticipants.add(actualPayee);

        //System.out.println(amtAdded);
        int remainder = price % numberOfParticipants;
        for (var pair : checkBoxParticipantMap.entrySet()) {
            ParticipantPayment newP = new ParticipantPayment(pair.getValue(), amtAdded);
            if (pair.getKey().isSelected()) {
                currParticipants.add(pair.getValue());
                participantPayments.add(newP);
            }
            participantSplits.put(pair.getValue(), newP);

        }
        Collections.shuffle(currParticipants);
        int counter = 0;
        while(remainder > 0){
            Participant subject = currParticipants.get(counter);
            double initAmt = participantSplits.get(subject).getPaymentAmount();
            participantSplits.get(currParticipants.get(counter)).setPaymentAmount(initAmt + 0.01);
            remainder--;
            counter++;
            //System.out.println(subject.toString() + " got the extra cent!");
        }

    }

    /**
     * Updates the participant payments list in the case that everyone is selected.
     * @param price - price of the expense
     * @param actualPayee - payee of the expense.
     * @param participantPayments - list of participant payments.
     */
    protected void everyoneCase(int price, Participant actualPayee,
                              List<ParticipantPayment> participantPayments) {

        Map<Participant, ParticipantPayment> participantSplits = new HashMap<>();

        double amtAdded = (double)(price/mainCtrl.getEvent().getParticipantsList().size())/100.0;
        ParticipantPayment payeePayment = new ParticipantPayment(actualPayee, amtAdded);
        participantSplits.put(actualPayee, payeePayment);
        participantPayments.add(payeePayment);

        //System.out.println(amtAdded);
        int remainder = price % mainCtrl.getEvent().getParticipantsList().size();
        for (Participant p : mainCtrl.getEvent().getParticipantsList()) {
            ParticipantPayment newP = new ParticipantPayment(p, amtAdded);
            if (!p.equals(actualPayee)) {
                participantPayments.add(newP);
            }
            participantSplits.put(p, newP);

        }
        List<Participant> currParticipants = mainCtrl.getEvent().getParticipantsList();
        Collections.shuffle(currParticipants);
        int counter = 0;
        while(remainder > 0){
            Participant subject = currParticipants.get(counter);
            double initAmt = participantSplits.get(subject).getPaymentAmount();
            participantSplits.get(currParticipants.get(counter)).setPaymentAmount(initAmt + 0.01);
            remainder--;
            counter++;
            //System.out.println(subject.toString() + " got the extra cent!");

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
     *
     * @param expenseTitle
     * @param expensePriceText
     * @param expenseDate
     * @return
     */
    protected boolean validate(String expenseTitle, String expensePriceText, LocalDate expenseDate){
        // Perform validation
        if (expenseTitle.isEmpty() || expensePriceText.isEmpty() || expenseDate == null
                || currency.getValue() == null || payee.getValue() == null) {
            throwAlert("addExpense.incompleteHeader", "addExpense.incompleteBody");
            removeHighlight();
            // Display an alert informing the user about missing input
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.titleProperty().bind(languageManager.bind("addExpense.alertTitle"));
            alert.headerTextProperty().bind(languageManager.bind("addExpense.incompleteHeader"));
            alert.contentTextProperty().bind(languageManager.bind("addExpense.incompleteBody"));
            alert.showAndWait();
            highlightMissing(expenseTitle.isEmpty(), expensePriceText.isEmpty(), expenseDate ==null,
                    currency.getValue()==null, payee.getValue() == null);
            return false;
        }
        return true;
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
        removeHighlight();
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "");
        confirmation.contentTextProperty().bind(languageManager.bind("addExpense.abortAlert"));
        confirmation.titleProperty().bind(languageManager.bind("commons.warning"));
        confirmation.headerTextProperty().bind(languageManager.bind("commons.warning"));
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
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
     * getter for button for testing
     * @param addExpense
     */
    public void setAddExpense(Button addExpense) {
        this.addExpense = addExpense;
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
