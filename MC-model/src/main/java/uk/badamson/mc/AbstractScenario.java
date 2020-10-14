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

import javax.annotation.concurrent.Immutable;
import javax.persistence.Entity;

/**
 * <p>
 * A game scenario of the Mission Command game.
 * </p>
 */
@Entity
@Immutable
public abstract class AbstractScenario implements Scenario {

   private final UUID id;
   private final String title;
   private final String description;

   /**
    * <p>
    * Construct a game scenario with given attribute values.
    * </p>
    *
    * <h2>Post Conditions</h2>
    * <ul>
    * <li>The {@linkplain #getId() ID} of this object is the given
    * {@code id}.</li>
    * <li>The {@linkplain #getTitle() title} of this object is the given
    * {@code title}.</li>
    * <li>The {@linkplain #getDescription() description} of this object is the
    * given {@code description}.</li>
    * </ul>
    *
    * @param id
    *           The unique identifier for this scenario.
    * @param title
    *           A short human readable identifier for this scenario.
    * @param description
    *           A human readable description for this scenario.
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code id} is null</li>
    *            <li>If {@code title} is null</li>
    *            <li>If {@code description} is null</li>
    *            </ul>
    * @throws IllegalArgumentException
    *            <ul>
    *            <li>If {@code title} {@linkplain String#isEmpty() is
    *            empty}.</li>
    *            </ul>
    */
   protected AbstractScenario(final UUID id, final String title,
            final String description) {
      this.id = Objects.requireNonNull(id, "id");
      this.title = Objects.requireNonNull(title, "title");// guard
      this.description = Objects.requireNonNull(description, "description");
      if (title.isEmpty()) {
         throw new IllegalArgumentException("title is empty");
      }
   }

   @Override
   public final boolean equals(final Object that) {
      if (this == that) {
         return true;
      }
      if (!(that instanceof Scenario)) {
         return false;
      }
      final Scenario other = (Scenario) that;
      return id.equals(other.getId());
   }

   @Override
   public final String getDescription() {
      return description;
   }

   @Override
   public final UUID getId() {
      return id;
   }

   @Override
   public final String getTitle() {
      return title;
   }

   @Override
   public final int hashCode() {
      return id.hashCode();
   }

}
