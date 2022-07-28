package uk.badamson.mc;
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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.badamson.dbc.assertions.EqualsSemanticsVerifier;
import uk.badamson.dbc.assertions.ObjectVerifier;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.annotation.Nonnull;

public class IdentifiedValueTest {

    public static <IDENTIFIER, VALUE> void assertInvariants(
            @Nonnull IdentifiedValue<IDENTIFIER, VALUE> identified
    ) {
        ObjectVerifier.assertInvariants(identified);
        assertAll(
                () -> assertThat("identifier", identified.getIdentifier(), notNullValue()),
                () -> assertThat("value", identified.getValue(), notNullValue()));
    }

    public static <IDENTIFIER, VALUE> void assertInvariants(
            @Nonnull IdentifiedValue<IDENTIFIER, VALUE> identifiedA,
            @Nonnull IdentifiedValue<IDENTIFIER, VALUE> identifiedB
    ) {
        ObjectVerifier.assertInvariants(identifiedA, identifiedB);
        EqualsSemanticsVerifier.assertEntitySemantics(identifiedA, identifiedB, IdentifiedValue::getIdentifier);
    }

    private static <IDENTIFIER, VALUE> void constructor(
            @Nonnull IDENTIFIER identifier,
            @Nonnull VALUE value
    ) {
        final var identified = new IdentifiedValue<>(identifier, value);

        assertInvariants(identified);
        assertAll(
                () -> assertThat("identifier", identified.getIdentifier(), sameInstance(identifier)),
                () -> assertThat("value", identified.getValue(), sameInstance(value)));
    }

    @Nested
    public class Constructor {

        @Nested
        public class One {

            @Test
            public void a() {
                constructor("id", "value");
            }

            @Test
            public void b() {
                constructor(1, new byte[0]);
            }

        }

        @Nested
        public class Two {

            @Test
            public void equalIdentifiers() {
                final var identifier = "id";
                final var identifiedA = new IdentifiedValue<>(identifier, "value A");
                final var identifiedB = new IdentifiedValue<>(identifier, "value B");

                assertInvariants(identifiedA, identifiedB);
                assertThat(identifiedA, is(identifiedB));
            }

            @Test
            public void differentIdentifiers() {
                final var value = "value";
                final var identifiedA = new IdentifiedValue<>("id A", value);
                final var identifiedB = new IdentifiedValue<>("id B", "value");

                assertInvariants(identifiedA, identifiedB);
                assertThat(identifiedA, not(identifiedB));
            }

        }
    }
}
