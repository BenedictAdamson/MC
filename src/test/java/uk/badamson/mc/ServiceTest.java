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

import static org.junit.Assert.assertNotNull;

import reactor.core.publisher.Flux;

/**
 * <p>
 * Auxiliary test code for classes that implement the {@link Service} interface.
 * </p>
 */
public class ServiceTest {

   public static void assertInvariants(final Service service) {
      // Do nothing
   }

   public static Flux<Player> getPlayers(final Service service) {
      final Flux<Player> players = service.getPlayers();

      assertInvariants(service);
      assertNotNull("Always returns a (non null) publisher.", players);

      return players;
   }
}
