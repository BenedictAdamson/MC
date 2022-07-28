package uk.badamson.mc.service;
/*
 * © Copyright Benedict Adamson 2020,22.
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

import uk.badamson.mc.NamedUUID;
import uk.badamson.mc.Scenario;
import uk.badamson.mc.repository.MCRepository;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ScenarioService {

    // TODO have useful scenarios.
    private static UUID SCENARIO_ID = UUID.randomUUID();
    private static final Scenario SCENARIO = new Scenario(SCENARIO_ID,
            "Section assault", "Basic fire and movement tactics.",
            List.of(new NamedUUID(UUID.randomUUID(), "Lt. Winters"),
                    new NamedUUID(UUID.randomUUID(), "Sgt. Summer"))) {
    };
    private static final Map<UUID, Scenario> SCENARIOS = Map
            .of(SCENARIO_ID, SCENARIO);

    private final MCRepository repository;

    public ScenarioService(MCRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Nonnull
    public Set<NamedUUID> getNamedScenarioIdentifiers() {
        try(var ignored = repository.openContext()) {
            return SCENARIOS.values().stream().map(Scenario::getNamedUUID).collect(Collectors.toUnmodifiableSet());
        }
    }

    @Nonnull
    public Optional<Scenario> getScenario(@Nonnull final UUID id) {
        Objects.requireNonNull(id, "id");
        try(var context = repository.openContext()) {
            return getScenario(context, id);
        }
    }

    @Nonnull
    Optional<Scenario> getScenario(@Nonnull MCRepository.Context context, @Nonnull final UUID id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(SCENARIOS.get(id));
    }

    @Nonnull
    public Stream<UUID> getScenarioIdentifiers() {
        try(var context = repository.openContext()) {
            return getScenarioIdentifiers(context);
        }
    }

    @Nonnull
    Stream<UUID> getScenarioIdentifiers(@Nonnull MCRepository.Context context) {
        return SCENARIOS.keySet().stream();
    }

}
