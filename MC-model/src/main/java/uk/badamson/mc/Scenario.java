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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Id;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A game scenario of the Mission Command game.
 * </p>
 */
public class Scenario {

   @Id
   @org.springframework.data.annotation.Id
   private final UUID identifier;
   private final String title;
   private final String description;
   private final Map<Instant, Game> games = new HashMap<>();

   /**
    * <p>
    * Construct a game scenario with given attributes and aggregates.
    * </p>
    *
    * <h2>Post Conditions</h2>
    * <ul>
    * <li>The {@linkplain #getIdentifier() identifier} of this object is the
    * given {@code identifier}.</li>
    * <li>The {@linkplain #getTitle() title} of this object is the given
    * {@code title}.</li>
    * <li>The {@linkplain #getDescription() description} of this object is the
    * given {@code description}.</li>
    * <li>The {@linkplain #getGames() collection of games} of this object
    * {@linkplain Collection#containsAll(Collection) contains all} the given
    * {@code games}.</li>
    * </ul>
    *
    * @param identifier
    *           The identifier for this scenario.
    * @param title
    *           A short human readable identifier for this scenario.
    * @param description
    *           A human readable description for the scenario.
    * @param games
    *           Games that have been created for this scenario.
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code identifier} is null</li>
    *            <li>If {@code title} is null</li>
    *            <li>If {@code description} is null</li>
    *            <li>If {@code games} is null</li>
    *            <li>If {@code games} contains a null</li>
    *            </ul>
    * @throws IllegalArgumentException
    *            <ul>
    *            <li>If the {@code title} is not
    *            {@linkplain NamedUUID#isValidTitle(String) valid}.</li>
    *            <li>If the scenario ID of any of the {@code games} does not
    *            equal the given {@code identifier}.</li>
    *            </ul>
    */
   @JsonCreator
   public Scenario(@JsonProperty("identifier") @NonNull final UUID identifier,
            @NonNull @JsonProperty("title") final String title,
            @NonNull @JsonProperty("description") final String description,
            @NonNull @JsonProperty("games") final Collection<Game> games) {
      this.identifier = Objects.requireNonNull(identifier, "identifier");
      this.title = Objects.requireNonNull(title, "title");
      this.description = Objects.requireNonNull(description, "description");

      if (!NamedUUID.isValidTitle(title)) {
         throw new IllegalArgumentException("invalid title");
      }

      for (final var game : games) {
         Objects.requireNonNull(game, "game");
         if (!game.getIdentifier().getScenario().equals(identifier)) {
            throw new IllegalArgumentException(
                     "game is for a different scenario");
         }
         this.games.put(game.getIdentifier().getCreated(), game);
      }
   }

   /**
    * <p>
    * Whether this object is <dfn>equivalent</dfn> to another object.
    * </p>
    * <ul>
    * <li>The {@link Scenario} class has <i>entity semantics</i>, with the
    * {@linkplain #getIdentifier() identifier} serving as a unique identifier:
    * this object is equivalent to another object if, and only of, the other
    * object is also a {@link Scenario} and the two have
    * {@linkplain NamedUUID#equals(Object) equivalent}
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
      if (!(that instanceof Scenario)) {
         return false;
      }
      final var other = (Scenario) that;
      return identifier.equals(other.getIdentifier());
   }

   /**
    * <p>
    * A human readable description for this scenario.
    * </p>
    * <p>
    * Although different scenarios should have different descriptions,
    * descriptions are not guaranteed to be unique.
    * </p>
    * <ul>
    * <li>Not null</li>
    * </ul>
    *
    * @return the description.
    *
    * @see NamedUUID#getTitle()
    */
   @NonNull
   @JsonProperty("description")
   public String getDescription() {
      return description;
   }

   /**
    * <p>
    * Games that have been created for this scenario.
    * </p>
    * <ul>
    * <li>Always have a (non null) collection of games.</li>
    * <li>The collection of games does not have any null elements.</li>
    * <li>The scenario IDs of the games equal the {@linkplain NamedUUID#getId()
    * unique ID} of the {@linkplain #getIdentifier() identification information}
    * of this scenario.</li>
    * <li>The games have distinct creation times, so their
    * {@linkplain Game#getIdentifier() identifiers} are unique.</li>
    * </ul>
    *
    * @return the games
    */
   @JsonProperty("games")
   public final Collection<Game> getGames() {
      return Collections.unmodifiableMap(games).values();
   }

   /**
    * <p>
    * The unique identifier for this scenario.
    * </p>
    * <ul>
    * <li>Not null.</li>
    * </ul>
    *
    * @return the identifier.
    */
   @NonNull
   @JsonProperty("identifier")
   public final UUID getIdentifier() {
      return identifier;
   }

   /**
    * <p>
    * A unique ID with a human readable title, for this scenario.
    * </p>
    * <ul>
    * <li>Not null.</li>
    * <li>The {@linkplain NamedUUID#getId() ID} of the named ID
    * {@linkplain UUID#equals(Object) equals} the {@linkplain #getIdentifier()
    * identifier} of this scenario.</li>
    * <li>The {@linkplain NamedUUID#getTitle() title} of the named ID
    * {@linkplain String#equals(Object) equals} the {@linkplain #getTitle()
    * title} of this scenario.</li>
    * </ul>
    *
    * @return the identifier.
    */
   @NonNull
   @JsonIgnore
   public final NamedUUID getNamedUUID() {
      return new NamedUUID(identifier, title);
   }

   /**
    * <p>
    * A short human readable identifier for this scenario.
    * </p>
    * <p>
    * Although the title is an identifier, it is not guaranteed to be a unique
    * identifier.
    * </p>
    * <ul>
    * <li>{@linkplain NamedUUID#isValidTitle(String) is a valid title}</li>
    * </ul>
    *
    * @return the title.
    */
   @NonNull
   @JsonProperty("title")
   public String getTitle() {
      return title;
   }

   @Override
   public final int hashCode() {
      return identifier.hashCode();
   }

}
