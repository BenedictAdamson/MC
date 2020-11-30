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

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * A user of the Mission Command game.
 * </p>
 */
@Entity
public final class User extends BasicUserDetails {

   private static final long serialVersionUID = 1L;

   /**
    * <p>
    * The {@linkplain #getId() ID} of an administrator user.
    * </p>
    */
   public static final UUID ADMINISTRATOR_ID = new UUID(0L, 0L);

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

   private User(final String password) {
      super(password);
      this.id = ADMINISTRATOR_ID;
   }

   /**
    * <p>
    * Construct a user of the Mission Command game, with given user details.
    * </p>
    * <ul>
    * <li>The {@linkplain #getId() ID} of this user is the given id.</li>
    * <li>The {@linkplain #getUsername() username} of this user is the same as
    * the {@linkplain BasicUserDetails#getUsername() username} of the given user
    * details.</li>
    * <li>The {@linkplain #getPassword() password} of this user is the same as
    * the {@linkplain BasicUserDetails#getPassword() password} of the given user
    * details.</li>
    * <li>The {@linkplain #getAuthorities() authorities} granted to this user
    * are the same as the {@linkplain BasicUserDetails#getAuthorities()
    * authorities} of the given user details.</li>
    * <li>Whether this user's {@linkplain #isAccountNonExpired() account has not
    * expired} is equal to the {@linkplain BasicUserDetails#isAccountNonExpired
    * value} for the given user details.</li>
    * <li>Whether this user's {@linkplain #isAccountNonLocked() account is not
    * locked} is equal to the {@linkplain BasicUserDetails#isAccountNonLocked()
    * value} for the given user details.</li>
    * <li>Whether this user's {@linkplain #isCredentialsNonExpired() credentials
    * have not expired} is equal to the
    * {@linkplain BasicUserDetails#isCredentialsNonExpired() value} for the
    * given user details.</li>
    * <li>Whether this user's {@linkplain #isEnabled() account is enabled} is
    * equal to the {@linkplain BasicUserDetails#isEnabled() value} for the given
    * user details.</li>
    * </ul>
    *
    * @param id
    *           The unique ID of this user.
    * @param userDetails
    *           the specification for this user.
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code id} is null</li>
    *            <li>If {@code userDetails} is null</li>
    *            </ul>
    */
   public User(@NonNull final UUID id,
            @NonNull final BasicUserDetails userDetails) {
      super(userDetails);
      this.id = Objects.requireNonNull(id, "id");
   }

   /**
    * <p>
    * Construct a user of the Mission Command game, with given attribute values.
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
      super(username, password, authorities, accountNonExpired,
               accountNonLocked, credentialsNonExpired, enabled);
      this.id = Objects.requireNonNull(id, "id");
   }

   /**
    * <p>
    * With this object is <dfn>equivalent</dfn> to another object.
    * </p>
    * <ul>
    * <li>The {@link User} class has <i>entity semantics</i>, with the
    * {@linkplain #getId() ID} attribute serving as a unique ID: this object is
    * equivalent to another object if, and only of, the other object is also a
    * {@link User} and the two have {@linkplain String#equals(Object)
    * equivalent} {@linkplain #getId() IDs}.</li>
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
      return id.equals(other.id);
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
   public int hashCode() {
      return id.hashCode();
   }

   @Override
   public String toString() {
      return "User [id=" + id + "]";
   }

}
