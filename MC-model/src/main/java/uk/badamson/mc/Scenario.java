package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2021-22.
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

/**
 * <p>
 * A game scenario of the Mission Command game.
 * </p>
 */
public class Scenario {

    private static boolean hasDuplicateIds(final List<NamedUUID> characters) {
        final var ids = characters.stream().map(NamedUUID::getId)
                .collect(toUnmodifiableSet());
        return ids.size() != Set.copyOf(ids).size();
    }

    /**
     * <p>
     * Whether a given list of named IDs is a valid list of persons in a scenario
     * that players can play.
     * </p>
     * A valid list meets all these constraints:
     * <ul>
     * <li>Non null.</li>
     * <li>not {@linkplain List#isEmpty() empty}.</li>
     * <li>has no null elements.</li>
     * <li>has no elements with duplicate {@linkplain NamedUUID#getId()
     * IDs}.</li>
     * </ul>
     */
    public static boolean isValidCharacters(
            final List<NamedUUID> characters) {
        return characters != null && !characters.isEmpty()
                && !hasDuplicateIds(characters);
    }

    private final String title;
    private final String description;
    private final List<NamedUUID> characters;

    public Scenario(@Nonnull final String title,
                    @Nonnull final String description,
                    @Nonnull final List<NamedUUID> characters) {
        this.title = Objects.requireNonNull(title, "title");
        this.description = Objects.requireNonNull(description, "description");
        // Can not use List.copyOf() because does not copy unmodifiable lists
        this.characters = List.copyOf(Objects.requireNonNull(characters, "characters"));

        if (!NamedUUID.isValidTitle(title)) {
            throw new IllegalArgumentException("invalid title");
        }
        if (!isValidCharacters(this.characters)) {// copy then check to avoid race
            // hazards
            throw new IllegalArgumentException("invalid characters");
        }
    }

    /**
     * <p>
     * The names and IDs of the persons in this scenario that
     * players can play.
     * </p>
     * <ul>
     * <li>The list of characters is in descending order of selection priority:
     * with all else equal, players should be allocated characters near the start
     * of the list.</li>
     * </ul>
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "authorities is unmodifiable")
    @Nonnull
    public final List<NamedUUID> getCharacters() {
        return characters;
    }

    /**
     * <p>
     * A human readable description for this scenario.
     * </p>
     * <p>
     * Although different scenarios should have different descriptions,
     * descriptions are not guaranteed to be unique.
     * </p>
     *
     * @see NamedUUID#getTitle()
     */
    @Nonnull
    public final String getDescription() {
        return description;
    }

    /**
     * <p>
     * A short human readable identifier for this scenario.
     * </p>
     * <p>
     * Although the title is an identifier, it is not guaranteed to be a unique
     * identifier.
     * </p>
     */
    @Nonnull
    public final String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scenario scenario = (Scenario) o;
        return title.equals(scenario.title) &&
                description.equals(scenario.description) &&
                characters.equals(scenario.characters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, characters);
    }
}
