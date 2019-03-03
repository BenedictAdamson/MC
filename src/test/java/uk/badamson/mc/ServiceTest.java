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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * <p>
 * Auxiliary test code for classes that implement the {@link Service} interface.
 * </p>
 */
@RunWith(JUnitPlatform.class)
public class ServiceTest {

   @Nested
   public class Scenario {

      private Service service;
      private Flux<Player> players;

      @Test
      public void get_players_of_fresh_instance() {
         given_a_fresh_instance_of_MC();
         when_getting_the_players();
         assertAll(() -> then_the_list_of_players_has_one_player(),
                  () -> then_the_list_of_players_includes_the_administrator(),
                  () -> then_the_list_of_players_includes_a_player_named_Administrator());
      }

      private void given_a_fresh_instance_of_MC() {
         service = new ServiceImpl();
      }

      private void then_the_list_of_players_has_one_player() {
         StepVerifier.create(players).expectNextCount(1);
      }

      private void then_the_list_of_players_includes_a_player_named_Administrator() {
         final List<Player> playersList = new ArrayList<>(1);
         players.subscribe(p -> playersList.add(p));
         assertThat(
                  "the list of players includes a player named \"Administrator\"",
                  playersList, containsInAnyOrder(new Player("Administrator")));
      }

      private void then_the_list_of_players_includes_the_administrator() {
         StepVerifier.create(players).expectNext(Player.DEFAULT_ADMINISTRATOR);
      }

      private void when_getting_the_players() {
         players = getPlayers(service);
      }
   }// class

   public static void assertInvariants(final Service service) {
      // Do nothing
   }

   public static Flux<Player> getPlayers(final Service service) {
      final Flux<Player> players = service.getPlayers();

      assertInvariants(service);
      assertNotNull(players, "Always returns a (non null) publisher.");

      return players;
   }
}
