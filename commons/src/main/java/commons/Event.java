package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.text.SimpleDateFormat;
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
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Expense> expensesList;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Participant> participantsList;
    @OneToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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
     * An equals method to compare 2 event by event id
     * @param o The object to compare to the event
     * @return returns if the other event is the same as the one this function is called on
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return this.getInviteCode() == event.getInviteCode();
    }

    /**
     * equals method to check all attributes of an event are the same
     * @param o The object to compare the event to
     * @return true if objects are equal
     */
    public boolean fullEquals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event that = (Event) o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        boolean creationDateCheck = (creationDate == null && that.creationDate == null);
        if (creationDate != null && that.creationDate != null) {
            creationDateCheck = Objects.equals(dateFormat.format(creationDate),
                    dateFormat.format(that.creationDate));
        }
        boolean lastActivityDateCheck = (lastActivity == null && that.lastActivity == null);
        if (creationDate != null && that.creationDate != null) {
            lastActivityDateCheck = Objects.equals(dateFormat.format(lastActivity),
                    dateFormat.format(that.lastActivity));
        }


        return Objects.equals(inviteCode, that.inviteCode) &&
                Objects.equals(title, that.title) &&
                participantsEquals(participantsList, that.participantsList) &&
                expensesEquals(expensesList, that.expensesList) &&
                tagsEquals(tagsList, that.tagsList) &&
                Objects.equals(dateFormat.format(creationDate),
                        dateFormat.format(that.creationDate)) &&
                Objects.equals(dateFormat.format(lastActivity),
                        dateFormat.format(that.lastActivity));
    }

    /**
     * Manual way to check if tags are truly equal
     * @param tagsList list1 with tags
     * @param list list2 with tags
     * @return a boolean to tell if they are equal
     */
    private boolean tagsEquals(List<Tag> tagsList, List<Tag> list) {
        if (tagsList == null && list == null) {
            return true;
        }
        if (tagsList == null || list == null) {
            return false;
        }
        if (tagsList.size() == list.size()) {
            for (Tag p : tagsList) {
                boolean isThere = false;
                for (Tag p2 : list) {
                    if (p.fullEquals(p2)) {
                        isThere = true;
                    }
                }
                if (!isThere) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Manual way to check if Expenses are truly equal
     * @param expensesList list1 with Expenses
     * @param list list2 with Expenses
     * @return a boolean to tell if they are equal
     */
    private boolean expensesEquals(List<Expense> expensesList, List<Expense> list) {
        if (expensesList == null && list == null) {
            return true;
        }
        if (expensesList == null || list == null) {
            return false;
        }
        if (expensesList.size() == list.size()) {
            for (Expense p : expensesList) {
                boolean isThere = false;
                for (Expense p2 : list) {
                    if (p.fullEquals(p2)) {
                        isThere = true;
                    }
                }
                if (!isThere) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Manual way to check if Participants are truly equal
     * @param participantsList list1 with Participants
     * @param list list2 with Participants
     * @return a boolean to tell if they are equal
     */
    public boolean participantsEquals(List<Participant> participantsList, List<Participant> list) {
        if (participantsList == null && list == null) {
            return true;
        }
        if (participantsList == null || list == null) {
            return false;
        }
        if (participantsList.size() == list.size()) {
            for (Participant p : participantsList) {
                boolean isThere = false;
                for (Participant p2 : list) {
                    if (p.fullEquals(p2)) {
                        isThere = true;
                    }
                }
                if (!isThere) {
                    return false;
                }
            }
        }
        return true;
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
        return this.getInviteCode() + "\t\t\t\t" +
                this.getTitle() + "\t\t\t" +
                this.getParticipantsList().size() + "\t\t\t\t\t\t" +
                this.getExpensesList().size() + "\t\t\t\t\t" +
                this.getLastActivity() + "\n";
    }
}
