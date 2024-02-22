package commons;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {
    double amount;
    String currency;
    String title;
    String description;
    Date date;
    List<ParticipantPayment> split;
    Tag tag;
    Participant payee;
    Expense expense;
    @BeforeEach
    public void init(){
        amount = 40.45;
        currency = "Euro";
        title = "Taxi";
        description = "Ride back home";
        date = new Date(2024, Calendar.FEBRUARY,14);
        split = new ArrayList<>();
        Participant participant1 = new Participant("George", "george@gmail.com",
                "NL27RABO2766662669", "RABONL2U");
        Participant participant2 = new Participant("Ilinca", "ilinca@gmail.com",
                "NL27RABO2711112669", "RABONL2U");
        ParticipantPayment payment1 = new ParticipantPayment(participant1, 30.40f);
        ParticipantPayment payment2 = new ParticipantPayment(participant2, 10.45f);
        split.add(payment1);
        split.add(payment2);
        tag = new Tag("transport", "blue");
        payee = new Participant("Rudolf", "rudolf@gmail.com",
                "NL27RABO2766662000", "RABONL2U");
        expense = new Expense(amount, currency, title, description, date, split, tag, payee);
    }

    @Test
    void ConstructorTest(){
        assertEquals(expense.amount, amount);
        assertEquals(expense.currency, currency);
        assertEquals(expense.title, title);
        assertEquals(expense.description, description);
        assertEquals(expense.date, date);
        assertEquals(expense.tag, tag);
        assertEquals(expense.payee, payee);
        assertEquals(expense.getSplit(), split);
    }

    @Test
    void EqualsTest(){
        double amount = 40.45;
        String currency = "Euro";
        String title = "Taxi";
        String description = "Ride back home";
        Date date = new Date(2024, Calendar.FEBRUARY,14);
        ArrayList<ParticipantPayment> split = new ArrayList<>();
        Participant participant1 = new Participant("George", "george@gmail.com",
                "NL27RABO2766662669", "RABONL2U");
        Participant participant2 = new Participant("Ilinca", "ilinca@gmail.com",
                "NL27RABO2711112669", "RABONL2U");
        ParticipantPayment payment1 = new ParticipantPayment(participant1, 30.40f);
        ParticipantPayment payment2 = new ParticipantPayment(participant2, 10.45f);
        split.add(payment1);
        split.add(payment2);
        Tag tag = new Tag("transport", "blue");
        Participant payee = new Participant("Rudolf", "rudolf@gmail.com",
                "NL27RABO2766662000", "RABONL2U");
        Expense expense2 = new Expense(amount, currency, title, description, date, split, tag, payee);
        assertEquals(expense2, expense);
    }
    @Test
    void NotEqualsTest(){
        double amount = 40.45;
        String currency = "Euro";
        String title = "Taxi";
        String description = "Ride back home";
        Date date = new Date(2024, Calendar.FEBRUARY,14);
        ArrayList<ParticipantPayment> split = new ArrayList<>();
        Participant participant1 = new Participant("George", "george@gmail.com",
                "NL27RABO2766662669", "RABONL2U");
        Participant participant2 = new Participant("Ilinca", "ilinca@gmail.com",
                "NL27RABO2711112669", "RABONL2U");
        ParticipantPayment payment1 = new ParticipantPayment(participant1, 30.40f);
        ParticipantPayment payment2 = new ParticipantPayment(participant2, 10.45f);
        split.add(payment1);
        split.add(payment2);
        Tag tag = new Tag("transport", "yellow");
        Participant payee = new Participant("Rudolf", "rudolf@gmail.com",
                "NL27RABO2766662000", "RABONL2U");
        Expense expense2 = new Expense(amount, currency, title, description, date, split, tag, payee);
        assertNotEquals(expense2, expense);
    }
    @Test
    void EqualHashCodeTest(){
        double amount = 40.45;
        String currency = "Euro";
        String title = "Taxi";
        String description = "Ride back home";
        Date date = new Date(2024, Calendar.FEBRUARY,14);
        ArrayList<ParticipantPayment> split = new ArrayList<>();
        Participant participant1 = new Participant("George", "george@gmail.com",
                "NL27RABO2766662669", "RABONL2U");
        Participant participant2 = new Participant("Ilinca", "ilinca@gmail.com",
                "NL27RABO2711112669", "RABONL2U");
        ParticipantPayment payment1 = new ParticipantPayment(participant1, 30.40f);
        ParticipantPayment payment2 = new ParticipantPayment(participant2, 10.45f);
        split.add(payment1);
        split.add(payment2);
        Tag tag = new Tag("transport", "blue");
        Participant payee = new Participant("Rudolf", "rudolf@gmail.com",
                "NL27RABO2766662000", "RABONL2U");
        Expense expense2 = new Expense(amount, currency, title, description, date, split, tag, payee);
        assertEquals(expense2.hashCode(), expense.hashCode());
    }
    @Test
    void NotEqualHashCodeTest(){
        double amount = 40.45;
        String currency = "Euro";
        String title = "Taxi";
        String description = "Ride back home";
        Date date = new Date(2024, Calendar.FEBRUARY,14);
        ArrayList<ParticipantPayment> split = new ArrayList<>();
        Participant participant1 = new Participant("George", "george@gmail.com",
                "NL27RABO2766662669", "RABONL2U");
        Participant participant2 = new Participant("Ilinca", "ilinca@gmail.com",
                "NL27RABO2711112669", "RABONL2U");
        ParticipantPayment payment1 = new ParticipantPayment(participant1, 30.40f);
        ParticipantPayment payment2 = new ParticipantPayment(participant2, 10.45f);
        split.add(payment1);
        split.add(payment2);
        Tag tag = new Tag("transport", "yellow");
        Participant payee = new Participant("Rudolf", "rudolf@gmail.com",
                "NL27RABO2766662000", "RABONL2U");
        Expense expense2 = new Expense(amount, currency, title, description, date, split, tag, payee);
        assertNotEquals(expense2.hashCode(), expense.hashCode());
    }
    @Test
    void toStringTest(){
        String s = "Expense{id=0, amount=40.45, currency='Euro', title='Taxi', " +
                "description='Ride back home', date=Thu Feb 14 00:00:00 CET 3924, " +
                "split=[ParticipantPayment{participant=Participant{name='George', " +
                "email='george@gmail.com', iban='NL27RABO2766662669', bic='RABONL2U'}, " +
                "value=30.4}, ParticipantPayment{participant=Participant{name='Ilinca', " +
                "email='ilinca@gmail.com', iban='NL27RABO2711112669', bic='RABONL2U'}, " +
                "value=10.45}], tag=Tag{name='transport', color='blue'}, " +
                "payee=Participant{name='Rudolf', email='rudolf@gmail.com', " +
                "iban='NL27RABO2766662000', bic='RABONL2U'}}";
        assertEquals(s, expense.toString());
    }
}