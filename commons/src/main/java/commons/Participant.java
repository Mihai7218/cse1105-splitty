package commons;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String email;
    private String iban;
    private String bic;

    /**
     * Empty constructor for object mapper
     */
    @SuppressWarnings("unused")
    public Participant() {
    }

    /**
     * Constructor for a participant
     * @param name The name of the participant
     * @param email The email-address of the participant
     * @param iban The IBAN number of the participant
     * @param bic The BIC code of the participant
     */
    public Participant(String name, String email, String iban, String bic) {
        this.name = name;
        this.email = email;
        this.iban = iban;
        this.bic = bic;
    }

    /**
     * Setter for the name attribute
     * @param name the value to set name to
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for the email attribute
     * @param email the value to set email to
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Setter for the iban attribute
     * @param iban the value to set iban to
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Setter for the bic attribute
     * @param bic the value to set bic to
     */
    public void setBic(String bic) {
        this.bic = bic;
    }

    /**
     * Getter for the id
     * @return id value for the participant
     */
    public long getId() {
        return id;
    }

    /**
     * sets the id of the participants
     * @param id id value for participant
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * getter for the name attribute
     * @return the value of the name attribute
     */
    public String getName() {
        return name;
    }

    /**
     * getter for the email attribute
     * @return the value of the email attribute
     */
    public String getEmail() {
        return email;
    }

    /**
     * getter for the iban attribute
     * @return the value of the iban attribute
     */
    public String getIban() {
        return iban;
    }

    /**
     * getter for the bic attribute
     * @return the value of the bic attribute
     */
    public String getBic() {
        return bic;
    }

    /**
     * Equals method of a participant - based only on id.
     * @param o - Object to check equality with.
     * @return true if equal and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant p = (Participant) o;
        return id == p.getId();
    }

    /**
     * Equals method of a participant - based on all attributes.
     * @param o - Object to check equality with.
     * @return true if equal and false otherwise.
     */
    public boolean fullEquals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                Objects.equals(iban, that.iban) &&
                Objects.equals(bic, that.bic);
    }


    /**
     * Function to hash a participant
     * @return returns a hashcode of a participant
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * A toString methode to display a participant in a human-readable format
     * @return returns a human-readable format of a participant
     */
    @Override
    public String toString() {
        return "Participant{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", iban='" + iban + '\'' +
                ", bic='" + bic + '\'' +
                '}';
    }
}
 