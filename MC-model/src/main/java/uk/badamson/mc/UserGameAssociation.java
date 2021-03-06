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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A reified association between a {@linkplain User user} and a
 * {@linkplain Game}, with the user and game referred to using their unique IDs.
 * </p>
 * <p>
 * This class is intended to be used only for recording associations in the
 * repository layer.
 * </p>
 */
@Immutable
public final class UserGameAssociation {

   @Id
   @org.springframework.data.annotation.Id
   private final UUID user;
   private final Game.Identifier game;

   /**
    * <p>
    * Construct an object with given attribute values.
    * </p>
    * <ul>
    * <li>The {@linkplain #getUser() user ID} of this object is the same as the
    * given user ID.</li>
    * <li>The {@linkplain #getGame() game ID} of this object is the same as the
    * given game ID.</li>
    * </ul>
    *
    * @param user
    *           The {@linkplain User#getId() unique ID} of the {@linkplain User
    *           user} associated with the game.
    * @param game
    *           The {@linkplain Game#getIdentifier() unique ID} of the
    *           {@linkplain Game game} associated with the user.
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code user} is null.</li>
    *            <li>If {@code game} is null.</li>
    *            </ul>
    */
   @JsonCreator
   public UserGameAssociation(@Nonnull @JsonProperty("user") final UUID user,
            @Nonnull @JsonProperty("game") final Game.Identifier game) {
      this.user = Objects.requireNonNull(user, "user");
      this.game = Objects.requireNonNull(game, "game");
   }

   /**
    * <p>
    * Whether this object is <i>equivalent</i> to another object.
    * </p>
    * <p>
    * The {@link UserGameAssociation} class has <i>value semantics</i>: this
    * object is equivalent to another only if the other object is also a
    * {@link UserGameAssociation} and they have equivalent attribute values.
    */
   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (!(obj instanceof UserGameAssociation)) {
         return false;
      }
      final var other = (UserGameAssociation) obj;
      return user.equals(other.user) && game.equals(other.game);
   }

   /**
    * <p>
    * The {@linkplain Game#getIdentifier() unique ID} of the {@linkplain Game
    * game} associated with the user.
    * </p>
    * <ul>
    * <li>Non null</li>
    * </ul>
    *
    * @return the user ID
    */
   @Nonnull
   public Game.Identifier getGame() {
      return game;
   }

   /**
    * <p>
    * The {@linkplain User#getId() unique ID} of the {@linkplain User user}
    * associated with the game.
    * </p>
    * <ul>
    * <li>Non null</li>
    * </ul>
    *
    * @return the user ID
    */
   @Nonnull
   public UUID getUser() {
      return user;
   }

   @Override
   public int hashCode() {
      return Objects.hash(game, user);
   }

   @Override
   public String toString() {
      return "(" + user + ", " + game + ")";
   }

}
