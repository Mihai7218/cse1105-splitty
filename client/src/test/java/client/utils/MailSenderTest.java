package client.utils;

import client.scenes.MainCtrl;
import client.scenes.TestConfig;
import commons.Participant;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MailSenderTest {

    MailSender sut;
    MainCtrl mainCtrl;
    ConfigInterface config;

    @BeforeEach
    void setup() {
        mainCtrl = mock(MainCtrl.class);
        config = new TestConfig();
        sut = new MailSender(mainCtrl, config);
    }

    @Test
    void noPassword() {
        assertThrows(MessagingException.class, () -> {
            sut.sendTestMail("", "", "", "");
        });
    }

    @Test
    void testSendMail() {
        when(mainCtrl.getPassword()).thenReturn(Optional.of("a"));
        assertThrows(MessagingException.class, () -> {
            sut.sendTestMail("", "", "", "");
        });
    }

    @Test
    void testSendInvite() {
        when(mainCtrl.getPassword()).thenReturn(Optional.of("a"));
        assertThrows(MessagingException.class, () -> {
            sut.sendInvite("",1, List.of(""), "", "", "", "");
        });
    }

    @Test
    void testSendReminder() {
        when(mainCtrl.getPassword()).thenReturn(Optional.of("a"));
        Participant bob = new Participant("Bob", "", "", "");
        Participant tom = new Participant("Tom", "", "", "");
        assertThrows(MessagingException.class, () -> {
            sut.sendReminder("", 1, bob, tom,
                    "10.0 EUR", "", "", "", "");
        });
    }

    @Test
    void testSendReminderProperDetails() {
        when(mainCtrl.getPassword()).thenReturn(Optional.of("a"));
        Participant bob = new Participant("Bob", "", "A", "B");
        Participant tom = new Participant("Tom", "", "A", "B");
        assertThrows(MessagingException.class, () -> {
            sut.sendReminder("", 1, bob, tom,
                    "10.0 EUR", "", "", "", "");
        });
    }
}