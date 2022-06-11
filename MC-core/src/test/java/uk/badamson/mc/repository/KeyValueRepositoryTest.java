package uk.badamson.mc.repository;
/*
 * © Copyright Benedict Adamson 2019-20,22.
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

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KeyValueRepositoryTest {

   public static abstract class AbstractFake<KEY, VALUE>
            implements KeyValueRepository<KEY, VALUE> {

      protected final Map<KEY, VALUE> store = new ConcurrentHashMap<>();

      @Override
      public final long count() {
         return store.size();
      }

      @Override
      public final void deleteAll() {
         store.clear();
      }

      @Override
      public final void delete(@Nonnull final KEY identifier) {
         Objects.requireNonNull(identifier, "identifier");
         store.remove(identifier);
      }

      @Override
      public final boolean exists(@Nonnull final KEY identifier) {
         Objects.requireNonNull(identifier, "identifier");
         return store.containsKey(identifier);
      }

      @Nonnull
      @Override
      public final Iterable<VALUE> findAll() {
         // Return copies of the values, so we are isolated from downstream
         // mutations
         return List.copyOf(store.values());
      }

      @Nonnull
      @Override
      public final Optional<VALUE> find(@Nonnull final KEY identifier) {
         Objects.requireNonNull(identifier, "identifier");
         return Optional.ofNullable(store.get(identifier));
      }

      @Override
      public final void save(@Nonnull final KEY id, @Nonnull VALUE value) {
         Objects.requireNonNull(id, "id");
         Objects.requireNonNull(value, "value");
         store.put(id, value);
      }

   }

   public static <KEY, VALUE> void assertInvariants(
            @Nonnull final KeyValueRepository<KEY, VALUE> repository) {
      // Do nothing
   }

}
