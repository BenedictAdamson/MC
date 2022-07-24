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

public class GameServiceTest {
    private static final ZoneId UTC = ZoneId.from(ZoneOffset.UTC);
    private static final Clock CLOCK_A = Clock.systemUTC();
    private static final Clock CLOCK_B = Clock.fixed(Instant.EPOCH, UTC);
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
    private static final UUID SCENARIO_ID_A = UUID.randomUUID();
    private static final Identifier IDENTIFIER_A = new Identifier(
            SCENARIO_ID_A, Instant.now());
    private MCRepository repositoryA;
    private MCRepository repositoryB;
    private ScenarioService scenarioServiceA;
    private ScenarioService scenarioServiceB;
    private UserService userServiceA;
    private UserService userServiceB;

    public static void assertInvariants(final GameService service) {
        ObjectVerifier.assertInvariants(service);// inherited
        assertNotNull(service.getClock(), "clock");
    }

    private static void assertIsDefault(final Game game) {
        assertAll(
                () -> assertThat("runState", game.getRunState(), is(Game.RunState.WAITING_TO_START)),
                () -> assertThat("recruiting", game.isRecruiting()),
                () -> assertThat("users", game.getUsers().entrySet(), empty()));
    }

    private static BasicUserDetails createPlayerUserDetails(@Nonnull String userName) {
    return new BasicUserDetails(
            userName,
            PASSWORD_A,
            EnumSet.of(Authority.ROLE_PLAYER),
                            false, false, false, false
            );
    }

    private static void constructor(
            final Clock clock,
            final ScenarioService scenarioService,
            final UserService userService,
            final MCRepository repository) {
        final var service = new GameService(clock, scenarioService, userService, repository);

        assertInvariants(service);
        assertSame(clock, service.getClock(), "clock");
    }

    public static Game create(final GameService service, final UUID scenario)
            throws NoSuchElementException {
        final Game game;
        try {
            game = service.create(scenario);
        } catch (NoSuchElementException e) {
            assertInvariants(service);
            throw e;
        }
        assertInvariants(service);
        assertThat(game, notNullValue());
        return game;
    }

    public static Set<Instant> getCreationTimesOfGamesOfScenario(
            final GameService service, final UUID scenario)
            throws NoSuchElementException {
        final Set<Instant> times;
        try {
            times = service.getCreationTimesOfGamesOfScenario(scenario);
        } catch (final NoSuchElementException e) {
            assertInvariants(service);
            throw e;
        }

        assertInvariants(service);
        assertNotNull(times, "Always returns a (non null) set.");
        return times;
    }

    public static Iterable<Identifier> getGameIdentifiers(
            final GameService service) {
        final var games = service.getGameIdentifiers();

        assertThat(games, notNullValue());
        return games;
    }

    private static UUID getAScenarioId(@Nonnull ScenarioService scenarioService) {
        final Optional<UUID> scenarioOptional = scenarioService.getScenarioIdentifiers()
                .findAny();
        assertThat("scenario", scenarioOptional.isPresent());
        return scenarioOptional.get();
    }

    private static Game endRecruitment(
            final GameService service, final Identifier id)
            throws NoSuchElementException {
        final Game result;
        try {
            result = service.endRecruitment(id);
        } catch (final NoSuchElementException e) {
            assertInvariants(service);
            throw e;
        }
        assertInvariants(service);
        assertNotNull(result, "Returns a (non null) value.");
        assertAll(() -> assertThat("identifier", result.getIdentifier(), is(id)),
                () -> assertFalse(result.isRecruiting(), "recruiting"));
        final Optional<Game> gameOptional = service.getGameAsGameManager(id);
        assertThat(gameOptional.isPresent(), is(true));
        assertFalse(gameOptional.get().isRecruiting(),
                "Subsequent retrieval of game players using an identifier equivalent to the given ID returns "
                        + "a value that is also not recruiting.");
        return result;
    }

    public static Optional<Identifier> getCurrentGameOfUser(
            final GameService service, final UUID user) {
        final var result = service.getCurrentGameOfUser(user);
        assertNotNull(result, "Returns a (non null) optional value.");
        assertInvariants(service);
        return result;
    }

