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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Unit tests for the class {@link Scenario}.
 * </p>
 */
public class ScenarioTest {

   @Nested
   public class Construct2 {
      @Test
      public void differentIdentifiers() {
         final var identifierA = new Scenario.Identifier(ID_A, TITLE_A,
                  DESCRIPTION_A);
         // Tough test: only the unique ID is different
         final var identifierB = new Scenario.Identifier(ID_B, TITLE_A,
                  DESCRIPTION_A);

         final var scenarioA = new Scenario(identifierA);
         final var scenarioB = new Scenario(identifierB);
         assertInvariants(scenarioA, scenarioB);
         assertNotEquals(scenarioA, scenarioB);
      }

      @Test
      public void equalIdentifiers() {
         final var identifier = new Scenario.Identifier(ID_A, TITLE_A,
                  DESCRIPTION_A);

         final var scenarioA = new Scenario(identifier);
         final var scenarioB = new Scenario(identifier);
         assertInvariants(scenarioA, scenarioB);
         assertEquals(scenarioA, scenarioB);
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
         final var identifier = new Scenario.Identifier(id, title, description);
         final var scenario = new Scenario(identifier);

         assertInvariants(scenario);
         assertSame(identifier, scenario.getIdentifier(), "identifier");
      }
   }// class

   /**
    * <p>
    * Unit tests for the {@link Scenario.Identifier} class.
    * </p>
    */
   @Nested
   public class IdentifierTest {

      @Nested
      public class Construct2 {
         @Test
         public void differentIds() {
            final var identifierA = new Scenario.Identifier(ID_A, TITLE_A,
                     DESCRIPTION_A);
            final var identifierB = new Scenario.Identifier(ID_B, TITLE_A,
                     DESCRIPTION_A);
            assertInvariants(identifierA, identifierB);
            assertNotEquals(identifierA, identifierB);
         }

         @Test
         public void equalDescriptions() {
            final var identifierA = new Scenario.Identifier(ID_A, TITLE_A,
                     DESCRIPTION_A);
            final var identifierB = new Scenario.Identifier(ID_B, TITLE_B,
                     DESCRIPTION_A);
            assertInvariants(identifierA, identifierB);
            assertNotEquals(identifierA, identifierB);
         }

         @Test
         public void equalIds() {
            final var identifierA = new Scenario.Identifier(ID_A, TITLE_A,
                     DESCRIPTION_A);
            final var identifierB = new Scenario.Identifier(ID_A, TITLE_B,
                     DESCRIPTION_B);
            assertInvariants(identifierA, identifierB);
            assertEquals(identifierA, identifierB);
         }

         @Test
         public void equalTitles() {
            final var identifierA = new Scenario.Identifier(ID_A, TITLE_A,
                     DESCRIPTION_A);
            final var identifierB = new Scenario.Identifier(ID_B, TITLE_A,
                     DESCRIPTION_B);
            assertInvariants(identifierA, identifierB);
            assertNotEquals(identifierA, identifierB);
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
            final var identifier = new Scenario.Identifier(id, title,
                     description);

            assertInvariants(identifier);
            assertAll("Attributes have the given values",
                     () -> assertSame(id, identifier.getId(), "id"),
                     () -> assertSame(title, identifier.getTitle(), "title"),
                     () -> assertSame(description, identifier.getDescription(),
                              "description"));
         }
      }// class
   }// class

   private static final UUID ID_A = UUID.randomUUID();
   private static final UUID ID_B = UUID.randomUUID();
   private static final String TITLE_A = "Beach Assault";
   private static final String TITLE_B = "0123456789012345678901234567890123456789012345678901234567890123";// longest

   private static final String DESCRIPTION_A = "";// shortest

   private static final String DESCRIPTION_B = "Simple training scenario.";

   public static void assertInvariants(final Scenario scenario) {
      assertEquals(scenario, scenario,
               "An object is always equivalent to itself.");
      final var identifier = scenario.getIdentifier();
      assertNotNull(identifier, "identifier not null");// guard
      assertInvariants(identifier);
   }

   public static void assertInvariants(final Scenario scenarioA,
            final Scenario scenarioB) {
      final var equals = scenarioA.equals(scenarioB);
      assertTrue(!(equals && !scenarioB.equals(scenarioA)),
               "Equality is symmetric");
      assertTrue(!(equals && scenarioA.hashCode() != scenarioB.hashCode()),
               "Equality implies equal hashCode");
      assertEquals(equals,
               scenarioA.getIdentifier().equals(scenarioB.getIdentifier()),
               "Entity semantics, with the identifier serving as a unique identifier");
   }

   public static void assertInvariants(final Scenario.Identifier identifier) {
      assertEquals(identifier, identifier,
               "An object is always equivalent to itself.");
      final var id = identifier.getId();
      final var description = identifier.getDescription();
      final var title = identifier.getTitle();
      assertAll("Not null", () -> assertNotNull(id, "id"),
               () -> assertNotNull(description, "description"),
               () -> assertNotNull(title, "title"));// guard
      assertAll("title",
               () -> assertThat("Not empty", title, not(emptyString())),
               () -> assertThat("Not longer that 64 code points",
                        title.length(), not(greaterThan(64))));
   }

   public static void assertInvariants(final Scenario.Identifier identifierA,
            final Scenario.Identifier identifierB) {
      final var equals = identifierA.equals(identifierB);
      assertTrue(!(equals && !identifierB.equals(identifierA)),
               "Equality is symmetric");
      assertTrue(!(equals && identifierA.hashCode() != identifierB.hashCode()),
               "Equality implies equal hashCode");
      assertEquals(equals, identifierA.getId().equals(identifierB.getId()),
               "Entity semantics, with the ID serving as a unique identifier");
   }
}
