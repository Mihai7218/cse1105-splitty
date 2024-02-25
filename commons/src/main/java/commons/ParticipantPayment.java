package commons;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class ParticipantPayment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private double value;
    @ManyToOne
    private Participant participant;

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
    public ParticipantPayment(Participant participant, double value) {
        this.participant = participant;
        this.value = value;
    }

    /**
     * retrieve the unique id for the transaction
     * @return long id value
     */
    public Long getId() {
        return id;
    }

    /**
     * retrieve the value associated with the transaction
     * @return double value for the amount owed
     */
    public double getValue() {
        return value;
    }

    /**
     * retrieve the participant involved in the transaction
     * @return participant object
     */
    public Participant getParticipant() {
        return participant;
    }

    /**
     * Setter for the value associated with the transaction
     * @param value double value for the amount owed
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Setter for the participant involved in the transaction
     * @param participant the participant involved
     */
    public void setParticipant(Participant participant) {
        this.participant = participant;
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
        boolean valueEqual = value == that.value;

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
