package uk.badamson.mc.service;

import uk.badamson.mc.Authority;
import uk.badamson.mc.BasicUserDetails;
import uk.badamson.mc.repository.*;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public final class CoreWorld {

    private static final String ADMINISTRATOR_PASSWORD = "top-secret";

    private final CurrentUserGameRepository currentUserGameRepository = new CurrentUserGameRepositoryTest.Fake();
    private final GamePlayersRepository gamePlayersRepository = new GamePlayersRepositoryTest.Fake();
    private final GameRepository gameRepository = new GameRepositoryTest.Fake();
    private final UserRepository userRepository = new UserRepositoryTest.Fake();

    private final Clock clock = Clock.fixed(Instant.now().truncatedTo(ChronoUnit.MILLIS), ZoneId.systemDefault());
    private final ScenarioService scenarioService = new ScenarioService();
    private final GameService gameService = new GameService(gameRepository, clock, scenarioService);
    private final UserService userService = new UserService(
            PasswordEncoderTest.FAKE, userRepository, ADMINISTRATOR_PASSWORD);
    private final GamePlayersService gamePlayersService = new GamePlayersService(
            gamePlayersRepository, currentUserGameRepository, gameService, userService);

    private int nUsers;

    @Nonnull
    public Clock getClock() {
        return clock;
    }

    @Nonnull
    public ScenarioService getScenarioService() {
        return scenarioService;
    }

    @Nonnull
    public GameService getGameService() {
        return gameService;
    }

    @Nonnull
    public UserService getUserService() {
        return userService;
    }

    @Nonnull
    public GamePlayersService getGamePlayersService() {
        return gamePlayersService;
    }

    @Nonnull
    public BasicUserDetails createBasicUserDetails(final Set<Authority> authorities) {
        final var sequenceId = ++nUsers;
        final var username = "User " + sequenceId;
        final var password = "password" + sequenceId;
        return new BasicUserDetails(username, password, authorities, true, true, true, true);
    }
}
