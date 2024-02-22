package commons;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        currency = "euro";
        title = "Taxi";
        description = "ride back home";
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
        tag = new Tag("food", "blue");
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
}