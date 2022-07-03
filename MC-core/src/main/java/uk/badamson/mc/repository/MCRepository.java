package uk.badamson.mc.repository;
/*
 * © Copyright Benedict Adamson 2022.
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

import uk.badamson.mc.Game;
import uk.badamson.mc.GamePlayers;
import uk.badamson.mc.User;
import uk.badamson.mc.UserGameAssociation;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.stream.Stream;

@ThreadSafe
public abstract class MCRepository {

    @NotThreadSafe
    public abstract class Context implements AutoCloseable {
        private final IdentityHashMap<Game, Game.Identifier> gameToIdMap = new IdentityHashMap<>();
        private final Map<Game.Identifier, Game> idToGameMap = new HashMap<>();
        private boolean haveAllGames = false;

        public final void addGame(@Nonnull Game.Identifier id, @Nonnull Game game) {
            if (gameToIdMap.containsKey(game) || idToGameMap.containsKey(id)) {
                throw new IllegalStateException("already present");
            }
            gameToIdMap.put(game, id);
            idToGameMap.put(id, game);
            addGameUncached(id, game);
        }

        public final void updateGame(@Nonnull Game game) {
            final var id = gameToIdMap.get(game);
            if (id == null) {
                throw new IllegalStateException("not present");
            }
            updateGameUncached(id, game);
        }

        @Nonnull
        public final Optional<Game> findGame(@Nonnull Game.Identifier id) {
            var game = idToGameMap.get(id);
            if (game != null) {
                return Optional.of(game);
            }
            final var result = findGameUncached(id);
            if (result.isPresent()) {
                game = result.get();
                gameToIdMap.put(game, id);
                idToGameMap.put(id, game);
            }
            return result;
        }

        @Nonnull
        public final Stream<Game.Identifier> findAllGameIdentifiers() {
            if (!haveAllGames) {
                findAllGameIdentifiersUncached().forEach(id -> idToGameMap.putIfAbsent(id, null) );
                haveAllGames = true;
            }
            return Set.copyOf(idToGameMap.keySet()).stream();
        }

        public abstract void saveGamePlayers(@Nonnull Game.Identifier id, @Nonnull GamePlayers gamePlayers);

        @Nonnull
        public abstract Optional<GamePlayers> findGamePlayers(@Nonnull Game.Identifier id);

        @Nonnull
        public abstract Optional<UserGameAssociation> findCurrentUserGame(@Nonnull UUID userId);

        public abstract void saveCurrentUserGame(@Nonnull UUID userId, @Nonnull UserGameAssociation entity);

        @Nonnull
        public abstract Optional<User> findUserByUsername(@Nonnull String username);

        @Nonnull
        public abstract Optional<User> findUser(@Nonnull UUID id);

        @Nonnull
        public abstract Stream<User> findAllUsers();

        public abstract void saveUser(@Nonnull UUID id, @Nonnull User user);

        @Nonnull
        public MCRepository getRepository() {
            return MCRepository.this;
        }

        /**
         * {@inheritDoc}
         *
         * Save operations performed through this context are not guaranteed to have been performed
         * until normal return from this method. That is, the implementation may cache save (write) operations.
         *
         * @throws RuntimeException
         * If not all save operations could be performed.
         * This class is however not required to provide transaction semantics:
         * if it throws an exception, some saves might have been performed and some might not.
         */
        @Override
        @OverridingMethodsMustInvokeSuper
        public void close() throws RuntimeException {
            gameToIdMap.clear();
            idToGameMap.clear();
            haveAllGames = false;
        }

        protected abstract void addGameUncached(@Nonnull Game.Identifier id, @Nonnull Game game);

        protected abstract void updateGameUncached(@Nonnull Game.Identifier id, @Nonnull Game game);

        @Nonnull
        protected abstract Optional<Game> findGameUncached(@Nonnull Game.Identifier id);

        @Nonnull
        protected abstract Stream<Game.Identifier> findAllGameIdentifiersUncached();
    }

    @Nonnull
    public abstract Context openContext();
}
