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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Unit tests for the class {@link AbstractScenario}.
 * </p>
 */
public class AbstractScenarioTest {

   @Nested
   public class Construct2 {
      @Test
      public void differentIds() {
         final var scenarioA = new TestScenario(ID_A, TITLE_A, DESCRIPTION_A);
         final var scenarioB = new TestScenario(ID_B, TITLE_A, DESCRIPTION_A);
         assertInvariants(scenarioA, scenarioB);
         assertNotEquals(scenarioA, scenarioB);
      }

      @Test
      public void equalDescriptions() {
         final var scenarioA = new TestScenario(ID_A, TITLE_A, DESCRIPTION_A);
         final var scenarioB = new TestScenario(ID_B, TITLE_B, DESCRIPTION_A);
         assertInvariants(scenarioA, scenarioB);
         assertNotEquals(scenarioA, scenarioB);
      }

      @Test
      public void equalIds() {
         final var scenarioA = new TestScenario(ID_A, TITLE_A, DESCRIPTION_A);
         final var scenarioB = new TestScenario(ID_A, TITLE_B, DESCRIPTION_B);
         assertInvariants(scenarioA, scenarioB);
         assertEquals(scenarioA, scenarioB);
      }

      @Test
      public void equalTitles() {
         final var scenarioA = new TestScenario(ID_A, TITLE_A, DESCRIPTION_A);
         final var scenarioB = new TestScenario(ID_B, TITLE_A, DESCRIPTION_B);
         assertInvariants(scenarioA, scenarioB);
         assertNotEquals(scenarioA, scenarioB);
      }
   }// class

   @Nested
   public class Constructor {

      @Test
      public void a() {
         test(ID_A, TITLE_A, DESCRIPTION_A);
      }

      @Test
      public void b() {
         test(ID_B, TITLE_B, DESCRIPTION_B);
      }

      private void test(final UUID id, final String title,
               final String description) {
         final var scenario = new TestScenario(id, title, description);

         assertInvariants(scenario);
         assertAll("Attributes have the given values",
                  () -> assertSame(id, scenario.getId(), "id"),
                  () -> assertSame(title, scenario.getTitle(), "title"),
                  () -> assertSame(description, scenario.getDescription(),
                           "description"));
      }
   }// class

   private static final class TestScenario extends AbstractScenario {

      TestScenario(final UUID id, final String title,
               final String description) {
         super(id, title, description);
      }

   }// class

   private static final UUID ID_A = UUID.randomUUID();
   private static final UUID ID_B = UUID.randomUUID();
   private static final String TITLE_A = "Beach Assault";

   private static final String TITLE_B = "0123456789012345678901234567890123456789012345678901234567890123";// longest

   private static final String DESCRIPTION_A = "";// shortest

   private static final String DESCRIPTION_B = "Simple training scenario.";

   public static void assertInvariants(final AbstractScenario scenario) {
      ScenarioTest.assertInvariants(scenario);// inherited
   }

   public static void assertInvariants(final AbstractScenario scenario1,
            final AbstractScenario scenario2) {
      ScenarioTest.assertInvariants(scenario1, scenario2);// inherited
   }
}
