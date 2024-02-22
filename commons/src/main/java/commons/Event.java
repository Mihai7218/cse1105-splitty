package commons;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    public int invitecode;
    public String title;
    // TO DO: specify the list to be a list of expenses
    public List expenses;
    public List<Participant> participants;
    public List<Tag> tags;
    public Date creationdate;
    public Date lastactivity;

    /**
     * Empty constructor for object mapper
     */
    @SuppressWarnings("unused")
    private Event() {
    }

    /**
     * Constructor for a event
     * @param invitecode The invitecode of the event
     * @param title The title of the event
     * @param expenses The expenses of the event
     * @param participants The participants of the event
     * @param tags The tags of the event
     * @param creationdate The creation date of the event
     * @param lastactivity The last activity of the event
     */
    public Event(int invitecode, String title, List expenses, List<Participant> participants,
                 List<Tag> tags, Date creationdate, Date lastactivity) {
        this.invitecode = invitecode;
        this.title = title;
        this.expenses = expenses;
        this.participants = participants;
        this.tags = tags;
        this.creationdate = creationdate;
        this.lastactivity = lastactivity;
    }


    /**
     * An equals methode for to compare 2 event
     * @param o The object to compare to the event
     * @return returns if the other event is the same as the one this function is called on
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event that = (Event) o;
        return Objects.equals(invitecode, that.invitecode) &&
                Objects.equals(title, that.title) &&
                Objects.equals(expenses, that.participants) &&
                Objects.equals(tags, that.tags) &&
                Objects.equals(creationdate, that.creationdate) &&
                Objects.equals(lastactivity, that.lastactivity);
    }


    /**
     * Function to hash a Event
     * @return returns a hashcode of a Event
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * A toString methode to display a participant in a human-readable format
     * @return returns a human-readable format of a event
     */
    @Override
    public String toString() {
        return "Event{" +
                "invitecode='" + invitecode + '\'' +
                ", title='" + title + '\'' +
                ", expenses='" + expenses + '\'' +
                ", tags='" + tags + '\'' +
                ", creationdate='" + creationdate + '\'' +
                ", lastactivity='" + lastactivity + '\'' +
                '}';
    }
}