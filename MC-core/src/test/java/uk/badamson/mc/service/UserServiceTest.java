package uk.badamson.mc.service;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.badamson.dbc.assertions.ObjectVerifier;
import uk.badamson.mc.Authority;
import uk.badamson.mc.BasicUserDetails;
import uk.badamson.mc.User;
import uk.badamson.mc.repository.MCRepository;
import uk.badamson.mc.repository.MCRepositoryTest;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private static final String USERNAME_C = "John";
    private static final String USERNAME_B = "Alan";
    private static final String USERNAME_A = "John";
    private static final String PASSWORD_A = "hello";
    private static final String PASSWORD_B = "password123";
    private static final String PASSWORD_C = "secret";
    private static final UUID USER_ID_A = UUID.randomUUID();
    private MCRepository repositoryA;
    private MCRepository repositoryB;
    private User userA;
    private User userB;
    private User userC;

    private static User add(final UserService service,
                            final BasicUserDetails userDetails) {
        final var user = service.add(userDetails);

        assertInvariants(service);
        assertThat(user, notNullValue());
        assertAll("Attributes",
                () -> assertEquals(userDetails.getAuthorities(),
                        user.getAuthorities(), "authorities"),
                () -> assertEquals(userDetails.getUsername(), user.getUsername(),
                        "username"),
                () -> assertTrue(
                        service.getPasswordEncoder().matches(
                                userDetails.getPassword(), user.getPassword()),
                        "password (encrypted)"),
                () -> assertEquals(userDetails.isAccountNonExpired(),
                        user.isAccountNonExpired(), "accountNonExpired"),
                () -> assertEquals(userDetails.isAccountNonLocked(),
                        user.isAccountNonLocked(), "accountNonLocked"),
                () -> assertEquals(userDetails.isCredentialsNonExpired(),
                        user.isCredentialsNonExpired(),
                        "credentialsNonExpired"),
                () -> assertEquals(userDetails.isEnabled(), user.isEnabled(),
                        "enabled"));
        assertThat("Can subsequently load the user using the username",
                service.getUserByUsername(userDetails.getUsername()).isPresent());

        return user;
    }

    public static void assertInvariants(final UserService service) {
        ObjectVerifier.assertInvariants(service);// inherited

        assertNotNull(service.getPasswordEncoder(), "Not null, passwordEncoder");
    }

    private static void constructor(
            final PasswordEncoder passwordEncoder,
            final String administratorPassword,
            final MCRepository repository) {
        final var service = new UserService(passwordEncoder, administratorPassword, repository);

        assertInvariants(service);
        assertSame(passwordEncoder, service.getPasswordEncoder(),
                "The password encoder of this service is the given password encoder.");
        getUsers(service);
        final Optional<User> administratorOptional = getUserByUsername(service, BasicUserDetails.ADMINISTRATOR_USERNAME);
        assertThat("administrator", administratorOptional.isPresent());
        final var encryptedAdminPassword = administratorOptional.get().getPassword();
        assertTrue(
                passwordEncoder.matches(administratorPassword,
                        encryptedAdminPassword),
                "The password of the administrator user details found through this service is equal to the given administrator password encrypted by the given password encoder.");

    }

    private static Optional<User> getUser(final UserService service,
                                          final UUID id) {
        final var result = service.getUser(id);

        assertInvariants(service);
        assertTrue(result.isEmpty() || id.equals(result.get().getId()),
                "Returns either an empty value, "
                        + "or a value for which the identifier is equivalent to the given ID");
        return result;
    }

    private static Stream<User> getUsers(final UserService service) {
        final var users = service.getUsers();

        assertInvariants(service);
        assertNotNull(users, "Always returns a (non null) stream.");// guard
        final var usersList = users.toList();
        assertThat("The sequence of users has no null elements",
                usersList.stream().noneMatch(Objects::isNull));// guard
        final var userNames = usersList.stream().map(BasicUserDetails::getUsername)
                .collect(toUnmodifiableSet());
        assertThat("The list of users always has an administrator.", userNames,
                hasItem(BasicUserDetails.ADMINISTRATOR_USERNAME));
        assertEquals(userNames.size(), usersList.size(),
                "Does not contain users with duplicate usernames.");

        return usersList.stream();
    }

    private static Optional<User> getUserByUsername(final UserService service,
                                                    final String username) {
        final var user = service.getUserByUsername(username);

        assertInvariants(service);
        assertThat(user, notNullValue());

        return user;
    }

    @Test
    public void administratorInRepository() {
        final var repository = repositoryA;
        final var service = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repository);
        final var administrator = User.createAdministrator(PASSWORD_B);
        repository.saveUser(administrator.getId(), administrator);

        getUsers(service);
    }

    @BeforeEach
    public void setUpRepositories() {
        repositoryA = new MCRepositoryTest.Fake();
        repositoryB = new MCRepositoryTest.Fake();
    }

    @BeforeEach
    public void setUpUsers() {
        userA = new User(UUID.randomUUID(), USERNAME_A, PASSWORD_A, Set.of(),
                true, true, true, true);
        userB = new User(UUID.randomUUID(), USERNAME_B, PASSWORD_B, Authority.ALL,
                true, true, true, true);
        userC = new User(UUID.randomUUID(), USERNAME_C, PASSWORD_C, Set.of(),
                true, true, true, true);
    }

    @Nested
    public class Add {

        @Nested
        public class AlreadyExists {

            @Test
            public void a() {
                test(userA);
            }

            @Test
            public void b() {
                test(userB);
            }

            private void test(final User user) {
                final var service = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);
                service.add(user);
                assertThrows(UserExistsException.class, () -> add(service, user));
            }

        }

        @Nested
        public class One {

            @Test
            public void accountNonExpired() {
                final var accountNonExpired = false;
                final var accountNonLocked = true;
                final var credentialsNonExpired = true;
                final var enabled = true;
                final var user = new BasicUserDetails(USERNAME_A, PASSWORD_A,
                        Authority.ALL, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);

                test(user);
            }

            @Test
            public void accountNonLocked() {
                final var accountNonExpired = true;
                final var accountNonLocked = false;
                final var credentialsNonExpired = true;
                final var enabled = true;
                final var user = new BasicUserDetails(USERNAME_A, PASSWORD_A,
                        Authority.ALL, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);

                test(user);
            }

            @Test
            public void authorities() {
                final var accountNonExpired = true;
                final var accountNonLocked = true;
                final var credentialsNonExpired = true;
                final var enabled = true;
                final var user = new BasicUserDetails(USERNAME_A, PASSWORD_A,
                        Set.of(Authority.ROLE_PLAYER), accountNonExpired,
                        accountNonLocked, credentialsNonExpired, enabled);

                test(user);
            }

            @Test
            public void base() {
                final var accountNonExpired = true;
                final var accountNonLocked = true;
                final var credentialsNonExpired = true;
                final var enabled = true;
                final var user = new BasicUserDetails(USERNAME_A, PASSWORD_A,
                        Authority.ALL, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);

                test(user);
            }

            @Test
            public void credentialsNonExpired() {
                final var accountNonExpired = true;
                final var accountNonLocked = true;
                final var credentialsNonExpired = false;
                final var enabled = true;
                final var user = new BasicUserDetails(USERNAME_A, PASSWORD_A,
                        Authority.ALL, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);

                test(user);
            }

            @Test
            public void enabled() {
                final var accountNonExpired = true;
                final var accountNonLocked = true;
                final var credentialsNonExpired = true;
                final var enabled = false;
                final var user = new BasicUserDetails(USERNAME_A, PASSWORD_A,
                        Authority.ALL, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);

                test(user);
            }

            @Test
            public void password() {
                final var accountNonExpired = true;
                final var accountNonLocked = true;
                final var credentialsNonExpired = true;
                final var enabled = true;
                final var user = new BasicUserDetails(USERNAME_A, PASSWORD_B,
                        Authority.ALL, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);

                test(user);
            }

            private void test(final BasicUserDetails userDetails) {
                final var service = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);

                final var user = add(service, userDetails);

                final var users = service.getUsers();
                final Optional<User> userDetailsAfterOptional = service.getUserByUsername(userDetails.getUsername());
                assertThat("Can subsequently retrieve the user by username", userDetailsAfterOptional.isPresent());
                final var userDetailsAfter = userDetailsAfterOptional.get();
                final var usersList = users.collect(toList());
                assertThat(
                        "A subsequently retrieved sequence of the users will include a user equivalent to the returned user.",
                        usersList, hasItem(user));
                assertAll(
                        "Subsequently finding user details using the username of the given user will retrieve user details "
                                + "equivalent to the user details of the given user.",
                        () -> assertThat("authorities",
                                userDetailsAfter.getAuthorities(),
                                is(userDetails.getAuthorities())),
                        () -> assertThat("username", userDetailsAfter.getUsername(),
                                is(userDetails.getUsername())),
                        () -> assertThat("accountNonExpired",
                                userDetailsAfter.isAccountNonExpired(),
                                is(userDetails.isAccountNonExpired())),
                        () -> assertThat("accountNonLocked",
                                userDetailsAfter.isAccountNonLocked(),
                                is(userDetails.isAccountNonLocked())),
                        () -> assertThat("credentialsNonExpired",
                                userDetailsAfter.isCredentialsNonExpired(),
                                is(userDetails.isCredentialsNonExpired())),
                        () -> assertThat("enabled", userDetailsAfter.isEnabled(),
                                is(userDetails.isEnabled())),
                        () -> assertTrue(
                                service.getPasswordEncoder().matches(
                                        userDetails.getPassword(),
                                        userDetailsAfter.getPassword()),
                                "Recorded password has been encrypted using the password encoder of this service."));

                assertThat("Added a user", service.getUsers().count(), is(2L));
            }

            @Test
            public void username() {
                final var accountNonExpired = true;
                final var accountNonLocked = true;
                final var credentialsNonExpired = true;
                final var enabled = true;
                final var user = new BasicUserDetails(USERNAME_B, PASSWORD_A,
                        Authority.ALL, accountNonExpired, accountNonLocked,
                        credentialsNonExpired, enabled);

                test(user);
            }

            @Test
            public void usesPasswordEncoder() {
                test(userA);
            }
        }

        @Nested
        public class Two {

            @Test
            public void a() {
                test(userA, userB);
            }

            @Test
            public void b() {
                test(userB, userC);
            }

            private void test(final BasicUserDetails userDetails1, final BasicUserDetails userDetails2) {
                final var service = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);
                final var user1 = add(service, userDetails1);
                final var user2 = add(service, userDetails2);

                final var users = service.getUsers();
                final var usersList = users.collect(toList());
                assertThat(
                        "A subsequently retrieved sequence of the users will include a user equivalent to the returned user [1].",
                        usersList, hasItem(user1));
                assertThat(
                        "A subsequently retrieved sequence of the users will include a user equivalent to the returned user [2].",
                        usersList, hasItem(user2));
                assertThat("Added user", service.getUsers().count(), is(3L));
            }
        }
    }

    @Nested
    public class Constructor {

        @Test
        public void a() {
            constructor(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);
        }

        @Test
        public void b() {
            constructor(PasswordEncoderTest.FAKE, PASSWORD_B, repositoryB);
        }
    }

    @Nested
    public class GetUser {

        @Test
        public void absent() {
            final var service = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);

            final var result = getUser(service, USER_ID_A);

            assertTrue(result.isEmpty(), "empty");
        }

        @Test
        public void administrator() {
            final var service = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);

            final var result = getUser(service, User.ADMINISTRATOR_ID);

            assertThat("Can always get user information for the administrator.",
                    result.isPresent());// guard
            final var administrator = result.get();
            assertThat("Administrator has full authority",
                    administrator.getAuthorities(), is(Authority.ALL));
        }

        @Test
        public void present() {
            final var userDetails = new BasicUserDetails(USERNAME_A, PASSWORD_A,
                    Authority.ALL, true, true, true, true);
            final var service = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);
            final var user = service.add(userDetails);

            final var result = getUser(service, user.getId());

            assertTrue(result.isPresent(), "present");
        }
    }

    @Nested
    public class GetUserByUserName {

        @Test
        public void absent() {
            final var service = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);

            final Optional<User> user = getUserByUsername(service, USERNAME_A);

            assertThat("user", user.isEmpty());
        }

        @Test
        public void administrator() {
            final var service = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);

            final Optional<User> user = getUserByUsername(service, BasicUserDetails.ADMINISTRATOR_USERNAME);

            assertThat("Always have an administrator.", user.isPresent());
            assertThat("The administrator has a complete set of authorities.",
                    user.get().getAuthorities(), is(Authority.ALL));
        }

        @Test
        public void present() {
            final var userDetails = new BasicUserDetails(USERNAME_A, PASSWORD_A,
                    Authority.ALL, true, true, true, true);
            final var service = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);
            final var user = service.add(userDetails);

            final Optional<User> result = getUserByUsername(service, user.getUsername());

            assertThat("present", result.isPresent());
        }

    }

    @Nested
    public class Scenario {

        private UserService service;
        private Collection<User> users;

        @Test
        public void get_users_of_fresh_instance() {
            given_a_fresh_instance_of_MC();
            when_getting_the_users();
            assertAll(this::then_the_list_of_users_has_one_user,
                    this::then_the_list_of_users_includes_a_user_named_Administrator);
        }

        private void given_a_fresh_instance_of_MC() {
            service = new UserService(PasswordEncoderTest.FAKE, PASSWORD_A, repositoryA);
        }

        private void then_the_list_of_users_has_one_user() {
            assertThat(users.size(), is(1));
        }

        private void then_the_list_of_users_includes_a_user_named_Administrator() {
            assertThat(users, not(hasItem((User) null)));// guard
            final var usernames = users.stream().map(BasicUserDetails::getUsername)
                    .collect(toUnmodifiableSet());
            assertThat("the list of users includes a user named \"Administrator\"",
                    usernames, hasItem(BasicUserDetails.ADMINISTRATOR_USERNAME));
        }

        private void when_getting_the_users() {
            users = getUsers(service).collect(toList());
        }
    }

}
