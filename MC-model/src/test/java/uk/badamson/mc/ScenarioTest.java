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

import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.badamson.dbc.assertions.EqualsSemanticsVerifier;
import uk.badamson.dbc.assertions.ObjectVerifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class ScenarioTest {

    @Nested
    public class Construct2 {
        @Test
        public void differentIdentifiers() {
            // Tough test: all else equal
            final var scenarioA = new Scenario(ID_A, TITLE_A, DESCRIPTION_A,
                    CHARACTERS_A);
            final var scenarioB = new Scenario(ID_B, TITLE_A, DESCRIPTION_A,
                    CHARACTERS_A);
            assertInvariants(scenarioA, scenarioB);
            assertNotEquals(scenarioA, scenarioB);
        }

        @Test
        public void equalIdentifiers() {
            // Tough test: have different attributes and aggregates
            final var scenarioA = new Scenario(ID_A, TITLE_A, DESCRIPTION_A,
                    CHARACTERS_A);
            final var scenarioB = new Scenario(ID_A, TITLE_B, DESCRIPTION_B,
                    CHARACTERS_B);
            assertInvariants(scenarioA, scenarioB);
            assertEquals(scenarioA, scenarioB);
        }

    }

    @Nested
    public class Constructor {

        @Test
        public void a() {
            constructor(ID_A, TITLE_A, DESCRIPTION_A, CHARACTERS_A);
        }

        @Test
        public void b() {
            constructor(ID_B, TITLE_B, DESCRIPTION_B, CHARACTERS_B);
        }
    }

    private static final UUID ID_A = UUID.randomUUID();
    private static final UUID ID_B = UUID.randomUUID();
    private static final String TITLE_A = "Beach Assault";
    private static final String TITLE_B = "0123456789012345678901234567890123456789012345678901234567890123";// longest
    private static final String DESCRIPTION_A = "";// shortest
    private static final String DESCRIPTION_B = "Simple training scenario.";
    private static final NamedUUID CHARACTER_A = new NamedUUID(ID_A,
            "Lt. Winters");
    private static final NamedUUID CHARACTER_B = new NamedUUID(ID_B,
            "Sgt. Summer");
    private static final List<NamedUUID> CHARACTERS_A = List.of(CHARACTER_A);
    private static final List<NamedUUID> CHARACTERS_B = List.of(CHARACTER_A,
            CHARACTER_B);

    private static final Matcher<List<NamedUUID>> VALID_CHARACTERS_MATCHER = new CustomTypeSafeMatcher<>(
            "valid characters") {

        @Override
        protected boolean matchesSafely(final List<NamedUUID> item) {
            return Scenario.isValidCharacters(item);
        }
    };

    private static void assertCharactersInvariants(
            final List<NamedUUID> characters) {
        assertAll("characters", () -> assertNotNull(characters, "not null"),
                () -> assertThat(characters, VALID_CHARACTERS_MATCHER));
    }

    public static void assertInvariants(final Scenario scenario) {
        ObjectVerifier.assertInvariants(scenario);

        final var identifier = scenario.getIdentifier();
        final var title = scenario.getTitle();
        final var description = scenario.getDescription();
        final var namedUUID = scenario.getNamedUUID();
        final var characters = scenario.getCharacters();
        assertAll("Non null attributes and aggregates",
                () -> assertNotNull(identifier, "identifier"), // guard
                () -> assertNotNull(title, "title"), // guard
                () -> assertNotNull(description, "description"),
                () -> assertNotNull(namedUUID, "namedUUID"), // guard
                () -> assertNotNull(characters, "characters") // guard
        );

        assertAll(() -> NamedUUIDTest.assertInvariants(namedUUID),
                () -> assertTrue(NamedUUID.isValidTitle(title),
                        "title is valid"),
                () -> assertThat("namedUUID.title", title,
                        is(namedUUID.getTitle())),
                () -> assertCharactersInvariants(characters));
    }

    public static void assertInvariants(final Scenario scenarioA,
                                        final Scenario scenarioB) {
        ObjectVerifier.assertInvariants(scenarioA, scenarioB);
        EqualsSemanticsVerifier.assertEntitySemantics(scenarioA, scenarioB, Scenario::getIdentifier);
    }

    private static void constructor(@Nonnull final UUID identifier,
                                    @Nonnull final String title, @Nonnull final String description,
                                    @Nonnull final List<NamedUUID> characters) {
        final var scenario = new Scenario(identifier, title, description,
                characters);

        assertInvariants(scenario);
        assertAll(
                () -> assertSame(identifier, scenario.getIdentifier(),
                        "identifier"),
                () -> assertSame(title, scenario.getTitle(), "title"),
                () -> assertSame(description, scenario.getDescription(),
                        "description"),
                () -> assertEquals(characters, scenario.getCharacters(),
                        "characters equal"));

    }
}
