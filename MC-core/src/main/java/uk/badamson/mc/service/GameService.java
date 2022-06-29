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

import uk.badamson.mc.Game;
import uk.badamson.mc.Game.Identifier;
import uk.badamson.mc.GamePlayers;
import uk.badamson.mc.repository.MCRepository;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GameService {

    private static final Map<UUID, UUID> NO_USERS = Map.of();

    private static GamePlayers createGamePlayersForNewGame(final Identifier id) {
        return new GamePlayers(id, true, NO_USERS);
    }

    private final Clock clock;

    private final ScenarioService scenarioService;

    private final MCRepository repository;

    public GameService(@Nonnull final Clock clock,
                       @Nonnull final ScenarioService scenarioService,
                       @Nonnull MCRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.scenarioService = Objects.requireNonNull(scenarioService, "scenarioService");
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
    public Game create(@Nonnull final UUID scenario) throws NoSuchElementException {
        Objects.requireNonNull(scenario);
        try(var context = repository.openContext()) {
            requireKnownScenario(context, scenario);
            final var identifier = new Identifier(scenario, getNow());
            final var game = new Game(identifier, Game.RunState.WAITING_TO_START);
            final var gamePlayers = createGamePlayersForNewGame(identifier);
            context.saveGame(identifier, game);
            context.saveGamePlayers(identifier, gamePlayers);
            return game;
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
        try(var context = repository.openContext()) {
            requireKnownScenario(context, scenario);// read-and-check
            return getGameIdentifiers(context)// read
                    .filter(id -> scenario.equals(id.getScenario()))
                    .map(Identifier::getCreated)
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

    @Nonnull
    public Optional<Game> getGame(@Nonnull final Identifier id) {
        try(var context = repository.openContext()) {
            return getGame(context, id);
        }
    }

    @Nonnull
    Optional<Game> getGame(@Nonnull MCRepository.Context context, @Nonnull final Identifier id) {
        return context.findGame(id);
    }

    @Nonnull
    public Stream<Identifier> getGameIdentifiers() {
        try(var context = repository.openContext()) {
            return getGames(context).map(Game::getIdentifier);
        }
    }

    @Nonnull
    Stream<Identifier> getGameIdentifiers(@Nonnull MCRepository.Context context) {
        return getGames(context).map(Game::getIdentifier);
    }

    private Stream<Game> getGames(@Nonnull MCRepository.Context context) {
        return context.findAllGames();
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
    public Game startGame(@Nonnull final Identifier id)
            throws NoSuchElementException, IllegalGameStateException {
        Objects.requireNonNull(id);
        try(var context = repository.openContext()) {
            Optional<Game> gameOptional = getGame(context, id);
            if (gameOptional.isEmpty()) {
                throw new NoSuchElementException("game");
            }
            var game = gameOptional.get();// read
            switch (game.getRunState()) {
                case WAITING_TO_START:
                    game = new Game(game);
                    game.setRunState(Game.RunState.RUNNING);
                    context.saveGame(id, game);// write
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

    public void stopGame(@Nonnull final Identifier id)
            throws NoSuchElementException {
        try(var context = repository.openContext()) {
            Optional<Game> gameOptional = getGame(context, id);
            if (gameOptional.isEmpty()) {
                throw new NoSuchElementException("game");
            }
            var game = gameOptional.get();// read
            switch (game.getRunState()) {
                case WAITING_TO_START, RUNNING -> {
                    game = new Game(game);
                    game.setRunState(Game.RunState.STOPPED);
                    context.saveGame(id, game);
                    // write
                }
                case STOPPED -> // do nothing
                        new Game(game);
                default ->// never happens
                        throw new AssertionError("Valid game state");
            }
        }
    }

}
