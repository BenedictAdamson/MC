package uk.badamson.mc.service;
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

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.security.core.userdetails.UserDetails;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.badamson.mc.Player;
import uk.badamson.mc.UserDetailsTest;

/**
 * <p>
 * Auxiliary test code for classes that implement the {@link Service} interface.
 * </p>
 */
@RunWith(JUnitPlatform.class)
public class ServiceTest {

   public static Mono<Void> add(final Service service, final Player player) {
      final var publisher = service.add(player);

      assertInvariants(service);
      assertNotNull(publisher, "Always returns a (non null) publisher.");

      return publisher;
   }

   public static void add_1(final Service service, final Player player) {
      add(service, player).block();
      final Flux<Player> players = service.getPlayers();
      final UserDetails userDetails = findByUsername(service,
               player.getUsername()).block();
      StepVerifier.create(players.filter(p -> player.equals(p)))
               .expectNext(player)
               .as("A subsequently retrieved sequence of the players will include a player equivalent to the given player.")
               .verifyComplete();
      assertEquals(player, userDetails,
               "Subsequently finding user details using the username of the given player will retrieve user details equivalent to the user details of the given player.");
   }

   public static void add_2(final Service service, final Player player1,
            final Player player2) {
      add(service, player1).block();
      add(service, player2).block();
      final Flux<Player> players = service.getPlayers();
      assertTrue(players.hasElement(player1).block(),
               "A subsequently retrieved sequence of the players will include a player equivalent to the given player [1].");
      assertTrue(players.hasElement(player2).block(),
               "A subsequently retrieved sequence of the players will include a player equivalent to the given player [2].");
   }

   public static void assertInvariants(final Service service) {
      // Do nothing
   }

   public static Mono<UserDetails> findByUsername(final Service service,
            final String username) {
      final var administrator = Player.ADMINISTRATOR_USERNAME.equals(username);

      final var publisher = service.findByUsername(username);

      assertInvariants(service);
      assertNotNull(publisher, "Non null user details publisher");
      final var userDetails = publisher.block();
      assertTrue(!(administrator && userDetails == null),
               "Always have user details for the administrator.");
      if (userDetails != null) {
         UserDetailsTest.assertInvariants(userDetails);
         assertThat("The administrator has a complete set of authorities.",
                  userDetails.getAuthorities(),
                  administrator ? is(Authority.ALL) : anything());
      }

      return publisher;
   }

   public static Flux<Player> getPlayers(final Service service) {
      final Flux<Player> players = service.getPlayers();

      assertInvariants(service);
      assertNotNull(players, "Always returns a (non null) publisher.");
      assertTrue(players
               .any(p -> Player.ADMINISTRATOR_USERNAME.equals(p.getUsername()))
               .block(), "The list of players always has an administrator.");

      return players;
   }
}
