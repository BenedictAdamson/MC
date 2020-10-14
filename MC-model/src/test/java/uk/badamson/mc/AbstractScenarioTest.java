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
      public void differentIdentifiers() {
         final var scenarioA = new TestScenario(IDENTIFIER_A);
         final var scenarioB = new TestScenario(IDENTIFIER_B);
         assertInvariants(scenarioA, scenarioB);
         assertNotEquals(scenarioA, scenarioB);
      }

      @Test
      public void equalIdentifierss() {
         final var scenarioA = new TestScenario(IDENTIFIER_A);
         final var scenarioB = new TestScenario(IDENTIFIER_A);
         assertInvariants(scenarioA, scenarioB);
         assertEquals(scenarioA, scenarioB);
      }

   }// class

   @Nested
   public class Constructor {

      @Test
      public void a() {
         test(IDENTIFIER_A);
      }

      @Test
      public void b() {
         test(IDENTIFIER_B);
      }

      private void test(final Scenario.Identifier identifier) {
         final var scenario = new TestScenario(identifier);

         assertInvariants(scenario);
         assertSame(identifier, scenario.getIdentifier(), "identifier");
      }
   }// class

   private static final class TestScenario extends AbstractScenario {

      TestScenario(final Scenario.Identifier identifier) {
         super(identifier);
      }

   }// class

   private static final UUID ID_A = UUID.randomUUID();
   private static final UUID ID_B = UUID.randomUUID();
   private static final String TITLE_A = "Beach Assault";
   private static final String TITLE_B = "0123456789012345678901234567890123456789012345678901234567890123";// longest
   private static final String DESCRIPTION_A = "";// shortest
   private static final String DESCRIPTION_B = "Simple training scenario.";
   private static final Scenario.Identifier IDENTIFIER_A = new Scenario.Identifier(
            ID_A, TITLE_A, DESCRIPTION_A);
   private static final Scenario.Identifier IDENTIFIER_B = new Scenario.Identifier(
            ID_B, TITLE_B, DESCRIPTION_B);

   public static void assertInvariants(final AbstractScenario scenario) {
      ScenarioTest.assertInvariants(scenario);// inherited
   }

   public static void assertInvariants(final AbstractScenario scenario1,
            final AbstractScenario scenario2) {
      ScenarioTest.assertInvariants(scenario1, scenario2);// inherited
   }
}
