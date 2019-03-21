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
import uk.badamson.mc.Authority;
import uk.badamson.mc.Player;
import uk.badamson.mc.repository.PlayerRepository;

/**
 * <p>
 * The concrete implementation of the service layer of the Mission Command game.
 * </p>
 */
@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

   private final PlayerRepository playerRepository;
   private final Player administrator;

   /**
    * <p>
    * Construct a service layer instance that uses a given repository.
    * </p>
    * <ul>
    * <li>The {@linkplain #getPlayerRepository() player repository} of this
    * service is the given player repository.</li>
    * <li>The {@linkplain Player#getPassword() password} of the
    * {@linkplain Player#ADMINISTRATOR_USERNAME administrator}
    * {@linkplain #findByUsername(String) user details found through this
    * service} is {@linkplain String#equals(Object) equal to} the given
    * administrator password.</li>
    * </ul>
    *
    * @param playerRepository
    *           The {@link Player} repository that this service layer instance
    *           uses.
    * @param administratorPassword
    *           The (encrypted) {@linkplain Player#getPassword() password} of
    *           the {@linkplain Player#ADMINISTRATOR_USERNAME administrator}
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code playerRepository} is null.</li>
    *            <li>If {@code administratorPasword} is null.</li>
    *            </ul>
    */
   public ServiceImpl(@NonNull final PlayerRepository playerRepository,
            @NonNull final String administratorPassword) {
      this.playerRepository = Objects.requireNonNull(playerRepository,
               "playerRepository");
      Objects.requireNonNull(administratorPassword, "administratorPassword");
      administrator = new Player(Player.ADMINISTRATOR_USERNAME,
               administratorPassword, Authority.ALL);
   }

   @Override
   public Mono<Void> add(final Player player) {
      Objects.requireNonNull(player, "player");
      if (Player.ADMINISTRATOR_USERNAME.equals(player.getUsername())) {
         throw new IllegalArgumentException("Player is administrator");
      }
      return playerRepository.save(player).then();
   }

   @Override
   public Mono<UserDetails> findByUsername(final String username) {
      Objects.requireNonNull(username, "username");
      if (Player.ADMINISTRATOR_USERNAME.equals(username)) {
         return Mono.just(administrator);
      } else {
         return playerRepository.findById(username).cast(UserDetails.class);
      }
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
      return Flux.concat(Mono.just(administrator), playerRepository.findAll());
   }

}
