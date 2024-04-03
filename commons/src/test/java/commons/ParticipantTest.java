/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParticipantTest {

    @Test
    public void ConstructorTest() {
        var p = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        assertEquals("Joe", p.getName());
        assertEquals("joe.doe@gmail.com", p.getEmail());
        assertEquals("NL12 1923 1237 8374 02", p.getIban());
        assertEquals("ALSUENBG", p.getBic());
    }

    @Test
    public void ToStringTest() {
        var participant = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG").toString();
        assertEquals(
                "Participant{name='Joe', email='joe.doe@gmail.com', iban='NL12 1923 1237 8374 02', bic='ALSUENBG'}", participant.toString());
    }

    @Test
    public void EqualsTest() {
        var participant1 = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        var participant2 = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        assertTrue(participant1.fullEquals(participant2));
    }
    @Test
    public void NotEqualsTest() {
        var participant1 = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        var participant2 = new Participant("Jo", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        assertFalse(participant1.fullEquals(participant2));
    }

    @Test
    public void EqualHashCodeTest() {
        var participant1 = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        var participant2 = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        assertEquals(participant2.hashCode(), participant1.hashCode());
    }
    @Test
    public void NotEqualHashCodeTest() {
        var participant1 = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        var participant2 = new Participant("Jo", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        assertNotEquals(participant2.hashCode(), participant1.hashCode());
    }

    @Test
    void setName() {
        var participant = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        assertEquals("Joe",participant.getName());
        participant.setName("Marco");
        assertEquals("Marco",participant.getName());
    }

    @Test
    void setEmail() {
        var participant = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        assertEquals("joe.doe@gmail.com",participant.getEmail());
        participant.setEmail("Marco@gmail.com");
        assertEquals("Marco@gmail.com",participant.getEmail());
    }

    @Test
    void setIban() {
        var participant = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        assertEquals("NL12 1923 1237 8374 02",participant.getIban());
        participant.setIban("NL78 2348 5640 2348 45");
        assertEquals("NL78 2348 5640 2348 45",participant.getIban());
    }

    @Test
    void setBic() {
        var participant = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        assertEquals("ALSUENBG",participant.getBic());
        participant.setBic("KFYCVAGS");
        assertEquals("KFYCVAGS",participant.getBic());
    }
}