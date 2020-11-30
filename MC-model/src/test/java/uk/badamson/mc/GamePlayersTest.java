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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * <p>
 * Unit tests for the class {@link Game}.
 * </p>
 */
public class GamePlayersTest {

   @Nested
   public class AddPlayer {

      @Test
      public void a() {
         test(Set.of(), PLAYER_ID_A);
      }

      @Test
      public void alreadyPlayer() {
         test(Set.of(PLAYER_ID_A), PLAYER_ID_A);
      }

      @Test
      public void b() {
         test(Set.of(), PLAYER_ID_B);
      }

      @Test
      public void notEmpty() {
         test(Set.of(PLAYER_ID_A), PLAYER_ID_B);
      }

      private void test(final Set<UUID> players0, final UUID player) {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var game = new Game(identifier, true, players0);

         addPlayer(game, player);
      }

   }// class

   @Nested
   public class Construct2 {

      @Test
      public void differentIdentifiers() {
         final var identifierA = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var identifierB = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
         final var gameA = new Game(identifierA, true, PLAYERS_A);
         final var gameB = new Game(identifierB, true, PLAYERS_A);

         assertInvariants(gameA, gameB);
         assertNotEquals(gameA, gameB);
      }

      @Test
      public void differentPlayers() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var gameA = new Game(identifier, true, PLAYERS_A);
         final var gameB = new Game(identifier, true, PLAYERS_B);

         assertInvariants(gameA, gameB);
         assertEquals(gameA, gameB);
      }

      @Test
      public void differentRecuitment() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var gameA = new Game(identifier, true, PLAYERS_A);
         final var gameB = new Game(identifier, false, PLAYERS_A);

