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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>
 * A unique gameIdentifier for a {@linkplain Game game} (play) of the Mission
 * Command game.
 * </p>
 */
@Immutable
public final class GameIdentifier {

    private final UUID scenario;
    private final Instant created;

    /**
     * <p>
     * Create an object with given attribute values.
     * </p>
     *
     * @param scenario The unique gameIdentifier for the {@linkplain Scenario scenario}
     *                 that the game is an instance of.
     * @param created  The point in time when the game was created (set up).
     * @throws NullPointerException <ul>
     *                              <li>If {@code scenario} is null.</li>
     *                              <li>If {@code created} is null.</li>
     *                              </ul>
     */
    public GameIdentifier(@Nonnull final UUID scenario,
                          @Nonnull final Instant created) {
        this.scenario = Objects.requireNonNull(scenario, "scenario");
        this.created = Objects.requireNonNull(created, "created");

    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof final GameIdentifier other)) {
            return false;
        }
        /*
         * Two Identifiers are unlikely to have the same created value, so
         * check those values first.
         */
        return created.equals(other.created)
                && scenario.equals(other.scenario);
    }

    /**
     * <p>
     * The point in time when the game was created (set up).
     * </p>
     * <p>
     * This will usually be not long before playing of the game started. This
     * type uses the creation time as an gameIdentifier, rather than the game
     * start time, so it can represent games that have not yet been started,
     * but are in the process of being set up.
     * </p>
     */
    @Nonnull
    public Instant getCreated() {
        return created;
    }

    /**
     * <p>
     * The unique gameIdentifier for the {@linkplain Scenario scenario} that the
     * game is an instance of.
     * </p>
     */
    @Nonnull
    public UUID getScenario() {
        return scenario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(created, scenario);
    }

    @Override
    public String toString() {
        return scenario + "@" + created;
    }

}