    public static Optional<Game> getGameAsGameManager(
            final GameService service, final Identifier id) {

        final var result = service.getGameAsGameManager(id);

        assertInvariants(service);
        assertNotNull(result, "Returns a (non null) optional value.");// guard

        return result;
    }

    private static Optional<Game> getGameAsNonGameManager(
            final GameService service, final Identifier id,
            final UUID user) {
        final var result = service.getGameAsNonGameManager(id, user);

        assertInvariants(service);
        assertNotNull(result, "Returns a (non null) optional value.");// guard
        if (result.isPresent()) {
            final var game = result.get();
            assertThat(
                    "The collection of players is either empty or contains the requesting user.",
                    Set.copyOf(game.getUsers().values()),
                    either(empty()).or(is(Set.of(user))));
        }
        return result;
    }

    public static boolean mayUserJoinGame(final GameService service,
                                          final UUID user, final Identifier game) {
        final var result = service.mayUserJoinGame(user, game);
        assertInvariants(service);
        return result;
    }

    public static void userJoinsGame(final GameService service,
                                     final UUID user, final Game.Identifier identifier)
            throws NoSuchElementException, UserAlreadyPlayingException,
            IllegalGameStateException, SecurityException {
        final var game0 = service.getGameAsGameManager(identifier);
        final Map<UUID, UUID> users0 = game0.map(Game::getUsers).orElseGet(Map::of);
        final var alreadyPlaying = users0.containsValue(user);

        try {
            service.userJoinsGame(user, identifier);
        } catch (UserAlreadyPlayingException | IllegalGameStateException
                 | SecurityException | NoSuchElementException e) {
            assertInvariants(service);
            throw e;
        }

        assertInvariants(service);
        final var currentGame = service.getCurrentGameOfUser(user);
        final Optional<Game> gameOptional = service.getGameAsGameManager(identifier);
        assertThat("game", gameOptional.isPresent());
        final var game = gameOptional.get();
        final var users = game.getUsers();
        final var characterPlayed = users.entrySet().stream()
                .filter(entry -> user.equals(entry.getValue()))
                .map(Map.Entry::getKey).findAny();
        assertThat("The players of the game includes the user.",
                characterPlayed.isPresent());// guard
        assertAll(
                () -> assertThat("The current game of the user is the given game.", currentGame,
                        is(Optional.of(identifier))),
                () -> assertThat(
                        "The user is already a player, or the character played by the player did not previously have a player.",
                        alreadyPlaying || !users0.containsKey(characterPlayed.get()))
        );
    }

