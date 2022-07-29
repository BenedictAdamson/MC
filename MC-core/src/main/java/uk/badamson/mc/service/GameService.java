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

import uk.badamson.mc.*;
import uk.badamson.mc.GameIdentifier;
import uk.badamson.mc.repository.MCRepository;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toUnmodifiableMap;

public final class GameService {

    private static final Map<UUID, UUID> NO_USERS = Map.of();

    private final Clock clock;

    private final ScenarioService scenarioService;

    private final UserService userService;

    private final MCRepository repository;

    public GameService(@Nonnull final Clock clock,
                       @Nonnull final ScenarioService scenarioService,
                       @Nonnull final UserService userService,
                       @Nonnull MCRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.scenarioService = Objects.requireNonNull(scenarioService, "scenarioService");
        this.userService = Objects.requireNonNull(userService, "userService");
    }

    private static FindGameResult filterForUser(
            @Nonnull final FindGameResult fullInformation,
            @Nonnull final UUID user) {
        final var game = fullInformation.game();
        final var allUsers = game.getUsers();
        final Map<UUID, UUID> filteredUsers = allUsers.entrySet().stream()
                .filter(entry -> Objects.equals(user, entry.getValue()))
                .collect(toUnmodifiableMap(Map.Entry::getKey,
                        Map.Entry::getValue));
        if (allUsers.size() == filteredUsers.size()) {
            return fullInformation;
        } else {
            final var filteredGame = new Game(
                    game.getScenario(),
                    game.getCreated(),
                    game.getRunState(),
                    game.isRecruiting(),
                    filteredUsers);
            return new FindGameResult(filteredGame, fullInformation.scenarioId());
        }
    }

    /**
     * <p>
     * Create a new game for a given scenario.
     * </p>
     *
     * @throws NoSuchElementException If {@code scenario} is not the ID of a recognised scenario.
     *                                That is, if {@code scenario} is not a known scenario ID.
     */
    @Nonnull
    public IdentifiedValue<GameIdentifier, Game> create(@Nonnull final UUID scenario) throws NoSuchElementException {
        Objects.requireNonNull(scenario);
        try (var context = repository.openContext()) {
            requireKnownScenario(context, scenario);
            final var created = getNow();
            final var identifier = new GameIdentifier(scenario, created);
            final var game = new Game(scenario, created, Game.RunState.WAITING_TO_START, true, NO_USERS);
            context.addGame(identifier, game);
            return new IdentifiedValue<>(identifier, game);
        }
    }

    @Nonnull
    public Clock getClock() {
        return clock;
    }

