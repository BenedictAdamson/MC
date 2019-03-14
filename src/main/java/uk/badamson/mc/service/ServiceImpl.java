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

import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.badamson.mc.Player;
import uk.badamson.mc.repository.PlayerRepository;

/**
 * <p>
 * The concrete implementation of the service layer of the Mission Command game.
 * </p>
 */
@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

   private static void prepareRepository(
            final PlayerRepository playerRepository) {
      // TODO do not add if already present
      playerRepository.save(Player.DEFAULT_ADMINISTRATOR).block();
   }

   private final PlayerRepository playerRepository;

   /**
    * <p>
    * Construct a service layer instance that uses a given repository.
    * </p>
    * <ul>
    * <li>The {@linkplain #getPlayerRepository() player repository} of this
    * service is the given player repository.</li>
    * </ul>
    *
    * @param playerRepository
    *           The {@link Player} repository that this service layer instance
    *           uses.
    * @throws NullPointerException
    *            If {@code playerRepository} is null.
    */
   public ServiceImpl(@NonNull final PlayerRepository playerRepository) {
      this.playerRepository = Objects.requireNonNull(playerRepository,
               "playerRepository");
      prepareRepository(playerRepository);
   }

   @Override
   public Mono<Void> add(final Player player) {
      Objects.requireNonNull(player, "player");
      return playerRepository.save(player).then();
   }

   @Override
   public Mono<UserDetails> findByUsername(final String username) {
      Objects.requireNonNull(username, "username");
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * <p>
    * The {@link Player} repository that this service layer instance uses.
    * </p>
    * <ul>
    * <li>Always have a (non null) player repository.</li>
    * </ul>
    */
   public final PlayerRepository getPlayerRepository() {
      return playerRepository;
   }

   @Override
   public Flux<Player> getPlayers() {
      return playerRepository.findAll();
   }

}
