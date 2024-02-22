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
    public long id;

    public String name;
    public String email;
    public String iban;
    public String bic;

    /**
     * Empty constructor for object mapper
     */
    @SuppressWarnings("unused")
    private Participant() {
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
     * An equals methode for to compare 2 participants
     * @param o The object to compare to the participant
     * @return returns if the other participant is the same as the one this function is called on
     */
    @Override
    public boolean equals(Object o) {
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
