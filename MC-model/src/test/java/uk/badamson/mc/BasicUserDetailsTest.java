package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2019-20,22.
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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.badamson.dbc.assertions.ObjectVerifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BasicUserDetailsTest {

    @Nested
    public class Constructor {

        @Nested
        public class Copy {

            @Test
            public void authorities() {
                test(USERNAME_A, PASSWORD_A, Set.of(), true, true, true, true);
            }

            @Test
            public void basic() {
                test(USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true, true);
            }

            @Test
            public void notAccountNonExpired() {
                test(USERNAME_A, PASSWORD_A, Authority.ALL, false, true, true,
                        true);
            }

            @Test
            public void notAccountNonLocked() {
                test(USERNAME_A, PASSWORD_A, Authority.ALL, true, false, true,
                        true);
            }

            @Test
            public void notCredentialsNonExpired() {
                test(USERNAME_A, PASSWORD_A, Authority.ALL, true, true, false,
                        true);
            }

            @Test
            public void notEnabled() {
                test(USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                        false);
            }

            @Test
            public void nullPassword() {
                test(USERNAME_A, null, Authority.ALL, true, true, true, true);
            }

            @Test
            public void password() {
                test(USERNAME_A, PASSWORD_B, Authority.ALL, true, true, true, true);
            }

            private void test(final String username, final String password,
                              final Set<Authority> authorities,
                              final boolean accountNonExpired,
                              final boolean accountNonLocked,
                              final boolean credentialsNonExpired, final boolean enabled) {
                final var original = new BasicUserDetails(username, password,
                        authorities, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);
                constructor(original);
            }

            @Test
            public void username() {
                test(USERNAME_B, PASSWORD_A, Authority.ALL, true, true, true, true);
            }

        }

        @Nested
        public class TwoDifferentAttributes {

            @Test
            public void accountNonExpired() {
                final var username = USERNAME_A;
                final var password = PASSWORD_A;
                final var authorities = Authority.ALL;
                final var accountNonLocked = true;
                final var credentialsNonExpired = true;
                final var enabled = true;

                final var userA = new BasicUserDetails(username, password,
                        authorities, true, accountNonLocked, credentialsNonExpired,
                        enabled);
                final var userB = new BasicUserDetails(username, password,
                        authorities, false, accountNonLocked,
                        credentialsNonExpired, enabled);

                test(userA, userB);
            }

            @Test
            public void accountNonLocked() {
                final var username = USERNAME_A;
                final var password = PASSWORD_A;
                final var authorities = Authority.ALL;
                final var accountNonExpired = true;
                final var credentialsNonExpired = true;
                final var enabled = true;

                final var userA = new BasicUserDetails(username, password,
                        authorities, accountNonExpired, true,
                        credentialsNonExpired, enabled);
                final var userB = new BasicUserDetails(username, password,
                        authorities, accountNonExpired, false,
                        credentialsNonExpired, enabled);

                test(userA, userB);
            }

            @Test
            public void authorities() {
                final var username = USERNAME_A;
                final var password = PASSWORD_A;
                final var accountNonExpired = true;
                final var accountNonLocked = true;
                final var credentialsNonExpired = true;
                final var enabled = true;

                final var userA = new BasicUserDetails(username, password,
                        Authority.ALL, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);
                final var userB = new BasicUserDetails(username, password,
                        Set.of(Authority.ROLE_PLAYER), accountNonExpired,
                        accountNonLocked, credentialsNonExpired, enabled);

                test(userA, userB);
            }

            @Test
            public void credentialsNonExpired() {
                final var username = USERNAME_A;
                final var password = PASSWORD_A;
                final var authorities = Authority.ALL;
                final var accountNonExpired = true;
                final var accountNonLocked = true;
                final var enabled = true;

                final var userA = new BasicUserDetails(username, password,
                        authorities, accountNonExpired, accountNonLocked, true,
                        enabled);
                final var userB = new BasicUserDetails(username, password,
                        authorities, accountNonExpired, accountNonLocked, false,
                        enabled);

                test(userA, userB);
            }

            @Test
            public void enabled() {
                final var username = USERNAME_A;
                final var password = PASSWORD_A;
                final var authorities = Authority.ALL;
                final var accountNonExpired = true;
                final var accountNonLocked = true;
                final var credentialsNonExpired = true;

                final var userA = new BasicUserDetails(username, password,
                        authorities, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, true);
                final var userB = new BasicUserDetails(username, password,
                        authorities, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, false);

                test(userA, userB);
            }

            @Test
            public void password() {
                final var username = USERNAME_A;
                final var authorities = Authority.ALL;
                final var accountNonExpired = true;
                final var accountNonLocked = true;
                final var credentialsNonExpired = true;
                final var enabled = true;

                final var userA = new BasicUserDetails(username, PASSWORD_A,
                        authorities, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);
                final var userB = new BasicUserDetails(username, PASSWORD_B,
                        authorities, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);

                test(userA, userB);
            }

            private void test(final BasicUserDetails userA,
                              final BasicUserDetails userB) {
                assertInvariants(userA, userB);
                assertNotEquals(userA, userB);
            }

            @Test
            public void username() {
                final var password = PASSWORD_A;
                final var authorities = Authority.ALL;
                final var accountNonExpired = true;
                final var accountNonLocked = true;
                final var credentialsNonExpired = true;
                final var enabled = true;

                final var userA = new BasicUserDetails(USERNAME_A, password,
                        authorities, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);
                final var userB = new BasicUserDetails(USERNAME_B, password,
                        authorities, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);

                test(userA, userB);
            }

        }

        @Test
        public void authorities() {
            constructor(USERNAME_A, PASSWORD_A, Set.of(), true, true, true, true);
        }

        @Test
        public void basic() {
            constructor(USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                    true);
        }

        @Test
        public void notAccountNonExpired() {
            constructor(USERNAME_A, PASSWORD_A, Authority.ALL, false, true, true,
                    true);
        }

        @Test
        public void notAccountNonLocked() {
            constructor(USERNAME_A, PASSWORD_A, Authority.ALL, true, false, true,
                    true);
        }

        @Test
        public void notCredentialsNonExpired() {
            constructor(USERNAME_A, PASSWORD_A, Authority.ALL, true, true, false,
                    true);
        }

        @Test
        public void notEnabled() {
            constructor(USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                    false);
        }

        @Test
        public void nullPassword() {
            constructor(USERNAME_A, null, Authority.ALL, true, true, true,
                    true);
        }

        @Test
        public void password() {
            constructor(USERNAME_A, PASSWORD_B, Authority.ALL, true, true, true,
                    true);
        }

        @Test
        public void username() {
            constructor(USERNAME_B, PASSWORD_A, Authority.ALL, true, true, true,
                    true);
        }

    }

    @Nested
    public class CreateAdministrator {

        @Test
        public void a() {
            createAdministrator(PASSWORD_A);
        }

        @Test
        public void b() {
            createAdministrator(PASSWORD_B);
        }

        @Test
        public void nullPassword() {
            createAdministrator(null);
        }
    }

    @Nested
    public class SetPassword {

        @Test
        public void a() {
            test(USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true, true,
                    PASSWORD_B);
        }

        @Test
        public void b() {
            test(USERNAME_B, PASSWORD_B, Set.of(), false, false, false, false,
                    PASSWORD_A);
        }

        @Test
        public void c() {
            test(USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true, true,
                    null);
        }

        private void test(final String username, final String password0,
                          final Set<Authority> authorities,
                          final boolean accountNonExpired, final boolean accountNonLocked,
                          final boolean credentialsNonExpired, final boolean enabled,
                          final String password) {
            final var userDetails = new BasicUserDetails(username, password0,
                    authorities, accountNonExpired, accountNonLocked,
                    credentialsNonExpired, enabled);

            setPassword(userDetails, password);
        }
    }

    private static final String USERNAME_A = "John";

    private static final String USERNAME_B = "Alan";

    private static final String PASSWORD_A = "secret";

    private static final String PASSWORD_B = "password123";

    public static void assertInvariants(final BasicUserDetails user) {
        ObjectVerifier.assertInvariants(user);
    }

    public static void assertInvariants(final BasicUserDetails user1,
                                        final BasicUserDetails user2) {
        ObjectVerifier.assertInvariants(user1, user2);
    }

    public static void setPassword(final BasicUserDetails userDetails,
                                   final String password) {
        final var username0 = userDetails.getUsername();
        final var authorities0 = userDetails.getAuthorities();
        final var accountNonExpired0 = userDetails.isAccountNonExpired();
        final var accountNonLocked0 = userDetails.isAccountNonLocked();
        final var credentialsNonExpired0 = userDetails.isCredentialsNonExpired();
        final var enabled0 = userDetails.isEnabled();

        userDetails.setPassword(password);

        assertInvariants(userDetails);
        assertAll("No other attributes of these details change",
                () -> assertSame(username0, userDetails.getUsername(),
                        "username"),
                () -> assertSame(authorities0, userDetails.getAuthorities(),
                        "authorities"),
                () -> assertEquals(accountNonExpired0,
                        userDetails.isAccountNonExpired(), "accountNonExpired"),
                () -> assertEquals(accountNonLocked0,
                        userDetails.isAccountNonLocked(), "accountNonLocked"),
                () -> assertEquals(credentialsNonExpired0,
                        userDetails.isCredentialsNonExpired(),
                        "credentialsNonExpired"),
                () -> assertEquals(enabled0, userDetails.isEnabled(),
                        "enabled"));
        assertSame(password, userDetails.getPassword(), "Changed password");
    }

    private static void createAdministrator(
            @Nullable final String password) {
        final var administrator = BasicUserDetails.createAdministrator(password);

        assertNotNull(administrator, "Not null, returned");// guard
        assertInvariants(administrator);
        assertAll("Attributes",
                () -> assertSame(BasicUserDetails.ADMINISTRATOR_USERNAME,
                        administrator.getUsername(), "username"),
                () -> assertSame(password, administrator.getPassword(),
                        "password"),
                () -> assertSame(Authority.ALL, administrator.getAuthorities(),
                        "authorities"),
                () -> assertTrue(administrator.isAccountNonExpired(),
                        "accountNonExpired"),
                () -> assertTrue(administrator.isAccountNonLocked(),
                        "accountNonLocked"),
                () -> assertTrue(administrator.isCredentialsNonExpired(),
                        "credentialsNonExpired"),
                () -> assertTrue(administrator.isEnabled(), "enabled"));

    }

    private static void constructor(
            @Nonnull final BasicUserDetails that) {
        final var copy = new BasicUserDetails(that);

        assertInvariants(copy);
        assertInvariants(that, copy);
        assertAll("Same attribute values",
                () -> assertSame(that.getUsername(), copy.getUsername(),
                        "username"),
                () -> assertSame(that.getPassword(), copy.getPassword(),
                        "password"),
                () -> assertSame(that.getAuthorities(), copy.getAuthorities(),
                        "authorities"),
                () -> assertEquals(that.isAccountNonExpired(),
                        copy.isAccountNonExpired(), "accountNonExpired"),
                () -> assertEquals(that.isAccountNonLocked(),
                        copy.isAccountNonLocked(), "accountNonLocked"),
                () -> assertEquals(that.isCredentialsNonExpired(),
                        copy.isCredentialsNonExpired(),
                        "credentialsNonExpired"),
                () -> assertEquals(that.isEnabled(), copy.isEnabled(),
                        "enabled"));

    }

    private static void constructor(@Nonnull final String username,
                                    @Nullable final String password,
                                    @Nonnull final Set<Authority> authorities,
                                    final boolean accountNonExpired, final boolean accountNonLocked,
                                    final boolean credentialsNonExpired, final boolean enabled) {
        final var user = new BasicUserDetails(username, password, authorities,
                accountNonExpired, accountNonLocked, credentialsNonExpired,
                enabled);

        assertInvariants(user);
        assertAll("Has the given attribute values",
                () -> assertSame(username, user.getUsername(), "username"),
                () -> assertSame(password, user.getPassword(), "password"),
                () -> assertEquals(authorities, user.getAuthorities(),
                        "authorities"),
                () -> assertEquals(accountNonExpired, user.isAccountNonExpired(),
                        "accountNonExpired"),
                () -> assertEquals(accountNonLocked, user.isAccountNonLocked(),
                        "accountNonLocked"),
                () -> assertEquals(credentialsNonExpired,
                        user.isCredentialsNonExpired(),
                        "credentialsNonExpired"),
                () -> assertEquals(enabled, user.isEnabled(), "enabled"));

    }
}
