package client.utils;

import commons.Participant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebtTest {

    Participant bob = new Participant("Bob", null, null, null);
    Participant mary = new Participant("Mary", null, null, null);
    Participant tom = new Participant("Tom", null, null, null);

    @Test
    void getDebtor() {
        bob.setId(1);
        mary.setId(2);
        Debt debt = new Debt(bob, mary, 1.9);
        assertEquals(bob, debt.getDebtor());
    }

    @Test
    void setDebtor() {
        bob.setId(1);
        mary.setId(2);
        Debt debt = new Debt(bob, mary, 1.9);
        debt.setDebtor(tom);
        assertEquals(tom, debt.getDebtor());
    }

    @Test
    void getCreditor() {
        bob.setId(1);
        mary.setId(2);
        Debt debt = new Debt(bob, mary, 1.9);
        assertEquals(mary, debt.getCreditor());
    }

    @Test
    void setCreditor() {
        bob.setId(1);
        mary.setId(2);
        Debt debt = new Debt(bob, mary, 1.9);
        debt.setCreditor(tom);
        assertEquals(tom, debt.getCreditor());
    }

    @Test
    void getSum() {
        bob.setId(1);
        mary.setId(2);
        Debt debt = new Debt(bob, mary, 1.9);
        assertEquals(1.9, debt.getSum());
    }

    @Test
    void setSum() {
        bob.setId(1);
        mary.setId(2);
        Debt debt = new Debt(bob, mary, 1.9);
        debt.setSum(2.9);
        assertEquals(2.9, debt.getSum());
    }
}