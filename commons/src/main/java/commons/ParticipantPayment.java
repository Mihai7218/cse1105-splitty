package commons;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class ParticipantPayment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;


    private double paymentAmount;
    @ManyToOne
    private Participant participant;

    /**
     * Empty constructor for object mapper
     */
    @SuppressWarnings("unused")
    public ParticipantPayment() {
    }

    /**
     * Constructor to create new ParticipantPayment
     * @param participant Participant object
     * @param value float value they owe
     */
    public ParticipantPayment(Participant participant, double value) {
        this.participant = participant;
        this.paymentAmount = value;
    }

    /**
     * retrieve the unique id for the transaction
     * @return long id value
     */
    public Long getId() {
        return id;
    }

    /**
     * setter for the id for the participantPayment
     * @param id the new id to change to
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * retrieve the value associated with the transaction
     * @return double value for the amount owed
     */
    public double getPaymentAmount() {
        return paymentAmount;
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
    public void setPaymentAmount(double value) {
        this.paymentAmount = value;
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
        boolean valueEqual = paymentAmount == that.paymentAmount;

        return  participantEqual && valueEqual;

    }

    /**
     * Generates unique hashcode for this object
     * @return int hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(participant, paymentAmount);
    }

    /**
     * Generate human-readable string to display participant payment and its values
     * @return human-readable string
     */
    @Override
    public String toString() {
        return "ParticipantPayment{" +
                "participant=" + participant +
                ", value=" + paymentAmount +
                '}';
    }
}
