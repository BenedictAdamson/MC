package uk.badamson.mc.service;
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

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.badamson.mc.Game;
import uk.badamson.mc.Game.Identifier;
import uk.badamson.mc.GamePlayers;
import uk.badamson.mc.repository.GamePlayersRepository;

/**
 * <p>
 * Implementation of the part of the service layer pertaining to players of
 * games of Mission Command.
 * </p>
 */
@Service
public class GamePlayersServiceImpl implements GamePlayersService {

   private static final Set<UUID> NO_USERS = Set.of();

   private static GamePlayers createDefault(final Game.Identifier id) {
      return new GamePlayers(id, true, NO_USERS);
   }

   private final GamePlayersRepository repository;

   private final GameService gameService;

   /**
    * <p>
    * Construct a service with give associations.
    * </p>
    * <ul>
    * <li>The created service has the given {@code repository} as its
    * {@linkplain #getRepository() repository}.</li>
    * <li>The created service has the given {@code gameService} as its
    * {@linkplain #getGameService() scenario service}.</li>
    * </ul>
    *
    * @param repository
    *           The repository that this service uses for persistent storage.
    * @param gameService
    *           The part of the service layer that this service uses for
    *           information about scenarios.
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code repository} is null.</li>
    *            <li>If {@code gameService} is null.</li>
    *            </ul>
    */
   @Autowired
   public GamePlayersServiceImpl(
            @Nonnull final GamePlayersRepository repository,
            @Nonnull final GameService gameService) {
      this.repository = Objects.requireNonNull(repository, "repository");
      this.gameService = Objects.requireNonNull(gameService, "gameService");
   }

   @Override
   @Nonnull
   public GamePlayers endRecruitment(@Nonnull final Identifier id)
            throws NoSuchElementException {
      final var players = get(id).get();
      players.endRecruitment();
      return repository.save(players);
   }

   private Optional<GamePlayers> get(final Game.Identifier id) {
      Objects.requireNonNull(id, "id");
      var result = Optional.<GamePlayers>empty();
      if (gameService.getGame(id).isPresent()) {
         result = repository.findById(id);
         if (result.isEmpty()) {
            result = Optional.of(createDefault(id));
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    * <p>
    * Furthermore:
    * </p>
    * <ul>
    * <li>If returns a {@linkplain Optional#isPresent() present} value, and the
    * associated {@linkplain #getRepository() repository}
    * {@linkplain GamePlayersRepository#findById(Identifier) has} a stored value
    * with the given ID, that returned value is the value retrieved from the
    * repository.</li>
    * </ul>
    *
    * @param id
    *           {@inheritDoc}
    * @return {@inheritDoc}
    * @throws NullPointerException
    *            {@inheritDoc}
    */
   @Override
   @Nonnull
   public Optional<GamePlayers> getGamePlayers(
            @Nonnull final Game.Identifier id) {
      return get(id);
   }

   @Override
   @Nonnull
   public final GameService getGameService() {
      return gameService;
   }

   /**
    * <p>
    * The repository that this service uses for persistent storage.
    * </p>
    * <ul>
    * <li>Always have a (non null) repository.</li>
    * </ul>
    *
    * @return the repository
    */
   @Nonnull
   public final GamePlayersRepository getRepository() {
      return repository;
   }

}
