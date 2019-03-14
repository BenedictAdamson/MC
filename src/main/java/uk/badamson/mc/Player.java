package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2019.
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
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A player of the Mission Command game.
 * </p>
 */
public final class Player implements UserDetails {

   private static final long serialVersionUID = 1L;

   public static final Player DEFAULT_ADMINISTRATOR = new Player(
            "Administrator");

   private final String username;

   /**
    * <p>
    * Construct a player of the Mission Command game, with a given user-name.
    * </p>
    * <ul>
    * <li>The {@linkplain #getUsername() username} of this player is the given
    * username.</li>
    * <li>This player {@linkplain Collection#isEmpty() has no} granted
    * {@linkplain #getAuthorities() authorities}.</li>
    * </ul>
    *
    * @param username
    *           the username used to authenticate the player
    * @throws NullPointerException
    *            If {@code username} is null
    */
   @JsonCreator
   public Player(@NonNull @JsonProperty("username") final String username) {
      this.username = Objects.requireNonNull(username, "username");
   }

   /**
    * <p>
    * With this object is <dfn>equivalent</dfn> to another object.
    * </p>
    * <ul>
    * <li>The {@link Player} class has <i>entity semantics</i>, with the
    * {@linkplain #getUsername() username} serving as a unique ID: this object
    * is equivalent to another object if, and only of, the other object is also
    * a {@link Player} and the two have {@linkplain String#equals(Object)
    * equivalent} {@linkplain #getUsername() usernames}.</li>
    * </ul>
    *
    * @param obj
    * @return
    */
   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof Player)) {
         return false;
      }
      final Player other = (Player) obj;
      return username.equals(other.username);
   }

   @Override
   public final Collection<? extends GrantedAuthority> getAuthorities() {
      return Collections.emptySet();// TODO
   }

   @Override
   public final String getPassword() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   @NonNull
   public final String getUsername() {
      return username;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + username.hashCode();
      return result;
   }

   @Override
   public final boolean isAccountNonExpired() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public final boolean isAccountNonLocked() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public final boolean isCredentialsNonExpired() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public final boolean isEnabled() {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public String toString() {
      return "Player [username=" + username + "]";
   }

}
