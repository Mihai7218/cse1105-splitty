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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ParticipantTest {

    @Test
    public void ConstructorTest() {
        var p = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        assertEquals("Joe", p.name);
        assertEquals("joe.doe@gmail.com", p.email);
        assertEquals("NL12 1923 1237 8374 02", p.iban);
        assertEquals("ALSUENBG", p.bic);
    }

    @Test
    public void ToStringTest() {
        var participant = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG").toString();
        assertTrue(participant.contains(Participant.class.getSimpleName()));
        int splitter = participant.indexOf("[");
        assertEquals("""
                [\r
                  bic=ALSUENBG\r
                  email=joe.doe@gmail.com\r
                  iban=NL12 1923 1237 8374 02\r
                  id=0\r
                  name=Joe\r
                ]""", participant.substring(splitter));
    }

    @Test
    public void EqualsTest() {
        var participant1 = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        var participant2 = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        assertEquals(participant1, participant2);
    }
    @Test
    public void NotEqualsTest() {
        var participant1 = new Participant("Joe", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        var participant2 = new Participant("Jo", "joe.doe@gmail.com", "NL12 1923 1237 8374 02", "ALSUENBG");
        assertNotEquals(participant1, participant2);
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
}