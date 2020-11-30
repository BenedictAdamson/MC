package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2020.
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

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.persistence.Id;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A game (play) of a scenario of the Mission Command game.
 * </p>
 */
public class GamePlayers {

   /**
    * <p>
    * A unique identifier for a {@linkplain GamePlayers game} (play) of the Mission
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
       * @param scenario
       *           The unique identifier for the {@linkplain Scenario scenario}
       *           that the game is an instance of.
       * @param created
       *           The point in time when the game was created (set up).
       * @throws NullPointerException
       *            <ul>
       *            <li>If {@code scenario} is null.</li>
       *            <li>If {@code created} is null.</li>
       *            </ul>
       */
      @JsonCreator
      public Identifier(@Nonnull @JsonProperty("scenario") final UUID scenario,
               @Nonnull @JsonProperty("created") final Instant created) {
         this.scenario = Objects.requireNonNull(scenario, "scenario");
         this.created = Objects.requireNonNull(created, "created");

      }

      @Override
      public boolean equals(final Object obj) {
         if (this == obj) {
            return true;
         }
         if (!(obj instanceof Identifier)) {
            return false;
         }
         final var other = (Identifier) obj;
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
       *
       * <ul>
       * <li>Not null.</li>
       * </ul>
       *
       * @return the creation time
       */
      @Nonnull
      @JsonFormat(shape = JsonFormat.Shape.STRING)
      public Instant getCreated() {
         return created;
      }

      /**
       * <p>
       * The unique identifier for the {@linkplain Scenario scenario} that the
       * game is an instance of.
       * </p>
       * <ul>
       * <li>Not null.</li>
       * </ul>
       *
       * @return the scenario identifier
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
         return scenario.toString() + "@" + created.toString();
      }

   }// class

   @Id
   @org.springframework.data.annotation.Id
   private final Identifier identifier;

   private boolean recruiting;
   private final Set<UUID> players = new HashSet<>();

   /**
    * <p>
    * Construct a copy of a game.
    * </p>
    *
    * <h2>Post Conditions</h2>
    * <ul>
    * <li>This game is {@linkplain #equals(Object) equivalent to} the given
    * game.</li>
    * <li>The {@linkplain #getIdentifier() identifier} of this game is the same
    * as the identifier of the given game.</li>
    * <li>Whether this game {@linkplain #isRecruiting() is recruiting} is equal
    * to whether the given game is recruiting flag.</li>
    * <li>The {@linkplain #getPlayers() set of players} of this game is
    * {@linkplain Set#equals(Object) equal to} but not the same as the set of
    * players of the given game.</li>
    * </ul>
    *
    * @param that
    *           The game to copy.
    * @throws NullPointerException
    *            If {@code that} is null
    */
   public GamePlayers(final GamePlayers that) {
      Objects.requireNonNull(that, "that");
      identifier = that.identifier;
      recruiting = that.recruiting;
      players.addAll(that.players);
   }

   /**
    * <p>
    * Construct a game with given attribute values.
    * </p>
    *
    * <h2>Post Conditions</h2>
    * <ul>
    * <li>The {@linkplain #getIdentifier() identifier} of this game is the given
    * {@code identifier}.</li>
    * <li>Whether this game {@linkplain #isRecruiting() is recruiting} is the
    * given {@code recruiting} flag.</li>
    * <li>The {@linkplain #getPlayers() set of players} of this game is
    * {@linkplain Set#equals(Object) equal to} but not the same as the given set
    * of {@code players}.</li>
    * </ul>
    *
    * @param identifier
    *           The unique identifier for this game.
    * @param recruiting
    *           Whether this game is <i>recruiting</i> new players.
    * @param players
    *           The ({@linkplain User#getId() unique IDs} of the
    *           {@linkplain User users} who played, or are playing, this game.
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code identifier} is null.</li>
    *            <li>If {@code players} is null.</li>
    *            </ul>
    */
   @JsonCreator
   @PersistenceConstructor
   public GamePlayers(@Nonnull @JsonProperty("identifier") final Identifier identifier,
            @JsonProperty("recruiting") final boolean recruiting,
            @Nonnull @JsonProperty("players") final Set<UUID> players) {
      this.identifier = Objects.requireNonNull(identifier, "identifier");
      this.recruiting = recruiting;
      this.players.addAll(Objects.requireNonNull(players, "players"));
   }

   /**
    * <p>
    * Add a player ({@linkplain User#getId() unique ID} of a {@linkplain User
    * users}) to the {@linkplain #getPlayers() set of users who played, or are
    * playing}, this game.
    * </p>
    * <ul>
    * <li>Does not remove any players from the {@linkplain #getPlayers() set of
    * players} of this game.</li>
    * <li>The {@linkplain #getPlayers() set of players}
    * {@linkplain Set#contains(Object) contains} the given player.</li>
    * </ul>
    *
    * @param player
    *           The unique ID of the user to add as a player.
    * @throws NullPointerException
    *            If {@code player} is null.
    * @throws IllegalStateException
    *            If this game is not {@linkplain #isRecruiting() recruiting}
    *            players.
    */
   public final void addPlayer(@Nonnull final UUID player) {
      Objects.requireNonNull(player, "player");
      if (!recruiting) {
         throw new IllegalStateException("Game not recruiting players");
      }
      players.add(player);
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
    * {@linkplain #getIdentifier() identifier} serving as a unique identifier:
    * this object is equivalent to another object if, and only of, the other
    * object is also a {@link GamePlayers} and the two have
    * {@linkplain Identifier#equals(Object) equivalent}
    * {@linkplain #getIdentifier() identifiers}.</li>
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
      return identifier.equals(other.getIdentifier());
   }

   /**
    * <p>
    * The unique identifier for this game.
    * </p>
    * <ul>
    * <li>Not null.</li>
    * </ul>
    *
    * @return the identifier.
    */
   @NonNull
   @JsonProperty("identifier")
   public final Identifier getIdentifier() {
      return identifier;
   }

   /**
    * <p>
    * The ({@linkplain User#getId() unique IDs} of the {@linkplain User users}
    * who played, or are playing, this game.
    * </p>
    * <ul>
    * <li>Always returns a (non null) set of players.</li>
    * <li>The set of players does not include null.</li>
    * <li>The returned set of players in not modifiable.</li>
    * </ul>
    *
    * @return the players
    */
   @NonNull
   @JsonProperty("players")
   public final Set<UUID> getPlayers() {
      return Collections.unmodifiableSet(players);
   }

   @Override
   public final int hashCode() {
      return identifier.hashCode();
   }

   /**
    * <p>
    * Whether this game is <i>recruiting</i> new players.
    * </p>
    * <p>
    * That is, whether players may be {@linkplain #addPlayer(UUID) added} to
    * this game.
    * </p>
    *
    * @return whether recruiting
    */
   @JsonProperty("recruiting")
   public final boolean isRecruiting() {
      return recruiting;
   }

}
