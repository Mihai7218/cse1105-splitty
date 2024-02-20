package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class ExpenseTitleDescriptionDate {
    @Id
    @GeneratedValue
    public Long id;
    public String title;
    public String description;
    public Date date;

    /**
     * No-arg constructor of ExpenseTitleDescriptionDate.
     */
    @SuppressWarnings("unused")
    public ExpenseTitleDescriptionDate() {
    }

    /**
     * Constructor of an ExpenseTitleDescriptionDate object.
     * @param title Title of the expense
     * @param description Description of the expense
     * @param date Date of the expense
     */
    public ExpenseTitleDescriptionDate(String title, String description, Date date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    /**
     * Equals method of an ExpenseTitleDescriptionDate.
     * @param obj - Object to check equality with.
     * @return true if equal and false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * hashCode method for the ExpenseTitleDescriptionDate.
     * @return - an int representing the hashCode of the object.
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * toString method for the ExpenseTitleDescriptionDate.
     * @return a human-friendly representation of the Expense object.
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
