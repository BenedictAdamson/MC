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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class MCRepository {

    public abstract class Context implements AutoCloseable {

        public abstract void saveGame(@Nonnull Game.Identifier id, @Nonnull Game game);

        @Nonnull
        public abstract Optional<Game> findGame(@Nonnull Game.Identifier id);

        @Nonnull
        public abstract Stream<Game> findAllGames();

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
        public abstract void close() throws RuntimeException;
    }

    @Nonnull
    public abstract Context openContext();
}
