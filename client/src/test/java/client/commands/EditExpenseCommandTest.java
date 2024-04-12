package client.commands;

import client.scenes.MainCtrl;
import client.utils.ServerUtils;
import commons.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class EditExpenseCommandTest {

    //Needed for the tests to run headless.
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless","true");
    }

    MainCtrl mainCtrl;
    ServerUtils serverUtils;
    Participant p;
    Participant p1;
    ParticipantPayment pPayment;
    ParticipantPayment p1Payment;
    List<ParticipantPayment> split;
    Expense expense;
    Event e;
    Tag t;

    @Start
    public void setup(Stage stage){
        p = new Participant("Jane", null, null, null);
        p1 = new Participant("Ed", null, null, null);
        pPayment = new ParticipantPayment(p, 7.5);
        p1Payment = new ParticipantPayment(p1, 7.5);
        split = List.of(pPayment, p1Payment);
        t = new Tag("yellow", "yellow");
        expense = new Expense(15,"EUR", "ExpTitle", "desc", null,split, t, p);
        e = new Event("eventTitle", null, null);
        e.addExpense(expense);
        mainCtrl = mock(MainCtrl.class);
        serverUtils = mock(ServerUtils.class);
        when(mainCtrl.getEvent()).thenReturn(e);

    }

    @Test
    public void undoTest(){
        List<Expense> expenses = new ArrayList<>();

        doAnswer(invocation -> {
            Expense expense = (Expense) invocation.getArguments()[1];
            expenses.add(expense);
            return null;
        }).when(serverUtils).updateExpense(anyInt(),any());
        Tag newTag = new Tag("redTag", "red");
        ICommand edit = new EditExpenseCommand(20, "USD", "newTitle",
                null, split,newTag,p1,expense,serverUtils,mainCtrl);
        ICommand spycommand = spy(edit);
        spycommand.execute();

        assertEquals(expense.getAmount(), 20.0);
        assertEquals(expense.getCurrency(), "USD");
        assertEquals(expense.getTitle(), "newTitle");
        assertEquals(expense.getSplit(), split);
        assertEquals(expense.getTag(), newTag);
        assertEquals(expense.getPayee(), p1);
        assertEquals(expense.getDate(), null);

        verify(mainCtrl, times(1)).getEvent();
        verify(serverUtils, times(1)).updateExpense(anyInt(),any());

        spycommand.undo();

        assertEquals(expense.getAmount(),15.0);
        assertEquals(expense.getCurrency(), "EUR");
        assertEquals(expense.getPayee(), p);
        assertEquals(expense.getSplit(), split);
        assertEquals(expense.getTag(), t);
        assertEquals(expense.getTitle(),"ExpTitle");

        verify(mainCtrl, times(2)).getEvent();
        verify(serverUtils, times(2)).updateExpense(anyInt(),any());    }

}