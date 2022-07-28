package uk.badamson.mc.repository;

import uk.badamson.mc.Game;
import uk.badamson.mc.GameIdentifier;
import uk.badamson.mc.User;
import uk.badamson.mc.UserGameAssociation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MCRepositoryTest {

    public static class Fake extends MCRepository {

        private final Map<GameIdentifier, Game> gameStore = new ConcurrentHashMap<>();
        private final Map<UUID, UserGameAssociation> currentUserGameStore = new ConcurrentHashMap<>();
        private final Map<UUID, User> userStore = new ConcurrentHashMap<>();

        @Nullable
        private static Game copy(@Nullable Game game) {
            return game == null ? null : new Game(game);
        }

        @Nullable
        private static User copy(@Nullable User user) {
            return user == null ? null : new User(user.getId(), user);
        }

        @Nonnull
        @Override
        public Context openContext() {
            return new FakeContext();
        }

        private class FakeContext extends Context {

            @Override
            public void addGameUncached(@Nonnull GameIdentifier id, @Nonnull Game game) {
                Objects.requireNonNull(id);
                gameStore.put(id, copy(game));
            }

            @Override
            public void updateGameUncached(@Nonnull GameIdentifier id, @Nonnull Game game) {
                Objects.requireNonNull(id);
                gameStore.put(id, copy(game));
            }

            @Nonnull
            @Override
            public Optional<Game> findGameUncached(@Nonnull GameIdentifier id) {
                return Optional.ofNullable(gameStore.get(id)).map(Fake::copy);
            }

            @Nonnull
            @Override
            public Iterable<Map.Entry<GameIdentifier, Game>> findAllGamesUncached() {
                return Set.copyOf(gameStore.entrySet());
            }

            @Override
            protected void addCurrentUserGameUncached(@Nonnull UUID userId, @Nonnull UserGameAssociation association) {
                Objects.requireNonNull(userId);
                Objects.requireNonNull(association);
                currentUserGameStore.put(userId, association);
            }

            @Override
            protected void updateCurrentUserGameUncached(@Nonnull UUID userId, @Nonnull UserGameAssociation association) {
                Objects.requireNonNull(userId);
                Objects.requireNonNull(association);
                currentUserGameStore.put(userId, association);
            }

            @Nonnull
            @Override
            protected Optional<UserGameAssociation> findCurrentUserGameUncached(@Nonnull UUID userId) {
                Objects.requireNonNull(userId);
                return Optional.ofNullable(currentUserGameStore.get(userId));
            }

            @Nonnull
            @Override
            protected Optional<User> findUserUncached(@Nonnull UUID id) {
                Objects.requireNonNull(id);
                return Optional.ofNullable(userStore.get(id)).map(Fake::copy);
            }

            @Nonnull
            @Override
            protected Optional<UUID> findUserIdForUsernameUncached(@Nonnull String username) {
                Objects.requireNonNull(username);
                return userStore.values().stream()
                        .filter(u -> u.getUsername().equals(username))
                        .map(User::getId)
                        .findAny();
            }

            @Nonnull
            @Override
            public Iterable<Map.Entry<UUID, User>> findAllUsersUncached() {
                return Set.copyOf(userStore.entrySet());
            }

            @Override
            public void addUserUncached(@Nonnull UUID id, @Nonnull User user) {
                Objects.requireNonNull(id);
                Objects.requireNonNull(user);
                userStore.put(id, user);
            }

            @Override
            public void updateUserUncached(@Nonnull UUID id, @Nonnull User user) {
                Objects.requireNonNull(id);
                Objects.requireNonNull(user);
                userStore.put(id, user);
            }

        }
    }
}
