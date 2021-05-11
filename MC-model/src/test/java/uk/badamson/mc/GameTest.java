package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2020-21.
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
   public class Construct2 {

      @Test
      public void differentIdentifiers() {
         final var identifierA = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var identifierB = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
         final var gameA = new Game(identifierA, Game.RunState.RUNNING);
         final var gameB = new Game(identifierB, Game.RunState.RUNNING);

         assertInvariants(gameA, gameB);
         assertNotEquals(gameA, gameB);
      }

      @Test
      public void equalAttributes() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var gameA = new Game(identifier, Game.RunState.RUNNING);
         final var gameB = new Game(identifier, Game.RunState.RUNNING);

         assertInvariants(gameA, gameB);
         assertEquals(gameA, gameB);
      }
   }// class

   @Nested
   public class ConstructCopy {

      @Test
      public void a() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         test(identifier, Game.RunState.WAITING_TO_START);
      }

      @Test
      public void b() {
         final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
         test(identifier, Game.RunState.RUNNING);
      }

      private void test(final Game.Identifier identifier,
               final Game.RunState runState) {
         final var game0 = new Game(identifier, runState);

         final var copy = new Game(game0);

         assertInvariants(copy);
         assertInvariants(game0, copy);
         assertAll("Copied", () -> assertEquals(game0, copy),
                  () -> assertSame(game0.getIdentifier(), copy.getIdentifier(),
                           "identifier"),
                  () -> assertSame(game0.getRunState(), copy.getRunState(),
                           "runState"));
      }
   }// class

   @Nested
   public class ConstructWithAttributes {

      @Test
      public void a() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         test(identifier, Game.RunState.WAITING_TO_START);
      }

      @Test
      public void b() {
         final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
         test(identifier, Game.RunState.RUNNING);
      }

      private void test(final Game.Identifier identifier,
               final Game.RunState runState) {
         final var game = new Game(identifier, runState);

         assertInvariants(game);
         assertAll("Has the given attribute values",
                  () -> assertSame(identifier, game.getIdentifier(),
                           "identifier"),
                  () -> assertSame(runState, game.getRunState(), "runState"));
      }
   }// class

   /**
    * <p>
    * Unit tests for the {@link Game.Identifier} class.
    * </p>
    */
   public static class IdentifierTest {

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
            constructor(SCENARIO_ID_A, CREATED_A);
         }

         @Test
         public void b() {
            constructor(SCENARIO_ID_B, CREATED_B);
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

            IdentifierTest.assertInvariants(deserialized);
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
         assertAll("Equality requires equal attributes",
                  () -> assertTrue(
                           !(equals && !identifierA.getScenario()
                                    .equals(identifierB.getScenario())),
                           "scenario identifier"),
                  () -> assertTrue(
                           !(equals && !identifierA.getCreated()
                                    .equals(identifierB.getCreated())),
                           "creation time"));
      }

      private static Game.Identifier constructor(final UUID scenario,
               final Instant created) {
         final var identifier = new Game.Identifier(scenario, created);

         IdentifierTest.assertInvariants(identifier);
         assertAll("Attributes have the given values",
                  () -> assertSame(scenario, identifier.getScenario(),
                           "scenario"),
                  () -> assertSame(created, identifier.getCreated(),
                           "created"));
         return identifier;
      }
   }// class

   @Nested
   public class Json {

      @Test
      public void a() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         test(identifier, Game.RunState.WAITING_TO_START);
      }

      @Test
      public void b() {
         final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
         test(identifier, Game.RunState.RUNNING);
      }

      private void test(final Game.Identifier identifier,
               final Game.RunState runState) {
         final var game = new Game(identifier, runState);
         final var deserialized = JsonTest.serializeAndDeserialize(game);

         assertInvariants(deserialized);
         assertInvariants(game, deserialized);
         assertEquals(game, deserialized);
         assertAll("Deserialised attributes",
                  () -> assertEquals(identifier, deserialized.getIdentifier(),
                           "identifier"),
                  () -> assertEquals(runState, deserialized.getRunState(),
                           "runState"));
      }
   }// class

   private static final UUID SCENARIO_ID_A = UUID.randomUUID();
   private static final UUID SCENARIO_ID_B = UUID.randomUUID();
   private static final Instant CREATED_A = Instant.EPOCH;
   private static final Instant CREATED_B = Instant.now();

   public static void assertInvariants(final Game game) {
      ObjectTest.assertInvariants(game);// inherited

      assertAll("Not null",
               () -> assertNotNull(game.getIdentifier(), "identifier"));
   }

   public static void assertInvariants(final Game gameA, final Game gameB) {
      ObjectTest.assertInvariants(gameA, gameB);// inherited
      assertEquals(gameA.equals(gameB),
               gameA.getIdentifier().equals(gameB.getIdentifier()),
               "Entity semantics, with the identifier serving as a unique identifier");
   }
}
