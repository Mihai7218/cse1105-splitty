package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.ParticipantPayment;
import jakarta.mail.MessagingException;
import javafx.animation.*;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class DebtsCtrl implements Initializable {
    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final Alert alert;

    private final MailSender mailSender;
    @FXML
    private Label confirmation;
    private boolean canRemind;
    @FXML
    private Accordion menu;
    @FXML
    private Button back;

    /**
     * @param mainCtrl
     * @param config
     * @param languageManager
     * @param serverUtils
     * @param alert
     */
    @Inject
    public DebtsCtrl(MainCtrl mainCtrl,
                     ConfigInterface config,
                     LanguageManager languageManager,
                     ServerUtils serverUtils,
                     Alert alert,
                     MailSender mailSender) {
        this.mainCtrl = mainCtrl;
        this.config = config;
        this.languageManager = languageManager;
        this.serverUtils = serverUtils;
        this.alert = alert;
        this.mailSender = mailSender;
    }

    /**
     * trying to get the mark received button into a green checkstyle, throws errors for now
     *
     * @param button
     */
    public static void animation(Button button) {
        Label checkMark = new Label("\u2714"); // Unicode for checkmark symbol
        checkMark.setStyle("-fx-text-fill: green; " +
                "-fx-font-size: 24px; -fx-opacity: 0;"); // Initially invisible
        Timeline timeline = new Timeline();

        // Animate button text opacity to 0
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5),
                new KeyValue(button.opacityProperty(), 0)));

        // Animate check mark opacity to 1
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5),
                new KeyValue(checkMark.opacityProperty(), 1)));

        // Change the button text after animation completes
        timeline.setOnFinished(e -> button.setText("\u2714"));

        // Play the animation
        timeline.play();
    }

    /**
     * @param url            The location used to resolve relative paths for the root object, or
     *                       {@code null} if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or {@code null} if
     *                       the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        back.textProperty().bind(languageManager.bind("debts.setButton"));
    }

    /**
     * loads the data for the scene
     */
    public void refresh() {
        menu.getPanes().clear();
        String host = config.getProperty("mail.host");
        String port = config.getProperty("mail.port");
        String user = config.getProperty("mail.user");
        canRemind = host != null && !host.isEmpty()
                && port != null && !port.isEmpty()
                && user != null && !user.isEmpty();
        setTitles(mainCtrl.getEvent());
    }

    /**
     * return to overview
     */
    public void goBack() {
        mainCtrl.showOverview();
    }

    /**
     * goes through all participants payments that have to be paid
     *
     * @param event to search for money splits
     */
    public void setTitles(Event event) {
        for (Expense expense : event.getExpensesList()) {
            for (ParticipantPayment pp : expense.getSplit()) {
                if (!pp.getParticipant().getName().equals(expense.getPayee().getName())) {
                    String title = pp.getParticipant().getName() + " : " + pp.getPaymentAmount()
                            + " " + expense.getCurrency() + " => " + expense.getPayee().getName();
                    TitledPane tp = new TitledPane(title, null);
                    menu.getPanes().add(tp);
                    AnchorPane anchorPane = new AnchorPane();
                    Label info = new Label();
                    Button mark = new Button();
                    Button remind = new Button();
                    if (expense.getPayee().getBic().equals("\u2714") ||
                            expense.getPayee().getIban().equals("")) {
                        info.textProperty().bind(languageManager.bind("debts.unavailable"));
                        mark.setVisible(false);
                    } else {
                        //info.textProperty().bind(languageManager.bind("debts.available"));
                        String data = expense.getPayee().getName() + "\nIBAN: " +
                                expense.getPayee().getIban() + "\nBIC: " +
                                expense.getPayee().getBic();

                        info.setText(data);
                        mark.textProperty().bind(languageManager.bind("debts.send"));
                        mark.setOnAction(x ->
                        {
                            mark.textProperty().bind(languageManager.bind("debts.check"));
                        });
                    }
                    remind.textProperty().bind(languageManager.bind("debts.remind"));
                    if (!canRemind || pp.getParticipant().getEmail() == null
                            || pp.getParticipant().getEmail().isEmpty()) {
                        Tooltip tooltip = new Tooltip();
                        tooltip.textProperty().bind(languageManager
                                .bind("debts.unavailableReminder"));
                        remind.setTooltip(tooltip);
                        remind.setId("disabledButton");
                    } else {
                        remind.setId(null);
                        remind.setTooltip(null);
                        remind.setOnAction(x -> {
                            String address = config.getProperty("server");
                            if (address == null || address.isEmpty())
                                address = "http://localhost:8080";
                            String host = config.getProperty("mail.host");
                            String port = config.getProperty("mail.port");
                            String username = config.getProperty("mail.user");
                            try {
                                mailSender.sendReminder(address, event.getInviteCode(),
                                        pp.getParticipant(), expense.getPayee(),
                                        String.format("%.2f %s",
                                                pp.getPaymentAmount(), expense.getCurrency()),
                                        host, port, username);
                                showConfirmation();
                            } catch (MessagingException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.initModality(Modality.APPLICATION_MODAL);
                                if (e.getClass().equals(MissingPasswordException.class)) {
                                    alert.contentTextProperty().bind(
                                            languageManager.bind("mail.noPassword"));
                                } else {
                                    alert.contentTextProperty().unbind();
                                    alert.setContentText(e.getMessage());
                                }
                                alert.showAndWait();
                                return;
                            }
                        });
                    }
                    anchorPane.getChildren().add(info);
                    anchorPane.getChildren().add(mark);
                    anchorPane.getChildren().add(remind);
                    anchorPane.setTopAnchor(info, 10.0);
                    anchorPane.setLeftAnchor(info, 10.0);

                    // Position the button relative to the label
                    anchorPane.setTopAnchor(mark,
                            AnchorPane.getTopAnchor(info) + info.getPrefHeight() + 30.0);
                    anchorPane.setLeftAnchor(mark,
                            AnchorPane.getLeftAnchor(info) + info.getPrefWidth() + 300.0);
                    anchorPane.setTopAnchor(remind,
                            AnchorPane.getTopAnchor(info) + info.getPrefHeight() + 30.0);
                    anchorPane.setLeftAnchor(remind,
                            AnchorPane.getLeftAnchor(info) + info.getPrefWidth() + 200.0);
                    tp.setContent(anchorPane);
                }
            }
        }
    }

    /**
     * method to display a confirmation message when the reminder is sent
     * this message disappears
     */
    public void showConfirmation() {
        confirmation.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), confirmation);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(event -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), confirmation);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(f -> confirmation.setVisible(false));
                fadeOut.play();
            });
            delay.play();
        });

        fadeIn.play();
    }

    /**
     * Getter for the language manager map.
     *
     * @return - the language manager observable map.
     */
    public ObservableMap<String, Object> getLanguageManager() {
        return languageManager.get();
    }

    /**
     * Getter for the language manager property.
     *
     * @return - the language manager property.
     */
    public LanguageManager languageManagerProperty() {
        return languageManager;
    }
}
