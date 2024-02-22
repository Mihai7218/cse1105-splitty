package commons;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    public double amount;
    public String currency;
    public String title;
    public String description;
    public Date date;
    @OneToMany (cascade = CascadeType.REMOVE)
    private List<ParticipantPayment> split;
    @ManyToOne
    public Tag tag;
    @ManyToOne
    public Participant payee;

    /**
     * No-arg constructor of Expense
     */
    @SuppressWarnings("unused")
    private Expense() {
    }

    /**
     * Constructor of an expense object
     * @param amount - Amount of the expense
     * @param currency - Currency of the expense
     * @param title - Title of the expense
     * @param description - Description of the expense
     * @param date - Date of the expense
     * @param split - List of participants that need to pay and the amount owed
     * @param tag - Tag of the expense
     * @param payee - Participant that needs to be paid back
     */
    public Expense(double amount, String currency, String title, String description, Date date,
                   List<ParticipantPayment> split, Tag tag, Participant payee) {
        this.currency = currency;
        this.amount = amount;
        this.title = title;
        this.description = description;
        this.date = date;
        this.split = split;
        this.tag = tag;
        this.payee = payee;
    }

    /**
     * Getter for the ID
     * @return the ID of the object
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for the list of ParticipantPayments
     * @return the list of ParticipantPayments
     */
    public List<ParticipantPayment> getSplit() {
        return split;
    }

    /**
     * Equals method of an expense
     * @param o - Object to check equality with.
     * @return true if equal and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return id == expense.id && Double.compare(amount, expense.amount) == 0
                && Objects.equals(currency, expense.currency)
                && Objects.equals(title, expense.title)
                && Objects.equals(description, expense.description)
                && Objects.equals(date, expense.date)
                && Objects.equals(split, expense.split)
                && Objects.equals(tag, expense.tag)
                && Objects.equals(payee, expense.payee);
    }

    /**
     * hashCode method for the Expense.
     * @return - an int representing the hashCode of the object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, amount, currency, title, description, date, split, tag, payee);
    }

    /**
     * toString method for the Expense.
     * @return a human-friendly representation of the Expense object.
     */
    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", split=" + split +
                ", tag=" + tag +
                ", payee=" + payee +
                '}';
    }
}
