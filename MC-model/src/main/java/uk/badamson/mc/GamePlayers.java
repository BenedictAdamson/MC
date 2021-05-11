package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2020-21.
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.persistence.Id;

import org.springframework.data.annotation.PersistenceConstructor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * The set of {@linkplain User users} who played, or are playing, a particular
 * {@link Game game}.
 * </p>
 */
public class GamePlayers {

   private static boolean hasNoDuplicates(final Collection<UUID> values) {
      return values.size() == Set.copyOf(values).size();
   }

   /**
    * <p>
    * Whether a given map is a valid {@linkplain #getUsers() users} map.
    * </p>
    * <p>
    * A valid map conforms to all the following constraints.
    * </p>
    * <ul>
    * <li>non null</li>
    * <li>does not include a null key.</li>
    * <li>does not include any null values.</li>
    * <li>does not include any duplicate values.</li>
    * </ul>
    *
    * @param users
    *           The map to examine
    * @return whether valid
    */
   public static final boolean isValidUsers(final Map<UUID, UUID> users) {
      return users != null
               && users.entrySet().stream()
                        .allMatch(entry -> isValidUsersEntry(entry))
               && hasNoDuplicates(users.values());
   }

   private static boolean isValidUsersEntry(final Map.Entry<UUID, UUID> entry) {
      final var character = entry.getKey();
      final var user = entry.getValue();
      return character != null && user != null;
   }

   @Id
   @org.springframework.data.annotation.Id
   private final Game.Identifier game;

   private boolean recruiting;
   private final Map<UUID, UUID> users;

   /**
    * <p>
    * Construct a set of players with given values.
    * </p>
    *
    * <h2>Post Conditions</h2>
    * <ul>
    * <li>The {@linkplain #getGame() game} of this set of players is the given
    * {@code identifier}.</li>
    * <li>Whether this set of players {@linkplain #isRecruiting() is recruiting}
    * is the given {@code recruiting} flag.</li>
    * <li>The {@linkplain #getUsers() set of users} of this set of players is
    * {@linkplain Set#equals(Object) equal to}, but not the same, as the given
    * set of {@code users}.</li>
    * </ul>
    *
    * @param game
    *           The unique identifier for the game for which this is the set of
    *           players.
    * @param recruiting
    *           Whether the game is <i>recruiting</i> new players.
    * @param users
    *           The ({@linkplain User#getId() unique IDs} of the
    *           {@linkplain User users} who played, or are playing, the
    *           {@code game}, and the unique IDs of the characters they played.
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code game} is null.</li>
    *            <li>If {@code users} is null.</li>
    *            </ul>
    * @throws IllegalArgumentException
    *            If {@code users} is not a {@linkplain #isValidUsers(Map) valid
    *            users map}
    */
   @JsonCreator
   @PersistenceConstructor
   public GamePlayers(@Nonnull @JsonProperty("game") final Game.Identifier game,
            @JsonProperty("recruiting") final boolean recruiting,
            @Nonnull @JsonProperty("users") final Map<UUID, UUID> users) {
      this.game = Objects.requireNonNull(game, "game");
      this.recruiting = recruiting;
      this.users = new HashMap<>(Objects.requireNonNull(users, "users"));

      if (!isValidUsers(this.users)) {// copy then test to avoid race hazards
         throw new IllegalArgumentException("users");
      }
   }

   /**
    * <p>
    * Construct a copy of a set of players.
    * </p>
    *
    * <h2>Post Conditions</h2>
    * <ul>
    * <li>This set of players is {@linkplain #equals(Object) equivalent to} the
    * given set of players.</li>
    * <li>The {@linkplain #getGame() game} of this set of players is the same as
    * the identifier of the given set of players.</li>
    * <li>Whether this set of players {@linkplain #isRecruiting() is recruiting}
    * is equal to whether the given set of players is recruiting.</li>
    * <li>The {@linkplain #getUsers() set of users} of this set of players is
    * {@linkplain Set#equals(Object) equal to}, but not the same, as the set of
    * users of the given set of players.</li>
    * </ul>
    *
    * @param that
    *           The set of players to copy.
    * @throws NullPointerException
    *            If {@code that} is null
    */
   public GamePlayers(final GamePlayers that) {
      Objects.requireNonNull(that, "that");
      game = that.game;
      recruiting = that.recruiting;
      this.users = new HashMap<>(that.users);
   }

   /**
    * <p>
    * Add a {@linkplain User#getId() user ID} to the {@linkplain #getUsers() set
    * of users who played, or are playing}, the {@linkplain #getGame() game}.
    * </p>
    * <ul>
    * <li>The {@linkplain #getUsers() map of users} contains an entry that maps
    * the given character ID to the given user ID.</li>
    * <li>The method does not alter any other entries of the map of users.</li>
    * <li>The method adds at most one entry to the map of users.</li>
    * </ul>
    *
    * @param character
    *           The ID of the character that the user played.
    * @param user
    *           The unique ID of the user to add as a player.
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code character} is null.</li>
    *            <li>If {@code user} is null.</li>
    *            </ul>
    * @throws IllegalStateException
    *            <ul>
    *            <li>If the game is not {@linkplain #isRecruiting() recruiting}
    *            players.</li>
    *            <li>If the game already has the given user, but for a different
    *            character.</li>
    *            </ul>
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
    * <h2>Post Conditions</h2>
    * <ul>
    * <li>This game is not {@linkplain #isRecruiting() recruiting}.</li>
    * </ul>
    */
   public final void endRecruitment() {
      recruiting = false;
   }

   /**
    * <p>
    * Whether this object is <dfn>equivalent</dfn> to another object.
    * </p>
    * <ul>
    * <li>The {@link GamePlayers} class has <i>entity semantics</i>, with the
    * {@linkplain #getGame() identifier} serving as a unique identifier: this
    * object is equivalent to another object if, and only of, the other object
    * is also a {@link GamePlayers} and the two have
    * {@linkplain Game.Identifier#equals(Object) equivalent}
    * {@linkplain #getGame() identifiers}.</li>
    * </ul>
    *
    * @param that
    *           The object to compare with this.
    * @return Whether this object is equivalent to {@code that} object.
    */
   @Override
   public final boolean equals(final Object that) {
      if (this == that) {
         return true;
      }
      if (!(that instanceof GamePlayers)) {
         return false;
      }
      final var other = (GamePlayers) that;
      return game.equals(other.game);
   }

   /**
    * <p>
    * The {@linkplain Game#getIdentifier() unique identifier for the game} for
    * which this provides the set of players.
    * </p>
    * <ul>
    * <li>Not null.</li>
    * </ul>
    *
    * @return the identifier.
    */
   @Nonnull
   @JsonProperty("game")
   public final Game.Identifier getGame() {
      return game;
   }

   /**
    * <p>
    * The ({@linkplain User#getId() unique IDs} of the {@linkplain User users}
    * who played, or are playing, the {@linkplain #getGame() game}, and the IDs
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
   @JsonProperty("users")
   public final Map<UUID, UUID> getUsers() {
      return Collections.unmodifiableMap(users);
   }

   @Override
   public final int hashCode() {
      return game.hashCode();
   }

   /**
    * <p>
    * Whether the {@linkplain #getGame() game} is <i>recruiting</i> new players.
    * </p>
    * <p>
    * That is, whether users may be {@linkplain #addUser(UUID, UUID) added} to
    * this set of players.
    * </p>
    *
    * @return whether recruiting
    */
   @JsonProperty("recruiting")
   public final boolean isRecruiting() {
      return recruiting;
   }

}
