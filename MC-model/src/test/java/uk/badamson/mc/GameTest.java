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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
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
public class GameTest {

   @Nested
   public class Construct {

      @Test
      public void a() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         test(identifier, false);
      }

      @Test
      public void b() {
         final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
         test(identifier, true);
      }

      private void test(final Game.Identifier identifier,
               final boolean recruiting) {
         final var game = new Game(identifier, recruiting);

         assertInvariants(game);
         assertAll("Has the given attribute values",
                  () -> assertSame(identifier, game.getIdentifier(),
                           "identifier"),
                  () -> assertEquals(recruiting, game.isRecruiting(),
                           "recruiting"));
         JsonTest.assertCanSerializeAndDeserialize(game);
      }
   }// class

   @Nested
   public class Construct2 {

      @Test
      public void differentIdentifiers() {
         final var identifierA = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var identifierB = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
         final var gameA = new Game(identifierA, true);
         final var gameB = new Game(identifierB, true);

         assertInvariants(gameA, gameB);
         assertNotEquals(gameA, gameB);
      }

      @Test
      public void differentRecuitment() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var gameA = new Game(identifier, true);
         final var gameB = new Game(identifier, false);

         assertInvariants(gameA, gameB);
         assertEquals(gameA, gameB);
      }

      @Test
      public void equalAttributes() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var gameA = new Game(identifier, true);
         final var gameB = new Game(identifier, true);

         assertInvariants(gameA, gameB);
         assertEquals(gameA, gameB);
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
         final var game = new Game(identifier, recruitment0);

         game.endRecruitment();

         assertInvariants(game);
         assertFalse(game.isRecruiting(), "This game is not recruiting.");
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
            JsonTest.assertCanSerializeAndDeserialize(identifier);
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

   private static final UUID SCENARIO_ID_A = UUID.randomUUID();
   private static final UUID SCENARIO_ID_B = UUID.randomUUID();
   private static final Instant CREATED_A = Instant.EPOCH;
   private static final Instant CREATED_B = Instant.now();

   public static void assertInvariants(final Game game) {
      ObjectTest.assertInvariants(game);// inherited

      assertNotNull(game.getIdentifier(), "Not null, identifier");
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
}
