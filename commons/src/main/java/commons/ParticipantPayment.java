package commons;

import java.util.Objects;

public class ParticipantPayment {

    public Participant participant;
    public float value;

    /**
     * Empty constructor for object mapper
     */
    @SuppressWarnings("unused")
    private ParticipantPayment() {
    }

    /**
     * Constructor to create new ParticipantPayment
     * @param participant Participant object
     * @param value float value they owe
     */
    public ParticipantPayment(Participant participant, float value) {
        this.participant = participant;
        this.value = value;
    }

    /**
     * Checks the equality between this and another object
     * @param o the other object to check
     * @return boolean true for equal, false for not equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParticipantPayment that = (ParticipantPayment) o;
        boolean participantEqual = Objects.equals(participant, that.participant);
        boolean valueEqual = Float.compare(value, that.value) == 0;

        return  participantEqual && valueEqual;

    }

    /**
     * Generates unique hashcode for this object
     * @return int hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(participant, value);
    }

    /**
     * Generate human-readable string to display participant payment and its values
     * @return human-readable string
     */
    @Override
    public String toString() {
        return "ParticipantPayment{" +
                "participant=" + participant +
                ", value=" + value +
                '}';
    }
}
