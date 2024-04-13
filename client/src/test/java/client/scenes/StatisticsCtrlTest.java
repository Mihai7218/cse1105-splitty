package client.scenes;

import client.utils.ConfigInterface;
import client.utils.CurrencyConverter;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import commons.Event;
import commons.Expense;
import commons.Tag;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.messaging.simp.stomp.StompSession;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
public class StatisticsCtrlTest {

    //Needed for the tests to run headless.
    static {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
    }

    MainCtrl mainCtrl;
    ServerUtils serverUtils;
    ConfigInterface config;
    LanguageManager languageManager;
    StatisticsCtrl sut;

    ObservableList op = FXCollections.observableArrayList();
    ObservableList oc = FXCollections.observableArrayList();
    ObservableList ot = FXCollections.observableArrayList();
    ObservableList noc = FXCollections.observableArrayList();
    Button manageTags;
    StompSession.Subscription subscription;
    javafx.scene.chart.PieChart pieChart;
    Button cancel;
    VBox ownLegend;
    String currency;

    StompSession.Subscription tagSubscription;

    Map<Tag, StompSession.Subscription> tagSubscriptionMap;

    StompSession.Subscription expensesSubscription;
    Map<Expense, StompSession.Subscription> expenseSubscriptionMap;
    CurrencyConverter currencyConverter;

    StringProperty sp;
    StompSession session;


    @Start
    void setUp(Stage stage) {
        session = mock(StompSession.class);
        mainCtrl = mock(MainCtrl.class);
        serverUtils = mock(ServerUtils.class);
        languageManager = mock(LanguageManager.class);
        currencyConverter = mock(CurrencyConverter.class);
        config = new TestConfig();
        sut = new StatisticsCtrl(mainCtrl, config, languageManager,
                serverUtils, currencyConverter);

        manageTags = mock(Button.class);
        currency = "";
        subscription = mock(StompSession.Subscription.class);
        pieChart = mock(PieChart.class);
        cancel = mock(Button.class);
        ownLegend = mock(VBox.class);
        tagSubscription = mock(StompSession.Subscription.class);
        expensesSubscription = mock(StompSession.Subscription.class);
        tagSubscriptionMap = mock(HashMap.class);
        expenseSubscriptionMap = mock(HashMap.class);

        sp = new SimpleStringProperty("Hello");
        when(pieChart.titleProperty()).thenReturn(sp);
        ObservableList<PieChart.Data> tagObservableList = FXCollections.observableArrayList();
        tagObservableList.add(new PieChart.Data("test",1));
        when(pieChart.getData()).thenReturn(tagObservableList);
        ObservableList<Node> nodeObservableList = FXCollections.observableArrayList();
        when(ownLegend.getChildren()).thenReturn(nodeObservableList);

        sut.setManageTags(manageTags);
        sut.setCurrency(currency);
        sut.setSubscription(subscription);
        sut.setPieChart(pieChart);
        sut.setCancel(cancel);
        sut.setOwnLegend(ownLegend);
        sut.setTagSubscription(tagSubscription);
        sut.setExpensesSubscription(expensesSubscription);
        sut.setTagSubscriptionMap(tagSubscriptionMap);
        sut.setExpenseSubscriptionMap(expenseSubscriptionMap);

        doNothing().when(manageTags).setGraphic(any(Node.class));
        doNothing().when(cancel).setGraphic(any(Node.class));
        sut.initialize(mock(URL.class), mock(ResourceBundle.class));
    }

    @Test
    void initialize() {
        assertFalse(ownLegend.isVisible());
        assertFalse(pieChart.isVisible());
        assertFalse(cancel.isVisible());
        assertFalse(manageTags.isVisible());
    }

