package client.utils;

import commons.Participant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebtPairTest {

    Participant bob = new Participant("Bob", null, null, null);
    Participant mary = new Participant("Mary", null, null, null);
    Participant tom = new Participant("Tom", null, null, null);

    @Test
    void getKey() {
        DebtPair debtPair = new DebtPair(bob, 1.2);
        bob.setId(1);
        assertEquals(bob, debtPair.getKey());
    }

    @Test
    void setKey() {
        DebtPair debtPair = new DebtPair(bob, 1.2);
        bob.setId(1);
        mary.setId(2);
        debtPair.setKey(mary);
        assertEquals(mary, debtPair.getKey());
    }

    @Test
    void getValue() {
        DebtPair debtPair = new DebtPair(bob, 1.2);
        assertEquals(1.2, debtPair.getValue());
    }

    @Test
    void setValue() {
        DebtPair debtPair = new DebtPair(bob, 1.2);
        debtPair.setValue(2.4);
        assertEquals(2.4, debtPair.getValue());
    }

    @Test
    void compareTo() {
        DebtPair debtPair1 = new DebtPair(bob, 1.2);
        DebtPair debtPair2 = new DebtPair(tom, 2.4);
        assertTrue(debtPair2.compareTo(debtPair1) < 0);
    }
}