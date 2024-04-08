package client.utils;

import commons.Participant;

public class Debt {
    private Participant debtor;
    private Participant creditor;
    private double sum;

    /**
     * Constructor
     * @param debtor - the person who is owed money
     * @param creditor - the person who owes money
     * @param sum - the sum of the debt
     */
    public Debt(Participant debtor, Participant creditor, double sum) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.sum = sum;
    }

    /**
     * Getter for the debtor.
     * @return - the debtor.
     */
    public Participant getDebtor() {
        return debtor;
    }

    /**
     * Setter for the debtor.
     * @param debtor - the new debtor.
     */
    public void setDebtor(Participant debtor) {
        this.debtor = debtor;
    }

    /**
     * Getter for the creditor.
     * @return - the creditor.
     */
    public Participant getCreditor() {
        return creditor;
    }

    /**
     * Setter for the creditor.
     * @param creditor - the new creditor.
     */
    public void setCreditor(Participant creditor) {
        this.creditor = creditor;
    }

    /**
     * Getter for the sum of the debt.
     * @return - the sum.
     */
    public double getSum() {
        return sum;
    }

    /**
     * Setter for the sum of the debt.
     * @param sum - the new value of the sum.
     */
    public void setSum(double sum) {
        this.sum = sum;
    }
}
