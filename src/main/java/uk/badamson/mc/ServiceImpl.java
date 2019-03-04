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

import java.util.List;
import java.util.Objects;

import org.springframework.security.core.userdetails.UserDetails;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 * The concrete implementation of the service layer of the Mission Command game.
 * </p>
 */
@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

   @Override
   public Mono<Void> add(final Player player) {
      Objects.requireNonNull(player, "player");
      return Mono.empty();// TODO
   }

   @Override
   public Mono<UserDetails> findByUsername(final String username) {
      Objects.requireNonNull(username, "username");
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Flux<Player> getPlayers() {
      return Flux.fromIterable(List.of(Player.DEFAULT_ADMINISTRATOR));
   }

}
