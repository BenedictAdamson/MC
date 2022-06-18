package uk.badamson.mc.repository;
/*
 * © Copyright Benedict Adamson 2022.
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

import uk.badamson.mc.Game;
import uk.badamson.mc.GamePlayers;
import uk.badamson.mc.User;
import uk.badamson.mc.UserGameAssociation;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface MCRepository {

    void saveGame(@Nonnull Game.Identifier id, @Nonnull Game game);

    @Nonnull
    Optional<Game> findGame(@Nonnull Game.Identifier id);

    @Nonnull
    Stream<Game> findAllGames();

    void saveGamePlayers(@Nonnull Game.Identifier id, @Nonnull GamePlayers gamePlayers);

    @Nonnull
    Optional<GamePlayers> findGamePlayers(@Nonnull Game.Identifier id);

    @Nonnull
    Optional<UserGameAssociation> findCurrentUserGame(@Nonnull UUID userId);

    void saveCurrentUserGame(@Nonnull UUID userId, @Nonnull UserGameAssociation entity);

    @Nonnull
    Optional<User> findUserByUsername(@Nonnull String username);

    @Nonnull
    Optional<User> findUser(@Nonnull UUID id);

    @Nonnull
    Stream<User> findAllUsers();

    void saveUser(@Nonnull UUID id, @Nonnull User user);
}
