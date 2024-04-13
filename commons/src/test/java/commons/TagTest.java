package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class TagTest {

    private static final Tag SOME_TAG = new Tag("a", "red");

    @Test
    public void checkConstructor(){
        Tag tag = new Tag();
        Tag TEST_TAG = new Tag("b", "blue");
        assertEquals("b", TEST_TAG.getName());
        assertEquals("blue", TEST_TAG.getColor());
    }

    @Test
    public void equalsHashcode(){
        Tag EQUAL_TAG = new Tag("a", "red");
        assertEquals(EQUAL_TAG, SOME_TAG);
        assertEquals(EQUAL_TAG.hashCode(), SOME_TAG.hashCode());
    }

    @Test
    public void notEqualsHashcode(){
        Tag NOT_EQUAL = new Tag("b", "blue");
        assertFalse(NOT_EQUAL.fullEquals(SOME_TAG));
        assertNotEquals(NOT_EQUAL.hashCode(), SOME_TAG.hashCode());
    }

    @Test
    public void getName(){
        assertEquals(SOME_TAG.getName(), "a");
    }

    @Test
    public void getColor(){
        assertEquals(SOME_TAG.getColor(), "red");
    }

    @Test
    public void setName(){
        Tag TAG_1 = new Tag("initialName", "yellow");
        TAG_1.setName("newName");
        assertEquals(TAG_1.getName(), "newName");
    }
    @Test
    public void setID(){
        Tag TAG_1 = new Tag("initialName", "yellow");
        TAG_1.setId(123);
        assertEquals(TAG_1.getId(), 123);
    }

    @Test
    public void setColor(){
        Tag TAG_2 = new Tag("a", "pink");
        TAG_2.setColor("purple");
        assertEquals(TAG_2.getColor(), "purple");
    }

    @Test
    public void checkToString(){
        assertEquals("Tag{name='a', color='red'}", SOME_TAG.toString());
    }
}
