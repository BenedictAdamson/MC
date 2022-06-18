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
import uk.badamson.mc.repository.MCRepository;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GameService {

    private final Clock clock;

    private final ScenarioService scenarioService;

    private final MCRepository repository;

    public GameService(@Nonnull final Clock clock,
                       @Nonnull final ScenarioService scenarioService,
                       @Nonnull MCRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
        this.scenarioService = Objects.requireNonNull(scenarioService,
                "scenarioService");
    }

    /**
     * <p>
     * Create a new game for a given scenario.
     * </p>
     *
     * @throws NoSuchElementException If {@code scenario} is not the ID of a recognised scenario.
     *                                That is, if {@code scenario} is not one of the
     *                                {@linkplain ScenarioService#getScenarioIdentifiers()
     *                                identifiers} of the associated
     *                                {@linkplain #getScenarioService() scenario service}.
     */
    @Nonnull
    public Game create(@Nonnull final UUID scenario) throws NoSuchElementException {
        requireKnownScenario(scenario);// read-and-check
        final var identifier = new Identifier(scenario, getNow());
        final var game = new Game(identifier, Game.RunState.WAITING_TO_START);
        repository.saveGame(identifier, game);// write
        return game;
    }

    private Optional<Game> get(final Identifier id) {
        return repository.findGame(id);
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
     *                                That is, if {@code scenario} is not one of the
     *                                {@linkplain ScenarioService#getScenarioIdentifiers()
     *                                identifiers} of the associated
     *                                {@linkplain #getScenarioService() scenario service}.
     */
    @Nonnull
    public Set<Instant> getCreationTimesOfGamesOfScenario(@Nonnull final UUID scenario)
            throws NoSuchElementException {
        requireKnownScenario(scenario);// read-and-check
        return getGameIdentifiers()// read
                .filter(id -> scenario.equals(id.getScenario()))
                .map(Identifier::getCreated)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Nonnull
    public Optional<Game> getGame(@Nonnull final Identifier id) {
        return get(id);
    }

    @Nonnull
    public Stream<Identifier> getGameIdentifiers() {
        return getGames().map(Game::getIdentifier);
    }

    private Stream<Game> getGames() {
        return repository.findAllGames();
    }

    @Nonnull
    public Instant getNow() {
        return clock.instant().truncatedTo(ChronoUnit.MILLIS);
    }

    @Nonnull
    public ScenarioService getScenarioService() {
        return scenarioService;
    }

    private void requireKnownScenario(final UUID scenario)
            throws NoSuchElementException {
        Objects.requireNonNull(scenario, "scenario");
        if (scenarioService.getScenarioIdentifiers()
                .noneMatch(scenario::equals)) {
            throw new NoSuchElementException("unknown scenario");
        }
    }

    @Nonnull
    public Game startGame(@Nonnull final Identifier id)
            throws NoSuchElementException, IllegalGameStateException {
        Optional<Game> gameOptional = get(id);
        if (gameOptional.isEmpty()) {
            throw new NoSuchElementException("game");
        }
        var game = gameOptional.get();// read
        switch (game.getRunState()) {
            case WAITING_TO_START:
                game = new Game(game);
                game.setRunState(Game.RunState.RUNNING);
                repository.saveGame(id, game);// write
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

    public void stopGame(@Nonnull final Identifier id)
            throws NoSuchElementException {
        Optional<Game> gameOptional = get(id);
        if (gameOptional.isEmpty()) {
            throw new NoSuchElementException("game");
        }
        var game = gameOptional.get();// read
        switch (game.getRunState()) {
            case WAITING_TO_START, RUNNING -> {
                game = new Game(game);
                game.setRunState(Game.RunState.STOPPED);
                repository.saveGame(id, game);
                // write
            }
            case STOPPED -> // do nothing
                    new Game(game);
            default ->// never happens
                    throw new AssertionError("Valid game state");
        }
    }

}
