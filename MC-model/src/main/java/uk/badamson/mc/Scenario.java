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

import java.util.UUID;

import javax.annotation.concurrent.Immutable;
import javax.persistence.Entity;

import org.springframework.lang.NonNull;

/**
 * <p>
 * A game scenario of the Mission Command game.
 * </p>
 */
@Entity
@Immutable
public interface Scenario {

   /**
    * <p>
    * Whether this object is <dfn>equivalent</dfn> to another object.
    * </p>
    * <ul>
    * <li>The {@link Scenario} class has <i>entity semantics</i>, with the
    * {@linkplain #getId() ID} serving as a unique identifier: this object is
    * equivalent to another object if, and only of, the other object is also a
    * {@link Scenario} and the two have {@linkplain String#equals(Object)
    * equivalent} {@linkplain #getId() IDs}.</li>
    * </ul>
    *
    * @param that
    *           The object to compare with this.
    * @return Whether this object is equivalent to {@code that} object.
    */
   @Override
   boolean equals(final Object that);

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
    * @see #getTitle()
    */
   @NonNull
   String getDescription();

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
   UUID getId();

   /**
    * <p>
    * A short human readable identifier for this scenario.
    * </p>
    * <p>
    * Although the title is an identifier, it is not guaranteed to be a unique
    * identifier.
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
    * @see #getDescription()
    */
   @NonNull
   String getTitle();

}
