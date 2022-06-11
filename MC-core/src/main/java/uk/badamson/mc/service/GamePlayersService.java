package uk.badamson.mc.service;
/*
 * © Copyright Benedict Adamson 2020-22.
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
import uk.badamson.mc.*;
import uk.badamson.mc.Game.Identifier;
import uk.badamson.mc.repository.CurrentUserGameRepository;
import uk.badamson.mc.repository.GamePlayersRepository;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.*;

import static java.util.stream.Collectors.toUnmodifiableMap;

public final class GamePlayersService {

    private static final Map<UUID, UUID> NO_USERS = Map.of();
    private final GamePlayersRepository gamePlayersRepository;
    private final CurrentUserGameRepository currentUserGameRepository;
    private final GameService gameService;
    private final UserService userService;

    public GamePlayersService(
            @Nonnull final GamePlayersRepository gamePlayersRepository,
            @Nonnull final CurrentUserGameRepository currentUserGameRepository,
            @Nonnull final GameService gameService,
            @Nonnull final UserService userService) {
        this.gamePlayersRepository = Objects.requireNonNull(gamePlayersRepository,
                "gamePlayersRepository");
        this.currentUserGameRepository = Objects.requireNonNull(
                currentUserGameRepository, "currentUserGameRepository");
        this.gameService = Objects.requireNonNull(gameService, "gameService");
        this.userService = Objects.requireNonNull(userService, "userService");
    }

    private static GamePlayers createDefault(final Identifier id) {
        return new GamePlayers(id, true, NO_USERS);
    }

    private static GamePlayers filterForUser(
            @Nonnull final GamePlayers fullInformation, @Nonnull final UUID user) {
        final var allUsers = fullInformation.getUsers();
        final Map<UUID, UUID> filteredUsers = allUsers.entrySet().stream()
                .filter(entry -> user.equals(entry.getValue()))
                .collect(toUnmodifiableMap(Map.Entry::getKey,
                        Map.Entry::getValue));
        if (allUsers.size() == filteredUsers.size()) {
            return fullInformation;
        } else {
            return new GamePlayers(fullInformation.getGame(), false, filteredUsers);
        }
    }

    /**
     * <p>
     * Indicate that a game is not {@linkplain GamePlayers#isRecruiting()
     * recruiting} players (any longer).
     * </p>
     * <p>
     * This mutator is idempotent: the mutator does not have the precondition
     * that the game is recruiting.
     * </p>
     *
     * @return The mutated game players' information.
     * @throws NoSuchElementException If the associated {@linkplain #getGameService() game service}
     *                                indicates that a {@linkplain GameService#getGame(Identifier)
     *                                game} with the given ID does not exist.
     */
    @Nonnull
    public GamePlayers endRecruitment(@Nonnull final Game.Identifier id)
            throws NoSuchElementException {
        final var gamePlayersOptional = get(id);
        if (gamePlayersOptional.isEmpty()) {
            throw new NoSuchElementException();
        }
        final var gamePlayers = gamePlayersOptional.get();
        gamePlayers.endRecruitment();
        gamePlayersRepository.save(id, gamePlayers);
        return gamePlayers;
    }

    private Optional<GamePlayers> get(@Nonnull final Identifier id) {
        Objects.requireNonNull(id, "id");
        var result = Optional.<GamePlayers>empty();
        if (gameService.getGame(id).isPresent()) {
            result = gamePlayersRepository.find(id);
            if (result.isEmpty()) {
                result = Optional.of(createDefault(id));
            }
        }
        return result;
    }

    @Nonnull
    private Optional<Game.Identifier> getCurrent(@Nonnull final UUID user) {
        return currentUserGameRepository.find(user).map(UserGameAssociation::getGame);
    }

    /**
     * <p>
     * The {@linkplain Game#getIdentifier() unique ID} of the <i>current game</i>
     * of a user who has a given {@linkplain User#getId() unique ID}.
     * </p>
     */
    @Nonnull
    public Optional<Identifier> getCurrentGameOfUser(
            @Nonnull final UUID userId) {
        final var user = getUser(userId);
        if (user.isPresent()) {
            return getCurrent(userId);
        } else {
            return Optional.empty();
        }
    }

    @Nonnull
    public CurrentUserGameRepository getCurrentUserGameRepository() {
        return currentUserGameRepository;
    }

    /**
     * <p>
     * Retrieve complete information about the game players for the game that has
     * a given unique ID.
     * </p>
     */
    @Nonnull
    public Optional<GamePlayers> getGamePlayersAsGameManager(
            @Nonnull final Game.Identifier id) {
        return get(id);
    }

    /**
     * <p>
     * Retrieve information about the game players for the game that has a given
     * unique ID, suitable for a non game manager.
     * </p>
     * <ul>
     * <li>The collection of {@linkplain GamePlayers#getUsers() players} is
     * either empty or contains only the requesting user: non game managers may
     * not see the complete list of players of a game, but may see that they are
     * a player of a game.</li>
     * </ul>
     */
    @Nonnull
    public Optional<GamePlayers> getGamePlayersAsNonGameManager(
            @Nonnull final Game.Identifier gameId, @Nonnull final UUID user) {
        Objects.requireNonNull(user, "user");
        return get(gameId).map(g -> filterForUser(g, user));
    }

    @Nonnull
    public GamePlayersRepository getGamePlayersRepository() {
        return gamePlayersRepository;
    }

    @Nonnull
    public GameService getGameService() {
        return gameService;
    }

    private Optional<User> getUser(final UUID userId) {
        return getUserService().getUser(userId);
    }

    private UserJoinsGameState getUserJoinsGameState(final UUID userId,
                                                     final Identifier gameId)
            throws NoSuchElementException, UserAlreadyPlayingException,
            IllegalGameStateException, SecurityException {
        final var userOptional = getUser(userId);
        if (userOptional.isEmpty()) {
            throw new NoSuchElementException("user");
        }
        final var user = userOptional.get();
        final var gamePlayersOptional = get(gameId);
        if (gamePlayersOptional.isEmpty()) {
            throw new NoSuchElementException("game");
        }
        final var gamePlayers = gamePlayersOptional.get();
        final var current = getCurrent(userId);

        if (!user.getAuthorities().contains(Authority.ROLE_PLAYER)) {
            throw new SecurityException("User does not have the player role");
        }

        final boolean alreadyJoined;
        final UUID character;
        final boolean endRecruitment;
        if (current.isPresent() && !gameId.equals(current.get())) {
            throw new UserAlreadyPlayingException();
        } else if (current.isPresent()) {// && gameId.equals(current.get())
            alreadyJoined = true;
            final var characterEntryOptional = gamePlayers.getUsers().entrySet().stream()
                    .filter(entry -> userId.equals(entry.getValue())).findAny();
            if (characterEntryOptional.isEmpty()) {
                throw new NoSuchElementException("character");
            }
            character = characterEntryOptional.get().getKey();
            endRecruitment = false;
        } else {
            if (!gamePlayers.isRecruiting()) {
                throw new IllegalGameStateException("Game is not recruiting");
            }
            alreadyJoined = false;
            final var scenarioId = gamePlayers.getGame().getScenario();
            final var scenarioOptional = gameService.getScenarioService()
                    .getScenario(scenarioId);
            if (scenarioOptional.isEmpty()) {
                throw new NoSuchElementException("scenario");
            }
            final var scenario = scenarioOptional.get();
            final var characters = scenario.getCharacters();
            final var playedCharacters = gamePlayers.getUsers().keySet();
            final var characterOptional = characters.stream().sequential()
                    .map(NamedUUID::getId)
                    .filter(c -> !playedCharacters.contains(c)).findFirst();
            if (characterOptional.isEmpty()) {
                throw new NoSuchElementException("character");
            }
            character = characterOptional.get();
            endRecruitment = characters.size() - 1 <= playedCharacters.size();
        }

        return new UserJoinsGameState(gamePlayers, character, alreadyJoined,
                endRecruitment);
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "reference semantics")
    @Nonnull
    public UserService getUserService() {
        return userService;
    }

    /**
     * <p>
     * Whether the {@link #userJoinsGame(UUID, Identifier)} operation would
     * succeed
     * </p>
     * <p>
     * That is, whether all the following are true.
     * </p>
     * <ul>
     * <li>The{@code user} is the ID of a known user, according to the associated
     * {@linkplain #getUserService() user service}.</li>
     * <li>The {@code game} is the ID of a known game, according to the
     * associated {@linkplain #getGameService() game service}.</li>
     * <li>The {@code user} is not already playing a different game.</li>
     * <li>The {@code user} {@linkplain User#getAuthorities() has}
     * {@linkplain Authority#ROLE_PLAYER permission} to play games. Note that the
     * given user need not be the current user.</li>
     * <li>The user has already joined the game <em>or</em> the game is
     * {@linkplain GamePlayers#isRecruiting() recruiting} players.</li>
     * </ul>
     */
    public boolean mayUserJoinGame(@Nonnull final UUID user, @Nonnull final Identifier game) {
        try {
            getUserJoinsGameState(user, game);
        } catch (UserAlreadyPlayingException | IllegalGameStateException
                 | SecurityException | NoSuchElementException e) {
            return false;
        }
        return true;
    }

    /**
     * <p>
     * Have a {@linkplain User user} become one of the
     * {@linkplain GamePlayers#getUsers() players of a game}.
     * </p>
     *
     * @throws NoSuchElementException      <ul>
     *                                                <li>If {@code user} is not the ID of a known user, according to
     *                                                the associated {@linkplain #getUserService() user
     *                                                service}.</li>
     *                                                <li>If {@code game} is not the ID of a game, according to the
     *                                                associated {@linkplain #getGameService() game service}.</li>
     *                                                </ul>
     * @throws UserAlreadyPlayingException If the {@code user} is already playing a different game.
     * @throws SecurityException           If the {@code user} does not {@linkplain User#getAuthorities()
     *                                     have} {@linkplain Authority#ROLE_PLAYER permission} to play
     *                                     games. Note that the given user need not be the current user.
     * @throws IllegalGameStateException   <ul>
     *                                                <li>If the game is not {@linkplain GamePlayers#isRecruiting()
     *                                                recruiting} players.</li>
     *                                                <li>If the game has no characters free.</li>
     *                                                </ul>
     */
    public void userJoinsGame(@Nonnull final UUID userId,
                              @Nonnull final Identifier gameId)
            throws NoSuchElementException, UserAlreadyPlayingException,
            IllegalGameStateException, SecurityException {
        // read and check:
        final var state = getUserJoinsGameState(userId, gameId);
        if (state.alreadyJoined) {
            // optimisation
            return;
        }

        // modify:
        final var association = new UserGameAssociation(userId, gameId);
        state.gamePlayers.addUser(state.character, userId);
        if (state.endRecruitment) {
            state.gamePlayers.endRecruitment();
        }

        // write:
        currentUserGameRepository.save(userId, association);
        gamePlayersRepository.save(gameId, state.gamePlayers);
    }

    @Immutable
    private static final class UserJoinsGameState {
        final GamePlayers gamePlayers;
        final UUID character;
        final boolean alreadyJoined;
        final boolean endRecruitment;

        UserJoinsGameState(final GamePlayers gamePlayers,
                           final UUID firstUnplayedCharacter, final boolean alreadyJoined,
                           final boolean endRecruitment) {
            this.gamePlayers = gamePlayers;
            this.character = firstUnplayedCharacter;
            this.alreadyJoined = alreadyJoined;
            this.endRecruitment = endRecruitment;
        }

    }

}
