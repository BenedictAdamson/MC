package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2020.
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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * Auxiliary test code for class that implement the {@link Scenario} interface.
 * </p>
 */
public class ScenarioTest {

   public static void assertInvariants(final Scenario user) {
      assertEquals(user, user, "An object is always equivalent to itself.");
   }

   public static void assertInvariants(final Scenario scenario1,
            final Scenario scenario2) {
      final boolean equals = scenario1.equals(scenario2);
      assertTrue(!(equals && !scenario2.equals(scenario1)),
               "Equality is symmetric");
      assertTrue(!(equals && scenario1.hashCode() != scenario2.hashCode()),
               "Equality implies equal hashCode");
      assertEquals(equals, scenario1.getId().equals(scenario2.getId()),
               "Entity semantics, with the ID serving as a unique identifier");
   }
}
