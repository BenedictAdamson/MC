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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import uk.badamson.dbc.assertions.EqualsSemanticsVerifier;
import uk.badamson.dbc.assertions.ObjectVerifier;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * Unit tests for the class {@link Game}.
 * </p>
 */
public class GameTest {

    @Nested
    public class Construct2 {

        @Test
        public void differentIdentifiers() {
            final var identifierA = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
            final var identifierB = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
            final var gameA = new Game(identifierA, Game.RunState.RUNNING);
            final var gameB = new Game(identifierB, Game.RunState.RUNNING);

            assertInvariants(gameA, gameB);
            assertNotEquals(gameA, gameB);
        }

        @Test
        public void equalAttributes() {
            final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
            final var gameA = new Game(identifier, Game.RunState.RUNNING);
            final var gameB = new Game(identifier, Game.RunState.RUNNING);

            assertInvariants(gameA, gameB);
            assertEquals(gameA, gameB);
        }
    }// class

    @Nested
    public class ConstructCopy {

        @Test
        public void a() {
            final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
            test(identifier, Game.RunState.WAITING_TO_START);
        }

        @Test
        public void b() {
            final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
            test(identifier, Game.RunState.RUNNING);
        }

        private void test(final Game.Identifier identifier,
                          final Game.RunState runState) {
            final var game0 = new Game(identifier, runState);

            constructor(game0);
        }
    }// class

    @Nested
    public class ConstructWithAttributes {

        @Test
        public void a() {
            final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
            constructor(identifier, Game.RunState.WAITING_TO_START);
        }

        @Test
        public void b() {
            final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
            constructor(identifier, Game.RunState.RUNNING);
        }
    }// class

    /**
     * <p>
     * Unit tests for the {@link Game.Identifier} class.
     * </p>
     */
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
        }// class

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
        }// class

        @Nested
        public class JSON {

            @Test
            public void a() {
                test(SCENARIO_ID_A, CREATED_A);
            }

            @Test
            public void b() {
                test(SCENARIO_ID_B, CREATED_B);
            }

            private void test(final UUID scenario, final Instant created) {
                final var identifier = new Game.Identifier(scenario, created);
                final var deserialized = JsonTest
                        .serializeAndDeserialize(identifier);

                IdentifierTest.assertInvariants(deserialized);
                assertInvariants(identifier, deserialized);
                assertAll("Deserialized attributes",
                        () -> assertEquals(scenario, identifier.getScenario(),
                                "scenario"),
                        () -> assertEquals(created, identifier.getCreated(),
                                "created"));
            }
        }// class

        @Nested
        public class Serialize {

            @Test
            public void a() {
                test(SCENARIO_ID_A, CREATED_A);
            }

            @Test
            public void b() {
                test(SCENARIO_ID_B, CREATED_B);
            }

            private void test(final UUID scenario, final Instant created) {
                final var expectedSerializedCreated = created.toString();
                final var identifier = new Game.Identifier(scenario, created);

                final String serialized;
                try {
                    serialized = JsonTest.serialize(identifier);
                } catch (final JsonProcessingException e) {
                    throw new AssertionFailedError("can serialize as JSON", e);
                }

                assertThat("contains serialized 'created' value", serialized,
                        containsString(expectedSerializedCreated));
            }

        }// class

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
    }// class

    @Nested
    public class Json {

        @Test
        public void a() {
            final var identifier = new Game.Identifier(SCENARIO_ID_A, CREATED_A);
            test(identifier, Game.RunState.WAITING_TO_START);
        }

        @Test
        public void b() {
            final var identifier = new Game.Identifier(SCENARIO_ID_B, CREATED_B);
            test(identifier, Game.RunState.RUNNING);
        }

        private void test(final Game.Identifier identifier,
                          final Game.RunState runState) {
            final var game = new Game(identifier, runState);
            final var deserialized = JsonTest.serializeAndDeserialize(game);

            assertInvariants(deserialized);
            assertInvariants(game, deserialized);
            assertEquals(game, deserialized);
            assertAll("Deserialized attributes",
                    () -> assertEquals(identifier, deserialized.getIdentifier(),
                            "identifier"),
                    () -> assertEquals(runState, deserialized.getRunState(),
                            "runState"));
        }
    }// class

    private static final UUID SCENARIO_ID_A = UUID.randomUUID();
    private static final UUID SCENARIO_ID_B = UUID.randomUUID();
    private static final Instant CREATED_A = Instant.EPOCH;
    private static final Instant CREATED_B = Instant.now();

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
                        "runState"));

    }

    private static void constructor(@Nonnull final Game.Identifier identifier,
                                    @Nonnull final Game.RunState runState) {
        final var game = new Game(identifier, runState);

        assertInvariants(game);
        assertAll("Has the given attribute values",
                () -> assertSame(identifier, game.getIdentifier(), "identifier"),
                () -> assertSame(runState, game.getRunState(), "runState"));
    }
}
