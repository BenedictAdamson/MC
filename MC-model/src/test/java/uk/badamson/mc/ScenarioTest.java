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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
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
         final var gameCreationTimes = Collections.<Instant>emptySortedSet();

         // Tough test: all else equal
         final var scenarioA = new Scenario(ID_A, TITLE_A, DESCRIPTION_A,
                  gameCreationTimes);
         final var scenarioB = new Scenario(ID_B, TITLE_A, DESCRIPTION_A,
                  gameCreationTimes);
         assertInvariants(scenarioA, scenarioB);
         assertNotEquals(scenarioA, scenarioB);
      }

      @Test
      public void equalIdentifiers() {
         // Tough test: have different attributes and aggregates
         final var gameCreationTimesA = Collections.<Instant>emptySortedSet();
         final SortedSet<Instant> gameCreationTimesB = new TreeSet<>(
                  Arrays.asList(CREATED_A));

         final var scenarioA = new Scenario(ID_A, TITLE_A, DESCRIPTION_A,
                  gameCreationTimesA);
         final var scenarioB = new Scenario(ID_A, TITLE_B, DESCRIPTION_B,
                  gameCreationTimesB);
         assertInvariants(scenarioA, scenarioB);
         assertEquals(scenarioA, scenarioB);
      }

   }// class

   @Nested
   public class Constructor {

      @Test
      public void a() {
         final var gameCreationTimes = Collections.<Instant>emptySortedSet();
         test(ID_A, TITLE_A, DESCRIPTION_A, gameCreationTimes);
      }

      @Test
      public void b() {
         final SortedSet<Instant> gameCreationTimes = new TreeSet<>(
                  Arrays.asList(CREATED_B));
         test(ID_B, TITLE_B, DESCRIPTION_B, gameCreationTimes);
      }

      private void test(final UUID identifier, final String title,
               final String description,
               final SortedSet<Instant> gameCreationTimes) {
         final var scenario = new Scenario(identifier, title, description,
                  gameCreationTimes);

         assertInvariants(scenario);
         assertAll(
                  () -> assertSame(identifier, scenario.getIdentifier(),
                           "identifier"),
                  () -> assertSame(title, scenario.getTitle(), "title"),
                  () -> assertSame(description, scenario.getDescription(),
                           "description"),
                  () -> assertEquals(gameCreationTimes,
                           scenario.getGameCreationTimes(),
                           "gameCreationTimes"));
         JsonTest.assertCanSerializeAndDeserialize(scenario);
      }
   }// class

   private static final UUID ID_A = UUID.randomUUID();
   private static final UUID ID_B = UUID.randomUUID();
   private static final String TITLE_A = "Beach Assault";
   private static final String TITLE_B = "0123456789012345678901234567890123456789012345678901234567890123";// longest
   private static final String DESCRIPTION_A = "";// shortest
   private static final String DESCRIPTION_B = "Simple training scenario.";
   private static final Instant CREATED_A = Instant.EPOCH;
   private static final Instant CREATED_B = Instant.now();

   public static void assertInvariants(final Scenario scenario) {
      ObjectTest.assertInvariants(scenario);// inherited

      final var identifier = scenario.getIdentifier();
      final var title = scenario.getTitle();
      final var description = scenario.getDescription();
      final var gameCreationTimes = scenario.getGameCreationTimes();
      final var namedUUID = scenario.getNamedUUID();
      assertAll("Non null attributes and aggregates",
               () -> assertNotNull(identifier, "identifier"), // guard
               () -> assertNotNull(title, "title"), // guard
               () -> assertNotNull(description, "description"),
               () -> assertNotNull(gameCreationTimes, "gameCreationTimes"), // guard
               () -> assertNotNull(namedUUID, "namedUUID") // guard
      );

      assertAll(() -> NamedUUIDTest.assertInvariants(namedUUID),
               () -> assertTrue(NamedUUID.isValidTitle(title),
                        "title is valid"));
   }

   public static void assertInvariants(final Scenario scenarioA,
            final Scenario scenarioB) {
      ObjectTest.assertInvariants(scenarioA, scenarioB);// inherited
      assertEquals(scenarioA.equals(scenarioB),
               scenarioA.getIdentifier().equals(scenarioB.getIdentifier()),
               "Entity semantics, with the identifier serving as a unique identifier");
   }
}
