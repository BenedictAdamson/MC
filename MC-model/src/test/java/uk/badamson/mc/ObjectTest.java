package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2020,22.
 *
 * This file is part of MC.
 *
 * MC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with MC.  If not, see <https://www.gnu.org/licenses/>.
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * <p>
 * Auxiliary test code for all classes (which are derived from the
 * {@link Object} bas class).
 * </p>
 */
public class ObjectTest {

    public static void assertInvariants(final Object object) {
        assertEquals(object, object, "An object is always equivalent to itself.");
    }

    public static void assertInvariants(final Object objectA,
                                        final Object objectB) {
        final var equals = objectA.equals(objectB);
        assertFalse(equals && !objectB.equals(objectA), "Equality is symmetric");
        assertFalse(equals && objectA.hashCode() != objectB.hashCode(), "Equality implies equal hashCode");
    }

}