    @Test
    void keyTestEscape() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.ESCAPE);
        StatisticsCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, atLeastOnce()).backToOverview();
    }

    @Test
    void keyTestM() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.M);
        when(ke.isControlDown()).thenReturn(true);
        StatisticsCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, atLeastOnce()).startMenu();
    }

    @Test
    void keyTestT() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.T);
        when(ke.isControlDown()).thenReturn(true);
        StatisticsCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, atLeastOnce()).showManageTagsScreen();
    }

    @Test
    void languageManagerTest() {
        sut.setLanguageManager(null);
        assertEquals(sut.getLanguageManager(), null);
        assertEquals(sut.languageManagerProperty(), languageManager);
    }

    @Test
    void setupTest() {
        Event test = new Event("test", null, null);
        Tag newTag = new Tag("TAG", "blue");
        newTag.setId(1);
        test.addTag(newTag);
        test.addExpense(new Expense(1, "EUR", "Title", "Desc", null, null, null, null));
        test.addExpense(new Expense(1, "EUR", "Title", "Desc", null, null, newTag, null));
        test.setInviteCode(1);
        when(serverUtils.getEvent(1)).thenReturn(test);
        when(mainCtrl.getEvent()).thenReturn(test);
        when(languageManager.bind("statistics.chartTitle")).thenReturn(new StringBinding() {
            @Override
            protected String computeValue() {
                return "total";
            }
        });
        sut.setTagSubscription(null);
        sut.setExpensesSubscription(null);
        sut.setup();
        verify(serverUtils).registerForMessages(eq("/topic/events/1/tags/1"), eq(Tag.class), any());
    }

    @Test
    void onExpenseChangeTest() {
        Event test = new Event("test", null, null);
        Tag newTag = new Tag("TAG", "blue");
        newTag.setId(1);
        test.addTag(newTag);
        Expense expense1 = new Expense(1, "EUR", "Title", "Desc", null, null, null, null);
        expense1.setId(1);
        test.addExpense(expense1);
        test.addExpense(new Expense(1, "EUR", "Title", "Desc", null, null, newTag, null));
        test.setInviteCode(1);

        when(serverUtils.getEvent(1)).thenReturn(test);
        when(mainCtrl.getEvent()).thenReturn(test);
        when(languageManager.bind("statistics.chartTitle")).thenReturn(new StringBinding() {
            @Override
            protected String computeValue() {
                return "total";
            }
        });

        StatisticsCtrl testSut = spy(sut);
        testSut.setTagSubscription(null);
        testSut.setExpensesSubscription(null);
        testSut.setup();

        Event testEvent = testSut.getMainCtrl().getEvent();
        assertEquals(testEvent.getExpensesList().size(),2);

        Expense expense2 = new Expense(1, "EUR", "NewTitle", "Desc", null, null, null, null);
        expense2.setId(1);

        testSut.onExpenseChange(expense1,expense2);

        verify(testSut, atLeastOnce()).setStatistics();
        assertEquals(testEvent.getExpensesList().size(),2);
        assertTrue(testEvent.getExpensesList().contains(expense2));
    }

    @Test
    void onNewExpenseReceive() {
        Event test = new Event("test", null, null);
        Tag newTag = new Tag("TAG", "blue");
        newTag.setId(1);
        test.addTag(newTag);
        Expense expense1 = new Expense(1, "EUR", "Title", "Desc", null, null, null, null);
        expense1.setId(1);
        test.addExpense(expense1);
        test.addExpense(new Expense(1, "EUR", "Title", "Desc", null, null, newTag, null));
        test.setInviteCode(1);

        when(serverUtils.getEvent(1)).thenReturn(test);
        when(mainCtrl.getEvent()).thenReturn(test);
        when(languageManager.bind("statistics.chartTitle")).thenReturn(new StringBinding() {
            @Override
            protected String computeValue() {
                return "total";
            }
        });

        StatisticsCtrl testSut = spy(sut);
        testSut.setTagSubscription(null);
        testSut.setExpensesSubscription(null);
        testSut.setup();

        Event testEvent = testSut.getMainCtrl().getEvent();
        assertEquals(testEvent.getExpensesList().size(),2);

        Expense expense2 = new Expense(1, "EUR", "NewTitle", "Desc", null, null, null, null);
        expense2.setId(2);

        testSut.onNewExpenseReceive(expense2);

        verify(testSut, atLeastOnce()).subscribeToExpense(expense2);
        verify(testSut, atLeastOnce()).setStatistics();
        assertEquals(testEvent.getExpensesList().size(),3);
        assertTrue(testEvent.getExpensesList().contains(expense2));
    }

    @Test
    void keyTestO() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.O);
        when(ke.isControlDown()).thenReturn(true);
        StatisticsCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, atLeastOnce()).backToOverview();
    }

    @Test
    void keyTestAnyThingElse() {
        KeyEvent ke = mock(KeyEvent.class);
        when(ke.getCode()).thenReturn(KeyCode.G);
        StatisticsCtrl test = spy(sut);
        test.keyPressed(ke);
        verify(test, never()).backToOverview();
        verify(test, never()).startMenu();
    }

//    @Test
//    void chooseOne() {
//        everyone.setSelected(true);
//        sut.everyoneCheck();
//        assertFalse(only.isSelected());
//        assertTrue(namesContainer.getChildren().isEmpty());
//
//        everyone.setSelected(false);
//        only.setSelected(true);
//        sut.onlyCheck();
//        assertFalse(everyone.isSelected());
//        if(everyone.isSelected()) noc.clear();
//        else addPeople(noc);
//        assertFalse(namesContainer.getChildren().isEmpty());
//    }
}
