package client.utils;

import commons.Participant;

public class DebtPair implements Comparable<DebtPair> {
    private Participant key;
    private double value;

    /**
     * Constructor for the DebtPair.
     * @param key - the participant.
     * @param value - the sum.
     */
    public DebtPair(Participant key, double value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Getter for the participant.
     * @return - the participant.
     */
    public Participant getKey() {
        return key;
    }

    /**
     * Setter for the participant.
     * @param key - the new participant.
     */
    public void setKey(Participant key) {
        this.key = key;
    }

    /**
     * Getter for the amount.
     * @return - the amount.
     */
    public double getValue() {
        return value;
    }

    /**
     * Setter for the amount.
     * @param value - the new amount.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     *
     * @param o the object to be compared.
     * @return
     */
    @Override
    public int compareTo(DebtPair o) {
        return -Double.compare(this.value, o.value);
    }
}
