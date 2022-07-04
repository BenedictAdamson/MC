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

    private final MCRepository repository = new MCRepositoryTest.Fake();

    private final Clock clock = Clock.fixed(Instant.now().truncatedTo(ChronoUnit.MILLIS), ZoneId.systemDefault());
    private final ScenarioService scenarioService = new ScenarioService(repository);
    private final UserService userService = new UserService(
            PasswordEncoderTest.FAKE, ADMINISTRATOR_PASSWORD, repository);
    private final GameService gameService = new GameService(clock, scenarioService, userService, repository);

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
    public BasicUserDetails createBasicUserDetails(final Set<Authority> authorities) {
        final var sequenceId = ++nUsers;
        final var username = "User " + sequenceId;
        final var password = "password" + sequenceId;
        return new BasicUserDetails(username, password, authorities, true, true, true, true);
    }
}
