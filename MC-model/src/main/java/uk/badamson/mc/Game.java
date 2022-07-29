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
import java.time.Instant;
import java.util.*;

/**
 * <p>
 * A game (play) of a scenario of the Mission Command game.
 * </p>
 */
public class Game {

    private final UUID scenario;
    private final Instant created;
    private final Map<UUID, UUID> users;
    private RunState runState;
    private boolean recruiting;

    /**
     * <p>
     * Construct a copy of a game.
     * </p>
     *
     * @throws NullPointerException If {@code that} is null
     */
    public Game(@Nonnull final Game that) {
        Objects.requireNonNull(that, "that");
        scenario = that.scenario;
        created = that.created;
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
     *                              <li>If {@code gameIdentifier} is null.</li>
     *                              <li>If {@code runState} is null.</li>
     *                              </ul>
     */
    public Game(@Nonnull final UUID scenario,
                @Nonnull final Instant created,
                @Nonnull final RunState runState,
                final boolean recruiting,
                @Nonnull final Map<UUID, UUID> users) {
        this.scenario = Objects.requireNonNull(scenario, "scenario");
        this.created = Objects.requireNonNull(created, "created");
        this.runState = Objects.requireNonNull(runState, "runState");
        this.recruiting = recruiting;
        this.users = new HashMap<>(Objects.requireNonNull(users, "users"));

        if (!isValidUsers(this.users)) {// copy then test to avoid race hazards
            throw new IllegalArgumentException("users");
        }
    }

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

    @Nonnull
    public RunState getRunState() {
        return runState;
    }

    public void setRunState(@Nonnull final RunState runState) {
        this.runState = Objects.requireNonNull(runState, "runState");
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

    /**
     * <p>
     * Add a {@linkplain User#getId() user ID} to the {@linkplain #getUsers() set
     * of users who played, or are playing}, the game.
     * </p>
     *
     * @param character The ID of the character that the user played.
     * @param user      The unique ID of the user to add as a player.
     * @throws NullPointerException  <ul>
     *                               <li>If {@code character} is null.</li>
     *                               <li>If {@code user} is null.</li>
     *                               </ul>
     * @throws IllegalStateException <ul>
     *                               <li>If the game is not {@linkplain #isRecruiting() recruiting}
     *                               players.</li>
     *                               <li>If the game already has the given user, but for a different
     *                               character.</li>
     *                               </ul>
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

    public enum RunState {
        WAITING_TO_START, RUNNING, STOPPED
    }

}
