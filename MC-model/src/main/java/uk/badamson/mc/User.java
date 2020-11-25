package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2019-20.
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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A user of the Mission Command game.
 * </p>
 */
@Entity
public final class User implements UserDetails {

   private static final long serialVersionUID = 1L;

   /**
    * <p>
    * The {@linkplain #getId() ID} of an administrator user.
    * </p>
    */
   public static final UUID ADMINISTRATOR_ID = new UUID(0L, 0L);

   /**
    * <p>
    * The {@linkplain #getUsername() username} of an administrator user.
    * </p>
    */
   public static final String ADMINISTRATOR_USERNAME = "Administrator";

   /**
    * <p>
    * Create a {@link User} that is a valid administrator user.
    * </p>
    * <ul>
    * <li>Returns a (non null) {@link User}.</li>
    * <li>The {@linkplain #getId() ID} of the administrator is the same as the
    * special {@linkplain #ADMINISTRATOR_ID administrator ID}.</li>
    * <li>The {@linkplain #getUsername() username} of the administrator is the
    * same as the special {@linkplain #ADMINISTRATOR_USERNAME administrator
    * username}.</li>
    * <li>The {@linkplain #getPassword() password} of the administrator is the
    * given password.</li>
    * <li>The {@linkplain #getAuthorities() authorities} granted to the
    * administrator is the same as the {@linkplain Authority#ALL full set of
    * authorities}.</li>
    * <li>The administrator's {@linkplain #isAccountNonExpired() account has not
    * expired}.</li>
    * <li>The administrator's {@linkplain #isAccountNonLocked() account is not
    * locked}.</li>
    * <li>The administrator's {@linkplain #isCredentialsNonExpired() credentials
    * have not expired}.</li>
    * <li>The administrator's {@linkplain #isEnabled() account is enabled}.</li>
    * </ul>
    *
    * @param password
    *           the password used to authenticate the user, or null if the
    *           password is being hidden or is unknown. This might be the
    *           password in an encrypted form.
    * @return the administrator user
    */
   @Nonnull
   public static User createAdministrator(@Nullable final String password) {
      return new User(password);
   }

   @Id
   @org.springframework.data.annotation.Id
   private final UUID id;
   private final String username;
   private final String password;
   private final Set<Authority> authorities;
   private final boolean accountNonExpired;
   private final boolean accountNonLocked;
   private final boolean credentialsNonExpired;
   private final boolean enabled;

   private User(final String password) {
      this.id = ADMINISTRATOR_ID;
      this.username = ADMINISTRATOR_USERNAME;
      this.password = password;
      this.authorities = Authority.ALL;
      this.accountNonExpired = true;
      this.accountNonLocked = true;
      this.credentialsNonExpired = true;
      this.enabled = true;
   }

