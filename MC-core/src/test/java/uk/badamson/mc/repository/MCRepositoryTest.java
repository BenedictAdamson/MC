package uk.badamson.mc.repository;

import uk.badamson.mc.Game;
import uk.badamson.mc.GamePlayers;
import uk.badamson.mc.User;
import uk.badamson.mc.UserGameAssociation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class MCRepositoryTest {

    public static class Fake extends MCRepository {

        private final Map<Game.Identifier, Game> gameStore = new ConcurrentHashMap<>();
        private final Map<Game.Identifier, GamePlayers> gamePlayersStore = new ConcurrentHashMap<>();
        private final Map<UUID, UserGameAssociation> currentUserGameStore = new ConcurrentHashMap<>();
        private final Map<UUID, User> userStore = new ConcurrentHashMap<>();

        @Nullable
        private static Game copy(@Nullable Game game) {
            return game == null? null: new Game(game);
        }

        @Nullable
        private static GamePlayers copy(@Nullable GamePlayers gamePlayers) {
            return gamePlayers == null? null: new GamePlayers(gamePlayers);
        }

        @Nullable
        private static User copy(@Nullable User user) {
            return user == null? null: new User(user.getId(), user);
        }

        private class FakeContext extends Context {

            @Override
            public void saveGame(@Nonnull Game.Identifier id, @Nonnull Game game) {
                Objects.requireNonNull(id);
                gameStore.put(id, copy(game));
            }

            @Nonnull
            @Override
            public Optional<Game> findGame(@Nonnull Game.Identifier id) {
                return Optional.ofNullable(gameStore.get(id)).map(Fake::copy);
            }

            @Nonnull
            @Override
            public Stream<Game> findAllGames() {
                return List.copyOf(gameStore.values()).stream().map(Fake::copy);
            }

            @Override
            public void saveGamePlayers(@Nonnull Game.Identifier id, @Nonnull GamePlayers gamePlayers) {
                Objects.requireNonNull(id);
                Objects.requireNonNull(gamePlayers);
                gamePlayersStore.put(id, copy(gamePlayers));
            }

            @Nonnull
            @Override
            public Optional<GamePlayers> findGamePlayers(@Nonnull Game.Identifier id) {
                return Optional.ofNullable(gamePlayersStore.get(id)).map(Fake::copy);
            }

            @Override
            public void saveCurrentUserGame(@Nonnull UUID userId, @Nonnull UserGameAssociation association) {
                Objects.requireNonNull(userId);
                Objects.requireNonNull(association);
                currentUserGameStore.put(userId, association);
            }

            @Nonnull
            @Override
            public Optional<UserGameAssociation> findCurrentUserGame(@Nonnull UUID userId) {
                Objects.requireNonNull(userId);
                return Optional.ofNullable(currentUserGameStore.get(userId));
            }

            @Nonnull
            public Optional<User> findUser(@Nonnull UUID id) {
                Objects.requireNonNull(id);
                return Optional.ofNullable(userStore.get(id)).map(Fake::copy);
            }

            @Nonnull
            public Optional<User> findUserByUsername(@Nonnull String username) {
                Objects.requireNonNull(username);
                return userStore.values().stream().filter(u -> u.getUsername().equals(username)).findAny().map(Fake::copy);
            }

            @Nonnull
            public Stream<User> findAllUsers() {
                return userStore.values().stream();
            }

            public void saveUser(@Nonnull UUID id, @Nonnull User user) {
                Objects.requireNonNull(id);
                Objects.requireNonNull(user);
                userStore.put(id, user);
            }

            @Override
            public void close() {
                // Do nothing
            }

        }

        @Nonnull
        @Override
        public Context openContext() {
            return new FakeContext();
        }
    }
}
