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
import java.util.*;

/**
 * <p>
 * A game (play) of a scenario of the Mission Command game.
 * </p>
 */
public class Game {

    /**
     * <p>
     * Whether a given map is a valid {@linkplain #getUsers() users} map.
     * </p>
     */
    public static boolean isValidUsers(final Map<UUID, UUID> users) {
        return users != null
                && users.entrySet().stream()
                .allMatch(Game::isValidUsersEntry)
                && hasNoDuplicates(users.values());
    }

    private static boolean isValidUsersEntry(final Map.Entry<UUID, UUID> entry) {
        final var character = entry.getKey();
        final var user = entry.getValue();
        return character != null && user != null;
    }

    private static boolean hasNoDuplicates(final Collection<UUID> values) {
        return values.size() == Set.copyOf(values).size();
    }

    /**
     * <p>
     * A unique identifier for a {@linkplain Game game} (play) of the Mission
     * Command game.
     * </p>
     */
    @Immutable
    public static final class Identifier {

        private final UUID scenario;
        private final Instant created;

        /**
         * <p>
         * Create an object with given attribute values.
         * </p>
         *
         * @param scenario The unique identifier for the {@linkplain Scenario scenario}
         *                 that the game is an instance of.
         * @param created  The point in time when the game was created (set up).
         * @throws NullPointerException <ul>
         *                                         <li>If {@code scenario} is null.</li>
         *                                         <li>If {@code created} is null.</li>
         *                                         </ul>
         */
        public Identifier(@Nonnull final UUID scenario,
                          @Nonnull final Instant created) {
            this.scenario = Objects.requireNonNull(scenario, "scenario");
            this.created = Objects.requireNonNull(created, "created");

        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof final Identifier other)) {
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
         * type uses the creation time as an identifier, rather than the game
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
         * The unique identifier for the {@linkplain Scenario scenario} that the
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

    public enum RunState {
        WAITING_TO_START, RUNNING, STOPPED
    }

    private final Identifier identifier;
    private RunState runState;
    private boolean recruiting;
    private final Map<UUID, UUID> users;

    /**
     * <p>
     * Construct a copy of a game.
     * </p>
     *
     * @throws NullPointerException If {@code that} is null
     */
    public Game(@Nonnull final Game that) {
        Objects.requireNonNull(that, "that");
        identifier = that.identifier;
        runState = that.runState;
        recruiting = that.recruiting;
        this.users = new HashMap<>(that.users);
    }

    /**
     * <p>
     * Construct a game with given attribute values.
     * </p>
     *
     * @throws NullPointerException <ul>
     *                                         <li>If {@code identifier} is null.</li>
     *                                         <li>If {@code runState} is null.</li>
     *                                         </ul>
     */
    public Game(@Nonnull final Identifier identifier,
                @Nonnull final RunState runState,
                final boolean recruiting,
                @Nonnull final Map<UUID, UUID> users) {
        this.identifier = Objects.requireNonNull(identifier, "identifier");
        this.runState = Objects.requireNonNull(runState, "runState");
        this.recruiting = recruiting;
        this.users = new HashMap<>(Objects.requireNonNull(users, "users"));

        if (!isValidUsers(this.users)) {// copy then test to avoid race hazards
            throw new IllegalArgumentException("users");
        }
    }

    /**
     * <p>
     * Whether this object is <dfn>equivalent</dfn> to another object.
     * </p>
     * <ul>
     * <li>The {@link Game} class has <i>entity semantics</i>, with the
     * {@linkplain #getIdentifier() identifier} serving as a unique identifier:
     * this object is equivalent to another object if, and only of, the other
     * object is also a {@link Game} and the two have
     * {@linkplain Identifier#equals(Object) equivalent}
     * {@linkplain #getIdentifier() identifiers}.</li>
     * </ul>
     */
    @Override
    public final boolean equals(final Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof final Game other)) {
            return false;
        }
        return identifier.equals(other.getIdentifier());
    }

    /**
     * <p>
     * The unique identifier for this game.
     * </p>
     */
    @Nonnull
    public final Identifier getIdentifier() {
        return identifier;
    }

    @Nonnull
    public RunState getRunState() {
        return runState;
    }

    /**
     * @see #endRecruitment()
     */
    public final boolean isRecruiting() {
        return recruiting;
    }

    /**
     * <p>
     * The ({@linkplain User#getId() unique IDs} of the {@linkplain User users}
     * who played, or are playing, the game, and the IDs
     * of the characters they played.
     * </p>
     * <ul>
     * <li>The map maps a character ID to the ID of the user who is playing (or
     * played, or will play) that character.</li>
     * </ul>
     * <ul>
     * <li>Always returns a {@linkplain #isValidUsers(Map) valid users map}.</li>
     * <li>The returned map of users is not modifiable.</li>
     * </ul>
     *
     * @return the users
     */
    @Nonnull
    public final Map<UUID, UUID> getUsers() {
        return Collections.unmodifiableMap(users);
    }

    @Override
    public final int hashCode() {
        return identifier.hashCode();
    }

    public void setRunState(@Nonnull final RunState runState) {
        this.runState = Objects.requireNonNull(runState, "runState");
    }

    /**
     * <p>
     * Add a {@linkplain User#getId() user ID} to the {@linkplain #getUsers() set
     * of users who played, or are playing}, the game.
     * </p>
     *
     * @param character The ID of the character that the user played.
     * @param user      The unique ID of the user to add as a player.
     * @throws NullPointerException  <ul>
     *                                          <li>If {@code character} is null.</li>
     *                                          <li>If {@code user} is null.</li>
     *                                          </ul>
     * @throws IllegalStateException <ul>
     *                                          <li>If the game is not {@linkplain #isRecruiting() recruiting}
     *                                          players.</li>
     *                                          <li>If the game already has the given user, but for a different
     *                                          character.</li>
     *                                          </ul>
     */
    public final void addUser(@Nonnull final UUID character,
                              @Nonnull final UUID user) {
        Objects.requireNonNull(character, "character");
        Objects.requireNonNull(user, "users");
        if (!recruiting) {
            throw new IllegalStateException("Game not recruiting players");
        }
        if (!user.equals(users.get(character)) && users.containsValue(user)) {
            throw new IllegalArgumentException(
                    "User already present with a different character");
        }

        users.put(character, user);
    }

    /**
     * <p>
     * Indicate that this game is not {@linkplain #isRecruiting() recruiting}
     * players (any longer).
     * </p>
     * <p>
     * This mutator is idempotent: the mutator does not have the precondition
     * that it is recruiting.
     * </p>
     *
     * @see #isRecruiting()
     */
    public final void endRecruitment() {
        recruiting = false;
    }

}
