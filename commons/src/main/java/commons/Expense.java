package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;
    public double amount;
    public String currency;

    @OneToOne(cascade = CascadeType.ALL)
    public ExpenseTitleDescriptionDate titleDescriptionDate;
    @ManyToMany (cascade = CascadeType.REMOVE)
    public List<ParticipantPayment> split;
    @ManyToOne
    public Tag tag;
    @ManyToOne
    public Participant payee;

    /**
     * No-arg constructor of Expense
     */
    @SuppressWarnings("unused")
    public Expense() {
    }

    /**
     * Constructor of an expense object
     * @param amount - Amount of the expense
     * @param currency - Currency of the expense
     * @param etdd - Object storing the title, description and date of the expense
     * @param split - List of participants that need to pay and the amount owed
     * @param tag - Tag of the expense
     * @param payee - Participant that needs to be paid back
     */
    public Expense(double amount, String currency, ExpenseTitleDescriptionDate etdd,
                   List<ParticipantPayment> split, Tag tag, Participant payee) {
        this.currency = currency;
        this.amount = amount;
        this.titleDescriptionDate = etdd;
        this.split = split;
        this.tag = tag;
        this.payee = payee;
    }

    /**
     * Equals method of an expense
     * @param obj - Object to check equality with.
     * @return true if equal and false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * hashCode method for the Expense.
     * @return - an int representing the hashCode of the object.
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * toString method for the Expense.
     * @return a human-friendly representation of the Expense object.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
