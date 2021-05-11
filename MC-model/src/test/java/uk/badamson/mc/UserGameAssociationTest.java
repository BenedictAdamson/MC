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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import uk.badamson.mc.GameTest.IdentifierTest;

/**
 * <p>
 * Unit tests and auxiliary test code for the {@link UserGameAssociation} class
 */
public class UserGameAssociationTest {

   @Nested
   public class Constructor {

      @Nested
      public class Duplicates {

         @Test
         public void a() {
            test(USER_A, GAME_A);
         }

         @Test
         public void b() {
            test(USER_B, GAME_B);
         }

         private void test(final UUID user, final Game.Identifier game) {
            final var association1 = new UserGameAssociation(user, game);
            // Tough test: equivalent but not same attribute objects
            final var association2 = new UserGameAssociation(copy(user),
                     copy(game));

            assertInvariants(association1, association2);
            assertEquals(association1, association2);
         }
      }// class

      @Nested
      public class TwoDifferent {

         @Test
         public void all() {
            final var association1 = new UserGameAssociation(USER_A, GAME_A);
            final var association2 = new UserGameAssociation(USER_B, GAME_B);

            assertInvariants(association1, association2);
            assertNotEquals(association1, association2);
         }

         @Test
         public void game() {
            final var association1 = new UserGameAssociation(USER_A, GAME_A);
            final var association2 = new UserGameAssociation(USER_A, GAME_B);

            assertInvariants(association1, association2);
            assertNotEquals(association1, association2);
         }

         @Test
         public void user() {
            final var association1 = new UserGameAssociation(USER_A, GAME_A);
            final var association2 = new UserGameAssociation(USER_B, GAME_A);

            assertInvariants(association1, association2);
            assertNotEquals(association1, association2);
         }

      }// class

      @Test
      public void a() {
         test(USER_A, GAME_A);
      }

      @Test
      public void b() {
         test(USER_B, GAME_B);
      }

      private UserGameAssociation test(final UUID user,
               final Game.Identifier game) {
         final var association = new UserGameAssociation(user, game);

         assertInvariants(association);
         assertAll("Attribute values",
                  () -> assertSame(user, association.getUser(), "user"),
                  () -> assertSame(game, association.getGame(), "game"));
         return association;
      }

   }// class

   @Nested
   public class Json {

      @Test
      public void a() {
         test(USER_A, GAME_A);
      }

      @Test
      public void b() {
         test(USER_B, GAME_B);
      }

      private void test(final UUID user, final Game.Identifier game) {
         final var association = new UserGameAssociation(user, game);

         final var deserialized = JsonTest.serializeAndDeserialize(association);

         assertInvariants(association, deserialized);
         assertEquals(association, deserialized);
      }
   }// class

   private static final UUID USER_A = UUID.randomUUID();

   private static final UUID USER_B = UUID.randomUUID();

   private static final Game.Identifier GAME_A = new Game.Identifier(
            UUID.randomUUID(), Instant.EPOCH);

   private static final Game.Identifier GAME_B = new Game.Identifier(
            UUID.randomUUID(), Instant.now());

   public static void assertInvariants(final UserGameAssociation association) {
      ObjectTest.assertInvariants(association);// inherited

      final var game = association.getGame();
      assertAll("Not null", () -> assertNotNull(association.getUser(), "user"),
               () -> assertNotNull(game, "game"));
      IdentifierTest.assertInvariants(game);
   }

   public static void assertInvariants(final UserGameAssociation association1,
            final UserGameAssociation association2) {
      ObjectTest.assertInvariants(association1, association2);// inherited
      final var equals = association1.equals(association2);
      assertAll("Value semantics requires equivalent attribute values",
               () -> assertFalse(equals && !association1.getUser()
                        .equals(association2.getUser()), "user"),
               () -> assertFalse(equals && !association1.getGame()
                        .equals(association2.getGame()), "game"));
   }

   private static Game.Identifier copy(final Game.Identifier id) {
      return new Game.Identifier(copy(id.getScenario()), copy(id.getCreated()));
   }

   private static Instant copy(final Instant t) {
      return Instant.ofEpochSecond(t.getEpochSecond(), t.getNano());
   }

   private static UUID copy(final UUID id) {
      return new UUID(id.getMostSignificantBits(),
               id.getLeastSignificantBits());
   }
}
