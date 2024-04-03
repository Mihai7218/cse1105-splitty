package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantPaymentTest {

    public Participant PARTICIPANT = new Participant("name",
            "email@gmail", "123456789", "12345");
    public Participant PARTICIPANT_2 = new Participant("newName",
            "email2@gmail", "123456779", "23456");

    @BeforeEach
    public void init(){
        PARTICIPANT.setId(0);
        PARTICIPANT_2.setId(1);
    }
    @Test
    public void checkConstructor(){
        ParticipantPayment PP1 = new ParticipantPayment(PARTICIPANT, 20.00);
        assertEquals(PP1.getParticipant(), PARTICIPANT);
        assertEquals(PP1.getPaymentAmount(), 20.00f);
    }

    @Test
    public void checkValueGetter(){
        ParticipantPayment PP1 = new ParticipantPayment(PARTICIPANT, 20.00);
        assertEquals(PP1.getPaymentAmount(), 20.00);
    }

    @Test
    public void checkValueSetter(){
        ParticipantPayment PP1 = new ParticipantPayment(PARTICIPANT, 20.00);
        PP1.setPaymentAmount(15);
        assertEquals(PP1.getPaymentAmount(), 15.00);
    }

    @Test
    public void checkParticipantGetter(){
        ParticipantPayment PP1 = new ParticipantPayment(PARTICIPANT, 20.00);
        assertEquals(PP1.getParticipant(), PARTICIPANT);
    }

    @Test
    public void checkParticipantSetter(){
        ParticipantPayment PP1 = new ParticipantPayment(PARTICIPANT, 20.00);
        PP1.setParticipant(PARTICIPANT_2);
        assertEquals(PP1.getParticipant(), PARTICIPANT_2);
    }

    @Test
    public void equalsHashcodeFalse() {
        ParticipantPayment PP1 = new ParticipantPayment(PARTICIPANT, 20.00);
        ParticipantPayment PP2 = new ParticipantPayment(PARTICIPANT_2, 20.00);

        assertNotEquals(PP1, PP2);
        assertNotEquals(PP1.hashCode(), PP2.hashCode());
    }

    @Test
    public void equalsHashcodeTrue(){
        ParticipantPayment PP1 = new ParticipantPayment(PARTICIPANT, 20.00);
        ParticipantPayment PP2 = new ParticipantPayment(PARTICIPANT, 20.00);

        assertEquals(PP1, PP2);
        assertEquals(PP1.hashCode(), PP2.hashCode());
    }

    @Test
    public void equalsHashcodeOtherObject(){
        ParticipantPayment PP1 = new ParticipantPayment(PARTICIPANT, 20.00);
        assertNotEquals(PP1, PARTICIPANT);
        assertNotEquals(PP1.hashCode(), PARTICIPANT.hashCode());
    }

    @Test
    public void equalsHashcodeNull(){
        ParticipantPayment PP3 = new ParticipantPayment(PARTICIPANT_2, 5.00);
        ParticipantPayment PP4 = null;
        assertNotEquals(PP3, PP4);
    }

    @Test
    public void equalsHashcodeNullField(){
        ParticipantPayment PP3 = new ParticipantPayment(PARTICIPANT_2, 5.00);
        ParticipantPayment PP4 = new ParticipantPayment(null, 5.00);
        assertNotEquals(PP3, PP4);
        assertNotEquals(PP3.hashCode(), PP4.hashCode());
    }

    @Test
    public void checkToString(){
        ParticipantPayment PP1 = new ParticipantPayment(PARTICIPANT, 20.00);
        assertEquals("ParticipantPayment{participant=Participant" +
                "{name='name', email='email@gmail', iban='123456789', bic='12345'}, " +
                "value=20.0}", PP1.toString());
    }

}