package uk.badamson.mc.service;
/*
 * © Copyright Benedict Adamson 2019-22.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.badamson.dbc.assertions.ObjectVerifier;
import uk.badamson.mc.*;
import uk.badamson.mc.Game.Identifier;
import uk.badamson.mc.repository.MCRepository;
import uk.badamson.mc.repository.MCRepositoryTest;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class GamePlayersServiceTest {
    private static final ZoneId UTC = ZoneId.from(ZoneOffset.UTC);
    /*
     * Guaranteed to produce a different time Instant each time used.
     */
    private static final Clock CLOCK_A = new Clock() {

        private long seconds = 0L;

        @Override
        public ZoneId getZone() {
            return UTC;
        }

        @Override
        public Instant instant() {
            return Instant.ofEpochSecond(seconds++);
        }

        @Override
        public Clock withZone(final ZoneId zone) {
            throw new UnsupportedOperationException();
        }

    };
    private static final Clock CLOCK_B = Clock.fixed(Instant.EPOCH, UTC);
    private static final UUID CHARACTER_ID_A = UUID.randomUUID();
    private static final UUID CHARACTER_ID_B = UUID.randomUUID();
    private static final UUID USER_ID_A = UUID.randomUUID();
    private static final UUID USER_ID_B = UUID.randomUUID();
    private static final String USERNAME_A = "John";
    private static final String USERNAME_B = "Paul";
    private static final String PASSWORD_A = "secret";
    private static final String PASSWORD_B = "password123";
    private static final Identifier GAME_IDENTIFIER_A = new Identifier(
            UUID.randomUUID(), Instant.EPOCH);
    private static final Identifier GAME_IDENTIFIER_B = new Identifier(
            UUID.randomUUID(), Instant.now());
    private ScenarioService scenarioServiceA;
    private ScenarioService scenarioServiceB;

    private MCRepository repositoryA;
    private MCRepository repositoryB;
    private GameService gameServiceA;
    private GameService gameServiceB;
    private UserService userServiceA;
    private UserService userServiceB;

    public static void assertInvariants(final GamePlayersService service) {
        ObjectVerifier.assertInvariants(service);// inherited
    }

    private static void assertIsDefault(final GamePlayers gamePlayers) {
        assertAll("Default",
                () -> assertTrue(gamePlayers.isRecruiting(), "recruiting"),
                () -> assertThat("users", gamePlayers.getUsers().entrySet(),
                        empty()));
    }

    private static void constructor(
            @Nonnull final GameService gameService,
            @Nonnull final UserService userService,
            @Nonnull final ScenarioService scenarioService,
            @Nonnull MCRepository repository) {
        final var service = new GamePlayersService(gameService, userService, scenarioService, repository);

        assertInvariants(service);
    }

    public static GamePlayers endRecruitment(
            final GamePlayersService service, final Identifier id)
            throws NoSuchElementException {
        final GamePlayers result;
        try {
            result = service.endRecruitment(id);
        } catch (final NoSuchElementException e) {
            assertInvariants(service);
            throw e;
        }
        assertInvariants(service);
        assertNotNull(result, "Returns a (non null) value.");
        assertAll(() -> assertEquals(id, result.getGame(), "game"),
                () -> assertFalse(result.isRecruiting(), "recruiting"));
        final Optional<GamePlayers> gamePlayersOptional = service.getGamePlayersAsGameManager(id);
        assertThat("gamePlayers", gamePlayersOptional.isPresent());
        assertFalse(gamePlayersOptional.get().isRecruiting(),
                "Subsequent retrieval of game players using an identifier equivalent to the given ID returns "
                        + "a value that is also not recruiting.");
        return result;
    }

    public static Optional<Identifier> getCurrentGameOfUser(
            final GamePlayersService service, final UUID user) {
        final var result = service.getCurrentGameOfUser(user);
        assertNotNull(result, "Returns a (non null) optional value.");
        assertInvariants(service);
        return result;
    }

    public static Optional<GamePlayers> getGamePlayersAsGameManager(
            final GamePlayersService service, final Identifier id) {

        final var result = service.getGamePlayersAsGameManager(id);

        assertInvariants(service);
        assertNotNull(result, "Returns a (non null) optional value.");// guard
        final var present = result.isPresent();
        if (present) {
            assertEquals(id, result.get().getGame(), "game");
        }

        return result;
    }

    public static Optional<GamePlayers> getGamePlayersAsNonGameManager(
            final GamePlayersService service, final Identifier id,
            final UUID user) {
        final var result = service.getGamePlayersAsNonGameManager(id, user);

        assertInvariants(service);
        assertNotNull(result, "Returns a (non null) optional value.");// guard
        if (result.isPresent()) {
            final var gamePlayers = result.get();
            assertEquals(id, gamePlayers.getGame(), "game");
            assertThat(
                    "The collection of players is either empty or contains the requesting user.",
                    Set.copyOf(gamePlayers.getUsers().values()),
                    either(empty()).or(is(Set.of(user))));
        }
        return result;
    }

    public static boolean mayUserJoinGame(final GamePlayersService service,
                                          final UUID user, final Identifier game) {
        final var result = service.mayUserJoinGame(user, game);
        assertInvariants(service);
        return result;
    }

    public static void userJoinsGame(final GamePlayersService service,
                                     final UUID user, final Identifier game)
            throws NoSuchElementException, UserAlreadyPlayingException,
            IllegalGameStateException, SecurityException {
        final var gamePlayers0 = service.getGamePlayersAsGameManager(game);
        final Map<UUID, UUID> users0 = gamePlayers0.map(GamePlayers::getUsers).orElseGet(Map::of);
        final var alreadyPlaying = users0.containsValue(user);

        try {
            service.userJoinsGame(user, game);
        } catch (UserAlreadyPlayingException | IllegalGameStateException
                 | SecurityException | NoSuchElementException e) {
            assertInvariants(service);
            throw e;
        }

        assertInvariants(service);
        final var currentGame = service.getCurrentGameOfUser(user);
        final Optional<GamePlayers> gamePlayersOptional = service.getGamePlayersAsGameManager(game);
        assertThat("gamePlayers", gamePlayersOptional.isPresent());
        final var gamePlayers = gamePlayersOptional.get();
        final var users = gamePlayers.getUsers();
        final var characterPlayed = users.entrySet().stream()
                .filter(entry -> user.equals(entry.getValue()))
                .map(Map.Entry::getKey).findAny();
        assertThat("The players of the game includes the user.",
                characterPlayed.isPresent());// guard
        assertAll(
                () -> assertThat("The current game of the user is the given game.", currentGame,
                        is(Optional.of(game))),
                () -> assertThat(
                        "The user is already a player, or the character played by the player did not previously have a player.",
                        alreadyPlaying || !users0.containsKey(characterPlayed.get()))
        );
    }

    private static UUID getAScenarioId(@Nonnull ScenarioService scenarioService) {
        final Optional<UUID> scenarioOptional = scenarioService
                .getScenarioIdentifiers().findAny();
        assert scenarioOptional.isPresent();
        return scenarioOptional.get();
    }

    @BeforeEach
    public void setUp() {
        repositoryA = new MCRepositoryTest.Fake();
        repositoryB = new MCRepositoryTest.Fake();

        scenarioServiceA = new ScenarioService(repositoryA);
        scenarioServiceB = new ScenarioService(repositoryB);
        gameServiceA = new GameService(CLOCK_A, scenarioServiceA, repositoryA);
        gameServiceB = new GameService(CLOCK_B, scenarioServiceB, repositoryB);
        userServiceA = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);
        userServiceB = new UserService(PasswordEncoderTest.FAKE, PASSWORD_B, repositoryB);
    }

    @Nested
    public class Constructor {

        @Test
        public void a() {
            constructor(gameServiceA, userServiceA, scenarioServiceA, repositoryA);
        }

        @Test
        public void b() {
            constructor(gameServiceB, userServiceB, scenarioServiceB, repositoryB);
        }
    }

    @Nested
    public class EndRecruitment {

        @Test
        public void notInRepository() {
            final var gameService = gameServiceA;
            final var scenarioService = scenarioServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var game = gameService.create(scenario);
            final var id = game.getIdentifier();
            final var service = new GamePlayersService(gameService, userServiceA, scenarioService, repositoryA);

            final var gamePlayers = endRecruitment(service, id);

            assertAll("Changed default",
                    () -> assertThat("recruiting", gamePlayers.isRecruiting(),
                            is(false)),
                    () -> assertThat("users", gamePlayers.getUsers().entrySet(),
                            empty()));
        }

        @Nested
        public class InRepository {

            @Test
            public void a() {
                test(true, Map.of());
            }

            @Test
            public void b() {
                test(false, Map.of(CHARACTER_ID_A, USER_ID_A));
            }

            @Test
            public void c() {
                test(true, Map.of(CHARACTER_ID_B, USER_ID_B));
            }

            private void test(final boolean recruiting0,
                              final Map<UUID, UUID> users) {
                final var repository = repositoryA;
                final var scenarioService = scenarioServiceA;
                final var gameService = gameServiceA;

                final var scenario = getAScenarioId(scenarioService);
                final var game = gameService.create(scenario);
                final var id = game.getIdentifier();
                final var gamePlayersInRepository = new GamePlayers(id, recruiting0,
                        users);
                try(final var context = repository.openContext()) {
                    context.saveGamePlayers(id, gamePlayersInRepository);
                }
                final var service = new GamePlayersService(gameService, userServiceA, scenarioService, repository);

                final var gamePlayers = endRecruitment(service, id);

                assertAll("Attributes of returned value",
                        () -> assertThat("game", gamePlayers.getGame(), is(id)),
                        () -> assertThat("recruiting", gamePlayers.isRecruiting(),
                                is(false)),
                        () -> assertThat("users", gamePlayers.getUsers(),
                                is(users)));
            }
        }

        @Nested
        public class NoSuchGame {
            @Test
            public void a() {
                test(GAME_IDENTIFIER_A);
            }

            @Test
            public void b() {
                test(GAME_IDENTIFIER_B);
            }

            @Test
            public void inRepository() {
                final var repository = repositoryA;
                final var scenarioService = scenarioServiceA;
                final var gameService = gameServiceA;

                // Tough test: a valid scenario ID
                final var scenarioId = getAScenarioId(scenarioService);
                final var id = new Identifier(scenarioId, Instant.EPOCH);
                final var gamePlayersInRepository = new GamePlayers(id, true,
                        Map.of());
                try(final var context = repository.openContext()) {
                    context.saveGamePlayers(id, gamePlayersInRepository);
                }
                final var service = new GamePlayersService(gameService, userServiceA, scenarioServiceA, repository);

                assertThrows(NoSuchElementException.class,
                        () -> endRecruitment(service, id));
            }

            private void test(final Identifier id) {
                final var service = new GamePlayersService(gameServiceA, userServiceA, scenarioServiceA, repositoryA);

                assertThrows(NoSuchElementException.class,
                        () -> endRecruitment(service, id));
            }

        }
    }

    @Nested
    public class GetCurrentGameOfUser {

        @Test
        public void unknownUser() {
            final var service = new GamePlayersService(gameServiceA, userServiceA, scenarioServiceA, repositoryA);

            final var result = getCurrentGameOfUser(service, USER_ID_A);

            assertTrue(result.isEmpty(), "empty");
        }

        @Test
        public void unknownUserWithRecord() {
            final var userId = USER_ID_A;
            final var repository = repositoryA;
            try(final var context = repository.openContext()) {
                context.saveCurrentUserGame(userId, new UserGameAssociation(userId, GAME_IDENTIFIER_A));
            }
            final var service = new GamePlayersService(gameServiceA, userServiceA, scenarioServiceA, repository);

            final var result = getCurrentGameOfUser(service, userId);

            assertTrue(result.isEmpty(), "empty");
        }
    }

    @Nested
    public class GetGamePlayersAsGameManager {

        @Test
        public void notInRepository() {
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var game = gameService.create(scenario).getIdentifier();
            final var service = new GamePlayersService(gameService, userServiceA, scenarioService, repositoryA);

            final var result = getGamePlayersAsGameManager(service, game);

            assertTrue(result.isPresent(), "present");// guard
            final var gamePlayers = result.get();
            assertIsDefault(gamePlayers);
        }

        @Nested
        public class InRepository {

            @Test
            public void a() {
                test(true, Map.of());
            }

            @Test
            public void b() {
                test(false, Map.of(CHARACTER_ID_A, USER_ID_A));
            }

            private void test(final boolean recruiting,
                              final Map<UUID, UUID> users) {
                final var repository = repositoryA;
                final var scenarioService = scenarioServiceA;
                final var gameService = gameServiceA;
                final var scenario = getAScenarioId(scenarioService);
                final var game = gameService.create(scenario);
                final var id = game.getIdentifier();
                final var gamePlayersInRepository = new GamePlayers(id, recruiting,
                        users);
                try(final var context = repository.openContext()) {
                    context.saveGamePlayers(id, gamePlayersInRepository);
                }
                final var service = new GamePlayersService(gameService, userServiceA, scenarioService, repository);

                final var result = getGamePlayersAsGameManager(service, id);

                assertTrue(result.isPresent(), "present");// guard
                final var gamePlayers = result.get();
                assertAll("Attributes of returned value",
                        () -> assertThat("game", gamePlayers.getGame(), is(id)),
                        () -> assertThat("recruiting", gamePlayers.isRecruiting(),
                                is(recruiting)),
                        () -> assertThat("users", gamePlayers.getUsers(),
                                is(users)));
            }
        }

        @Nested
        public class NoSuchGame {
            @Test
            public void a() {
                test(GAME_IDENTIFIER_A);
            }

            @Test
            public void b() {
                test(GAME_IDENTIFIER_B);
            }

            @Test
            public void inRepository() {
                final var repository = repositoryA;
                final var scenarioService = scenarioServiceA;
                final var gameService = gameServiceA;
                // Tough test: a valid scenario ID
                final var scenario = getAScenarioId(scenarioService);
                final var id = new Identifier(scenario, Instant.EPOCH);
                final var gamePlayersInRepository = new GamePlayers(id, true,
                        Map.of());
                try(final var context = repository.openContext()) {
                    context.saveGamePlayers(id, gamePlayersInRepository);
                }
                final var service = new GamePlayersService(gameService, userServiceA, scenarioService, repository);

                final var result = getGamePlayersAsGameManager(service, id);

                assertTrue(result.isEmpty(), "empty");
            }

            private void test(final Identifier id) {
                final var service = new GamePlayersService(gameServiceA, userServiceA, scenarioServiceA, repositoryA);

                final var result = getGamePlayersAsGameManager(service, id);

                assertTrue(result.isEmpty(), "empty");
            }

        }
    }

    @Nested
    public class GetGamePlayersAsNonGameManager {

        @Test
        public void notInRepository() {
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var game = gameService.create(scenario).getIdentifier();
            final var service = new GamePlayersService(gameService, userServiceA, scenarioService, repositoryA);

            final var result = getGamePlayersAsNonGameManager(service, game,
                    USER_ID_A);

            assertTrue(result.isPresent(), "present");// guard
            final var gamePlayers = result.get();
            assertIsDefault(gamePlayers);
        }

        @Nested
        public class InRepository {

            @Test
            public void noPlayers() {
                test(true, Map.of(), USER_ID_A, Map.of());
            }

            @Test
            public void notPlayer() {
                test(false, Map.of(CHARACTER_ID_A, USER_ID_A), USER_ID_B, Map.of());
            }

            @Test
            public void solePlayer() {
                test(false, Map.of(CHARACTER_ID_A, USER_ID_A), USER_ID_A,
                        Map.of(CHARACTER_ID_A, USER_ID_A));
            }

            private void test(final boolean recruiting,
                              final Map<UUID, UUID> users, final UUID user,
                              final Map<UUID, UUID> expectedUsers) {
                final var repository = repositoryA;
                final var scenarioService = scenarioServiceA;
                final var gameService = gameServiceA;
                final var scenario = getAScenarioId(scenarioService);
                final var game = gameService.create(scenario);
                final var id = game.getIdentifier();
                final var gamePlayersInRepository = new GamePlayers(id, recruiting,
                        users);
                try(final var context = repository.openContext()) {
                    context.saveGamePlayers(id, gamePlayersInRepository);
                }
                final var service = new GamePlayersService(gameService, userServiceA, scenarioService, repository);

                final var result = getGamePlayersAsNonGameManager(service, id,
                        user);

                assertTrue(result.isPresent(), "present");// guard
                final var gamePlayers = result.get();
                assertAll("Attributes of returned value",
                        () -> assertThat("game", gamePlayers.getGame(), is(id)),
                        () -> assertThat("recruiting", gamePlayers.isRecruiting(),
                                is(recruiting)),
                        () -> assertThat("users", gamePlayers.getUsers(),
                                is(expectedUsers)));
            }
        }

        @Nested
        public class NoSuchGame {
            @Test
            public void a() {
                test(GAME_IDENTIFIER_A, USER_ID_A);
            }

            @Test
            public void b() {
                test(GAME_IDENTIFIER_B, USER_ID_B);
            }

            @Test
            public void inRepository() {
                final var repository = repositoryA;
                final var scenarioService = scenarioServiceA;
                final var gameService = gameServiceA;
                // Tough test: a valid scenario ID
                final var scenario = getAScenarioId(scenarioService);
                final var id = new Identifier(scenario, Instant.EPOCH);
                final var gamePlayersInRepository = new GamePlayers(id, true,
                        Map.of());
                try(final var context = repository.openContext()) {
                    context.saveGamePlayers(id, gamePlayersInRepository);
                }
                final var service = new GamePlayersService(gameService, userServiceA, scenarioService, repository);

                final var result = getGamePlayersAsNonGameManager(service, id,
                        USER_ID_A);

                assertTrue(result.isEmpty(), "empty");
            }

            private void test(final Identifier id, final UUID user) {
                final var service = new GamePlayersService(gameServiceA, userServiceA, scenarioServiceA, repositoryA);

                final var result = getGamePlayersAsNonGameManager(service, id,
                        user);

                assertTrue(result.isEmpty(), "empty");
            }

        }
    }

    @Nested
    public class MayUserJoinGame {

        @Test
        public void gameNotRecruiting() {
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var game = gameService.create(scenario).getIdentifier();
            // Tough test: user exists and is permitted
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();

            final var service = new GamePlayersService(gameService, userService, scenarioService, repositoryA);
            service.endRecruitment(game);

            assertFalse(mayUserJoinGame(service, user, game));
        }

        @Test
        public void may() {
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var game = gameService.create(scenario).getIdentifier();
            // Tough test: user has minimum permission
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Set.of(Authority.ROLE_PLAYER), true, true, true,
                    true)).getId();

            final var service = new GamePlayersService(gameService, userService, scenarioService, repositoryA);

            assertTrue(mayUserJoinGame(service, user, game));
        }

        @Test
        public void unknownGame() {
            final var userService = userServiceA;
            // Tough test: user exists and is permitted
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();
            final var service = new GamePlayersService(gameServiceA, userService, scenarioServiceA, repositoryA);

            assertFalse(mayUserJoinGame(service, user, GAME_IDENTIFIER_A));
        }

        @Test
        public void unknownUser() {
            // Tough test: game exists and is recruiting
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var game = gameService.create(scenario).getIdentifier();
            final var service = new GamePlayersService(gameService, userServiceA, scenarioService, repositoryA);

            assertFalse(mayUserJoinGame(service, USER_ID_A, game));
        }

        @Test
        public void userAlreadyPlayingDifferentGame() {
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var gameA = gameService.create(scenario).getIdentifier();
            final var gameB = gameService.create(scenario).getIdentifier();
            assert !gameA.equals(gameB);
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();

            final var service = new GamePlayersService(gameService, userService, scenarioService, repositoryA);
            service.userJoinsGame(user, gameA);

            assertFalse(mayUserJoinGame(service, user, gameB));
        }

        @Test
        public void userAlreadyPlayingSameGame() {
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var game = gameService.create(scenario).getIdentifier();
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();

            final var service = new GamePlayersService(gameService, userService, scenarioService, repositoryA);
            service.userJoinsGame(user, game);

            assertTrue(mayUserJoinGame(service, user, game));
        }

        @Test
        public void userNotPermitted() {
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var userService = userServiceA;
            // Tough test: game exists and is recruiting
            final var scenario = getAScenarioId(scenarioService);
            final var game = gameService.create(scenario).getIdentifier();
            // Tough test: user has all the other permissions
            final Set<Authority> authorities = EnumSet
                    .complementOf(EnumSet.of(Authority.ROLE_PLAYER));
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, authorities, true, true, true, true)).getId();

            final var service = new GamePlayersService(gameService, userService, scenarioService, repositoryA);

            assertFalse(mayUserJoinGame(service, user, game));
        }
    }

    @Nested
    public class UserJoinsGame {

        @Test
        public void gameNotRecruiting() {
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var game = gameService.create(scenario).getIdentifier();
            // Tough test: user exists and is permitted
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();

            final var service = new GamePlayersService(gameService, userService, scenarioService, repositoryA);
            service.endRecruitment(game);

            assertThrows(IllegalGameStateException.class,
                    () -> userJoinsGame(service, user, game));
        }

        @Test
        public void unknownGame() {
            final var userService = userServiceA;
            // Tough test: user exists and is permitted
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true));
            final var service = new GamePlayersService(gameServiceA, userService, scenarioServiceA, repositoryA);

            assertThrows(NoSuchElementException.class, () -> userJoinsGame(service,
                    user.getId(), GAME_IDENTIFIER_A));
        }

        @Test
        public void unknownUser() {
            // Tough test: game exists and is recruiting
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var game = gameService.create(scenario).getIdentifier();
            final var service = new GamePlayersService(gameService, userServiceA, scenarioService, repositoryA);

            assertThrows(NoSuchElementException.class,
                    () -> userJoinsGame(service, USER_ID_A, game));
        }

        @Test
        public void userAlreadyPlayingDifferentGame() {
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var gameA = gameService.create(scenario).getIdentifier();
            final var gameB = gameService.create(scenario).getIdentifier();
            assert !gameA.equals(gameB);
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();

            final var service = new GamePlayersService(gameService, userService, scenarioService, repositoryA);
            service.userJoinsGame(user, gameA);

            assertThrows(UserAlreadyPlayingException.class,
                    () -> userJoinsGame(service, user, gameB));
        }

        @Test
        public void userAlreadyPlayingSameGame() {
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var game = gameService.create(scenario).getIdentifier();
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();

            final var service = new GamePlayersService(gameService, userService, scenarioService, repositoryA);
            service.userJoinsGame(user, game);

            userJoinsGame(service, user, game);

            final Optional<Identifier> currentGameOptional = service.getCurrentGameOfUser(user);
            assertThat("currentGame", currentGameOptional.isPresent());
            final var currentGame = currentGameOptional.get();
            final Optional<GamePlayers> gamePlayersOptional = service.getGamePlayersAsGameManager(game);
            assertThat("gamePlayers", gamePlayersOptional.isPresent());
            final var gamePlayers = gamePlayersOptional.get();
            assertThat("The current game of the user is (still) the given game.",
                    currentGame, is(game));
            assertThat("The players of the game (still) includes the user.",
                    gamePlayers.getUsers().values(), hasItem(user));
        }

        @Test
        public void userNotPermitted() {
            final var scenarioService = scenarioServiceA;
            final var gameService = gameServiceA;
            final var userService = userServiceA;
            // Tough test: game exists and is recruiting
            final var scenario = getAScenarioId(scenarioService);
            final var game = gameService.create(scenario).getIdentifier();
            // Tough test: user has all the other permissions
            final Set<Authority> authorities = EnumSet
                    .complementOf(EnumSet.of(Authority.ROLE_PLAYER));
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, authorities, true, true, true, true)).getId();

            final var service = new GamePlayersService(gameService, userService, scenarioService, repositoryA);

            assertThrows(SecurityException.class,
                    () -> userJoinsGame(service, user, game));
        }

        @Nested
        public class Valid {

            @Test
            public void last() {
                final var scenarioService = scenarioServiceA;
                final var gameService = gameServiceA;
                final var userService = userServiceA;
                final var scenarioId = getAScenarioId(scenarioService);
                final Optional<Scenario> scenarioOptional = scenarioService.getScenario(scenarioId);
                assertThat("scenario", scenarioOptional.isPresent());
                final var scenario = scenarioOptional.get();
                final var nCharacters = scenario.getCharacters().size();
                final var game = gameService.create(scenarioId).getIdentifier();
                final var gamePlayersService = new GamePlayersService(gameService, userService, scenarioService, repositoryA);
                for (var c = 0; c < nCharacters - 1; ++c) {
                    final var userName = "User " + c;
                    final var user = userService.add(new BasicUserDetails(userName,
                            PASSWORD_A, Set.of(Authority.ROLE_PLAYER), true, true,
                            true, true)).getId();
                    gamePlayersService.userJoinsGame(user, game);
                }
                final var user = userService.add(new BasicUserDetails(USERNAME_B,
                        PASSWORD_B, Set.of(Authority.ROLE_PLAYER), true, true,
                        true, true)).getId();

                test(scenarioService, gamePlayersService, user, game);

                final Optional<GamePlayers> gamePlayersOptional = gamePlayersService.getGamePlayersAsGameManager(game);
                assertThat("gamePlayers", gamePlayersOptional.isPresent());
                final var gamePlayers = gamePlayersOptional.get();
                assertThat("Game is not recruiting", gamePlayers.isRecruiting(),
                        is(false));
            }

            @Test
            public void one() {
                final var scenarioService = scenarioServiceA;
                final var gameService = gameServiceA;
                final var userService = userServiceA;
                final var scenarioId = getAScenarioId(scenarioService);
                final var game = gameService.create(scenarioId).getIdentifier();
                // Tough test: user has minimum permission
                final var user = userService.add(new BasicUserDetails(USERNAME_A,
                        PASSWORD_A, Set.of(Authority.ROLE_PLAYER), true, true,
                        true, true)).getId();

                final var gamePlayersService = new GamePlayersService(gameService, userService, scenarioService, repositoryA);

                test(scenarioService, gamePlayersService, user, game);
            }

            private void test(final ScenarioService scenarioService, final GamePlayersService gamePlayersService,
                              final UUID user, final Identifier game) {
                final Optional<Scenario> scenarioOptional = scenarioService.getScenario(game.getScenario());
                assertThat("scenario", scenarioOptional.isPresent());
                final var scenario = scenarioOptional.get();
                final var characterIds = scenario.getCharacters().stream()
                        .sequential().map(NamedUUID::getId).toList();
                final Optional<GamePlayers> gamePlayers0Optional = gamePlayersService
                        .getGamePlayersAsGameManager(game);
                assertThat("gamePlayers", gamePlayers0Optional.isPresent());
                final var gamePlayers0 = gamePlayers0Optional.get();
                final var playedCharacters0 = gamePlayers0.getUsers().keySet();
                final Optional<UUID> firstUnPlayedCharacter0Optional = characterIds.stream()
                        .sequential().filter(c -> !playedCharacters0.contains(c))
                        .findFirst();
                assertThat("firstUnPlayedCharacter", firstUnPlayedCharacter0Optional.isPresent());
                final var firstUnPlayedCharacter0 = firstUnPlayedCharacter0Optional.get();

                userJoinsGame(gamePlayersService, user, game);

                final Optional<Identifier> currentGameOptional = gamePlayersService
                        .getCurrentGameOfUser(user);
                assertThat("currentGame", currentGameOptional.isPresent());
                final var currentGame = currentGameOptional.get();
                final Optional<GamePlayers> gamePlayersOptional = gamePlayersService
                        .getGamePlayersAsGameManager(game);
                assertThat("gamePlayers", gamePlayersOptional.isPresent());
                final var gamePlayers = gamePlayersOptional.get();
                final var users = gamePlayers.getUsers();
                assertThat("The current game of the user becomes the given game.",
                        currentGame, is(game));
                assertAll("The played characters of the game",
                        () -> assertTrue(characterIds.containsAll(users.keySet()),
                                "is a subset of the characters of the scenario."),
                        () -> assertThat("has the user as a player",
                                users.values(), hasItem(user)),
                        () -> assertThat(
                                "has the user as the player of the first un-played character",
                                users, hasEntry(firstUnPlayedCharacter0, user)));
                assertThat(
                        "If the scenario can not allow any more players the game is no longer recruiting players.",
                        gamePlayers.isRecruiting(),
                        is(users.size() < characterIds.size()));
            }

            @Test
            public void two() {
                final var scenarioService = scenarioServiceA;
                final var gameService = gameServiceA;
                final var userService = userServiceA;
                final var scenarioId = getAScenarioId(scenarioService);
                final var game = gameService.create(scenarioId).getIdentifier();
                final var userA = userService.add(new BasicUserDetails(USERNAME_A,
                        PASSWORD_A, Set.of(Authority.ROLE_PLAYER), true, true,
                        true, true)).getId();
                final var userB = userService.add(new BasicUserDetails(USERNAME_B,
                        PASSWORD_B, Set.of(Authority.ROLE_PLAYER), true, true,
                        true, true)).getId();

                final var gamePlayersService = new GamePlayersService(gameService, userService, scenarioService, repositoryA);
                gamePlayersService.userJoinsGame(userA, game);

                test(scenarioService, gamePlayersService, userB, game);

                final Optional<GamePlayers> gamePlayersOptional = gamePlayersService.getGamePlayersAsGameManager(game);
                assertThat("gamePlayers", gamePlayersOptional.isPresent());
                final var gamePlayers = gamePlayersOptional.get();
                final var users = gamePlayers.getUsers();
                assertThat("Previous player is (still) a player", users.values(),
                        hasItem(userA));
            }

        }

    }
}
