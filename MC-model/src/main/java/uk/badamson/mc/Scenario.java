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

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Id;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A game scenario of the Mission Command game.
 * </p>
 */
public class Scenario {

   /**
    * <p>
    * Identification information for a game {@linkplain Scenario scenario} of
    * the Mission Command game.
    * </p>
    */
   public static final class Identifier {

      @Id
      @org.springframework.data.annotation.Id
      private final UUID id;
      private final String title;

      /**
       * <p>
       * Construct identification information for a game scenario, with given
       * attribute values.
       * </p>
       *
       * <h2>Post Conditions</h2>
       * <ul>
       * <li>The {@linkplain #getId() ID} of this object is the given
       * {@code id}.</li>
       * <li>The {@linkplain #getTitle() title} of this object is the given
       * {@code title}.</li>
       * </ul>
       *
       * @param id
       *           The unique identifier for the scenario.
       * @param title
       *           A short human readable identifier for the scenario.
       * @throws NullPointerException
       *            <ul>
       *            <li>If {@code id} is null</li>
       *            <li>If {@code title} is null</li>
       *            </ul>
       * @throws IllegalArgumentException
       *            <ul>
       *            <li>If {@code title} {@linkplain String#isEmpty() is
       *            empty}.</li>
       *            <li>If {@code title} is {@linkplain String#length() longer}
       *            than 64 code points.</li>
       *            </ul>
       */
      @JsonCreator
      public Identifier(@NonNull @JsonProperty("id") final UUID id,
               @NonNull @JsonProperty("title") final String title) {
         this.id = Objects.requireNonNull(id, "id");
         this.title = Objects.requireNonNull(title, "title");// guard
         if (title.isEmpty()) {
            throw new IllegalArgumentException("title is empty");
         }
      }

      /**
       * <p>
       * Whether this object is <dfn>equivalent</dfn> to another object.
       * </p>
       * <ul>
       * <li>The {@link Scenario.Identifier} class has <i>entity semantics</i>,
       * with the {@linkplain #getId() ID} serving as a unique identifier: this
       * object is equivalent to another object if, and only of, the other
       * object is also a {@link Scenario.Identifier} and the two have
       * {@linkplain String#equals(Object) equivalent} {@linkplain #getId()
       * IDs}.</li>
       * </ul>
       *
       * @param that
       *           The object to compare with this.
       * @return Whether this object is equivalent to {@code that} object.
       */
      @Override
      public boolean equals(final Object that) {
         if (this == that) {
            return true;
         }
         if (!(that instanceof Identifier)) {
            return false;
         }
         final var other = (Identifier) that;
         return id.equals(other.getId());
      }

      /**
       * <p>
       * The unique identifier for this scenario.
       * </p>
       * <ul>
       * <li>Not null</li>
       * </ul>
       *
       * @return the identifier.
       * @see #getTitle()
       */
      @NonNull
      public UUID getId() {
         return id;
      }

      /**
       * <p>
       * A short human readable identifier for this scenario.
       * </p>
       * <p>
       * Although the title is an identifier, it is not guaranteed to be a
       * unique identifier.
       * </p>
       * <ul>
       * <li>Not null</li>
       * <li>Not {@linkplain String#isEmpty() empty}</li>
       * <li>Not {@linkplain String#length() longer} that 64 code points</li>
       * </ul>
       *
       * @return the title.
       *
       * @see #getId()
       * @see Scenario#getDescription()
       */
      @NonNull
      public String getTitle() {
         return title;
      }

      @Override
      public int hashCode() {
         return id.hashCode();
      }
   }// class

   private final Identifier identifier;
   private final String description;

   /**
    * <p>
    * Construct a game scenario with a given identifier.
    * </p>
    *
    * <h2>Post Conditions</h2>
    * <ul>
    * <li>The {@linkplain #getIdentifier() identifier} of this object is the
    * given {@code identifier}.</li>
    * <li>The {@linkplain #getDescription() description} of this object is the
    * given {@code description}.</li>
    * </ul>
    *
    * @param identifier
    *           The identifier for this scenario.
    * @param description
    *           A human readable description for the scenario.
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code identifier} is null</li>
    *            <li>If {@code description} is null</li>
    *            </ul>
    */
   public Scenario(@NonNull final Scenario.Identifier identifier,
            @NonNull @JsonProperty("description") final String description) {
      this.identifier = Objects.requireNonNull(identifier, "identifier");
      this.description = Objects.requireNonNull(description, "description");
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
    * {@linkplain Scenario.Identifier#equals(Object) equivalent}
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
    * @see Identifier#getTitle()
    */
   @NonNull
   public String getDescription() {
      return description;
   }

   /**
    * <p>
    * The identification information for this scenario.
    * </p>
    * <ul>
    * <li>Not null.</li>
    * </ul>
    *
    * @return the identifier.
    */
   @NonNull
   public final Identifier getIdentifier() {
      return identifier;
   }

   @Override
   public final int hashCode() {
      return identifier.hashCode();
   }
}
