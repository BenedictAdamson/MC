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

/**
 * <p>
 * Unit tests for the class {@link GamePlayers}.
 * </p>
 */
public class GamePlayersTest {

   @Nested
   public class AddUser {

      @Test
      public void a() {
         test(Set.of(), USER_ID_A);
      }

      @Test
      public void alreadyPlayer() {
         test(Set.of(USER_ID_A), USER_ID_A);
      }

      @Test
      public void b() {
         test(Set.of(), USER_ID_B);
      }

      @Test
      public void notEmpty() {
         test(Set.of(USER_ID_A), USER_ID_B);
      }

      private void test(final Set<UUID> users0, final UUID user) {
         final var players = new GamePlayers(GAME_A, true, users0);

         addUser(players, user);
      }

   }// class

   @Nested
   public class Construct2 {

      @Test
      public void differentGame() {
         final var playersA = new GamePlayers(GAME_A, true, USERS_A);
         final var playersB = new GamePlayers(GAME_B, true, USERS_A);

         assertInvariants(playersA, playersB);
         assertNotEquals(playersA, playersB);
      }

      @Test
      public void differentRecuitment() {
         final var playersA = new GamePlayers(GAME_A, true, USERS_A);
         final var playersB = new GamePlayers(GAME_A, false, USERS_A);

         assertInvariants(playersA, playersB);
         assertEquals(playersA, playersB);
      }

      @Test
      public void differentUsers() {
         final var playersA = new GamePlayers(GAME_A, true, USERS_A);
         final var playersB = new GamePlayers(GAME_A, true, USERS_B);

         assertInvariants(playersA, playersB);
         assertEquals(playersA, playersB);
      }

      @Test
      public void equalAttributes() {
         final var playersA = new GamePlayers(GAME_A, true, USERS_A);
         final var playersB = new GamePlayers(GAME_A, true, USERS_A);

         assertInvariants(playersA, playersB);
         assertEquals(playersA, playersB);
      }
   }// class

   @Nested
   public class ConstructCopy {

      @Test
      public void a() {
         test(GAME_A, false, USERS_A);
      }

      @Test
      public void b() {
         test(GAME_B, true, USERS_B);
      }

      private void test(final Game.Identifier game, final boolean recruiting,
               final Set<UUID> users) {
         final var players0 = new GamePlayers(game, recruiting, users);

         final var copy = new GamePlayers(players0);

         assertInvariants(copy);
         assertInvariants(players0, copy);
         assertAll("Copied", () -> assertEquals(players0, copy),
                  () -> assertSame(players0.getGame(), copy.getGame(), "game"),
                  () -> assertEquals(players0.isRecruiting(),
                           copy.isRecruiting(), "recruiting"),
                  () -> assertEquals(players0.getUsers(), copy.getUsers(),
                           "users"),
                  () -> assertNotSame(players0.getUsers(), copy.getUsers(),
                           "users (not same)"));
      }
   }// class

   @Nested
   public class ConstructWithAttributes {

      @Test
      public void a() {
         test(GAME_A, false, USERS_A);
      }

      @Test
      public void b() {
         test(GAME_B, true, USERS_B);
      }

      private void test(final Game.Identifier game, final boolean recruiting,
               final Set<UUID> users) {
         final var players = new GamePlayers(game, recruiting, users);

         assertInvariants(players);
         assertAll("Has the given attribute values",
                  () -> assertSame(game, players.getGame(), "game"),
                  () -> assertEquals(recruiting, players.isRecruiting(),
                           "recruiting"),
                  () -> assertEquals(users, players.getUsers(), "users"));
         assertNotSame(users, players.getUsers(), "users not same");
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
         final var players = new GamePlayers(GAME_A, recruitment0, USERS_A);

         endRecruitment(players);
      }
   }// class

   @Nested
   public class Json {

      @Test
      public void a() {
         test(GAME_A, false, USERS_A);
      }

      @Test
      public void b() {
         test(GAME_B, true, USERS_B);
      }

      private void test(final Game.Identifier game, final boolean recruiting,
               final Set<UUID> users) {
         final var players = new GamePlayers(game, recruiting, users);
         final var deserialized = JsonTest.serializeAndDeserialize(players);

         assertInvariants(deserialized);
         assertInvariants(players, deserialized);
         assertEquals(players, deserialized);
         assertAll("Deserialised attributes",
                  () -> assertEquals(game, deserialized.getGame(), "game"),
                  () -> assertEquals(recruiting, deserialized.isRecruiting(),
                           "recruiting"),
                  () -> assertEquals(users, deserialized.getUsers(), "users"));
      }
   }// class

   private static final UUID USER_ID_A = UUID.randomUUID();
   private static final UUID USER_ID_B = UUID.randomUUID();
   private static final Game.Identifier GAME_A = new Game.Identifier(
            UUID.randomUUID(), Instant.EPOCH);
   private static final Game.Identifier GAME_B = new Game.Identifier(
            UUID.randomUUID(), Instant.now());
   private static final Set<UUID> USERS_A = Set.of();
   private static final Set<UUID> USERS_B = Set.of(USER_ID_B);

   public static void addUser(final GamePlayers players, final UUID user) {
      final var users0 = Set.copyOf(players.getUsers());

      players.addUser(user);

      assertInvariants(players);
      final var users = players.getUsers();
      assertAll(() -> assertThat(
               "Does not remove any users from the set of users of the game.",
               users.containsAll(users0)),
               () -> assertThat("The set of users contains the given user.",
                        users, hasItem(user)));
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

   public static void assertInvariants(final GamePlayers players) {
      ObjectTest.assertInvariants(players);// inherited

      final var users = players.getUsers();
      assertAll("Not null", () -> assertNotNull(players.getGame(), "game"),
               () -> assertNotNull(users, "users"));
      assertTrue(users.stream().filter(p -> p == null).findAny().isEmpty(),
               "The set of users does not include null.");
   }

   public static void assertInvariants(final GamePlayers playersA,
            final GamePlayers playersB) {
      ObjectTest.assertInvariants(playersA, playersB);// inherited
      assertEquals(playersA.equals(playersB),
               playersA.getGame().equals(playersB.getGame()),
               "Entity semantics, with the game serving as a unique identifier");
   }

   public static void endRecruitment(final GamePlayers players) {
      players.endRecruitment();

      assertInvariants(players);
      assertFalse(players.isRecruiting(), "This game is not recruiting.");
   }
}
