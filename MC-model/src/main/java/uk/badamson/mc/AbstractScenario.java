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

   private final Scenario.Identifier identifier;

   /**
    * <p>
    * Construct a game scenario with a given identifier.
    * </p>
    *
    * <h2>Post Conditions</h2>
    * <ul>
    * <li>The {@linkplain #getIdentifier() identifer} of this object is the
    * given {@code identifier}.</li>
    * </ul>
    *
    * @param identifier
    *           The identifier for this scenario.
    * @throws NullPointerException
    *            If {@code identifier} is null
    */
   protected AbstractScenario(final Scenario.Identifier identifier) {
      this.identifier = Objects.requireNonNull(identifier, "identifier");
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
      return identifier.equals(other.getIdentifier());
   }

   @Override
   public final Scenario.Identifier getIdentifier() {
      return identifier;
   }

   @Override
   public final int hashCode() {
      return identifier.hashCode();
   }

}
