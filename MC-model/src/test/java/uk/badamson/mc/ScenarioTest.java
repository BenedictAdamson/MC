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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.opentest4j.MultipleFailuresError;

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
         final Collection<Game> games = List.of();

         // Tough test: all else equal
         final var scenarioA = new Scenario(ID_A, TITLE_A, DESCRIPTION_A,
                  games);
         final var scenarioB = new Scenario(ID_B, TITLE_A, DESCRIPTION_A,
                  games);
         assertInvariants(scenarioA, scenarioB);
         assertNotEquals(scenarioA, scenarioB);
      }

      @Test
      public void equalIdentifiers() {
         // Tough test: have different attributes and aggregates
         final Collection<Game> gamesA = List.of();
         final Collection<Game> gamesB = List
                  .of(new Game(new Game.Identifier(ID_A, CREATED_A)));

         final var scenarioA = new Scenario(ID_A, TITLE_A, DESCRIPTION_A,
                  gamesA);
         final var scenarioB = new Scenario(ID_A, TITLE_B, DESCRIPTION_B,
                  gamesB);
         assertInvariants(scenarioA, scenarioB);
         assertEquals(scenarioA, scenarioB);
      }

   }// class

   @Nested
   public class Constructor {

      @Test
      public void a() {
         final Collection<Game> games = List.of();
         test(ID_A, TITLE_A, DESCRIPTION_A, games);
      }

      @Test
      public void b() {
         final Collection<Game> games = List
                  .of(new Game(new Game.Identifier(ID_B, CREATED_B)));
         test(ID_B, TITLE_B, DESCRIPTION_B, games);
      }

      private void test(final UUID identifier, final String title,
               final String description, final Collection<Game> games) {
         final var scenario = new Scenario(identifier, title, description,
                  games);
         final var scenarioGames = scenario.getGames();

         assertInvariants(scenario);
         assertAll(
                  () -> assertSame(identifier, scenario.getIdentifier(),
                           "identifier"),
                  () -> assertSame(title, scenario.getTitle(), "title"),
                  () -> assertSame(description, scenario.getDescription(),
                           "description"),
                  () -> assertThat("games contains all the given games",
                           scenarioGames,
                           containsInAnyOrder(games.toArray(new Game[0]))));
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

   private static void assertGamesInvariants(final Collection<Game> games,
            final UUID id) throws MultipleFailuresError {
      assertAll("games", games.stream().map(game -> new Executable() {

         @Override
         public void execute() {
            assertNotNull(game, "does not have any null elements.");// guard
            final var gameId = game.getIdentifier();
            assertAll("[" + gameId + "]", () -> GameTest.assertInvariants(game),
                     () -> assertEquals(gameId.getScenario(), id,
                              "scenario IDs equal unique ID of the identification information of this scenario."));
         }
      }));
   }

   public static void assertInvariants(final Scenario scenario) {
      ObjectTest.assertInvariants(scenario);// inherited

      final var identifier = scenario.getIdentifier();
      final var title = scenario.getTitle();
      final var description = scenario.getDescription();
      final var games = scenario.getGames();
      final var namedUUID = scenario.getNamedUUID();
      assertAll("Non null attributes and aggregates",
               () -> assertNotNull(identifier, "identifier"), // guard
               () -> assertNotNull(title, "title"), // guard
               () -> assertNotNull(description, "description"),
               () -> assertNotNull(games, "games"), // guard
               () -> assertNotNull(namedUUID, "namedUUID") // guard
      );

      assertAll(() -> NamedUUIDTest.assertInvariants(namedUUID),
               () -> assertTrue(NamedUUID.isValidTitle(title),
                        "title is valid"),
               () -> assertGamesInvariants(games, identifier));
   }

   public static void assertInvariants(final Scenario scenarioA,
            final Scenario scenarioB) {
      ObjectTest.assertInvariants(scenarioA, scenarioB);// inherited
      assertEquals(scenarioA.equals(scenarioB),
               scenarioA.getIdentifier().equals(scenarioB.getIdentifier()),
               "Entity semantics, with the identifier serving as a unique identifier");
   }
}
