package uk.badamson.mc.service;
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
import uk.badamson.mc.BasicUserDetails;
import uk.badamson.mc.User;
import uk.badamson.mc.repository.MCRepository;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UserService {

    @Nonnull
    private final PasswordEncoder passwordEncoder;
    @Nonnull
    private final User administrator;
    @Nonnull
    private final MCRepository repository;

    public UserService(@Nonnull final PasswordEncoder passwordEncoder,
                       @Nonnull final String administratorPassword,
                       @Nonnull final MCRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
        Objects.requireNonNull(administratorPassword, "administratorPassword");
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder,
                "passwordEncoder");
        administrator = User.createAdministrator(
                passwordEncoder.encode(administratorPassword));
    }

    /**
     * <p>
     * Create a new user, having given {@linkplain BasicUserDetails user
     * details}, and add them to the {@linkplain #getUsers() list of users}.
     * </p>
     *
     * @param userDetails The details of the user to add, with an unencrypted
     *                    {@linkplain BasicUserDetails#getPassword() password}.
     * @throws IllegalArgumentException If the {@linkplain BasicUserDetails#getUsername() username} of
     *                                  {@code userDetails} indicates it is the
     *                                  {@linkplain User#ADMINISTRATOR_USERNAME administrator}.
     * @throws UserExistsException      If the {@linkplain BasicUserDetails#getUsername() username} of
     *                                  {@code userDetails} is already the username of a user, and is
     *                                  not the administrator.
     */
    @Nonnull
    public User add(@Nonnull final BasicUserDetails userDetails) {
        Objects.requireNonNull(userDetails, "userDetails");
        try(final var context = repository.openContext()) {
            if (BasicUserDetails.ADMINISTRATOR_USERNAME
                    .equals(userDetails.getUsername())) {
                throw new IllegalArgumentException("User is administrator");
            } else if (context.findUserByUsername(userDetails.getUsername()).isPresent()) {// read
                throw new UserExistsException();
            }

            final var encryptedUserDetails = new BasicUserDetails(userDetails);
            encryptedUserDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            final var id = UUID.randomUUID();
            final var user = new User(id, encryptedUserDetails);
            context.saveUser(id, user);// write
            return user;
        }
    }

    @Nonnull
    public final PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    @Nonnull
    public Optional<User> getUser(@Nonnull final UUID id) {
        try(final var context = repository.openContext()) {
            return getUser(context, id);
        }
    }

    @Nonnull
    final Optional<User> getUser(@Nonnull MCRepository.Context context, @Nonnull final UUID id) {
        Objects.requireNonNull(id, "id");
        if (User.ADMINISTRATOR_ID.equals(id)) {
            return Optional.of(administrator);
        } else {
            return context.findUser(id);
        }
    }

    @Nonnull
    public Stream<User> getUsers() {
        try(final var context = repository.openContext()) {
            final var userStream = context.findAllUsers();
            final var adminUses = Stream.of(administrator);
            final var normalUsers = StreamSupport
                    .stream(userStream.spliterator(), false)
                    .filter(u -> !u.getUsername()
                            .equals(BasicUserDetails.ADMINISTRATOR_USERNAME));
            return Stream.concat(adminUses, normalUsers);
        }
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "reference semantics")
    @Nonnull
    public Optional<User> getUserByUsername(@Nonnull final String username) {
        Objects.requireNonNull(username, "username");
        if (BasicUserDetails.ADMINISTRATOR_USERNAME.equals(username)) {
            return Optional.of(administrator);
        } else {
            try(final var context = repository.openContext()) {
                return context.findUserByUsername(username);
            }
        }
    }

}