    @BeforeEach
    public void setUp() {
        repositoryA = new MCRepositoryTest.Fake();
        repositoryB = new MCRepositoryTest.Fake();
        scenarioServiceA = new ScenarioService(repositoryA);
        scenarioServiceB = new ScenarioService(repositoryB);
        userServiceA = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);
        userServiceB = new UserService(PasswordEncoderTest.FAKE, PASSWORD_B, repositoryB);
    }

    @Nested
    public class Constructor {

        @Test
        public void a() {
            constructor(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);
        }

        @Test
        public void b() {
            constructor(CLOCK_B, scenarioServiceB, userServiceB, repositoryB);
        }
    }

    @Nested
    public class Create {

        @Test
        public void unknownScenario() {
            final var scenario = UUID.randomUUID();
            final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);

            assertThrows(NoSuchElementException.class,
                    () -> create(service, scenario));
        }

        @Nested
        public class KnownScenario {

            @Test
            public void a() {
                test(Instant.EPOCH);
            }

            @Test
            public void b() {
                test(Instant.now());
            }

            private void test(final Instant now) {
                final var clock = Clock.fixed(now, UTC);
                final var scenarioService = scenarioServiceA;
                final var scenario = getAScenarioId(scenarioService);
                final var service = new GameService(clock, scenarioService, userServiceA, repositoryA);
                final var truncatedNow = service.getNow();

                final var game = create(service, scenario);

                final var identifier = game.getIdentifier();
                assertThat(
                        "The returned game has the current time as the creation time of its identifier.",
                        identifier.getCreated(), is(truncatedNow));
                final var retrievedGame = service.getGameAsGameManager(identifier);
                assertNotNull(retrievedGame,
                        "can retrieve something using the ID (not null)");// guard
                assertTrue(retrievedGame.isPresent(),
                        "can retrieve something using the ID");// guard
                final var retrievedIdentifier = retrievedGame.get().getIdentifier();
                assertAll("can retrieve the created game using the ID",
                        () -> assertThat("scenario",
                                retrievedIdentifier.getScenario(), is(scenario)),
                        () -> assertThat("created",
                                retrievedIdentifier.getCreated(),
                                is(truncatedNow)));
            }
        }
    }

    @Nested
    public class GetCreationTimesOfGamesOfScenario {

        @Test
        public void none() {
            final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);
            final var scenario = getAScenarioId(scenarioServiceA);

            final var result = getCreationTimesOfGamesOfScenario(service,
                    scenario);

            assertThat(result, empty());
        }

        @Test
        public void one() {
            final var scenario = getAScenarioId(scenarioServiceA);
            final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);
            final var created = service.create(scenario).getIdentifier()
                    .getCreated();

            final var result = getCreationTimesOfGamesOfScenario(service,
                    scenario);

            assertThat(result, contains(created));
        }

        @Test
        public void unknownScenario() {
            final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);
            final var scenario = UUID.randomUUID();

            assertThrows(NoSuchElementException.class,
                    () -> getCreationTimesOfGamesOfScenario(service, scenario));
        }
    }

    @Nested
    public class GetGameIdentifiers {

        @Test
        public void none() {
            final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);

            final var result = getGameIdentifiers(service);

            assertThat("empty", result.iterator().hasNext(), is(false));
        }

        @Test
        public void one() {
            final var repository = repositoryA;
            final var id = IDENTIFIER_A;
            try(final var context = repository.openContext()) {
                context.addGame(id, new Game(id, Game.RunState.RUNNING, true, Map.of()));
            }
            final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repository);

            final var result = getGameIdentifiers(service);

            final var i = result.iterator();
            assertThat(i.hasNext(), is(true));
            assertThat(i.next(), is(id));
        }
    }


    @Nested
    public class EndRecruitment {

        @Test
        public void notInRepository() {
            final var scenarioService = scenarioServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);
            final var game = service.create(scenario);
            final var id = game.getIdentifier();

            final var gamePlayers = endRecruitment(service, id);

            assertAll("Changed default",
                    () -> assertThat("recruiting", gamePlayers.isRecruiting(),
                            is(false)),
                    () -> assertThat("users", gamePlayers.getUsers().entrySet(),
                            empty()));
        }

        @Test
        public void inRepository() {
            final var scenarioService = scenarioServiceA;

            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);
            final var game0 = service.create(scenario);
            final var id = game0.getIdentifier();

            final var game = endRecruitment(service, id);

            assertAll(
                    () -> assertThat("identifier", game.getIdentifier(), is(id)),
                    () -> assertThat("recruiting", game.isRecruiting(), is(false)));
        }

        @Test
        public void twice() {
            final var scenarioService = scenarioServiceA;

            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);
            final var id = service.create(scenario).getIdentifier();
            service.endRecruitment(id);

            final var game = endRecruitment(service, id);

            assertAll(
                    () -> assertThat("identifier", game.getIdentifier(), is(id)),
                    () -> assertThat("recruiting", game.isRecruiting(),  is(false)));
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

            private void test(final Identifier id) {
                final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);

                assertThrows(NoSuchElementException.class,
                        () -> endRecruitment(service, id));
            }

        }
    }

    @Nested
    public class GetCurrentGameOfUser {

        @Test
        public void unknownUser() {
            final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);

            final var result = getCurrentGameOfUser(service, USER_ID_A);

            assertTrue(result.isEmpty(), "empty");
        }

        @Test
        public void unknownUserWithRecord() {
            final var userId = USER_ID_A;
            final var repository = repositoryA;
            try(final var context = repository.openContext()) {
                context.addCurrentUserGame(userId, new UserGameAssociation(userId, GAME_IDENTIFIER_A));
            }
            final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repository);

            final var result = getCurrentGameOfUser(service, userId);

            assertTrue(result.isEmpty(), "empty");
        }
    }

    @Nested
    public class GetGameAsGameManager {

        @Test
        public void absent() {
            final var scenarioService = scenarioServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioService, userServiceA, repositoryA);
            final var id = service.create(scenario).getIdentifier();

            final var result = getGameAsGameManager(service, id);

            assertTrue(result.isPresent(), "present");// guard
            final var game = result.get();
            assertIsDefault(game);
        }

        @Nested
        public class Present {

            @Test
            public void recruitingNoPlayers() {
                final var repository = repositoryA;
                final var scenarioService = scenarioServiceA;
                final var scenario = getAScenarioId(scenarioService);
                final var service = new GameService(CLOCK_A, scenarioService, userServiceA, repository);
                final var gameId = service.create(scenario).getIdentifier();

                final var result = getGameAsGameManager(service, gameId);

                assertThat("present", result.isPresent());
                final var game = result.get();
                assertAll(
                        () -> assertThat("identifier", game.getIdentifier(), is(gameId)),
                        () -> assertThat("recruiting", game.isRecruiting(), is(true)),
                        () -> assertThat("users", game.getUsers(), anEmptyMap()));
            }

            @Test
            public void notRecruitingHasPlayers() {
                final var repository = repositoryA;
                final var scenarioService = scenarioServiceA;
                final var userService = userServiceA;
                final var scenario = getAScenarioId(scenarioService);
                final var userIdA = userService.add(createPlayerUserDetails(USERNAME_A)).getId();
                final var userIdB = userService.add(createPlayerUserDetails(USERNAME_B)).getId();
                final var service = new GameService(CLOCK_A, scenarioService, userService, repository);
                final var gameId = service.create(scenario).getIdentifier();
                service.userJoinsGame(userIdA, gameId);
                service.userJoinsGame(userIdB, gameId);
                service.endRecruitment(gameId);

                final var result = getGameAsGameManager(service, gameId);

                assertThat("present", result.isPresent());
                final var game = result.get();
                assertAll(
                        () -> assertThat("identifier", game.getIdentifier(), is(gameId)),
                        () -> assertThat("recruiting", game.isRecruiting(), is(false)),
                        () -> assertThat("users",
                                game.getUsers().values(), containsInAnyOrder(userIdA, userIdB)));
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

            private void test(final Identifier id) {
                final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);

                final var result = getGameAsGameManager(service, id);

                assertTrue(result.isEmpty(), "empty");
            }

        }
    }

    @Nested
    public class GetGameAsNonGameManager {

        @Test
        public void absent() {
            final var scenarioService = scenarioServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioService, userServiceA, repositoryA);
            final var game = service.create(scenario).getIdentifier();

            final var result = getGameAsNonGameManager(service, game,
                    USER_ID_A);

            assertTrue(result.isPresent(), "present");// guard
            final var gamePlayers = result.get();
            assertIsDefault(gamePlayers);
        }

        @Nested
        public class Present {

            @Test
            public void recruitingNoPlayers() {
                final var repository = repositoryA;
                final var scenarioService = scenarioServiceA;
                final var userService = userServiceA;
                final var scenario = getAScenarioId(scenarioService);
                final var service = new GameService(CLOCK_A, scenarioService, userService, repository);
                final var userId = userService.add(createPlayerUserDetails(USERNAME_A)).getId();
                final var gameId = service.create(scenario).getIdentifier();

                final var result = getGameAsNonGameManager(service, gameId, userId);

                assertTrue(result.isPresent(), "present");// guard
                final var game = result.get();
                assertAll(
                        () -> assertThat("identifier", game.getIdentifier(), is(gameId)),
                        () -> assertThat("recruiting", game.isRecruiting(), is(true)),
                        () -> assertThat("users", game.getUsers(), anEmptyMap()));
            }

            @Test
            public void recruitingNotPlayer() {
                final var repository = repositoryA;
                final var scenarioService = scenarioServiceA;
                final var userService = userServiceA;
                final var scenario = getAScenarioId(scenarioService);
                final var service = new GameService(CLOCK_A, scenarioService, userService, repository);
                final var userId = userService.add(createPlayerUserDetails(USERNAME_A)).getId();
                final var playerUserId = userService.add(createPlayerUserDetails(USERNAME_B)).getId();
                final var gameId = service.create(scenario).getIdentifier();
                service.userJoinsGame(playerUserId, gameId);

                final var result = getGameAsNonGameManager(service, gameId, userId);

                assertThat("present", result.isPresent());
                final var game = result.get();
                assertAll(
                        () -> assertThat("identifier", game.getIdentifier(), is(gameId)),
                        () -> assertThat("recruiting", game.isRecruiting(),is(true)),
                        () -> assertThat("users", game.getUsers(), anEmptyMap()));
            }

            @Test
            public void notRecruitingNotPlayer() {
                final var repository = repositoryA;
                final var scenarioService = scenarioServiceA;
                final var userService = userServiceA;
                final var scenario = getAScenarioId(scenarioService);
                final var service = new GameService(CLOCK_A, scenarioService, userService, repository);
                final var userId = userService.add(createPlayerUserDetails(USERNAME_A)).getId();
                final var playerUserId = userService.add(createPlayerUserDetails(USERNAME_B)).getId();
                final var gameId = service.create(scenario).getIdentifier();
                service.userJoinsGame(playerUserId, gameId);
                service.endRecruitment(gameId);

                final var result = getGameAsNonGameManager(service, gameId, userId);

                assertThat("present", result.isPresent());
                final var game = result.get();
                assertAll(
                        () -> assertThat("identifier", game.getIdentifier(), is(gameId)),
                        () -> assertThat("recruiting", game.isRecruiting(),is(false)),
                        () -> assertThat("users", game.getUsers(), anEmptyMap()));
            }

            @Test
            public void solePlayer() {
                final var repository = repositoryA;
                final var scenarioService = scenarioServiceA;
                final var userService = userServiceA;
                final var scenario = getAScenarioId(scenarioService);
                final var service = new GameService(CLOCK_A, scenarioService, userService, repository);
                final var gameId = service.create(scenario).getIdentifier();
                final var userId = userService.add(createPlayerUserDetails(USERNAME_A)).getId();
                service.userJoinsGame(userId, gameId);

                final var result = getGameAsNonGameManager(service, gameId, userId);

                assertTrue(result.isPresent(), "present");// guard
                final var game = result.get();
                assertAll(
                        () -> assertThat("identifier", game.getIdentifier(), is(gameId)),
                        () -> assertThat("recruiting", game.isRecruiting(), is(true)),
                        () -> assertThat("users", game.getUsers().values(), contains(userId)));
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

            private void test(final Identifier id, final UUID user) {
                final var service = new GameService(CLOCK_A, scenarioServiceA, userServiceA, repositoryA);

                final var result = getGameAsNonGameManager(service, id,
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
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioService, userService, repositoryA);
            final var game = service.create(scenario).getIdentifier();
            // Tough test: user exists and is permitted
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();

            service.endRecruitment(game);

            assertFalse(mayUserJoinGame(service, user, game));
        }

        @Test
        public void may() {
            final var scenarioService = scenarioServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioService, userService, repositoryA);
            final var game = service.create(scenario).getIdentifier();
            // Tough test: user has minimum permission
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Set.of(Authority.ROLE_PLAYER), true, true, true,
                    true)).getId();

            assertTrue(mayUserJoinGame(service, user, game));
        }

        @Test
        public void unknownGame() {
            final var userService = userServiceA;
            // Tough test: user exists and is permitted
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();
            final var service = new GameService(CLOCK_A, scenarioServiceA, userService, repositoryA);

            assertFalse(mayUserJoinGame(service, user, GAME_IDENTIFIER_A));
        }

        @Test
        public void unknownUser() {
            // Tough test: game exists and is recruiting
            final var scenarioService = scenarioServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioService, userServiceA, repositoryA);
            final var game = service.create(scenario).getIdentifier();

            assertFalse(mayUserJoinGame(service, USER_ID_A, game));
        }

        @Test
        public void userAlreadyPlayingDifferentGame() throws InterruptedException {
            final var scenarioService = scenarioServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(Clock.systemUTC(), scenarioService, userService, repositoryA);
            final var gameA = service.create(scenario).getIdentifier();
            Thread.sleep(10L);
            final var gameB = service.create(scenario).getIdentifier();
            assert !gameA.equals(gameB);
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();

            service.userJoinsGame(user, gameA);

            assertFalse(mayUserJoinGame(service, user, gameB));
        }

        @Test
        public void userAlreadyPlayingSameGame() {
            final var scenarioService = scenarioServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioService, userService, repositoryA);
            final var game = service.create(scenario).getIdentifier();
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();
            service.userJoinsGame(user, game);

            assertTrue(mayUserJoinGame(service, user, game));
        }

        @Test
        public void userNotPermitted() {
            final var scenarioService = scenarioServiceA;
            final var userService = userServiceA;
            // Tough test: game exists and is recruiting
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioService, userService, repositoryA);
            final var game = service.create(scenario).getIdentifier();
            // Tough test: user has all the other permissions
            final Set<Authority> authorities = EnumSet
                    .complementOf(EnumSet.of(Authority.ROLE_PLAYER));
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, authorities, true, true, true, true)).getId();

            assertFalse(mayUserJoinGame(service, user, game));
        }
    }

    @Nested
    public class UserJoinsGame {

        @Test
        public void gameNotRecruiting() {
            final var scenarioService = scenarioServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioService, userService, repositoryA);
            final var game = service.create(scenario).getIdentifier();
            // Tough test: user exists and is permitted
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();
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
            final var service = new GameService(CLOCK_A, scenarioServiceA, userService, repositoryA);

            assertThrows(NoSuchElementException.class, () -> userJoinsGame(service,
                    user.getId(), GAME_IDENTIFIER_A));
        }

        @Test
        public void unknownUser() {
            // Tough test: game exists and is recruiting
            final var scenarioService = scenarioServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioService, userServiceA, repositoryA);
            final var game = service.create(scenario).getIdentifier();

            assertThrows(NoSuchElementException.class,
                    () -> userJoinsGame(service, USER_ID_A, game));
        }

        @Test
        public void userAlreadyPlayingDifferentGame() throws InterruptedException {
            final var scenarioService = scenarioServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(Clock.systemUTC(), scenarioService, userService, repositoryA);
            final var gameA = service.create(scenario).getIdentifier();
            Thread.sleep(10L);
            final var gameB = service.create(scenario).getIdentifier();
            assert !gameA.equals(gameB);
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();
            service.userJoinsGame(user, gameA);

            assertThrows(UserAlreadyPlayingException.class,
                    () -> userJoinsGame(service, user, gameB));
        }

        @Test
        public void userAlreadyPlayingSameGame() {
            final var scenarioService = scenarioServiceA;
            final var userService = userServiceA;
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioService, userService, repositoryA);
            final var id = service.create(scenario).getIdentifier();
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, Authority.ALL, true, true, true, true)).getId();
            service.userJoinsGame(user, id);

            userJoinsGame(service, user, id);

            final Optional<Identifier> currentGameOptional = service.getCurrentGameOfUser(user);
            assertThat("currentGame", currentGameOptional.isPresent());
            final var currentGame = currentGameOptional.get();
            final Optional<Game> gameOptional = service.getGameAsGameManager(id);
            assertThat("gamePlayers", gameOptional.isPresent());
            final var game = gameOptional.get();
            assertThat("The current game of the user is (still) the given game.",
                    currentGame, is(id));
            assertThat("The players of the game (still) includes the user.",
                    game.getUsers().values(), hasItem(user));
        }

        @Test
        public void userNotPermitted() {
            final var scenarioService = scenarioServiceA;
            final var userService = userServiceA;
            // Tough test: game exists and is recruiting
            final var scenario = getAScenarioId(scenarioService);
            final var service = new GameService(CLOCK_A, scenarioService, userService, repositoryA);
            final var game = service.create(scenario).getIdentifier();
            // Tough test: user has all the other permissions
            final Set<Authority> authorities = EnumSet
                    .complementOf(EnumSet.of(Authority.ROLE_PLAYER));
            final var user = userService.add(new BasicUserDetails(USERNAME_A,
                    PASSWORD_A, authorities, true, true, true, true)).getId();

            assertThrows(SecurityException.class,
                    () -> userJoinsGame(service, user, game));
        }

        @Nested
        public class Valid {

            @Test
            public void last() {
                final var scenarioService = scenarioServiceA;
                final var userService = userServiceA;
                final var scenarioId = getAScenarioId(scenarioService);
                final Optional<Scenario> scenarioOptional = scenarioService.getScenario(scenarioId);
                assertThat("scenario", scenarioOptional.isPresent());
                final var scenario = scenarioOptional.get();
                final var nCharacters = scenario.getCharacters().size();
                final var service = new GameService(CLOCK_A, scenarioService, userService, repositoryA);
                final var id = service.create(scenarioId).getIdentifier();
                for (var c = 0; c < nCharacters - 1; ++c) {
                    final var userName = "User " + c;
                    final var user = userService.add(new BasicUserDetails(userName,
                            PASSWORD_A, Set.of(Authority.ROLE_PLAYER), true, true,
                            true, true)).getId();
                    service.userJoinsGame(user, id);
                }
                final var user = userService.add(new BasicUserDetails(USERNAME_B,
                        PASSWORD_B, Set.of(Authority.ROLE_PLAYER), true, true,
                        true, true)).getId();

                test(scenarioService, service, user, id);

                final Optional<Game> gameOptional = service.getGameAsGameManager(id);
                assertThat("gamePlayers", gameOptional.isPresent());
                final var game = gameOptional.get();
                assertThat("recruiting", game.isRecruiting(), is(false));
            }

            @Test
            public void one() {
                final var scenarioService = scenarioServiceA;
                final var userService = userServiceA;
                final var scenarioId = getAScenarioId(scenarioService);
                final var service = new GameService(CLOCK_A, scenarioService, userService, repositoryA);
                final var game = service.create(scenarioId).getIdentifier();
                // Tough test: user has minimum permission
                final var user = userService.add(new BasicUserDetails(USERNAME_A,
                        PASSWORD_A, Set.of(Authority.ROLE_PLAYER), true, true,
                        true, true)).getId();

                test(scenarioService, service, user, game);
            }

            private void test(final ScenarioService scenarioService, final GameService service,
                              final UUID user, final Identifier id) {
                final Optional<Scenario> scenarioOptional = scenarioService.getScenario(id.getScenario());
                assertThat("scenario", scenarioOptional.isPresent());
                final var scenario = scenarioOptional.get();
                final var characterIds = scenario.getCharacters().stream()
                        .sequential().map(NamedUUID::getId).toList();
                final Optional<Game> game0Optional = service.getGameAsGameManager(id);
                assertThat("game", game0Optional.isPresent());
                final var game0 = game0Optional.get();
                final var playedCharacters0 = game0.getUsers().keySet();
                final Optional<UUID> firstUnPlayedCharacter0Optional = characterIds.stream()
                        .sequential().filter(c -> !playedCharacters0.contains(c))
                        .findFirst();
                assertThat("firstUnPlayedCharacter", firstUnPlayedCharacter0Optional.isPresent());
                final var firstUnPlayedCharacter0 = firstUnPlayedCharacter0Optional.get();

                userJoinsGame(service, user, id);

                final Optional<Identifier> currentGameOptional = service
                        .getCurrentGameOfUser(user);
                assertThat("currentGame", currentGameOptional.isPresent());
                final var currentGame = currentGameOptional.get();
                final Optional<Game> gameOptional = service.getGameAsGameManager(id);
                assertThat("game", gameOptional.isPresent());
                final var game = gameOptional.get();
                final var users = game.getUsers();
                assertThat("The current game of the user becomes the given game.", currentGame, is(id));
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
                        game.isRecruiting(),
                        is(users.size() < characterIds.size()));
            }

            @Test
            public void two() {
                final var scenarioService = scenarioServiceA;
                final var userService = userServiceA;
                final var scenarioId = getAScenarioId(scenarioService);
                final var service = new GameService(CLOCK_A, scenarioService, userService, repositoryA);
                final var id = service.create(scenarioId).getIdentifier();
                final var userA = userService.add(new BasicUserDetails(USERNAME_A,
                        PASSWORD_A, Set.of(Authority.ROLE_PLAYER), true, true,
                        true, true)).getId();
                final var userB = userService.add(new BasicUserDetails(USERNAME_B,
                        PASSWORD_B, Set.of(Authority.ROLE_PLAYER), true, true,
                        true, true)).getId();
                service.userJoinsGame(userA, id);

                test(scenarioService, service, userB, id);

                final Optional<Game> gameOptional = service.getGameAsGameManager(id);
                assertThat("game", gameOptional.isPresent());
                assertThat("Previous player is (still) a player", gameOptional.get().getUsers().values(),
                        hasItem(userA));
            }

        }

    }
}
