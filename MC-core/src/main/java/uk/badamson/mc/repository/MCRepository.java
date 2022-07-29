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

import uk.badamson.mc.*;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.*;

@ThreadSafe
public abstract class MCRepository {

    @Nonnull
    public abstract Context openContext();

    @NotThreadSafe
    public abstract class Context implements AutoCloseable {

        private final IdentityHashMap<Game, GameIdentifier> gameToIdMap = new IdentityHashMap<>();
        private final Map<GameIdentifier, FindGameResult> idToGameMap = new HashMap<>();
        private final IdentityHashMap<UserGameAssociation, UUID> userGameAssociationToIdMap = new IdentityHashMap<>();
        private final Map<UUID, UserGameAssociation> idToUserGameAssociationMap = new HashMap<>();
        private final IdentityHashMap<User, UUID> userToIdMap = new IdentityHashMap<>();
        private final Map<UUID, User> idToUserMap = new HashMap<>();
        private final Map<String, User> usernameToUserMap = new HashMap<>();
        private boolean haveAllGames = false;
        private boolean haveAllUsers = false;

        public final void addGame(@Nonnull GameIdentifier id, @Nonnull Game game) {
            if (gameToIdMap.containsKey(game) || idToGameMap.containsKey(id)) {
                throw new IllegalStateException("already present");
            }
            gameToIdMap.put(game, id);
            idToGameMap.put(id, new FindGameResult(game, id.getScenario()));
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
        public final Optional<FindGameResult> findGame(@Nonnull GameIdentifier id) {
            Optional<FindGameResult> result = Optional.ofNullable(idToGameMap.get(id));
            if (result.isEmpty()) {
                result = findGameUncached(id);
                result.ifPresent(value -> cacheGame(id, value));
            }
            return result;
        }

        @Nonnull
        public final Iterable<Map.Entry<GameIdentifier, FindGameResult>> findAllGames() {
            if (!haveAllGames) {
                findAllGamesUncached().forEach(entry -> cacheGame(entry.getKey(), entry.getValue()));
                haveAllGames = true;
            }
            return Set.copyOf(idToGameMap.entrySet());
        }

        private void cacheGame(@Nonnull GameIdentifier id, @Nonnull FindGameResult findGameResult) {
            gameToIdMap.put(findGameResult.game(), id);
            idToGameMap.put(id, findGameResult);
        }

        @Nonnull
        public final Optional<UserGameAssociation> findCurrentUserGame(@Nonnull UUID id) {
            var game = idToUserGameAssociationMap.get(id);
            if (game != null) {
                return Optional.of(game);
            }
            final var result = findCurrentUserGameUncached(id);
            if (result.isPresent()) {
                game = result.get();
                userGameAssociationToIdMap.put(game, id);
                idToUserGameAssociationMap.put(id, game);
            }
            return result;
        }

        public final void addCurrentUserGame(@Nonnull UUID id, @Nonnull UserGameAssociation entry) {
            if (userGameAssociationToIdMap.containsKey(entry) || idToUserGameAssociationMap.containsKey(id)) {
                throw new IllegalStateException("already present");
            }
            userGameAssociationToIdMap.put(entry, id);
            idToUserGameAssociationMap.put(id, entry);
            addCurrentUserGameUncached(id, entry);
        }

        public final void updateCurrentUserGame(@Nonnull UserGameAssociation entry) {
            final var id = userGameAssociationToIdMap.get(entry);
            if (id == null) {
                throw new IllegalStateException("not present");
            }
            updateCurrentUserGameUncached(id, entry);
        }

        public final void addUser(@Nonnull UUID id, @Nonnull User user) {
            if (userToIdMap.containsKey(user) || idToUserMap.containsKey(id)) {
                throw new IllegalStateException("already present");
            }
            cacheUser(id, user);
            addUserUncached(id, user);
        }

        public final void updateUser(@Nonnull User user) {
            final var id = userToIdMap.get(user);
            if (id == null) {
                throw new IllegalStateException("not present");
            }
            updateUserUncached(id, user);
        }

        @Nonnull
        public final Optional<User> findUser(@Nonnull UUID id) {
            var user = idToUserMap.get(id);
            if (user != null) {
                return Optional.of(user);
            }
            final var result = findUserUncached(id);
            if (result.isPresent()) {
                user = result.get();
                cacheUser(id, user);
            }
            return result;
        }

        @Nonnull
        public final Optional<User> findUserByUsername(@Nonnull String username) {
            var user = usernameToUserMap.get(username);
            if (user != null) {
                return Optional.of(user);
            }
            final var id = findUserIdForUsernameUncached(username);
            return id.flatMap(this::findUser);
        }

        @Nonnull
        public final Iterable<User> findAllUsers() {
            if (!haveAllUsers) {
                findAllUsersUncached().forEach(entry -> {
                    final var id = entry.getKey();
                    final var user = entry.getValue();
                    cacheUser(id, user);
                });
                haveAllUsers = true;
            }
            return List.copyOf(idToUserMap.values());
        }

        private void cacheUser(UUID id, User user) {
            userToIdMap.put(user, id);
            idToUserMap.put(id, user);
            usernameToUserMap.put(user.getUsername(), user);
        }

        @Nonnull
        public MCRepository getRepository() {
            return MCRepository.this;
        }

        /**
         * {@inheritDoc}
         * <p>
         * Save operations performed through this context are not guaranteed to have been performed
         * until normal return from this method. That is, the implementation may cache save (write) operations.
         *
         * @throws RuntimeException If not all save operations could be performed.
         *                          This class is however not required to provide transaction semantics:
         *                          if it throws an exception, some saves might have been performed and some might not.
         */
        @Override
        @OverridingMethodsMustInvokeSuper
        public void close() throws RuntimeException {
            gameToIdMap.clear();
            idToGameMap.clear();
            haveAllGames = false;
            userGameAssociationToIdMap.clear();
            idToUserGameAssociationMap.clear();
            userToIdMap.clear();
            idToUserMap.clear();
            usernameToUserMap.clear();
            userToIdMap.clear();
            idToUserMap.clear();
            usernameToUserMap.clear();
            haveAllUsers = false;
        }

        protected abstract void addGameUncached(@Nonnull GameIdentifier id, @Nonnull Game game);

        protected abstract void updateGameUncached(@Nonnull GameIdentifier id, @Nonnull Game game);

        @Nonnull
        protected abstract Optional<FindGameResult> findGameUncached(@Nonnull GameIdentifier id);

        @Nonnull
        protected abstract Iterable<Map.Entry<GameIdentifier, FindGameResult>> findAllGamesUncached();

        protected abstract void addCurrentUserGameUncached(@Nonnull UUID id, @Nonnull UserGameAssociation entry);

        protected abstract void updateCurrentUserGameUncached(@Nonnull UUID id, @Nonnull UserGameAssociation entry);

        @Nonnull
        protected abstract Optional<UserGameAssociation> findCurrentUserGameUncached(@Nonnull UUID userId);

        @Nonnull
        protected abstract Optional<UUID> findUserIdForUsernameUncached(@Nonnull String username);

        @Nonnull
        protected abstract Optional<User> findUserUncached(@Nonnull UUID id);

        protected abstract void addUserUncached(@Nonnull UUID id, @Nonnull User user);

        protected abstract void updateUserUncached(@Nonnull UUID id, @Nonnull User user);

        @Nonnull
        protected abstract Iterable<Map.Entry<UUID, User>> findAllUsersUncached();
    }

}