    /**
     * <p>
     * The creation times of the games that are for a given
     * scenario.
     * </p>
     * <p>
     * The given {@code scenario} ID could be combined with the returned creation
     * times to create the identifiers of the games for the given scenario.
     * </p>
     *
     * @throws NoSuchElementException If {@code scenario} is not the ID of a recognised scenario.
     *                                That is, if {@code scenario} is a recognised scenario ID.
     */
    @Nonnull
    public Set<Instant> getCreationTimesOfGamesOfScenario(@Nonnull final UUID scenario)
            throws NoSuchElementException {
        Objects.requireNonNull(scenario);
        try (var context = repository.openContext()) {
            requireKnownScenario(context, scenario);// read-and-check
            return getGameIdentifiers(context).stream()// read
                    .filter(id -> scenario.equals(id.getScenario()))
                    .map(GameIdentifier::getCreated)
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

    @Nonnull
    public Iterable<GameIdentifier> getGameIdentifiers() {
        try (var context = repository.openContext()) {
            return getGameIdentifiers(context);
        }
    }

    @Nonnull
    Set<GameIdentifier> getGameIdentifiers(@Nonnull MCRepository.Context context) {
        final Set<GameIdentifier> result = new HashSet<>();
        for (var entry : context.findAllGames()) {
            result.add(entry.getKey());
        }
        return result;
    }

    @Nonnull
    public Instant getNow() {
        return clock.instant().truncatedTo(ChronoUnit.MILLIS);
    }

    private void requireKnownScenario(@Nonnull MCRepository.Context context, final UUID scenario)
            throws NoSuchElementException {
        Objects.requireNonNull(scenario, "scenario");
        if (scenarioService.getScenarioIdentifiers(context)
                .noneMatch(scenario::equals)) {
            throw new NoSuchElementException("unknown scenario");
        }
    }

    @Nonnull
    public Game startGame(@Nonnull final GameIdentifier id)
            throws NoSuchElementException, IllegalGameStateException {
        Objects.requireNonNull(id);
        try (var context = repository.openContext()) {
            Optional<FindGameResult> gameOptional = context.findGame(id);
            if (gameOptional.isEmpty()) {
                throw new NoSuchElementException("game");
            }
            var game = gameOptional.get().game();// read
            switch (game.getRunState()) {
                case WAITING_TO_START:
                    game.setRunState(Game.RunState.RUNNING);
                    context.updateGame(game);// write
                    return game;
                case RUNNING:
                    // do nothing
                    return new Game(game);
                case STOPPED:
                    throw new IllegalGameStateException("Game stopped");
                default:// never happens
                    throw new AssertionError("Valid game state");
            }
        }
    }

    public void stopGame(@Nonnull final GameIdentifier id)
            throws NoSuchElementException {
        try (var context = repository.openContext()) {
            Optional<FindGameResult> gameOptional = context.findGame(id);
            if (gameOptional.isEmpty()) {
                throw new NoSuchElementException("game");
            }
            var game = gameOptional.get().game();// read
            switch (game.getRunState()) {
                case WAITING_TO_START, RUNNING -> {
                    game.setRunState(Game.RunState.STOPPED);
                    context.updateGame(game);
                    // write
                }
                case STOPPED -> // do nothing
                        new Game(game);
                default ->// never happens
                        throw new AssertionError("Valid game state");
            }
        }
    }

    /**
     * <p>
     * Indicate that a game is not {@linkplain Game#isRecruiting()
     * recruiting} players (any longer).
     * </p>
     * <p>
     * This mutator is idempotent: the mutator does not have the precondition
     * that the game is recruiting.
     * </p>
     *
     * @return The mutated game players' information.
     * @throws NoSuchElementException If a game with the given ID does not exist.
     */
    @Nonnull
    public FindGameResult endRecruitment(@Nonnull final GameIdentifier id)
            throws NoSuchElementException {
        try (var context = repository.openContext()) {
            final var resultOptional = context.findGame(id);
            if (resultOptional.isEmpty()) {
                throw new NoSuchElementException();
            }
            final var result = resultOptional.get();
            final var game = result.game();
            game.endRecruitment();
            context.updateGame(game);
            return result;
        }
    }

    @Nonnull
    private Optional<GameIdentifier> getCurrent(@Nonnull MCRepository.Context context, @Nonnull final UUID user) {
        return context.findCurrentUserGame(user).map(UserGameAssociation::getGame);
    }

    /**
     * <p>
     * The unique ID of the <i>current game</i>
     * of a user who has a given {@linkplain User#getId() unique ID}.
     * </p>
     */
    @Nonnull
    public Optional<GameIdentifier> getCurrentGameOfUser(
            @Nonnull final UUID userId) {
        Objects.requireNonNull(userId);
        try (var context = repository.openContext()) {
            final var user = getUser(context, userId);
            if (user.isPresent()) {
                return getCurrent(context, userId);
            } else {
                return Optional.empty();
            }
        }
    }

    /**
     * <p>
     * Retrieve complete information about the game players for the game that has
     * a given unique ID.
     * </p>
     */
    @Nonnull
    public Optional<FindGameResult> getGameAsGameManager(
            @Nonnull final GameIdentifier id) {
        try (var context = repository.openContext()) {
            return context.findGame(id);
        }
    }

    /**
     * <p>
     * Retrieve information about the game players for the game that has a given
     * unique ID, suitable for a non game manager.
     * </p>
     * <ul>
     * <li>The collection of {@linkplain Game#getUsers() players} is
     * either empty or contains only the requesting user: non game managers may
     * not see the complete list of players of a game, but may see that they are
     * a player of a game.</li>
     * </ul>
     */
    @Nonnull
    public Optional<FindGameResult> getGameAsNonGameManager(
            @Nonnull final GameIdentifier gameId, @Nonnull final UUID user) {
        Objects.requireNonNull(user, "user");
        final Optional<FindGameResult> result;
        try (var context = repository.openContext()) {
            result = context.findGame(gameId);
        }
        return result.map(r -> filterForUser(r, user));
    }

    private Optional<User> getUser(MCRepository.Context context, final UUID userId) {
        return userService.getUser(context, userId);
    }

    private UserJoinsGameState getUserJoinsGameState(@Nonnull MCRepository.Context context,
                                                     final UUID userId,
                                                     final GameIdentifier gameId)
            throws NoSuchElementException, UserAlreadyPlayingException,
            IllegalGameStateException, SecurityException {
        final var userOptional = getUser(context, userId);
        if (userOptional.isEmpty()) {
            throw new NoSuchElementException("user");
        }
        final var user = userOptional.get();
        final var gameOptional = context.findGame(gameId);
        if (gameOptional.isEmpty()) {
            throw new NoSuchElementException("game");
        }
        final var game = gameOptional.get().game();
        final var current = getCurrent(context, userId);

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
            final var characterEntryOptional = game.getUsers().entrySet().stream()
                    .filter(entry -> userId.equals(entry.getValue())).findAny();
            if (characterEntryOptional.isEmpty()) {
                throw new NoSuchElementException("character");
            }
            character = characterEntryOptional.get().getKey();
            endRecruitment = false;
        } else {
            if (!game.isRecruiting()) {
                throw new IllegalGameStateException("Game is not recruiting");
            }
            alreadyJoined = false;
            final var scenarioId = gameId.getScenario();
            final var scenarioOptional = scenarioService.getScenario(context, scenarioId);
            if (scenarioOptional.isEmpty()) {
                throw new NoSuchElementException("scenario");
            }
            final var scenario = scenarioOptional.get();
            final var characters = scenario.getCharacters();
            final var playedCharacters = game.getUsers().keySet();
            final var characterOptional = characters.stream().sequential()
                    .map(NamedUUID::getId)
                    .filter(c -> !playedCharacters.contains(c)).findFirst();
            if (characterOptional.isEmpty()) {
                throw new NoSuchElementException("character");
            }
            character = characterOptional.get();
            endRecruitment = characters.size() - 1 <= playedCharacters.size();
        }

        return new UserJoinsGameState(game, character, alreadyJoined,
                endRecruitment);
    }

    /**
     * <p>
     * Whether the {@link #userJoinsGame(UUID, GameIdentifier)} operation would
     * succeed
     * </p>
     * <p>
     * That is, whether all the following are true.
     * </p>
     * <ul>
     * <li>The{@code user} is the ID of a known user.</li>
     * <li>The {@code game} is the ID of a known game.</li>
     * <li>The {@code user} is not already playing a different game.</li>
     * <li>The {@code user} {@linkplain User#getAuthorities() has}
     * {@linkplain Authority#ROLE_PLAYER permission} to play games. Note that the
     * given user need not be the current user.</li>
     * <li>The user has already joined the game <em>or</em> the game is
     * {@linkplain Game#isRecruiting() recruiting} players.</li>
     * </ul>
     */
    public boolean mayUserJoinGame(@Nonnull final UUID user, @Nonnull final GameIdentifier game) {
        try (var context = repository.openContext()) {
            getUserJoinsGameState(context, user, game);
        } catch (UserAlreadyPlayingException | IllegalGameStateException
                 | SecurityException | NoSuchElementException e) {
            return false;
        }
        return true;
    }

    /**
     * <p>
     * Have a {@linkplain User user} become one of the
     * {@linkplain Game#getUsers() players of a game}.
     * </p>
     *
     * @throws NoSuchElementException      <ul>
     *                                     <li>If {@code user} is not the ID of a known user.</li>
     *                                     <li>If {@code game} is not the ID of a game.</li>
     *                                     </ul>
     * @throws UserAlreadyPlayingException If the {@code user} is already playing a different game.
     * @throws SecurityException           If the {@code user} does not {@linkplain User#getAuthorities()
     *                                     have} {@linkplain Authority#ROLE_PLAYER permission} to play
     *                                     games. Note that the given user need not be the current user.
     * @throws IllegalGameStateException   <ul>
     *                                     <li>If the game is not {@linkplain Game#isRecruiting()
     *                                     recruiting} players.</li>
     *                                     <li>If the game has no characters free.</li>
     *                                     </ul>
     */
    public void userJoinsGame(@Nonnull final UUID userId,
                              @Nonnull final GameIdentifier gameId)
            throws NoSuchElementException, UserAlreadyPlayingException,
            IllegalGameStateException, SecurityException {
        try (var context = repository.openContext()) {
            // read and check:
            final var state = getUserJoinsGameState(context, userId, gameId);
            if (state.alreadyJoined) {
                // optimisation
                return;
            }

            // modify:
            final var association = new UserGameAssociation(userId, gameId);
            state.game.addUser(state.character, userId);
            if (state.endRecruitment) {
                state.game.endRecruitment();
            }

            // write:
            context.addCurrentUserGame(userId, association);
            context.updateGame(state.game);
        }
    }

    @Immutable
    private static final class UserJoinsGameState {
        final Game game;
        final UUID character;
        final boolean alreadyJoined;
        final boolean endRecruitment;

        UserJoinsGameState(final Game game,
                           final UUID firstUnplayedCharacter, final boolean alreadyJoined,
                           final boolean endRecruitment) {
            this.game = game;
            this.character = firstUnplayedCharacter;
            this.alreadyJoined = alreadyJoined;
            this.endRecruitment = endRecruitment;
        }

    }


}
