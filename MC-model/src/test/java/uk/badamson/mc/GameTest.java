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
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
         test(identifier);
      }

      @Test
      public void b() {
         final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
         test(identifier);
      }

      private void test(final Game.Identifier identifier) {
         final var game = new Game(identifier);

         assertInvariants(game);
         assertSame(identifier, game.getIdentifier(), "identifier");
         JsonTest.assertCanSerializeAndDeserialize(game);
      }
   }// class

   @Nested
   public class Construct2 {

      @Test
      public void differentIdentifiers() {
         final var identifierA = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var identifierB = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
         final var gameA = new Game(identifierA);
         final var gameB = new Game(identifierB);

         assertInvariants(gameA, gameB);
         assertNotEquals(gameA, gameB);
      }

      @Test
      public void differentScenarioDescriptions() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var gameA = new Game(identifier);
         final var gameB = new Game(identifier);

         assertInvariants(gameA, gameB);
         assertEquals(gameA, gameB);
      }

      @Test
      public void differentScenarioTitles() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var gameA = new Game(identifier);
         final var gameB = new Game(identifier);

         assertInvariants(gameA, gameB);
         assertEquals(gameA, gameB);
      }

      @Test
      public void equalAttributes() {
         final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
         final var gameA = new Game(identifier);
         final var gameB = new Game(identifier);

         assertInvariants(gameA, gameB);
         assertEquals(gameA, gameB);
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