   /**
    * <p>
    * Construct a user of the Mission Command game, with a given user-name.
    * </p>
    * <ul>
    * <li>The {@linkplain #getId() ID} of this user is the given id.</li>
    * <li>The {@linkplain #getUsername() username} of this user is the given
    * username.</li>
    * <li>The {@linkplain #getPassword() password} of this user is the given
    * password.</li>
    * <li>The {@linkplain #getAuthorities() authorities} granted to this user
    * are {@linkplain Set#equals(Object) equal to} the given authorities.</li>
    * <li>Whether this user's {@linkplain #isAccountNonExpired() account has not
    * expired} is equal to the given value.</li>
    * <li>Whether this user's {@linkplain #isAccountNonLocked() account is not
    * locked} is equal to the given value.</li>
    * <li>Whether this user's {@linkplain #isCredentialsNonExpired() credentials
    * have not expired} is equal to the given value.</li>
    * <li>Whether this user's {@linkplain #isEnabled() account is enabled} is
    * equal to the given value.</li>
    * </ul>
    *
    * @param id
    *           The unique ID of this user.
    * @param username
    *           the username used to authenticate the user
    * @param password
    *           the password used to authenticate the user, or null if the
    *           password is being hidden or is unknown. This might be the
    *           password in an encrypted form.
    * @param authorities
    *           The authorities granted to the user.
    * @param accountNonExpired
    *           whether the user's account has expired, and so cannot be
    *           authenticated.
    * @param accountNonLocked
    *           whether the user's account is locked, and so cannot be
    *           authenticated.
    * @param credentialsNonExpired
    *           whether the user's credentials (password) has expired, and so
    *           can not be authenticated.
    * @param enabled
    *           whether the user is enabled; a disabled user cannot be
    *           authenticated.
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code id} is null</li>
    *            <li>If {@code username} is null</li>
    *            <li>If {@code authorities} is null</li>
    *            <li>If {@code authorities} contains null</li>
    *            </ul>
    */
   @JsonCreator
   @PersistenceConstructor
   public User(@NonNull @JsonProperty("id") final UUID id,
            @NonNull @JsonProperty("username") final String username,
            @Nullable @JsonProperty("password") final String password,
            @NonNull @JsonProperty("authorities") final Set<Authority> authorities,
            @JsonProperty("accountNonExpired") final boolean accountNonExpired,
            @JsonProperty("accountNonLocked") final boolean accountNonLocked,
            @JsonProperty("credentialsNonExpired") final boolean credentialsNonExpired,
            @JsonProperty("enabled") final boolean enabled) {
      this.id = Objects.requireNonNull(id, "id");
      this.username = Objects.requireNonNull(username, "username");
      this.password = password;
      this.authorities = authorities.isEmpty() ? Collections.emptySet()
               : Collections.unmodifiableSet(EnumSet.copyOf(authorities));
      this.accountNonExpired = accountNonExpired;
      this.accountNonLocked = accountNonLocked;
      this.credentialsNonExpired = credentialsNonExpired;
      this.enabled = enabled;
   }

   /**
    * <p>
    * With this object is <dfn>equivalent</dfn> to another object.
    * </p>
    * <ul>
    * <li>The {@link User} class has <i>entity semantics</i>, with the
    * {@linkplain #getUsername() username} serving as a unique ID: this object
    * is equivalent to another object if, and only of, the other object is also
    * a {@link User} and the two have {@linkplain String#equals(Object)
    * equivalent} {@linkplain #getUsername() usernames}.</li>
    * </ul>
    *
    * @param that
    *           The object to compare with this.
    * @return Whether this is equivalent to that.
    */
   @Override
   public boolean equals(final Object that) {
      if (this == that) {
         return true;
      }
      if (that == null) {
         return false;
      }
      if (!(that instanceof User)) {
         return false;
      }
      final var other = (User) that;
      return username.equals(other.username);
   }

   @Override
   public Set<Authority> getAuthorities() {
      return authorities;
   }

   /**
    * <p>
    * The unique ID of this user.
    * </p>
    * <ul>
    * <li>Not null</li>
    * </ul>
    * <p>
    * Note that the {@linkplain #getUsername() username} need not be unique.
    * However, in practice it will be enforced to be unique, with the username
    * used as the human readable ID.
    * </p>
    *
    * @return the unique ID.
    */
   @NonNull
   public UUID getId() {
      return id;
   }

   @Override
   public String getPassword() {
      return password;
   }

   @Override
   @NonNull
   public String getUsername() {
      return username;
   }

   @Override
   public int hashCode() {
      return username.hashCode();
   }

   @Override
   public boolean isAccountNonExpired() {
      return accountNonExpired;
   }

   @Override
   public boolean isAccountNonLocked() {
      return accountNonLocked;
   }

   @Override
   public boolean isCredentialsNonExpired() {
      return credentialsNonExpired;
   }

   @Override
   public boolean isEnabled() {
      return enabled;
   }

   @Override
   public String toString() {
      return "User [username=" + username + "]";
   }

}
