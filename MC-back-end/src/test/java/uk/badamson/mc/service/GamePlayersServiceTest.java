package uk.badamson.mc.service;
/*
 * © Copyright Benedict Adamson 2019-21.
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
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.AccessControlException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import uk.badamson.mc.Game;
import uk.badamson.mc.Game.Identifier;
import uk.badamson.mc.GamePlayers;

/**
 * <p>
 * Auxiliary test code for classes that implement the {@link GamePlayersService}
 * interface.
 * </p>
 */
public class GamePlayersServiceTest {

   public static void assertInvariants(final GamePlayersService service) {
      assertNotNull(service.getGameService(), "Not null, gameService");
   }

   public static GamePlayers endRecruitment(final GamePlayersService service,
            final Game.Identifier id) throws NoSuchElementException {
      final var present = service.getGameService().getGame(id).isPresent();

      final GamePlayers result;
      try {
         result = service.endRecruitment(id);
      } catch (final NoSuchElementException e) {
         assertInvariants(service);
         assertFalse(present, "game with the given ID is not present");
         throw e;
      }

      assertInvariants(service);
      assertTrue(present, "game with the given ID is present");
      assertNotNull(result, "Returns a (non null) value.");// guard
      assertAll(() -> assertEquals(id, result.getGame(), "game"),
               () -> assertFalse(result.isRecruiting(), "recruiting"));
      assertFalse(service.getGamePlayersAsGameManager(id).get().isRecruiting(),
               "Subsequent retrieval of game players using an identifier equivalent to the given ID returns "
                        + "a value that is also not recruiting.");
      return result;
   }

   public static Optional<Game.Identifier> getCurrentGameOfUser(
            final GamePlayersService service, final UUID user) {
      final var result = service.getCurrentGameOfUser(user);
      assertInvariants(service);
      assertNotNull(result, "Returns a (non null) optional value.");
      return result;
   }

   public static Optional<GamePlayers> getGamePlayersAsGameManager(
            final GamePlayersService service, final Game.Identifier id) {
      final var result = service.getGamePlayersAsGameManager(id);

      assertInvariants(service);
      assertNotNull(result, "Returns a (non null) optional value.");// guard
      final var present = result.isPresent();
      assertEquals(service.getGameService().getGame(id).isPresent(), present,
               "Returns a present value if, and only if, "
                        + "the associated game service indicates that a game with the given ID exists.");
      if (present) {
         assertEquals(id, result.get().getGame(), "game");
      }
      return result;
   }

   public static Optional<GamePlayers> getGamePlayersAsNonGameManager(
            final GamePlayersService service, final Game.Identifier id,
            final UUID user) {
      final var result = service.getGamePlayersAsNonGameManager(id, user);

      assertInvariants(service);
      assertNotNull(result, "Returns a (non null) optional value.");// guard
      final var present = result.isPresent();
      assertEquals(service.getGameService().getGame(id).isPresent(), present,
               "Returns a present value if, and only if, "
                        + "the associated game service indicates that a game with the given ID exists.");
      if (present) {
         final var gamePlayers = result.get();
         assertEquals(id, gamePlayers.getGame(), "game");
         assertThat(
                  "The collection of players is either empty or contains the requesting user.",
                  Set.copyOf(gamePlayers.getUsers().values()),
                  either(empty()).or(is(Set.of(user))));
      }
      return result;
   }

   public static boolean mayUserJoinGame(final GamePlayersService service,
            final UUID user, final Identifier game) {
      final var result = service.mayUserJoinGame(user, game);
      assertInvariants(service);
      return result;
   }

   public static void userJoinsGame(final GamePlayersService service,
            final UUID user, final Game.Identifier game)
            throws NoSuchElementException, UserAlreadyPlayingException,
            IllegalGameStateException, AccessControlException {
      try {
         service.userJoinsGame(user, game);
      } catch (UserAlreadyPlayingException | IllegalGameStateException
               | AccessControlException | NoSuchElementException e) {
         assertInvariants(service);
         throw e;
      }
      assertInvariants(service);
   }
}
