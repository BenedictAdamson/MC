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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

/**
 * <p>
 * Unit tests and auxiliary test code for the {@link User} class
 */
public class UserTest {

   @Nested
   public class Constructor {

      @Nested
      public class TwoDifferentAttributes {

         @Test
         public void accountNonExpired() {
            final var username = USERNAME_A;
            final var password = PASSWORD_A;
            final var authorities = Authority.ALL;
            final boolean accountNonLocked = true;
            final boolean credentialsNonExpired = true;
            final boolean enabled = true;

            final var userA = new User(username, password, authorities, true,
                     accountNonLocked, credentialsNonExpired, enabled);
            final var userB = new User(username, password, authorities, false,
                     accountNonLocked, credentialsNonExpired, enabled);

            test(userA, userB);
         }

         @Test
         public void accountNonLocked() {
            final var username = USERNAME_A;
            final var password = PASSWORD_A;
            final var authorities = Authority.ALL;
            final boolean accountNonExpired = true;
            final boolean credentialsNonExpired = true;
            final boolean enabled = true;

            final var userA = new User(username, password, authorities,
                     accountNonExpired, true, credentialsNonExpired, enabled);
            final var userB = new User(username, password, authorities,
                     accountNonExpired, false, credentialsNonExpired, enabled);

            test(userA, userB);
         }

         @Test
         public void authorities() {
            final var username = USERNAME_A;
            final var password = PASSWORD_A;
            final boolean accountNonExpired = true;
            final boolean accountNonLocked = true;
            final boolean credentialsNonExpired = true;
            final boolean enabled = true;

            final var userA = new User(username, password, Authority.ALL,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     enabled);
            final var userB = new User(username, password,
                     Set.of(Authority.PLAYER), accountNonExpired,
                     accountNonLocked, credentialsNonExpired, enabled);

            test(userA, userB);
         }

         @Test
         public void credentialsNonExpired() {
            final var username = USERNAME_A;
            final var password = PASSWORD_A;
            final var authorities = Authority.ALL;
            final boolean accountNonExpired = true;
            final boolean accountNonLocked = true;
            final boolean enabled = true;

            final var userA = new User(username, password, authorities,
                     accountNonExpired, accountNonLocked, true, enabled);
            final var userB = new User(username, password, authorities,
                     accountNonExpired, accountNonLocked, false, enabled);

            test(userA, userB);
         }

         @Test
         public void enabled() {
            final var username = USERNAME_A;
            final var password = PASSWORD_A;
            final var authorities = Authority.ALL;
            final boolean accountNonExpired = true;
            final boolean accountNonLocked = true;
            final boolean credentialsNonExpired = true;

            final var userA = new User(username, password, authorities,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     true);
            final var userB = new User(username, password, authorities,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     false);

            test(userA, userB);
         }

         @Test
         public void password() {
            final var username = USERNAME_A;
            final var authorities = Authority.ALL;
            final boolean accountNonExpired = true;
            final boolean accountNonLocked = true;
            final boolean credentialsNonExpired = true;
            final boolean enabled = true;

            final var userA = new User(username, PASSWORD_A, authorities,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     enabled);
            final var userB = new User(username, PASSWORD_B, authorities,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     enabled);

            test(userA, userB);
         }

         private void test(final User userA, final User userB) {
            assertInvariants(userA, userB);
            assertEquals(userA, userB);
         }

      }// class

      @Nested
      public class TwoEquivalent {

         @Test
         public void a() {
            test(USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true, true);
         }

         @Test
         public void b() {
            test(USERNAME_B, PASSWORD_B, Set.of(), true, true, false, true);
         }

         @Test
         public void c() {
            final String password = null;
            test(USERNAME_A, password, Set.of(), true, false, true, true);
         }

         @Test
         public void d() {
            test(USERNAME_A, PASSWORD_A, Authority.ALL, false, true, true,
                     true);
         }

