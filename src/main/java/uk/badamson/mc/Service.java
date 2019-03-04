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

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p>
 * The service layer of the Mission Command game.
 * </p>
 */
public interface Service extends ReactiveUserDetailsService {

   /**
    * <p>
    * Add a player to the {@linkplain #getPlayers() list of players}.
    * </p>
    * <ul>
    * <li>Always returns a (non null) publisher.</li>
    * <li>As for all publishers, the returned publisher will not
    * {@linkplain Subscriber#onNext(Object) provide} a null element to a
    * subscriber.</li>
    * </ul>
    *
    * @param player
    *           The player to add
    * @return a {@linkplain Publisher publisher} that
    *         {@linkplain Subscriber#onComplete() completes} on addition of the
    *         player or {@linkplain Subscriber#onError(Throwable) publishes an
    *         error condition} if the addition fails.
    * @throws NullPointerException
    *            If {@code player} is null
    */
   public Mono<Void> add(final Player player);

   /**
    * <p>
    * Retrieve a list of the current players of this instance of the Mission
    * Command game.
    * </p>
    * <ul>
    * <li>Always returns a (non null) publisher.</li>
    * <li>As for all publishers, the returned publisher will not
    * {@linkplain Subscriber#onNext(Object) provide} a null element to a
    * subscriber.</li>
    * </ul>
    *
    * @return a {@linkplain Publisher publisher} of the players.
    */
   public Flux<Player> getPlayers();
}
