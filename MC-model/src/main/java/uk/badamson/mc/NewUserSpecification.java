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

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p>
 * A specification for a new {@linkplain User user}.
 * </p>
 */
public class NewUserSpecification implements UserDetails {

   private static final long serialVersionUID = 1L;

   private final String username;
   private final String password;
   private final Set<Authority> authorities;
   private final boolean accountNonExpired;
   private final boolean accountNonLocked;
   private final boolean credentialsNonExpired;
   private final boolean enabled;

   /**
    * <p>
    * Construct a specification for a new {@linkplain User user}, with given
    * attribute values.
    * </p>
    * <ul>
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
    *
    * @throws NullPointerException
    *            <ul>
    *            <li>If {@code username} is null</li>
    *            <li>If {@code authorities} is null</li>
    *            <li>If {@code authorities} contains null</li>
    *            </ul>
    */
   public NewUserSpecification(@NonNull final String username,
            @Nullable final String password,
            @NonNull final Set<Authority> authorities,
            final boolean accountNonExpired, final boolean accountNonLocked,
            final boolean credentialsNonExpired, final boolean enabled) {
      this.username = Objects.requireNonNull(username, "username");
      this.password = password;
      this.authorities = authorities.isEmpty() ? Collections.emptySet()
               : Collections.unmodifiableSet(EnumSet.copyOf(authorities));
      this.accountNonExpired = accountNonExpired;
      this.accountNonLocked = accountNonLocked;
      this.credentialsNonExpired = credentialsNonExpired;
      this.enabled = enabled;
   }

   @Override
   public Set<Authority> getAuthorities() {
      return authorities;
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

}