         private void test(@NonNull final String username,
                  final String password, final Set<Authority> authorities,
                  final boolean accountNonExpired,
                  final boolean accountNonLocked,
                  final boolean credentialsNonExpired, final boolean enabled) {
            final var user1 = new User(username, password, authorities,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     enabled);
            final var user2 = new User(new String(username),
                     password == null ? password : new String(password),
                     authorities.isEmpty() ? authorities
                              : EnumSet.copyOf(authorities),
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     enabled);

            assertInvariants(user1, user2);
            assertEquals(user1, user2);
         }

      }// class

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
         test(USERNAME_A, PASSWORD_A, Authority.ALL, false, true, true, true);
      }

      @Test
      public void notAccountNonLocked() {
         test(USERNAME_A, PASSWORD_A, Authority.ALL, true, false, true, true);
      }

      @Test
      public void notCredentialsNonExpired() {
         test(USERNAME_A, PASSWORD_A, Authority.ALL, true, true, false, true);
      }

      @Test
      public void notEnabled() {
         test(USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true, false);
      }

      @Test
      public void nullPassword() {
         final String password = null;
         test(USERNAME_A, password, Authority.ALL, true, true, true, true);
      }

      @Test
      public void password() {
         test(USERNAME_A, PASSWORD_B, Authority.ALL, true, true, true, true);
      }

      private User test(final String username, final String password,
               final Set<Authority> authorities,
               final boolean accountNonExpired, final boolean accountNonLocked,
               final boolean credentialsNonExpired, final boolean enabled) {
         final var user = new User(username, password, authorities,
                  accountNonExpired, accountNonLocked, credentialsNonExpired,
                  enabled);

         assertInvariants(user);
         assertAll("Has the given attribute values",
                  () -> assertSame(username, user.getUsername(), "userSname."),
                  () -> assertSame(password, user.getPassword(), "password."),
                  () -> assertEquals(authorities, user.getAuthorities(),
                           "authorities"),
                  () -> assertEquals(accountNonExpired,
                           user.isAccountNonExpired(), "accountNonExpired"),
                  () -> assertEquals(accountNonLocked,
                           user.isAccountNonLocked(), "accountNonLocked"),
                  () -> assertEquals(credentialsNonExpired,
                           user.isCredentialsNonExpired(),
                           "credentialsNonExpired"),
                  () -> assertEquals(enabled, user.isEnabled(), "enabled"));

         return user;
      }

      @Test
      public void twoDifferentUsername() {
         final var password = PASSWORD_A;
         final var authorities = Authority.ALL;
         final boolean accountNonExpired = true;
         final boolean accountNonLocked = true;
         final boolean credentialsNonExpired = true;
         final boolean enabled = true;

         final var userA = new User(USERNAME_A, password, authorities,
                  accountNonExpired, accountNonLocked, credentialsNonExpired,
                  enabled);
         final var userB = new User(USERNAME_B, password, authorities,
                  accountNonExpired, accountNonLocked, credentialsNonExpired,
                  enabled);

         assertInvariants(userA, userB);
         assertNotEquals(userA, userB);
      }

      @Test
      public void username() {
         test(USERNAME_B, PASSWORD_A, Authority.ALL, true, true, true, true);
      }

   }// class

   private static final String USERNAME_A = "John";

   private static final String USERNAME_B = "Alan";

   private static final String PASSWORD_A = "letmein";

   private static final String PASSWORD_B = "password123";

   public static void assertInvariants(final User user) {
      UserDetailsTest.assertInvariants(user);
      assertEquals(user, user, "An object is always equivalent to itself.");
   }

   public static void assertInvariants(final User user1, final User user2) {
      UserDetailsTest.assertInvariants(user1, user2);
      final boolean equals = user1.equals(user2);
      assertTrue(!(equals && !user2.equals(user1)), "Equality is symmetric");
      assertTrue(!(equals && user1.hashCode() != user2.hashCode()),
               "Equality implies equal hashCode");
   }
}
