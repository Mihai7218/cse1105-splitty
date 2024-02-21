package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParticipantPaymentTest {

    public static final Participant PARTICIPANT = new Participant("name", "email@gmail",
            "123456789", "12345");
    public static final Participant PARTICIPANT_2 = new Participant("newName", "email2@gmail",
            "123456779", "23456");
    @Test
    public void checkConstructor(){
        ParticipantPayment PP1 = new ParticipantPayment(PARTICIPANT, 20.00f);
        assertEquals(PP1.participant, PARTICIPANT);
        assertEquals(PP1.value, 20.00f);
    }

    @Test
    public void equalsHashcodeTrue() {
        ParticipantPayment PP1 = new ParticipantPayment(PARTICIPANT, 20.00f);
        ParticipantPayment PP2 = new ParticipantPayment(PARTICIPANT_2, 15.00f);

        assertNotEquals(PP1, PP2);
        assertNotEquals(PP1.hashCode(), PP2.hashCode());
    }

}