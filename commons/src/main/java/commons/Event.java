package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class Event {
    //TODO: change invite code into string
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int inviteCode;
    private String title;
    @OneToMany
    private List<Expense> expensesList;
    @OneToMany
    private List<Participant> participantsList;
    @OneToMany
    private List<Tag> tagsList;
    private Date creationDate;
    private Date lastActivity;

    /**
     * Empty constructor for object mapper
     */
    @SuppressWarnings("unused")
    public Event() {
    }

    /**
     * Constructor for an event
     * @param title The title of the event
     * @param creationDate The creation date of the event
     * @param lastActivity The last activity of the event
     */
    public Event(String title, Date creationDate, Date lastActivity) {
        this.inviteCode = 0;
        this.title = title;
        this.expensesList = new ArrayList<>();
        this.participantsList = new ArrayList<>();
        this.tagsList = new ArrayList<>();
        this.creationDate = creationDate;
        this.lastActivity = lastActivity;
    }

    /**
     * @return returns the invite code of the event
     */
    public int getInviteCode() {
        return inviteCode;
    }

    /**
     * @param inviteCode sets the invite code of the event
     */
    public void setInviteCode(int inviteCode) {
        this.inviteCode = inviteCode;
    }

    /**
     * @return the title of the event
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title rename the event
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the list of expenses of the event
     */
    public List<Expense> getExpensesList() {
        return expensesList;
    }

    /**
     *
     * @param expense to be added to the even
     */
    public void addExpense(Expense expense){
        expensesList.add(expense);
    }

    /**
     * @param expensesList set the expenses list to a new list
     *                     TODO: addExpense method
     */
    public void setExpensesList(List<Expense> expensesList) {
        this.expensesList = expensesList;
    }

    /**
     * @return the list of participants of the event
     */
    public List<Participant> getParticipantsList() {
        return participantsList;
    }

    /**
     *
     * @param participant to be added to the event
     */
    public void addParticipant(Participant participant){
        participantsList.add(participant);
    }

    /**
     * @param participantsList set the list of participants of the event
     *                         TODO: addParticipant method
     */
    public void setParticipantsList(List<Participant> participantsList) {
        this.participantsList = participantsList;
    }

    /**
     * @return the tags of the event
     */
    public List<Tag> getTagsList() {
        return tagsList;
    }

    /**
     *
     * @param tag to be added to the event
     */
    public void addTag(Tag tag){
        tagsList.add(tag);
    }

    /**
     * @param tagsList change the list of tags of the event
     *                 TODO: addTag method
     */
    public void setTagsList(List<Tag> tagsList) {
        this.tagsList = tagsList;
    }

    /**
     * @return the date the event was created
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate change/set the creation date of the event
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the last time the event was managed
     */
    public Date getLastActivity() {
        return lastActivity;
    }

    /**
     * @param lastActivity change/set the date the event was last managed
     */
    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
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
        return Objects.equals(inviteCode, that.inviteCode) &&
                Objects.equals(title, that.title) &&
                Objects.equals(expensesList, that.expensesList) &&
                Objects.equals(participantsList, that.participantsList) &&
                Objects.equals(tagsList, that.tagsList) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(lastActivity, that.lastActivity);
    }

    /**
     * Function to hash an Event
     * @return returns a hashcode of an Event
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * A toString method to display an event in a human-readable format
     * @return returns a human-readable format of an event
     */
    @Override
    public String toString() {
        return "Event{" +
                "inviteCode='" + inviteCode + '\'' +
                ", title='" + title + '\'' +
                ", expenses='" + expensesList.toString() + '\'' +
                ", participants='" + participantsList.toString() + '\'' +
                ", tags='" + tagsList.toString() + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", lastActivity='" + lastActivity + '\'' +
                '}';
    }
}
