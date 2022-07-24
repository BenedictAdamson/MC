package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2020-22.
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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.badamson.dbc.assertions.EqualsSemanticsVerifier;
import uk.badamson.dbc.assertions.ObjectVerifier;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Nested
    public class Construct2 {

        @Test
        public void differentIdentifiers() {
            final var identifierA = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
            final var identifierB = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
            final var gameA = new Game(identifierA, Game.RunState.RUNNING, true, USERS_A);
            final var gameB = new Game(identifierB, Game.RunState.RUNNING, true, USERS_A);

            assertInvariants(gameA, gameB);
            assertNotEquals(gameA, gameB);
        }

        @Test
        public void equalAttributes() {
            final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
            final var gameA = new Game(identifier, Game.RunState.RUNNING, true, USERS_A);
            final var gameB = new Game(identifier, Game.RunState.RUNNING, true, USERS_A);

            assertInvariants(gameA, gameB);
            assertEquals(gameA, gameB);
        }
    }

    @Nested
    public class ConstructCopy {

        @Test
        public void a() {
            final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
            test(identifier, Game.RunState.WAITING_TO_START, false, USERS_A);
        }

        @Test
        public void b() {
            final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
            test(identifier, Game.RunState.RUNNING, true, USERS_B);
        }

        private void test(final Game.Identifier identifier,
                          final Game.RunState runState,
                          final boolean recruiting,
                          final Map<UUID, UUID> users) {
            final var game0 = new Game(identifier, runState, recruiting, users);

            constructor(game0);
        }
    }

    @Nested
    public class ConstructWithAttributes {

        @Test
        public void a() {
            final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
            constructor(identifier, Game.RunState.WAITING_TO_START, false, USERS_A);
        }

        @Test
        public void b() {
            final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
            constructor(identifier, Game.RunState.RUNNING, true, USERS_B);
        }
    }

    @Nested
    public class AddUser {

        @Test
        public void a() {
            test(Map.of(), CHARACTER_ID_A, USER_ID_A);
        }

        @Test
        public void alreadyPlayer() {
            test(Map.of(CHARACTER_ID_A, USER_ID_A), CHARACTER_ID_A, USER_ID_A);
        }

        @Test
        public void b() {
            test(Map.of(), CHARACTER_ID_B, USER_ID_B);
        }

        @Test
        public void notEmpty() {
            test(Map.of(CHARACTER_ID_A, USER_ID_A), CHARACTER_ID_B, USER_ID_B);
        }

        @Test
        public void replace() {
            test(Map.of(CHARACTER_ID_A, USER_ID_A), CHARACTER_ID_A, USER_ID_B);
        }

        private void test(final Map<UUID, UUID> users0, final UUID character,
                          final UUID user) {
            final var players = new Game(new Game.Identifier(SCENARIO_ID_A, CREATED_A), Game.RunState.WAITING_TO_START, true, users0);

            addUser(players, character, user);
        }

    }

    @Nested
    public class EndRecruitment {

        @Test
        public void initiallyFalse() {
            test(false);
        }

        @Test
        public void initiallyTrue() {
            test(true);
        }

        private void test(final boolean recruitment0) {
            final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
            final var players = new Game(identifier, Game.RunState.WAITING_TO_START, recruitment0, USERS_A);

            endRecruitment(players);
        }
    }



    @Nested
    public class IsValidUsers {

        @Test
        public void empty() {
            final Map<UUID, UUID> users = Map.of();
            assertTrue(Game.isValidUsers(users));
        }

        @Test
        public void one() {
            final Map<UUID, UUID> users = Map.of(CHARACTER_ID_A, USER_ID_A);
            assertTrue(Game.isValidUsers(users));
        }

        @Test
        public void two() {
            final Map<UUID, UUID> users = Map.of(CHARACTER_ID_A, USER_ID_A,
                    CHARACTER_ID_B, USER_ID_B);
            assertTrue(Game.isValidUsers(users));
        }
    }

    public static class IdentifierTest {

        @Nested
        public class Construct2 {
            @Test
            public void differentCreated() {
                final var identifierA = new Game.Identifier(SCENARIO_ID_A,
                        CREATED_A);
                final var identifierB = new Game.Identifier(SCENARIO_ID_A,
                        CREATED_B);
                assertInvariants(identifierA, identifierB);
                assertNotEquals(identifierA, identifierB);
            }

            @Test
            public void differentScenarios() {
                final var identifierA = new Game.Identifier(SCENARIO_ID_A,
                        CREATED_A);
                final var identifierB = new Game.Identifier(SCENARIO_ID_B,
                        CREATED_A);
                assertInvariants(identifierA, identifierB);
                assertNotEquals(identifierA, identifierB);
            }

            @Test
            public void equal() {
                final var identifierA = new Game.Identifier(SCENARIO_ID_A,
                        CREATED_A);
                final var identifierB = new Game.Identifier(SCENARIO_ID_A,
                        CREATED_A);
                assertInvariants(identifierA, identifierB);
                assertEquals(identifierA, identifierB);
            }
        }

        @Nested
        public class Constructor {

            @Test
            public void a() {
                constructor(SCENARIO_ID_A, CREATED_A);
            }

            @Test
            public void b() {
                constructor(SCENARIO_ID_B, CREATED_B);
            }
        }

        public static void assertInvariants(final Game.Identifier identifier) {
            // inherited
            ObjectVerifier.assertInvariants(identifier);

            final var scenario = identifier.getScenario();
            final var created = identifier.getCreated();
            assertAll("Not null", () -> assertNotNull(scenario, "scenario"),
                    () -> assertNotNull(created, "created"));
        }

        public static void assertInvariants(final Game.Identifier identifierA,
                                            final Game.Identifier identifierB) {
            // inherited
            ObjectVerifier.assertInvariants(identifierA, identifierB);

            final var equals = identifierA.equals(identifierB);
            assertAll("Equality requires equal attributes",
                    () -> assertFalse(equals && !identifierA.getScenario()
                            .equals(identifierB.getScenario()), "scenario identifier"),
                    () -> assertFalse(equals && !identifierA.getCreated()
                            .equals(identifierB.getCreated()), "creation time"));
        }

        private static void constructor(final UUID scenario,
                                        final Instant created) {
            final var identifier = new Game.Identifier(scenario, created);

            IdentifierTest.assertInvariants(identifier);
            assertAll("Attributes have the given values",
                    () -> assertSame(scenario, identifier.getScenario(),
                            "scenario"),
                    () -> assertSame(created, identifier.getCreated(),
                            "created"));
        }
    }

    private static final UUID SCENARIO_ID_A = UUID.randomUUID();
    private static final UUID SCENARIO_ID_B = UUID.randomUUID();
    private static final Instant CREATED_A = Instant.EPOCH;
    private static final Instant CREATED_B = Instant.now();

    private static final UUID USER_ID_A = UUID.randomUUID();
    private static final UUID USER_ID_B = UUID.randomUUID();
    private static final UUID CHARACTER_ID_A = UUID.randomUUID();
    private static final UUID CHARACTER_ID_B = UUID.randomUUID();

    private static final Map<UUID, UUID> USERS_A = Map.of();
    private static final Map<UUID, UUID> USERS_B = Map.of(CHARACTER_ID_B,
            USER_ID_B);

    public static void assertInvariants(final Game game) {
        ObjectVerifier.assertInvariants(game);

        assertAll("Not null",
                () -> assertNotNull(game.getIdentifier(), "identifier"));
    }

    public static void assertInvariants(final Game gameA, final Game gameB) {
        ObjectVerifier.assertInvariants(gameA, gameB);
        EqualsSemanticsVerifier.assertEntitySemantics(gameA, gameB, Game::getIdentifier);
    }

    private static void constructor(@Nonnull final Game that) {
        final var copy = new Game(that);

        assertInvariants(copy);
        assertInvariants(that, copy);
        assertAll("Copied", () -> assertEquals(that, copy),
                () -> assertSame(that.getIdentifier(), copy.getIdentifier(),
                        "identifier"),
                () -> assertSame(that.getRunState(), copy.getRunState(),
                        "runState"),
                () -> assertEquals(that.isRecruiting(), copy.isRecruiting(),
                        "recruiting"),
                () -> assertEquals(that.getUsers(), copy.getUsers(),
                        "users"));

    }

    private static void constructor(@Nonnull final Game.Identifier identifier,
                                    @Nonnull final Game.RunState runState,
                                    final boolean recruiting,
                                    @Nonnull final Map<UUID, UUID> users) {
        final var game = new Game(identifier, runState, recruiting, users);

        assertInvariants(game);
        assertAll("Has the given attribute values",
                () -> assertSame(identifier, game.getIdentifier(), "identifier"),
                () -> assertSame(runState, game.getRunState(), "runState"));
    }

    private static void addUser(final Game game, final UUID character,
                               final UUID user) {
        final var users0 = Map.copyOf(game.getUsers());
        final var otherCharacters0 = users0.keySet().stream()
                .filter(c -> !character.equals(c)).collect(toUnmodifiableSet());

        game.addUser(character, user);

        assertInvariants(game);
        final var users = game.getUsers();
        assertAll(() -> assertThat(
                        "The map of users contains an entry that maps the given character name to the given user ID.",
                        users, hasEntry(character, user)),
                () -> assertTrue(
                        otherCharacters0.stream().allMatch(
                                c -> users.get(c).equals(users0.get(c))),
                        "The method does not alter any other entries of the map of users."),
                () -> assertTrue(users.size() <= users0.size() + 1,
                        "The method adds at most one entry to the map of users."));
    }

    private static void endRecruitment(final Game game) {
        game.endRecruitment();

        assertInvariants(game);
        assertFalse(game.isRecruiting(), "This game is not recruiting.");
    }
}
