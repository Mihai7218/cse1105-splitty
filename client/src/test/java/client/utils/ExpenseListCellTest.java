package client.utils;

import client.scenes.EditExpenseCtrl;
import client.scenes.EditTransferCtrl;
import client.scenes.MainCtrl;
import commons.*;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
class ExpenseListCellTest {

    //Needed for the tests to run headless.
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless","true");
    }

    MainCtrl mainCtrl;
    LanguageManager languageManager;
    CurrencyConverter currencyConverter;
    ConfigInterface config;
    ServerUtils server;
    ExpenseListCell sut;


    Event testEvent;
    Expense expense;
    ParticipantPayment p;
    ParticipantPayment p3;
    Participant p1;
    Participant p2;
    StringProperty first;
    StringProperty last;
    @Start
    public void setUp(Stage stage){
        mainCtrl = mock(MainCtrl.class);
        languageManager = mock(LanguageManager.class);
        currencyConverter = mock(CurrencyConverter.class);
        config = mock(Config.class);
        server = mock(ServerUtils.class);

        sut = new ExpenseListCell(mainCtrl,languageManager,currencyConverter,config,server);

        testEvent = new Event("testEvent", null, null);
        when(mainCtrl.getEvent()).thenReturn(testEvent);
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
    public void updateNullTest(){
        sut.createGraphic(expense);
        sut.setItem(expense);
        expense.setTag(new Tag("testTag", "#ff46fa"));
        when(config.getProperty(anyString())).thenReturn("USD");
        when(currencyConverter.convert(any(), anyString(), anyString(), anyDouble())).thenReturn(expense.getAmount());


        sut.setItem(null);
        sut.update();
        assertEquals(sut.getExpenseName().getText(), "<no title>");
        assertEquals(sut.getPayeeName().getText(), "<no payee>");
        assertEquals(sut.getPrice().getText(), "<no price>");
        assertEquals(sut.getPayers().getText(), "none");
        assertEquals(sut.getDate().getText(), "<no date>");
        assertEquals(sut.getTagName().getText(), "<no tag>");
        assertEquals(sut.getRectangle2().fillProperty().get(), Paint.valueOf("#ffffff"));

    }

    @Test
    public void conversionTransferTest(){
        sut.createGraphic(expense);
        expense.setTag(new Tag("testTag", "#ff46fa"));
        when(config.getProperty(anyString())).thenReturn("USD");

        when(currencyConverter.convert(any(),anyString(),anyString(),anyDouble())).thenThrow(CouldNotConvertException.class);
        sut.setItem(expense);
        sut.updateTransfer();
        assertEquals(sut.getPrice().getText(), "15.00");
    }

    @Test
    public void conversionSettlementTest(){
        sut.createGraphic(expense);
        expense.setTag(new Tag("testTag", "#ff46fa"));
        when(config.getProperty(anyString())).thenReturn("USD");

        when(currencyConverter.convert(any(),anyString(),anyString(),anyDouble())).thenThrow(CouldNotConvertException.class);
        sut.setItem(expense);
        sut.updateSettlement();
        assertEquals(sut.getPrice().getText(), "15.00");
    }

    @Test
    public void conversionUpdateTest(){
        sut.createGraphic(expense);
        expense.setTag(new Tag("testTag", "#ff46fa"));
        when(config.getProperty(anyString())).thenReturn("USD");

        when(currencyConverter.convert(any(),anyString(),anyString(),anyDouble())).thenThrow(CouldNotConvertException.class);
        sut.setItem(expense);
        sut.update();
        assertEquals(sut.getPrice().getText(), "15.00");
    }

    @Test
    public void updateRegularTest(){
        sut.createGraphic(expense);
        sut.setItem(expense);
        expense.setTag(new Tag("testTag", "#ff46fa"));
        when(config.getProperty(anyString())).thenReturn("USD");
        when(currencyConverter.convert(any(), anyString(), anyString(), anyDouble())).thenReturn(expense.getAmount());

        sut.update();

        assertEquals(sut.getTagName().getText(), "testTag");
        assertEquals(sut.getExpenseName().getText(), "Transfer");
        assertEquals(sut.getPayeeName().getText(), "jill");
        assertEquals(sut.getPrice().getText(), "15.00");
        assertEquals(sut.getCurrency().getText(), "USD");
        assertEquals(sut.getPayers().getText(), "(bob, jill)");
        assertEquals(sut.getRectangle2().fillProperty().get(), Paint.valueOf("#ff46fa"));
        assertEquals(sut.getTagName().fillProperty().get(), Paint.valueOf("#ffffff"));

        expense.getTag().setColor("#ffa9a3");
        sut.update();
        assertEquals(sut.getTagName().fillProperty().get(), Paint.valueOf("#000000"));

        expense.setSplit(List.of());

        sut.update();
        assertEquals(sut.getPayers().getText(), "none");

        when(currencyConverter.convert(any(),anyString(),anyString(),anyDouble())).thenThrow(NumberFormatException.class);
        sut.setItem(expense);
        sut.update();
        assertEquals(sut.getPrice().getText(), "<invalid price>");



    }

    @Test
    public void updateTransferTest(){

        sut.createTransfer(expense);
        sut.setItem(expense);
        when(config.getProperty(anyString())).thenReturn("USD");
        when(currencyConverter.convert(any(), anyString(), anyString(), anyDouble())).thenReturn(expense.getAmount());

        sut.updateTransfer();
        assertEquals(sut.getTagName().textProperty().getValue(), "test value");


        assertEquals(sut.getExpenseName().getText(), "bob");
        assertEquals(sut.getPayeeName().getText(), "jill");
        assertEquals(sut.getPrice().getText(), "15.00");
        assertEquals(sut.getCurrency().getText(), "USD");
        assertEquals(sut.getPayers().getText(), "(bob, jill)");

        expense.setSplit(List.of());

        sut.updateTransfer();
        assertEquals(sut.getPayers().getText(), "none");

        sut.setItem(null);
        sut.updateTransfer();
        assertEquals(sut.getExpenseName().getText(), "<no receiver>");
        assertEquals(sut.getPayeeName().getText(), "<no payee>");
        assertEquals(sut.getPrice().getText(), "<no price>");
        assertEquals(sut.getPayers().getText(), "none");
        assertEquals(sut.getDate().getText(), "<no date>");

        when(currencyConverter.convert(any(),anyString(),anyString(),anyDouble())).thenThrow(NumberFormatException.class);
        sut.setItem(expense);
        sut.updateTransfer();
        assertEquals(sut.getPrice().getText(), "<invalid price>");


    }

    @Test
    public void createTransferBasicTest(){
        sut.createTransfer(expense);

        assertEquals(sut.getPaidLabel().textProperty().getValue(), "test value");
        assertEquals(sut.getForLabel().textProperty().getValue(), "test value");

    }

    @Test
    public void transferRemoveTest() {

        sut.createTransfer(expense);

        doNothing().when(server).removeExpense(anyInt(),anyLong());

        ExpenseListCell spy = spy(sut);
        spy.getRemove().getOnAction().handle(new ActionEvent());

        verify(server, times(1)).removeExpense(anyInt(),anyLong());

    }

    @Test
    public void transferEditTest() {
        expense.setDescription("transfer");
        sut.createTransfer(expense);

        ExpenseListCell spy = spy(sut);

        doNothing().when(spy).createSettlement(any());
        doNothing().when(spy).updateSettlement();
        EditTransferCtrl editTransferCtrl = mock(EditTransferCtrl.class);
        when(mainCtrl.getEditTransferCtrl()).thenReturn(editTransferCtrl);
        doNothing().when(editTransferCtrl).setExpense(any());

        spy.getEdit().getOnAction().handle(new ActionEvent());

        verify(mainCtrl, times(1)).getEditTransferCtrl();
        verify(mainCtrl, times(1)).showEditTransfer();
    }

    @Test
    public void createSettlementBasicTest(){

        sut.createSettlement(expense);

        assertEquals(sut.getPaidLabel().textProperty().getValue(), "test value");
        assertEquals(sut.getForLabel().textProperty().getValue(), "test value");

        assertTrue(sut.getEdit().isDisable());
        assertEquals(sut.getEdit().styleProperty().getValue(), "-fx-background-color: none; -fx-text-fill: none");

    }

    @Test
    public void updateSettlementTest(){

        sut.createSettlement(expense);
        sut.setItem(expense);
        when(config.getProperty(anyString())).thenReturn("USD");
        when(currencyConverter.convert(any(), anyString(), anyString(), anyDouble())).thenReturn(expense.getAmount());

        sut.updateSettlement();
        assertEquals(sut.getTagName().textProperty().getValue(), "test value");


        assertEquals(sut.getExpenseName().getText(), "bob");
        assertEquals(sut.getPayeeName().getText(), "jill");
        assertEquals(sut.getPrice().getText(), "15.00");
        assertEquals(sut.getCurrency().getText(), "USD");
        assertEquals(sut.getPayers().getText(), "(bob, jill)");

        expense.setSplit(List.of());

        sut.updateSettlement();
        assertEquals(sut.getPayers().getText(), "none");

        sut.setItem(null);
        sut.updateSettlement();
        assertEquals(sut.getExpenseName().getText(), "<no receiver>");
        assertEquals(sut.getPayeeName().getText(), "<no payee>");
        assertEquals(sut.getPrice().getText(), "<no price>");
        assertEquals(sut.getPayers().getText(), "none");
        assertEquals(sut.getDate().getText(), "<no date>");

        when(currencyConverter.convert(any(),anyString(),anyString(),anyDouble())).thenThrow(NumberFormatException.class);
        sut.setItem(expense);
        sut.updateSettlement();
        assertEquals(sut.getPrice().getText(), "<invalid price>");


    }

    @Test
    public void settlementRemoveTest() {

        sut.createSettlement(expense);

        doNothing().when(server).removeExpense(anyInt(),anyLong());

        ExpenseListCell spy = spy(sut);
        spy.getRemove().getOnAction().handle(new ActionEvent());

        verify(server, times(1)).removeExpense(anyInt(),anyLong());

    }

        @Test
    public void createGraphicBasicTest(){


        sut.createGraphic(expense);

        assertEquals(sut.getPaidLabel().textProperty().getValue(), "test value");
        assertEquals(sut.getForLabel().textProperty().getValue(), "test value");

        assertEquals(sut.getPayeeName().styleProperty().getValue(), "-fx-font-weight: 700;");
        assertEquals(sut.getExpenseName().styleProperty().getValue(), "-fx-font-weight: 700;");
        assertEquals(sut.getPrice().styleProperty().getValue(), "-fx-font-weight: 700;");
        assertEquals(sut.getCurrency().styleProperty().getValue(), "-fx-font-weight: 700;");

    }

    @Test
    public void testRemoveButton(){

        sut.createGraphic(expense);

        doNothing().when(server).removeExpense(anyInt(),anyLong());

        ExpenseListCell spy = spy(sut);
        spy.getRemove().getOnAction().handle(new ActionEvent());

        verify(server, times(1)).removeExpense(anyInt(),anyLong());
    }

    @Test
    public void testEditButtonGraphic(){

        sut.createGraphic(expense);

        ExpenseListCell spy = spy(sut);
        spy.getEdit().getOnAction().handle(new ActionEvent());

        verify(mainCtrl, times(1)).getEditExpenseCtrl();
        verify(mainCtrl, times(1)).showEditExpense();
    }

    @Test
    public void testUpdatesTransfer(){
        ExpenseListCell spy = spy(sut);
        spy.updateItem(null, false);

        doNothing().when(spy).createTransfer(any());
        doNothing().when(spy).updateTransfer();

        expense.setDescription("transfer");
        spy.updateItem(expense, false);

        verify(spy).createTransfer(any());
        verify(spy).updateTransfer();
    }

    @Test
    public void testUpdatesSettlement(){
        ExpenseListCell spy = spy(sut);

        doNothing().when(spy).createSettlement(any());
        doNothing().when(spy).updateSettlement();

        expense.setDescription("settlement");
        spy.updateItem(expense, false);

        verify(spy).createSettlement(any());
        verify(spy).updateSettlement();
    }

    @Test
    public void testUpdatesDefault(){
        ExpenseListCell spy = spy(sut);

        doNothing().when(spy).createGraphic(any());
        doNothing().when(spy).update();

        expense.setDescription("other");
        spy.updateItem(expense, false);

        verify(spy).createGraphic(any());
        verify(spy).update();
    }



}