package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Participant;
import jakarta.mail.MessagingException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Modality;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
//import javafx.scene.control.*;


public class InvitationCtrl implements Initializable, NotificationSender{
    @FXML
    public Text code;
    @FXML
    private Text test;
    @FXML
    private TextArea mailSpace;
    @FXML
    private Label label1;
    @FXML
    private Label label2;
    @FXML
    private Button abort;
    @FXML
    private Button send;
    @FXML
    private Label confirmation;
    private final ServerUtils serverUtils;
    private final ConfigInterface config;
    private final MainCtrl mainCtrl;
    private final LanguageManager languageManager;
    private final Alert alert;
    private final MailSender mailSender;

    /**
     *
     * @param mainCtrl
     * @param config
     * @param languageManager
     * @param serverUtils
     * @param alert
     */
    @Inject
    public InvitationCtrl(MainCtrl mainCtrl,
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
    }

    /**
     * loads the title and code of the event
     */
    public void refresh() {
        label1.textProperty().bind(languageManager.bind("invitation.label1"));
        label2.textProperty().bind(languageManager.bind("invitation.label2"));
        abort.textProperty().bind(languageManager.bind("invitation.abort"));
        send.textProperty().bind(languageManager.bind("invitation.send"));
        test.setText(mainCtrl.getEvent().getTitle());
        code.setText(String.valueOf(mainCtrl.getEvent().getInviteCode()));
        String host = config.getProperty("mail.host");
        String port = config.getProperty("mail.port");
        String user = config.getProperty("mail.user");
        String email = config.getProperty("mail.email");
        if (host == null || host.isEmpty()
                || port == null || port.isEmpty()
                || user == null || user.isEmpty()
                || email == null || email.isEmpty()) {
            alert.contentTextProperty().bind(languageManager.bind("invitation.missingConfig"));
            alert.showAndWait();
            mainCtrl.showOverview();
        }
    }

    /**
     * sends invites to the mail in the text area
     * has to be implemented with mail functionality
     */
    public void sendInvites(){
        showNotification("mail.sending");
        String emailRegex = "^[\\w!#$%&’*+/=?{|}~^-]+(?:\\." +
                "[\\w!#$%&’*+/=?{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        String mails = mailSpace.getText();
        List<String> emailList = mails.lines().filter(x -> Pattern.matches(emailRegex, x)).toList();
        if (emailList.size() < mails.lines().toList().size()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.contentTextProperty().bind(languageManager.bind("invitation.invalidEmails"));
            alert.showAndWait();
            return;
        }
        String server = config.getProperty("server");
        if (server.isEmpty()) {
            server = "http://localhost:8080";
            config.setProperty("server", server);
        }
        String host = config.getProperty("mail.host");
        String port = config.getProperty("mail.port");
        String user = config.getProperty("mail.user");
        String emailAddress = config.getProperty("mail.email");
        try {
            mailSender.sendInvite(server, mainCtrl.getEvent().getInviteCode(),
                    emailList, host, port, user, emailAddress);
        }
        catch (MessagingException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            if (e.getClass().equals(MissingPasswordException.class)) {
                alert.contentTextProperty().bind(languageManager.bind("mail.noPassword"));
            }
            else {
                alert.contentTextProperty().unbind();
                alert.setContentText(e.getMessage());
            }
            alert.showAndWait();
            return;
        }
        for (String email : emailList) {
            serverUtils.addParticipant(mainCtrl.getEvent().getInviteCode(),
                    new Participant(email, email, null, null));
        }
        mainCtrl.getOverviewCtrl().populateParticipants();
        if(mailSpace!=null) mailSpace.setText("");
        mainCtrl.showInviteConfirmation();
        mainCtrl.showOverview();
    }

    /**
     * clears the text area and goes back to overview
     */
    public void goBack(){
        mainCtrl.showOverview();
        if(mailSpace!=null) mailSpace.setText("");
    }

    /**
     * Gets the notification label.
     * @return - the notification label.
     */
    @Override
    public Label getNotificationLabel() {
        return confirmation;
    }

    /**
     * Getter for the language manager property.
     *
     * @return - the language manager property.
     */
    @Override
    public LanguageManager languageManagerProperty() {
        return languageManager;
    }
}
