package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2021.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

   /**
    * <p>
    * Whether a given list of strings is a valid list of names of the persons in
    * a scenario that {@linkplain GamePlayers players} can play.
    * </p>
    * A valid list meets all these constraints:
    * <ul>
    * <li>Non null.</li>
    * <li>not {@linkplain List#isEmpty() empty}.</li>
    * <li>has no null elements.</li>
    * <li>has no {@linkplain String#isBlank() blank} elements.</li>
    * <li>has no duplicate elements.</li>
    * </ul>
    *
    * @return whether valid.
    */
   public static final boolean isValidCharacters(
            final List<String> characters) {
      return characters != null && !characters.isEmpty()
               && characters.stream().map(c -> c != null && !c.isBlank())
                        .allMatch(ok -> ok)
               && characters.size() == Set.copyOf(characters).size();
   }

   @Id
   @org.springframework.data.annotation.Id
   private final UUID identifier;
   private final String title;
   private final String description;
   private final List<String> characters;

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
    * <li>The {@linkplain #getCharacters() characters} of this object is
    * {@linkplain List#equals(Object) equal to} but not the same as the given
    * {@code characters}.</li>
    * </ul>
    *
    * @param identifier
    *           The identifier for this scenario.
    * @param title
    *           A short human readable identifier for this scenario.
    * @param description
    *           A human readable description for the scenario.
    * @param characters
    *           The names of the persons in this scenario that
    *           {@linkplain GamePlayers players} can play.
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code identifier} is null</li>
    *            <li>If {@code title} is null</li>
    *            <li>If {@code description} is null</li>
    *            <li>If {@code description} is characters</li>
    *            </ul>
    * @throws IllegalArgumentException
    *            <ul>
    *            <li>If the {@code title} is not
    *            {@linkplain NamedUUID#isValidTitle(String) valid}.</li>
    *            <li>If the {@code characters} are not a
    *            {@linkplain #isValidCharacters(List) valid list of
    *            characters}.</li>
    *            </ul>
    */
   @JsonCreator
   public Scenario(@JsonProperty("identifier") @NonNull final UUID identifier,
            @NonNull @JsonProperty("title") final String title,
            @NonNull @JsonProperty("description") final String description,
            @NonNull @JsonProperty("characters") final List<String> characters) {
      this.identifier = Objects.requireNonNull(identifier, "identifier");
      this.title = Objects.requireNonNull(title, "title");
      this.description = Objects.requireNonNull(description, "description");
      // Can not use List.copyOf() because does not copy unmodifiable lists
      this.characters = Collections.unmodifiableList(new ArrayList<>(
               Objects.requireNonNull(characters, "characters")));

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
    * Whether this object is <dfn>equivalent</dfn> to another object.
    * </p>
    * <ul>
    * <li>The {@link Scenario} class has <i>entity semantics</i>, with the
    * {@linkplain #getIdentifier() identifier} serving as a unique identifier:
    * this object is equivalent to another object if, and only of, the other
    * object is also a {@link Scenario} and the two have
    * {@linkplain UUID#equals(Object) equivalent} {@linkplain #getIdentifier()
    * identifiers}.</li>
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
    * The names of the persons in this scenario that {@linkplain GamePlayers
    * players} can play.
    * </p>
    * <ul>
    * <li>Always a {@linkplain #isValidCharacters(List) valid list of
    * characters}.</li>
    * </ul>
    * <ul>
    * <li>The list of characters is in descending order of selection priority:
    * for all else equal, players should be allocated characters near the start
    * of the list.</li>
    * <li>The returned list is not modifiable.</li>
    * </ul>
    *
    * @return the title.
    */
   @NonNull
   @JsonProperty("characters")
   public final List<String> getCharacters() {
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
   public final String getDescription() {
      return description;
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
   public final String getTitle() {
      return title;
   }

   @Override
   public final int hashCode() {
      return identifier.hashCode();
   }

}
