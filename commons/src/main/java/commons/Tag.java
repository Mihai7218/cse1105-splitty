
package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Tag {

    @Id
    public String name;
    public String color;

    /**
     * Empty constructor for object mapper
     */
    @SuppressWarnings("unused")
    private Tag() {
    }

    /**
     * Creates new tag
     * @param name name of the tag
     * @param color color the tag is displayed as
     */
    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Retrieves tag name
     * @return String for tag name
     */
    public String getName() {
        return name;
    }

    /**
     * Modifies tag name
     * @param name string for new tag name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieve the tag color
     * @return String representing tag color
     */
    public String getColor() {
        return color;
    }

    /**
     * Modify tag color
     * @param color String with the new color to assign to tag
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Generate string representation of the tag
     * @return string representation of the tag object
     */
    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    /**
     * Checks equality of this tag against another object
     * @param o other object to compare with
     * @return boolean equality value
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name) && Objects.equals(color, tag.color);
    }

    /**
     * generate unique hashcode value for this tag
     * @return integer hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
