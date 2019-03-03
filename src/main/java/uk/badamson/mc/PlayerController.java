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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 * End-points for the players and player pages.
 * </p>
 */
@RestController
public class PlayerController {

   @PostMapping("/player")
   public Mono<Void> add(final Player player) {
      Objects.requireNonNull(player, "player");
      return null;// FIXME
   }

   @GetMapping("/player")
   public Flux<Player> getAll() {
      return Flux.fromIterable(List.of(Player.DEFAULT_ADMINISTRATOR));
   }

   @PostMapping("/login")
   public Mono<Void> login(final String name) {
      return null;// FIXME
   }
}
