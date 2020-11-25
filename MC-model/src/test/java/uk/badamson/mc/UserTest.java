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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Unit tests and auxiliary test code for the {@link User} class
 */
public class UserTest {

   @Nested
   public class Constructor {

      @Nested
      public class FromBasicUserDetails {

         @Test
         public void authorities() {
            test(ID_A, USERNAME_A, PASSWORD_A, Set.of(), true, true, true,
                     true);
         }

         @Test
         public void basic() {
            test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                     true);
         }

         @Test
         public void id() {
            test(ID_B, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                     true);
         }

         @Test
         public void notAccountNonExpired() {
            test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, false, true, true,
                     true);
         }

         @Test
         public void notAccountNonLocked() {
            test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, false, true,
                     true);
         }

         @Test
         public void notCredentialsNonExpired() {
            test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, false,
                     true);
         }

         @Test
         public void notEnabled() {
            test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                     false);
         }

         @Test
         public void nullPassword() {
            final String password = null;
            test(ID_A, USERNAME_A, password, Authority.ALL, true, true, true,
                     true);
         }

         @Test
         public void password() {
            test(ID_A, USERNAME_A, PASSWORD_B, Authority.ALL, true, true, true,
                     true);
         }

         private User test(final UUID id, final String username,
                  final String password, final Set<Authority> authorities,
                  final boolean accountNonExpired,
                  final boolean accountNonLocked,
                  final boolean credentialsNonExpired, final boolean enabled) {
            final var basicUserDetails = new BasicUserDetails(username,
                     password, authorities, accountNonExpired, accountNonLocked,
                     credentialsNonExpired, enabled);
            final var user = new User(id, basicUserDetails);

            assertInvariants(user);
            assertAll("Attributes", () -> assertSame(id, user.getId(), "id"),
                     () -> assertSame(basicUserDetails.getUsername(),
                              user.getUsername(), "username"),
                     () -> assertSame(basicUserDetails.getPassword(),
                              user.getPassword(), "password"),
                     () -> assertEquals(basicUserDetails.getAuthorities(),
                              user.getAuthorities(), "authorities"),
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
         public void username() {
            test(ID_A, USERNAME_B, PASSWORD_A, Authority.ALL, true, true, true,
                     true);
         }

      }// class

      @Nested
      public class TwoDifferentAttributes {

         @Test
         public void accountNonExpired() {
            final var id = ID_A;
            final var username = USERNAME_A;
            final var password = PASSWORD_A;
            final var authorities = Authority.ALL;
            final var accountNonLocked = true;
            final var credentialsNonExpired = true;
            final var enabled = true;

            final var userA = new User(id, username, password, authorities,
                     true, accountNonLocked, credentialsNonExpired, enabled);
            final var userB = new User(id, username, password, authorities,
                     false, accountNonLocked, credentialsNonExpired, enabled);

            test(userA, userB);
         }

         @Test
         public void accountNonLocked() {
            final var id = ID_A;
            final var username = USERNAME_A;
            final var password = PASSWORD_A;
            final var authorities = Authority.ALL;
            final var accountNonExpired = true;
            final var credentialsNonExpired = true;
            final var enabled = true;

            final var userA = new User(id, username, password, authorities,
                     accountNonExpired, true, credentialsNonExpired, enabled);
            final var userB = new User(id, username, password, authorities,
                     accountNonExpired, false, credentialsNonExpired, enabled);

            test(userA, userB);
         }

         @Test
         public void authorities() {
            final var id = ID_A;
            final var username = USERNAME_A;
            final var password = PASSWORD_A;
            final var accountNonExpired = true;
            final var accountNonLocked = true;
            final var credentialsNonExpired = true;
            final var enabled = true;

            final var userA = new User(id, username, password, Authority.ALL,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     enabled);
            final var userB = new User(id, username, password,
                     Set.of(Authority.ROLE_PLAYER), accountNonExpired,
                     accountNonLocked, credentialsNonExpired, enabled);

            test(userA, userB);
         }

         @Test
         public void credentialsNonExpired() {
            final var id = ID_A;
            final var username = USERNAME_A;
            final var password = PASSWORD_A;
            final var authorities = Authority.ALL;
            final var accountNonExpired = true;
            final var accountNonLocked = true;
            final var enabled = true;

            final var userA = new User(id, username, password, authorities,
                     accountNonExpired, accountNonLocked, true, enabled);
            final var userB = new User(id, username, password, authorities,
                     accountNonExpired, accountNonLocked, false, enabled);

            test(userA, userB);
         }

         @Test
         public void enabled() {
            final var id = ID_A;
            final var username = USERNAME_A;
            final var password = PASSWORD_A;
            final var authorities = Authority.ALL;
            final var accountNonExpired = true;
            final var accountNonLocked = true;
            final var credentialsNonExpired = true;

            final var userA = new User(id, username, password, authorities,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     true);
            final var userB = new User(id, username, password, authorities,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     false);

            test(userA, userB);
         }

         @Test
         public void password() {
            final var id = ID_A;
            final var username = USERNAME_A;
            final var authorities = Authority.ALL;
            final var accountNonExpired = true;
            final var accountNonLocked = true;
            final var credentialsNonExpired = true;
            final var enabled = true;

            final var userA = new User(id, username, PASSWORD_A, authorities,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     enabled);
            final var userB = new User(id, username, PASSWORD_B, authorities,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     enabled);

            test(userA, userB);
         }

         private void test(final User userA, final User userB) {
            assertInvariants(userA, userB);
            assertEquals(userA, userB);
         }

         @Test
         public void username() {
            final var id = ID_A;
            final var password = PASSWORD_A;
            final var authorities = Authority.ALL;
            final var accountNonExpired = true;
            final var accountNonLocked = true;
            final var credentialsNonExpired = true;
            final var enabled = true;

            final var userA = new User(id, USERNAME_A, password, authorities,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     enabled);
            final var userB = new User(id, USERNAME_B, password, authorities,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     enabled);

            test(userA, userB);
         }

      }// class

      @Nested
      public class TwoEquivalent {

         @Test
         public void a() {
            test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                     true);
         }

         @Test
         public void b() {
            test(ID_A, USERNAME_B, PASSWORD_B, Set.of(), true, true, false,
                     true);
         }

         @Test
         public void c() {
            final String password = null;
            test(ID_A, USERNAME_A, password, Set.of(), true, false, true, true);
         }

         @Test
         public void d() {
            test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, false, true, true,
                     true);
         }

         @Test
         public void e() {
            test(ID_B, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                     true);
         }

         private void test(final UUID id, final String username,
                  final String password, final Set<Authority> authorities,
                  final boolean accountNonExpired,
                  final boolean accountNonLocked,
                  final boolean credentialsNonExpired, final boolean enabled) {
            final var user1 = new User(id, username, password, authorities,
                     accountNonExpired, accountNonLocked, credentialsNonExpired,
                     enabled);
            final var user2 = new User(
                     new UUID(id.getMostSignificantBits(),
                              id.getLeastSignificantBits()),
                     new String(username),
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
         test(ID_A, USERNAME_A, PASSWORD_A, Set.of(), true, true, true, true);
      }

      @Test
      public void basic() {
         test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                  true);
      }

      @Test
      public void id() {
         test(ID_B, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                  true);
      }

      @Test
      public void notAccountNonExpired() {
         test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, false, true, true,
                  true);
      }

      @Test
      public void notAccountNonLocked() {
         test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, false, true,
                  true);
      }

      @Test
      public void notCredentialsNonExpired() {
         test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, false,
                  true);
      }

      @Test
      public void notEnabled() {
         test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                  false);
      }

      @Test
      public void nullPassword() {
         final String password = null;
         test(ID_A, USERNAME_A, password, Authority.ALL, true, true, true,
                  true);
      }

      @Test
      public void password() {
         test(ID_A, USERNAME_A, PASSWORD_B, Authority.ALL, true, true, true,
                  true);
      }

      private User test(final UUID id, final String username,
               final String password, final Set<Authority> authorities,
               final boolean accountNonExpired, final boolean accountNonLocked,
               final boolean credentialsNonExpired, final boolean enabled) {
         final var user = new User(id, username, password, authorities,
                  accountNonExpired, accountNonLocked, credentialsNonExpired,
                  enabled);

         assertInvariants(user);
         assertAll("Has the given attribute values",
                  () -> assertSame(id, user.getId(), "id"),
                  () -> assertSame(username, user.getUsername(), "username"),
                  () -> assertSame(password, user.getPassword(), "password"),
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
      public void twoIds() {
         final var username = USERNAME_A;
         final var password = PASSWORD_A;
         final var authorities = Authority.ALL;
         final var accountNonExpired = true;
         final var accountNonLocked = true;
         final var credentialsNonExpired = true;
         final var enabled = true;

         final var userA = new User(ID_A, username, password, authorities,
                  accountNonExpired, accountNonLocked, credentialsNonExpired,
                  enabled);
         final var userB = new User(ID_B, username, password, authorities,
                  accountNonExpired, accountNonLocked, credentialsNonExpired,
                  enabled);

         assertInvariants(userA, userB);
         assertNotEquals(userA, userB);
      }

      @Test
      public void username() {
         test(ID_A, USERNAME_B, PASSWORD_A, Authority.ALL, true, true, true,
                  true);
      }

   }// class

   @Nested
   public class CreateAdministrator {

      @Test
      public void a() {
         test(PASSWORD_A);
      }

      @Test
      public void b() {
         test(PASSWORD_B);
      }

      @Test
      public void nullPassword() {
         test(null);
      }

      private void test(final String password) {
         final var administrator = User.createAdministrator(password);

         assertNotNull(administrator, "Not null, returned");// guard
         assertInvariants(administrator);
         assertAll("Attributes",
                  () -> assertSame(User.ADMINISTRATOR_ID, administrator.getId(),
                           "ID"),
                  () -> assertSame(User.ADMINISTRATOR_USERNAME,
                           administrator.getUsername(), "username"),
                  () -> assertSame(password, administrator.getPassword(),
                           "password"),
                  () -> assertSame(Authority.ALL,
                           administrator.getAuthorities(), "authorities"),
                  () -> assertTrue(administrator.isAccountNonExpired(),
                           "accountNonExpired"),
                  () -> assertTrue(administrator.isAccountNonLocked(),
                           "accountNonLocked"),
                  () -> assertTrue(administrator.isCredentialsNonExpired(),
                           "credentialsNonExpired"),
                  () -> assertTrue(administrator.isEnabled(), "enabled"));
      }
   }// class

   @Nested
   public class Json {

      @Test
      public void authorities() {
         test(ID_A, USERNAME_A, PASSWORD_A, Set.of(), true, true, true, true);
      }

      @Test
      public void basic() {
         test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                  true);
      }

      @Test
      public void id() {
         test(ID_B, USERNAME_A, PASSWORD_B, Authority.ALL, true, true, true,
                  true);
      }

      @Test
      public void notAccountNonExpired() {
         test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, false, true, true,
                  true);
      }

      @Test
      public void notAccountNonLocked() {
         test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, false, true,
                  true);
      }

      @Test
      public void notCredentialsNonExpired() {
         test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, false,
                  true);
      }

      @Test
      public void notEnabled() {
         test(ID_A, USERNAME_A, PASSWORD_A, Authority.ALL, true, true, true,
                  false);
      }

      @Test
      public void nullPassword() {
         final String password = null;
         test(ID_A, USERNAME_A, password, Authority.ALL, true, true, true,
                  true);
      }

      @Test
      public void password() {
         test(ID_A, USERNAME_A, PASSWORD_B, Authority.ALL, true, true, true,
                  true);
      }

      private void test(final UUID id, final String username,
               final String password, final Set<Authority> authorities,
               final boolean accountNonExpired, final boolean accountNonLocked,
               final boolean credentialsNonExpired, final boolean enabled) {
         final var user = new User(id, username, password, authorities,
                  accountNonExpired, accountNonLocked, credentialsNonExpired,
                  enabled);

         final var deserialized = JsonTest.serializeAndDeserialize(user);

         assertInvariants(user);
         assertInvariants(user, deserialized);
         assertAll("Deserialised attributes",
                  () -> assertEquals(id, user.getId(), "id"),
                  () -> assertEquals(username, user.getUsername(), "username"),
                  () -> assertEquals(password, user.getPassword(), "password"),
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
      }

      @Test
      public void username() {
         test(ID_A, USERNAME_B, PASSWORD_B, Authority.ALL, true, true, true,
                  true);
      }
   }// class

   private static final UUID ID_A = UUID.randomUUID();

   private static final UUID ID_B = UUID.randomUUID();

   private static final String USERNAME_A = "John";

   private static final String USERNAME_B = "Alan";

   private static final String PASSWORD_A = "letmein";

   private static final String PASSWORD_B = "password123";

   public static void assertInvariants(final User user) {
      BasicUserDetailsTest.assertInvariants(user);// inherited
      assertNotNull(user.getId(), "Not null, id");
   }

   public static void assertInvariants(final User user1, final User user2) {
      BasicUserDetailsTest.assertInvariants(user1, user2);// inherited
   }
}
