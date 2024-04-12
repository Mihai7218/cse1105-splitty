package commons;

import jakarta.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double amount;
    private String currency;
    private String title;
    private String description;
    private Date date;
    @OneToMany(cascade = CascadeType.ALL)
    private List<ParticipantPayment> split;
    @ManyToOne
    private Tag tag;
    @ManyToOne
    private Participant payee;

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
     * Setter for the ID
     * @param id id to set the id to
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the list of ParticipantPayments
     * @return the list of ParticipantPayments
     */
    public List<ParticipantPayment> getSplit() {
        return split;
    }

    /**
     * Getter for the amount.
     * @return the amount of the expense.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Setter for the amount.
     * @param amount the amount of the expense.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Getter for the currency.
     * @return the currency of the expense.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Setter for the currency.
     * @param currency the currency of the expense.
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Getter for the title.
     * @return the title of the expense.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for the title.
     * @param title the title of the expense.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the description.
     * @return the description of the expense.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the description.
     * @param description the description of the expense.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the date.
     * @return the date of the expense.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Setter for the date.
     * @param date the date of the expense.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Getter for the tag.
     * @return the tag of the expense.
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * Setter for the tag.
     * @param tag the tag of the expense.
     */
    public void setTag(Tag tag) {
        this.tag = tag;
    }

    /**
     * Getter for the payee.
     * @return the payee of the expense.
     */
    public Participant getPayee() {
        return payee;
    }


    /**
     * Setter for the payee.
     * @param payee the payee of the expense.
     */
    public void setPayee(Participant payee) {
        this.payee = payee;
    }

    /**
     * Equals method of an expense - based only on id.
     * @param o - Object to check equality with.
     * @return true if equal and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return id == expense.id;
    }

    /**
     * Equals method of an expense - based on all attributes.
     * @param o - Object to check equality with.
     * @return true if equal and false otherwise.
     */
    public boolean fullEquals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        boolean tagCheck = (tag == null && expense.tag == null);
        if (tag != null){
            tagCheck = tag.fullEquals(expense.tag);
        }
        return id == expense.id
                && Double.compare(amount, expense.amount) == 0
                && Objects.equals(currency, expense.currency)
                && Objects.equals(title, expense.title)
                && Objects.equals(description, expense.description)
                && Objects.equals(dateFormat.format(date),
                dateFormat.format(expense.date))
                && tagCheck
                && payee.fullEquals(expense.getPayee()) &&
                participantPaymentEquals(split,expense.split);
    }

    /**
     * Manual way to check if ParticipantPayments are truly equal
     * @param split list1 with ParticipantPayments
     * @param participantPayments list2 with ParticipantPayments
     * @return a boolean to tell if they are equal
     */
    public boolean participantPaymentEquals(List<ParticipantPayment> split,
                                            List<ParticipantPayment> participantPayments) {
        if (split == null && participantPayments == null) {
            return true;
        }
        if (split == null || participantPayments == null) {
            return false;
        }
        if (split.size() == participantPayments.size()) {
            for (ParticipantPayment p : split) {
                boolean isThere = false;
                for (ParticipantPayment p2 : participantPayments) {
                    if (p.fullEquals(p2)) {
                        isThere = true;
                    }
                }
                if (!isThere) {
                    return false;
                }
            }
            if (otherWayAroundCheck(split, participantPayments)) return false;
        } else return false;
        return true;
    }

    /**
     * to Really check equivalents and check both sides the other way around
     * @param split split list from one Expense
     * @param participantPayments split list from other Expense
     * @return if they are equal
     */
    private static boolean otherWayAroundCheck(List<ParticipantPayment> split,
                                               List<ParticipantPayment> participantPayments) {
        for (ParticipantPayment p : participantPayments) {
            boolean isThere = false;
            for (ParticipantPayment p2 : split) {
                if (p2.fullEquals(p)) {
                    isThere = true;
                }
            }
            if (!isThere) {
                return true;
            }
        }
        return false;
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

    /**
     * sets payment split among participants
     * @param split
     */
    public void setSplit(List<ParticipantPayment> split) {
        this.split = split;
    }
}
