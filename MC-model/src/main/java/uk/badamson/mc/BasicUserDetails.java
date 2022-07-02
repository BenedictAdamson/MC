package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2019-22.
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * A specification for a new {@linkplain User user}.
 * </p>
 */
public class BasicUserDetails {

    /**
     * <p>
     * The {@linkplain #getUsername() username} of an administrator user.
     * </p>
     *
     * @see #createAdministrator(String)
     */
    public static final String ADMINISTRATOR_USERNAME = "Administrator";

    /**
     * <p>
     * Create {@link BasicUserDetails} for a a valid administrator user.
     * </p>
     *
     * @param password the password used to authenticate the user, or null if the
     *                 password is being hidden or is unknown. This might be the
     *                 password in an encrypted form.
     * @see #ADMINISTRATOR_USERNAME
     */
    @Nonnull
    public static BasicUserDetails createAdministrator(
            @Nullable final String password) {
        return new BasicUserDetails(password);
    }

    private final String username;
    private String password;
    private final Set<Authority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;

    /**
     * <p>
     * Copy a specification for a new {@linkplain User user}.
     * </p>
     *
     * @param that the specification to copy
     * @throws NullPointerException If {@code that} is null
     */
    public BasicUserDetails(@Nonnull final BasicUserDetails that) {
        Objects.requireNonNull(that, "that");
        this.username = that.username;
        this.password = that.password;
        this.authorities = that.authorities;
        this.accountNonExpired = that.accountNonExpired;
        this.accountNonLocked = that.accountNonLocked;
        this.credentialsNonExpired = that.credentialsNonExpired;
        this.enabled = that.enabled;
    }

    BasicUserDetails(final String password) {
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
     * Construct a specification for a new {@linkplain User user}, with given
     * attribute values.
     * </p>
     *
     * @param username              the username used to authenticate the user
     * @param password              the password used to authenticate the user, or null if the
     *                              password is being hidden or is unknown. This might be the
     *                              password in an encrypted form.
     * @param authorities           The authorities granted to the user.
     * @param accountNonExpired     whether the user's account has expired, and so cannot be
     *                              authenticated.
     * @param accountNonLocked      whether the user's account is locked, and so cannot be
     *                              authenticated.
     * @param credentialsNonExpired whether the user's credentials (password) has expired, and so
     *                              can not be authenticated.
     * @param enabled               whether the user is enabled; a disabled user cannot be
     *                              authenticated.
     * @throws NullPointerException <ul>
     *                                         <li>If {@code username} is null</li>
     *                                         <li>If {@code authorities} is null</li>
     *                                         <li>If {@code authorities} contains null</li>
     *                                         </ul>
     */
    public BasicUserDetails(
            @Nonnull final String username,
            @Nullable final String password,
            @Nonnull final Set<Authority> authorities,
            final boolean accountNonExpired,
            final boolean accountNonLocked,
            final boolean credentialsNonExpired,
            final boolean enabled) {
        this.username = Objects.requireNonNull(username, "username");
        this.password = password;
        this.authorities = authorities.isEmpty() ? Collections.emptySet()
                : Collections.unmodifiableSet(EnumSet.copyOf(authorities));
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "authorities is unmodifiable")
    public final Set<Authority> getAuthorities() {
        return authorities;
    }

    public final String getPassword() {
        return password;
    }

    @Nonnull
    public final String getUsername() {
        return username;
    }

    public final boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public final boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public final boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setPassword(@Nullable final String password) {
        this.password = password;
    }

}
