package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
        date = new Date(230);
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
    void participantPaymentTest() {
        Participant marco = new Participant("Marco","Marco","Marco","Marco");
        Expense expense1 = new Expense();
        Expense expense2 = new Expense();
        List<ParticipantPayment> list1 = new ArrayList<>();
        List<ParticipantPayment> list2 = new ArrayList<>();
        list1.add(new ParticipantPayment(marco,1));
        list2.add(new ParticipantPayment(marco,1));
        expense1.setSplit(list1);
        expense2.setSplit(list2);
        assertTrue(expense1.participantPaymentEquals(expense1.getSplit(),expense2.getSplit()));
        list1.add(new ParticipantPayment(marco,1));
        list2.add(new ParticipantPayment(marco,2));
        assertFalse(expense1.participantPaymentEquals(expense1.getSplit(),expense2.getSplit()));
        list1.add(new ParticipantPayment(marco,3));
        list2.add(new ParticipantPayment(marco,1));
        assertFalse(expense1.participantPaymentEquals(expense1.getSplit(),expense2.getSplit()));
        expense2.setSplit(null);
        assertFalse(expense1.participantPaymentEquals(expense1.getSplit(),expense2.getSplit()));
        expense2.setSplit(null);
        assertFalse(expense1.participantPaymentEquals(expense1.getSplit(),expense2.getSplit()));
        expense2.setSplit(null);
        expense1.setSplit(null);
        assertTrue(expense1.participantPaymentEquals(expense1.getSplit(),expense2.getSplit()));
    }

    @Test
    void ConstructorTest(){
        assertEquals(expense.getAmount(), amount);
        assertEquals(expense.getCurrency(), currency);
        assertEquals(expense.getTitle(), title);
        assertEquals(expense.getDescription(), description);
        assertEquals(expense.getDate(), date);
        assertEquals(expense.getTag(), tag);
        assertEquals(expense.getPayee(), payee);
        assertEquals(expense.getSplit(), split);
    }
    @Test
    void setAmountTest(){
        expense.setAmount(30.24);
        assertEquals(30.24, expense.getAmount());
    }
    @Test
    void setCurrencyTest(){
        expense.setCurrency("rupes");
        assertEquals("rupes", expense.getCurrency());
    }
    @Test
    void setTitleTest(){
        expense.setTitle("Dinner");
        assertEquals("Dinner", expense.getTitle());
    }
    @Test
    void setDateTest(){
        expense.setDate(new Date(2300));
        assertEquals(new Date(2300), expense.getDate());
    }
    @Test
    void setDescriptionTest(){
        expense.setDescription("octopus with tuna");
        assertEquals("octopus with tuna", expense.getDescription());
    }
    @Test
    void setTagTest(){
        Tag tag = new Tag("food", "green");
        expense.setTag(tag);
        assertEquals(tag, expense.getTag());
    }
    @Test
    void setPayeeTest(){
        Participant participant1 = new Participant("George", "george@gmail.com",
                "NL27RABO2766662669", "RRRAAAA");
        expense.setPayee(participant1);
        assertEquals(participant1, expense.getPayee());
    }

    @Test
    void EqualsTest(){
        double amount = 40.45;
        String currency = "Euro";
        String title = "Taxi";
        String description = "Ride back home";
        Date date = new Date(230);
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
        Date date = new Date(230);
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
        assertFalse(expense2.fullEquals(expense));
    }
    @Test
    void EqualHashCodeTest(){
        double amount = 40.45;
        String currency = "Euro";
        String title = "Taxi";
        String description = "Ride back home";
        Date date = new Date(230);
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
        Date date = new Date(230);
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
//    @Test
//    void toStringTest(){
//        String s = "Expense{id=0, amount=40.45, currency='Euro', title='Taxi', description='Ride back home', date=Thu Jan 01 01:00:00 CET 1970, split=[ParticipantPayment{participant=Participant{name='George', email='george@gmail.com', iban='NL27RABO2766662669', bic='RABONL2U'}, value=30.399999618530273}, ParticipantPayment{participant=Participant{name='Ilinca', email='ilinca@gmail.com', iban='NL27RABO2711112669', bic='RABONL2U'}, value=10.449999809265137}], tag=Tag{name='transport', color='blue'}, payee=Participant{name='Rudolf', email='rudolf@gmail.com', iban='NL27RABO2766662000', bic='RABONL2U'}}";
//        expense.setId(0);
//        assertEquals(s, expense.toString());
//    }
}