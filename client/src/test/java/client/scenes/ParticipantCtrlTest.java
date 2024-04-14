package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Participant;
import commons.ParticipantPayment;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;

@ExtendWith(ApplicationExtension.class)
class ParticipantCtrlTest {


    //Needed for the tests to run headless.
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless","true");
    }


    ServerUtils server;
    MainCtrl mainCtrl;
    LanguageManager languageManager;
    TextField name;
    TextField email;
    TextField iban;
    TextField bic;
    Participant participant;
    StompSession.Subscription participantSubscription;
    ParticipantCtrl sut;


    Event testEvent;
    Expense expense;
    ParticipantPayment p;
    ParticipantPayment p3;
    Participant p1;
    Participant p2;

    StringProperty first;
    StringProperty last;

    private void waitForRunLater() throws InterruptedException {
        var countDownLatch = new CountDownLatch(1);
        Platform.runLater(countDownLatch::countDown);
        countDownLatch.await();
    }

    @Start
    void setUp(Stage stage) {
        server = mock(ServerUtils.class);
        mainCtrl = mock(MainCtrl.class);
        languageManager = mock(LanguageManager.class);
        name = mock(TextField.class);
        email = mock(TextField.class);
        iban = mock(TextField.class);
        bic = mock(TextField.class);
        participantSubscription = mock(StompSession.Subscription.class);

        sut = new ParticipantCtrl(server, mainCtrl, languageManager);

        sut.setName(name);
        sut.setEmail(email);
        sut.setIban(iban);
        sut.setBic(bic);

        testEvent = new Event("testEvent", null, null);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
        var mockOverviewCtrl = mock(OverviewCtrl.class);
        doNothing().when(mockOverviewCtrl).populateParticipants();
        when(mainCtrl.getOverviewCtrl()).thenReturn(mockOverviewCtrl);
        p1 = new Participant("bob", null, null, null);
        p2 = new Participant("jill", null, null, null);
        testEvent.addParticipant(p1);
        testEvent.addParticipant(p2);
        testEvent.setInviteCode(1);
        p1.setId(1);
        p2.setId(2);
        p = new ParticipantPayment(p1, 15);
        p3 = new ParticipantPayment(p2,15);
        expense = new Expense(15, "EUR", "Transfer", "transfer", java.sql.Date.valueOf(LocalDate.now()), List.of(p, p3), null, p2);

        first = new SimpleStringProperty("test");
        last = new SimpleStringProperty("value");
        StringBinding fullNameBinding = new StringBinding() {
            {
                bind(first, last);
            }

            @Override
            protected String computeValue() {
                return first.get() + " " + last.get();
            }
        };
        when(languageManager.bind(anyString())).thenReturn(fullNameBinding);
        EditExpenseCtrl expenseCtrl = mock(EditExpenseCtrl.class);
        when(mainCtrl.getEditExpenseCtrl()).thenReturn(expenseCtrl);
        doNothing().when(expenseCtrl).setExpense(any());
        doNothing().when(mainCtrl).showEditExpense();
    }

    @Test
    public void startMenuTest(){
        doNothing().when(mainCtrl).showStartMenu();
        ParticipantCtrl spy = spy(sut);
        spy.startMenu();
        verify(mainCtrl).showStartMenu();
    }

    @Test
    public void okTest() {

        when(email.getText()).thenReturn("email@exmape.com");
        when(iban.getText()).thenReturn("");
        when(bic.getText()).thenReturn("");
        when(name.getText()).thenReturn("jill");

        when(server.changeParticipant(any(), any())).thenReturn(p1);
        doNothing().when(mainCtrl).showEditConfirmation();
        ParticipantCtrl spy = spy(sut);
        spy.ok();

        verify(spy).clearFields();
        verify(mainCtrl).showOverview();
        verify(mainCtrl).showParticipantConfirmation();
    }

    @Test
    public void okWrongIbanTest() throws InterruptedException {
        when(email.getText()).thenReturn("email@exmape.com");
        when(iban.getText()).thenReturn("eeban");
        when(bic.getText()).thenReturn("HBUKGB4B");
        when(name.getText()).thenReturn("name");

        when(server.changeParticipant(any(), any())).thenReturn(p1);
        doNothing().when(mainCtrl).showEditConfirmation();

        ParticipantCtrl spy = spy(sut);
        Platform.runLater(spy::ok);
        waitForRunLater();

        verify(spy, times(0)).abort();
        verify(mainCtrl, times(0)).showEditConfirmation();
    }


    @Test
    public void okWrongBicTest() throws InterruptedException {
        when(email.getText()).thenReturn("email@exmape.com");
        when(iban.getText()).thenReturn("NL85RABO5253446745");
        when(bic.getText()).thenReturn("bic");
        when(name.getText()).thenReturn("name");

        when(server.changeParticipant(any(), any())).thenReturn(p1);
        doNothing().when(mainCtrl).showEditConfirmation();

        ParticipantCtrl spy = spy(sut);
        Platform.runLater(spy::ok);
        waitForRunLater();

        verify(spy, times(0)).abort();
        verify(mainCtrl, times(0)).showEditConfirmation();
    }


    @Test
    public void okEmptyNameTest() throws InterruptedException {
        when(email.getText()).thenReturn("email@exmape.com");
        when(iban.getText()).thenReturn("eeban");
        when(bic.getText()).thenReturn("biiic");
        when(name.getText()).thenReturn("");

        when(server.changeParticipant(any(), any())).thenReturn(p1);
        doNothing().when(mainCtrl).showEditConfirmation();

        ParticipantCtrl spy = spy(sut);
        Platform.runLater(spy::ok);
        waitForRunLater();

        verify(spy, times(0)).abort();
        verify(mainCtrl, times(0)).showEditConfirmation();
    }

    @Test
    public void okWrongEmailTest() throws InterruptedException {
        when(email.getText()).thenReturn("email");
        when(iban.getText()).thenReturn("eeban");
        when(bic.getText()).thenReturn("biiic");
        when(name.getText()).thenReturn("name");

        when(server.changeParticipant(any(), any())).thenReturn(p1);
        doNothing().when(mainCtrl).showEditConfirmation();

        ParticipantCtrl spy = spy(sut);
        Platform.runLater(spy::ok);
        waitForRunLater();

        verify(spy, times(0)).abort();
        verify(mainCtrl, times(0)).showEditConfirmation();
    }


    @Test
    public void okNoBicTest() throws InterruptedException {
        when(email.getText()).thenReturn("email@exmape.com");
        when(iban.getText()).thenReturn("eeban");
        when(bic.getText()).thenReturn("");
        when(name.getText()).thenReturn("jill");

        when(server.changeParticipant(any(), any())).thenReturn(p1);
        doNothing().when(mainCtrl).showEditConfirmation();

        ParticipantCtrl spy = spy(sut);
        Platform.runLater(spy::ok);
        waitForRunLater();

        verify(spy, times(0)).abort();
        verify(mainCtrl, times(0)).showEditConfirmation();
    }

    @Test
    public void okErrorTest() throws InterruptedException {
        when(email.getText()).thenReturn("email@exmape.com");
        when(iban.getText()).thenReturn("");
        when(bic.getText()).thenReturn("");
        when(name.getText()).thenReturn("jill");

        when(server.changeParticipant(any(), any())).thenThrow(WebApplicationException.class);
        doNothing().when(mainCtrl).showEditConfirmation();

        ParticipantCtrl spy = spy(sut);
        Platform.runLater(spy::ok);
        waitForRunLater();

        verify(spy, times(0)).abort();
        verify(mainCtrl, times(0)).showEditConfirmation();
    }


    KeyEvent mockEvent = mock(KeyEvent.class);

    @Test
    void testKeyPressedEscape() {
        ParticipantCtrl spies = spy(sut);

        when(mockEvent.getCode()).thenReturn(KeyCode.ESCAPE);
        spies.keyPressed(mockEvent);

        verify(spies).abort();
    }

    @Test
    void testEnterKey(){
        ParticipantCtrl spies = spy(sut);

        when(mockEvent.getCode()).thenReturn(KeyCode.ENTER);

        doNothing().when(email).setText(anyString());
        doNothing().when(iban).setText(anyString());
        doNothing().when(bic).setText(anyString());

        when(email.getText()).thenReturn("email@exmape.com");
        when(iban.getText()).thenReturn("");
        when(bic.getText()).thenReturn("");
        when(name.getText()).thenReturn("jill");


        when(server.changeParticipant(any(), any())).thenReturn(p1);
        doNothing().when(mainCtrl).showEditConfirmation();

        doNothing().when(spies).ok();
        spies.keyPressed(mockEvent);

        verify(spies).ok();
    }

    @Test
    void testMKey(){
        ParticipantCtrl spies = spy(sut);

        when(mockEvent.isControlDown()).thenReturn(true);

        when(mockEvent.getCode()).thenReturn(KeyCode.M);
        spies.keyPressed(mockEvent);

        verify(spies).startMenu();
    }

    @Test
    void testOKey(){
        ParticipantCtrl spies = spy(sut);
        when(mockEvent.isControlDown()).thenReturn(true);

        when(mockEvent.getCode()).thenReturn(KeyCode.O);
        spies.keyPressed(mockEvent);

        verify(spies).abort();
    }

    @Test
    public void getParticipantTest(){
        when(name.getText()).thenReturn("bob");
        when(email.getText()).thenReturn(null);
        when(bic.getText()).thenReturn(null);
        when(iban.getText()).thenReturn(null);
        assertTrue(p1.fullEquals(sut.getParticipant()));
    }

}