package client.utils;

import client.scenes.MainCtrl;
import commons.Participant;
import jakarta.inject.Inject;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class MailSender {

    private final MainCtrl mainCtrl;
    private final ConfigInterface config;
    private Properties mailProperties;

    /**
     * Constructor for the mail sender.
     *
     * @param mainCtrl - the MainCtrl.
     */
    @Inject
    public MailSender(MainCtrl mainCtrl, ConfigInterface config) {
        this.mainCtrl = mainCtrl;
        this.config = config;
        mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", "true");
        mailProperties.put("mail.smtp.starttls.enable", "true");
    }

    /**
     * Method that sends the test mail.
     *
     * @param host     - the host.
     * @param port     - the port.
     * @param username - the username.
     * @param email    - the email.
     * @throws MessagingException - if the message couldn't be sent.
     */
    public void sendTestMail(String host,
                             String port,
                             String username,
                             String email) throws MessagingException {
        String subject = "Splitty test message";
        String content = "This is a test message for Splitty. " +
                "If you received this, your configuration should be working!";
        sendMessage(List.of(email), host, port, username, email, subject, content);
    }

    /**
     * Method that sends an invite to join an event.
     *
     * @param address    - the address of the Splitty server.
     * @param invite     - the invite code.
     * @param recipients - the list of recipients.
     * @param host       - the host.
     * @param port       - the port.
     * @param username   - the username.
     * @param email      - the email
     * @throws MessagingException - if the message couldn't be sent.
     */
    public void sendInvite(String address,
                           int invite,
                           List<String> recipients,
                           String host,
                           String port,
                           String username,
                           String email) throws MessagingException {
        String subject = "Splitty invite";
        String content = String.format("""
                        You have been invited to join an event on Splitty!

                        To join, use the following address: %s and the following invite code: %s""",
                address, invite);
        sendMessage(recipients, host, port, username, email, subject, content);
    }

    /**
     * Method that sends the payment reminder.
     *
     * @param address  - the Splitty server address.
     * @param invite   - the invite code to the event.
     * @param debtor   - the person who needs to pay.
     * @param creditor - the person who needs to be paid back.
     * @param amount   - the amount (including the currency).
     * @param host     - the host.
     * @param port     - the port.
     * @param username - the username.
     * @param email    - the email.
     * @throws MessagingException - if the message couldn't be sent.
     */
    public void sendReminder(String address,
                             int invite,
                             Participant debtor,
                             Participant creditor,
                             String amount,
                             String host,
                             String port,
                             String username,
                             String email) throws MessagingException {
        String subject = "Splitty payment reminder";
        String formatString = """
                Dear %s,
                                
                This is a reminder that you need to pay %s to %s.
                                
                %sFor more information, join the Splitty event at: %s using this invite code: %s.
                """;
        String content = String.format(formatString, debtor.getName(), amount, creditor.getName(),
                getPaymentDetails(creditor, amount), address, invite);
        sendMessage(List.of(debtor.getEmail()), host, port, username, email, subject, content);
    }

    /**
     * Method that returns the payment details as a string,
     * or it returns an empty string if they are missing.
     *
     * @param creditor - the person who needs to be paid back.
     * @param amount   - the amount (including the currency).
     * @return - the specified string.
     */
    private String getPaymentDetails(Participant creditor, String amount) {
        if (creditor.getBic() == null || creditor.getIban() == null
                || creditor.getBic().isEmpty() || creditor.getIban().isEmpty()) {
            return "";
        }
        return String.format("""
                Payment details:
                To: %s
                Amount: %s
                IBAN: %s
                BIC: %s
                                
                """, creditor.getName(), amount, creditor.getIban(), creditor.getBic());
    }

    /**
     * Method that sends a message to the list of recipients.
     *
     * @param recipients - the list of recipients.
     * @param host       - the host.
     * @param port       - the port.
     * @param username   - the username.
     * @param email      - the email
     * @param subject    - the subject of the email.
     * @param content    - the contents of the email.
     * @throws MessagingException - if it cannot send the message.
     */
    private void sendMessage(List<String> recipients,
                             String host,
                             String port,
                             String username,
                             String email,
                             String subject,
                             String content) throws MessagingException {
        Session session = getSession(host, port);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(email);
        for (String recipient : recipients) {
            msg.setRecipients(Message.RecipientType.TO, recipient);
        }
        msg.setRecipients(Message.RecipientType.CC, email);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(content);
        String password = getPassword();
        Transport.send(msg, username, password);
        config.setProperty("mail.password", password);
    }

    /**
     * Method that gets the password from the user.
     *
     * @return - the password.
     * @throws MessagingException - in case the user doesn't input anything.
     */
    private String getPassword() throws MessagingException {
        Optional<String> optional = mainCtrl.getPassword();
        if (optional.isEmpty())
            throw new MissingPasswordException();
        return optional.get();
    }

    /**
     * Method that gets the session with the specified host and port.
     *
     * @param host - the host.
     * @param port - the port.
     * @return - the session.
     */
    private Session getSession(String host, String port) {
        mailProperties.put("mail.smtp.host", host);
        mailProperties.put("mail.smtp.port", port);
        return Session.getInstance(mailProperties);
    }
}
