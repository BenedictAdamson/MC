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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import uk.badamson.mc.Player;
import uk.badamson.mc.PlayerTest;
import uk.badamson.mc.repository.PlayerRepository;
import uk.badamson.mc.repository.PlayerRepositoryTest;

/**
 * <p>
 * Unit tests and auxiliary test code for the {@link ServiceImpl} class.
 * </p>
 */
@RunWith(JUnitPlatform.class)
public class ServiceImplTest {

   @Nested
   public class Add_Player {

      @Nested
      public class One {

         @Test
         public void a() {
            test(playerA);
         }

         @Test
         public void b() {
            test(playerB);
         }

         private void test(final Player player) {
            final var service = new ServiceImpl(playerRepositoryA);
            ServiceTest.add_1(service, player);
            StepVerifier.create(service.getPlayers()).expectNextCount(2)
                     .verifyComplete();
         }
      }// class

      @Nested
      public class Two {

         @Test
         public void a() {
            test(playerA, playerB);
         }

         @Test
         public void b() {
            test(playerB, playerC);
         }

         private void test(final Player player1, final Player player2) {
            final var service = new ServiceImpl(playerRepositoryA);
            ServiceTest.add_2(service, player1, player2);
            StepVerifier.create(service.getPlayers()).expectNextCount(3)
                     .verifyComplete();
         }
      }// class
   }// class

   @Nested
   public class Constructor {

      @Test
      public void a() {
         constructor(playerRepositoryA);
      }

      @Test
      public void b() {
         constructor(playerRepositoryB);
      }
   }// class

   @Nested
   public class Scenario {

      private ServiceImpl service;
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
         service = new ServiceImpl(playerRepositoryA);
      }

      private void then_the_list_of_players_has_one_player() {
         StepVerifier.create(players).expectNextCount(1);
      }

      private void then_the_list_of_players_includes_a_player_named_Administrator() {
         final List<Player> playersList = new ArrayList<>(1);
         players.subscribe(p -> playersList.add(p));
         playersList.forEach(Assertions::assertNotNull);
         playersList.forEach(PlayerTest::assertInvariants);
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

   public static void assertInvariants(final ServiceImpl service) {
      ServiceTest.assertInvariants(service);// inherited

      final PlayerRepository playerRepository = service.getPlayerRepository();
      assertNotNull(playerRepository,
               "Always have a (non null) player repository.");
      PlayerRepositoryTest.assertInvariants(playerRepository);
   }

   private static ServiceImpl constructor(
            final PlayerRepository playerRepository) {
      final var service = new ServiceImpl(playerRepository);

      assertInvariants(service);
      assertSame(playerRepository, service.getPlayerRepository(),
               "The player repository of this service is the given player repository.");
      getPlayers(service);

      return service;
   }

   private static Flux<Player> getPlayers(final ServiceImpl service) {
      final Flux<Player> players = ServiceTest.getPlayers(service);// inherited

      assertInvariants(service);

      return players;
   }

   private PlayerRepository playerRepositoryA;

   private PlayerRepository playerRepositoryB;

   private Player playerA;

   private Player playerB;

   private Player playerC;

   @BeforeEach
   public void setUpPlayers() {
      playerA = new Player("John");
      playerB = new Player("Alan");
      playerC = new Player("Gweezer");
   }

   @BeforeEach
   public void setUpRepositories() {
      playerRepositoryA = new PlayerRepositoryTest.Fake();
      playerRepositoryB = new PlayerRepositoryTest.Fake();
   }
}