         assertInvariants(gameA, gameB);
         assertEquals(gameA, gameB);
      }

      @Test
      public void equalAttributes() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var gameA = new Game(identifier, true, PLAYERS_A);
         final var gameB = new Game(identifier, true, PLAYERS_A);

         assertInvariants(gameA, gameB);
         assertEquals(gameA, gameB);
      }
   }// class

   @Nested
   public class ConstructCopy {

      @Test
      public void a() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         test(identifier, false, PLAYERS_A);
      }

      @Test
      public void b() {
         final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
         test(identifier, true, PLAYERS_B);
      }

      private void test(final Game.Identifier identifier,
               final boolean recruiting, final Set<UUID> players) {
         final var game0 = new Game(identifier, recruiting, players);

         final var copy = new Game(game0);

         assertInvariants(copy);
         assertInvariants(game0, copy);
         assertAll("Copied", () -> assertEquals(game0, copy),
                  () -> assertSame(game0.getIdentifier(), copy.getIdentifier(),
                           "identifier"),
                  () -> assertEquals(game0.isRecruiting(), copy.isRecruiting(),
                           "recruiting"),
                  () -> assertEquals(game0.getPlayers(), copy.getPlayers(),
                           "players"),
                  () -> assertNotSame(game0.getPlayers(), copy.getPlayers(),
                           "players (not game0)"));
      }
   }// class

   @Nested
   public class ConstructWithAttributes {

      @Test
      public void a() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         test(identifier, false, PLAYERS_A);
      }

      @Test
      public void b() {
         final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
         test(identifier, true, PLAYERS_B);
      }

      private void test(final Game.Identifier identifier,
               final boolean recruiting, final Set<UUID> players) {
         final var game = new Game(identifier, recruiting, players);

         assertInvariants(game);
         assertAll("Has the given attribute values",
                  () -> assertSame(identifier, game.getIdentifier(),
                           "identifier"),
                  () -> assertEquals(recruiting, game.isRecruiting(),
                           "recruiting"),
                  () -> assertEquals(players, game.getPlayers(), "players"));
         assertNotSame(players, game.getPlayers(), "players not same");
      }
   }// class

   @Nested
   public class EndRecruitment {

      @Test
      public void initiallyFalse() {
         test(false);
      }

      @Test
      public void initiallyTrue() {
         test(true);
      }

      private void test(final boolean recruitment0) {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var game = new Game(identifier, recruitment0, PLAYERS_A);

         endRecruitment(game);
      }
   }// class

   /**
    * <p>
    * Unit tests for the {@link Game.Identifier} class.
    * </p>
    */
   @Nested
   public class IdentifierTest {

      @Nested
      public class Construct2 {
         @Test
         public void differentCreated() {
            final var identifierA = new Game.Identifier(SCENARIO_ID_A,
                     CREATED_A);
            final var identifierB = new Game.Identifier(SCENARIO_ID_A,
                     CREATED_B);
            assertInvariants(identifierA, identifierB);
            assertNotEquals(identifierA, identifierB);
         }

         @Test
         public void differentScenarios() {
            final var identifierA = new Game.Identifier(SCENARIO_ID_A,
                     CREATED_A);
            final var identifierB = new Game.Identifier(SCENARIO_ID_B,
                     CREATED_A);
            assertInvariants(identifierA, identifierB);
            assertNotEquals(identifierA, identifierB);
         }

         @Test
         public void equal() {
            final var identifierA = new Game.Identifier(SCENARIO_ID_A,
                     CREATED_A);
            final var identifierB = new Game.Identifier(SCENARIO_ID_A,
                     CREATED_A);
            assertInvariants(identifierA, identifierB);
            assertEquals(identifierA, identifierB);
         }
      }// class

      @Nested
      public class Constructor {

         @Test
         public void a() {
            test(SCENARIO_ID_A, CREATED_A);
         }

         @Test
         public void b() {
            test(SCENARIO_ID_B, CREATED_B);
         }

         private void test(final UUID scenario, final Instant created) {
            final var identifier = new Game.Identifier(scenario, created);

            assertInvariants(identifier);
            assertAll("Attributes have the given values",
                     () -> assertSame(scenario, identifier.getScenario(),
                              "scenario"),
                     () -> assertSame(created, identifier.getCreated(),
                              "created"));
         }
      }// class

      @Nested
      public class JSON {

         @Test
         public void a() {
            test(SCENARIO_ID_A, CREATED_A);
         }

         @Test
         public void b() {
            test(SCENARIO_ID_B, CREATED_B);
         }

         private void test(final UUID scenario, final Instant created) {
            final var identifier = new Game.Identifier(scenario, created);
            final var deserialized = JsonTest
                     .serializeAndDeserialize(identifier);

            assertInvariants(deserialized);
            assertInvariants(identifier, deserialized);
            assertAll("Deserialised attributes",
                     () -> assertEquals(scenario, identifier.getScenario(),
                              "scenario"),
                     () -> assertEquals(created, identifier.getCreated(),
                              "created"));
         }
      }// class

      @Nested
      public class Serialize {

         @Test
         public void a() {
            test(SCENARIO_ID_A, CREATED_A);
         }

         @Test
         public void b() {
            test(SCENARIO_ID_B, CREATED_B);
         }

         private void test(final UUID scenario, final Instant created) {
            final var expectedSerializedCreated = created.toString();
            final var identifier = new Game.Identifier(scenario, created);

            final String serialized;
            try {
               serialized = JsonTest.serialize(identifier);
            } catch (final JsonProcessingException e) {
               throw new AssertionFailedError("can serialize as JSON", e);
            }

            assertThat("contains serialized 'created' value", serialized,
                     containsString(expectedSerializedCreated));
         }

      }// class
   }// class

   @Nested
   public class Json {

      @Test
      public void a() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         test(identifier, false, PLAYERS_A);
      }

      @Test
      public void b() {
         final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
         test(identifier, true, PLAYERS_B);
      }

      private void test(final Game.Identifier identifier,
               final boolean recruiting, final Set<UUID> players) {
         final var game = new Game(identifier, recruiting, players);
         final var deserialized = JsonTest.serializeAndDeserialize(game);

         assertInvariants(deserialized);
         assertInvariants(game, deserialized);
         assertEquals(game, deserialized);
         assertAll("Deserialised attributes",
                  () -> assertEquals(identifier, deserialized.getIdentifier(),
                           "identifier"),
                  () -> assertEquals(recruiting, deserialized.isRecruiting(),
                           "recruiting"));
      }
   }// class

   private static final UUID SCENARIO_ID_A = UUID.randomUUID();
   private static final UUID SCENARIO_ID_B = UUID.randomUUID();
   private static final UUID PLAYER_ID_A = UUID.randomUUID();
   private static final UUID PLAYER_ID_B = UUID.randomUUID();
   private static final Instant CREATED_A = Instant.EPOCH;
   private static final Instant CREATED_B = Instant.now();
   private static final Set<UUID> PLAYERS_A = Set.of();
   private static final Set<UUID> PLAYERS_B = Set.of(PLAYER_ID_B);

   public static void addPlayer(final Game game, final UUID player) {
      final var players0 = Set.copyOf(game.getPlayers());

      game.addPlayer(player);

      assertInvariants(game);
      final var players = game.getPlayers();
      assertAll(() -> assertThat(
               "Does not remove any players from the set of players of this game.",
               players.containsAll(players0)),
               () -> assertThat("The set of players contains the given player.",
                        players, hasItem(player)));
   }

   public static void assertInvariants(final Game game) {
      ObjectTest.assertInvariants(game);// inherited

      final var players = game.getPlayers();
      assertAll("Not null",
               () -> assertNotNull(game.getIdentifier(), "identifier"),
               () -> assertNotNull(players, "players"));
      assertTrue(players.stream().filter(p -> p == null).findAny().isEmpty(),
               "The set of players does not include null.");
   }

   public static void assertInvariants(final Game gameA, final Game gameB) {
      ObjectTest.assertInvariants(gameA, gameB);// inherited
      assertEquals(gameA.equals(gameB),
               gameA.getIdentifier().equals(gameB.getIdentifier()),
               "Entity semantics, with the identifier serving as a unique identifier");
   }

   public static void assertInvariants(final Game.Identifier identifier) {
      ObjectTest.assertInvariants(identifier);// inherited

      final var scenario = identifier.getScenario();
      final var created = identifier.getCreated();
      assertAll("Not null", () -> assertNotNull(scenario, "scenario"),
               () -> assertNotNull(created, "created"));
   }

   public static void assertInvariants(final Game.Identifier identifierA,
            final Game.Identifier identifierB) {
      ObjectTest.assertInvariants(identifierA, identifierB);// inherited

      final var equals = identifierA.equals(identifierB);
      assertAll("Equality requires equal attributes", () -> assertTrue(!(equals
               && !identifierA.getScenario().equals(identifierB.getScenario())),
               "scenario identifier"),
               () -> assertTrue(
                        !(equals && !identifierA.getCreated()
                                 .equals(identifierB.getCreated())),
                        "creation time"));
   }

   public static void endRecruitment(final Game game) {
      game.endRecruitment();

      assertInvariants(game);
      assertFalse(game.isRecruiting(), "This game is not recruiting.");
   }
}
