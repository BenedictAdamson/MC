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
import uk.badamson.mc.GameTest.GameIdentifierTest;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserGameAssociationTest {

    @Nested
    public class Constructor {

        @Nested
        public class Duplicates {

            @Test
            public void a() {
                test(USER_A, GAME_A);
            }

            @Test
            public void b() {
                test(USER_B, GAME_B);
            }

            private void test(final UUID user, final GameIdentifier game) {
                final var association1 = new UserGameAssociation(user, game);
                // Tough test: equivalent but not same attribute objects
                final var association2 = new UserGameAssociation(copy(user),
                        copy(game));

                assertInvariants(association1, association2);
                assertEquals(association1, association2);
            }
        }

        @Nested
        public class TwoDifferent {

            @Test
            public void all() {
                final var association1 = new UserGameAssociation(USER_A, GAME_A);
                final var association2 = new UserGameAssociation(USER_B, GAME_B);

                assertInvariants(association1, association2);
                assertNotEquals(association1, association2);
            }

            @Test
            public void game() {
                final var association1 = new UserGameAssociation(USER_A, GAME_A);
                final var association2 = new UserGameAssociation(USER_A, GAME_B);

                assertInvariants(association1, association2);
                assertNotEquals(association1, association2);
            }

            @Test
            public void user() {
                final var association1 = new UserGameAssociation(USER_A, GAME_A);
                final var association2 = new UserGameAssociation(USER_B, GAME_A);

                assertInvariants(association1, association2);
                assertNotEquals(association1, association2);
            }

        }

        @Test
        public void a() {
            constructor(USER_A, GAME_A);
        }

        @Test
        public void b() {
            constructor(USER_B, GAME_B);
        }

    }

    private static final UUID USER_A = UUID.randomUUID();

    private static final UUID USER_B = UUID.randomUUID();

    private static final GameIdentifier GAME_A = new GameIdentifier(
            UUID.randomUUID(), Instant.EPOCH);

    private static final GameIdentifier GAME_B = new GameIdentifier(
            UUID.randomUUID(), Instant.now());

    public static void assertInvariants(final UserGameAssociation association) {
        ObjectVerifier.assertInvariants(association);

        final var game = association.getGame();
        assertAll("Not null", () -> assertNotNull(association.getUser(), "user"),
                () -> assertNotNull(game, "game"));
        GameIdentifierTest.assertInvariants(game);
    }

    public static void assertInvariants(final UserGameAssociation association1,
                                        final UserGameAssociation association2) {
        ObjectVerifier.assertInvariants(association1, association2);
        EqualsSemanticsVerifier.assertValueSemantics(
                association1, association2, "user", UserGameAssociation::getUser
        );
        EqualsSemanticsVerifier.assertValueSemantics(
                association1, association2, "game", UserGameAssociation::getGame
        );
    }

    private static void constructor(final UUID user,
                                    final GameIdentifier game) {
        final var association = new UserGameAssociation(user, game);

        assertInvariants(association);
        assertAll("Attribute values",
                () -> assertSame(user, association.getUser(), "user"),
                () -> assertSame(game, association.getGame(), "game"));
    }

    private static GameIdentifier copy(final GameIdentifier id) {
        return new GameIdentifier(copy(id.getScenario()), copy(id.getCreated()));
    }

    private static Instant copy(final Instant t) {
        return Instant.ofEpochSecond(t.getEpochSecond(), t.getNano());
    }

    private static UUID copy(final UUID id) {
        return new UUID(id.getMostSignificantBits(),
                id.getLeastSignificantBits());
    }
}
