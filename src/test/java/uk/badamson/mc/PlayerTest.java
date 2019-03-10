package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2019.
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.lang.NonNull;

/**
 * <p>
 * Unit tests and auxiliary test code for the {@link Player} class
 */
@RunWith(JUnitPlatform.class)
public class PlayerTest {

   @Nested
   public class Constructor {

      @Nested
      public class TwoEquivalent {

         @Test
         public void a() {
            test(USERNAME_A);
         }

         @Test
         public void b() {
            test(USERNAME_B);
         }

         private void test(@NonNull final String username) {
            final var player1 = new Player(username);
            final var player2 = new Player(new String(username));

            assertInvariants(player1, player2);
            assertEquals(player1, player2);
         }

      }// class

      @Test
      public void a() {
         test(USERNAME_A);
      }

      @Test
      public void b() {
         test(USERNAME_B);
      }

      private Player test(@NonNull final String username) {
         final var player = new Player(username);

         assertInvariants(player);
         assertSame(username, player.getUsername(),
                  "The username of this player is the given username.");
         assertTrue(player.getAuthorities().isEmpty(),
                  "This player has no granted authorities.");

         return player;
      }
   }// class

   private static final String USERNAME_A = "John";

   private static final String USERNAME_B = "Alan";

   public static void assertInvariants(final Player player) {
      assertNotNull(player.getAuthorities(), "Non null, authorities");
      assertNotNull(player.getUsername(), "Non null, username");
      assertEquals(player, player, "An objectis always equivalent to itself.");
   }

   public static void assertInvariants(final Player player1,
            final Player player2) {
      final boolean equals = player1.equals(player2);
      assertTrue(!(equals && !player2.equals(player1)),
               "Equality is symmetric");
      assertTrue(!(equals
               && !player1.getUsername().equals(player2.getUsername())),
               "Equivalence requires equivalent username values");
   }
}
