package commons;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    public String invitecode;
    public String title;
    // TO DO: specify the list to be a list of expenses
    public List expensesList;
    public List<Participant> participantsList;
    public List<Tag> tagsList;
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
     * @param creationdate The creation date of the event
     * @param lastactivity The last activity of the event
     */
    public Event(String invitecode, String title, Date creationdate, Date lastactivity) {
        this.invitecode = invitecode;
        this.title = title;
        this.expensesList = new ArrayList<>();
        this.participantsList = new ArrayList<>();
        this.tagsList = new ArrayList<>();
        this.creationdate = creationdate;
        this.lastactivity = lastactivity;
    }

    /**
     * Retrieves Event invitecode
     * @return String for the Event invitecode
     */
    public String getInvitecode() {
        return invitecode;
    }

    /**
     * Modifies the invitecode
     * @param invitecode string for new Event invitecode
     */
    public void setInvitecode(String invitecode) {
        this.invitecode = invitecode;
    }

    /**
     * Retrieves Event title
     * @return String for the Event title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Modifies the title
     * @param title String for new Event title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves Event expensesList
     * @return list of the expenses of the Event
     */
    public List getExpensesList() {
        return expensesList;
    }

    /**
     * Changes the list to a different list
     * @param expensesList List for new Event expenses list
     */
    public void setExpensesList(List expensesList) {
        this.expensesList = expensesList;
    }

    /**
     * Retrieves Event participantsList
     * @return list of the participants of the Event
     */
    public List<Participant> getParticipantsList() {
        return participantsList;
    }

    /**
     * Changes the list to a different list
     * @param participantsList List for new Event participant list
     */
    public void setParticipantsList(List<Participant> participantsList) {
        this.participantsList = participantsList;
    }

    /**
     * Retrieves Event tagsList
     * @return list of the tags of the Event
     */
    public List<Tag> getTagsList() {
        return tagsList;
    }

    /**
     * Changes the list to a different list
     * @param tagsList List for new Event tags list
     */
    public void setTagsList(List<Tag> tagsList) {
        this.tagsList = tagsList;
    }

    /**
     * Retrieves Event creation date
     * @return Date for the creation date of the Event
     */
    public Date getCreationdate() {
        return creationdate;
    }

    /**
     * Changes the creation date of the Event
     * @param creationdate Date for the new creation date of the Event
     */
    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }

    /**
     * Retrieves date of the last activity
     * @return Date for the last activity of the Event
     */
    public Date getLastactivity() {
        return lastactivity;
    }

    /**
     * Changes the last activity date of the Event
     * @param lastactivity Date for the new last activity date of the Event
     */
    public void setLastactivity(Date lastactivity) {
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
                Objects.equals(expensesList, that.participantsList) &&
                Objects.equals(tagsList, that.tagsList) &&
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
                ", expenses='" + expensesList + '\'' +
                ", tags='" + tagsList + '\'' +
                ", creationdate='" + creationdate + '\'' +
                ", lastactivity='" + lastactivity + '\'' +
                '}';
    }
}
