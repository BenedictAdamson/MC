package uk.badamson.mc.service

import org.hamcrest.Matchers
import uk.badamson.mc.NamedUUID

import static org.hamcrest.MatcherAssert.assertThat
import static spock.util.matcher.HamcrestSupport.expect
/** © Copyright Benedict Adamson 2019,20,22.
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

/**
 * The Mission Command game provides multiple scenarios that can be played.
 */
class ScenarioCoreSpec extends CoreSpecification {

    def "Examine scenario"() {
        given: "a scenario that has a game"
        def scenarioIdOptional = world.scenarioService.scenarioIdentifiers.findAny()
        assertThat('has a scenario', scenarioIdOptional.isPresent())
        def scenarioId = scenarioIdOptional.get()
        def identifiedGame = world.gameService.create(scenarioId)
        def gameId = identifiedGame.identifier
        def game = identifiedGame.value
        def created = game.created

        when: "examine the scenario"
        def scenarioOptional = world.scenarioService.getScenario(scenarioId)
        scenarioOptional.isPresent()
        def scenario = scenarioOptional.get()

        then: "the scenario includes the scenario description"
        expect(scenario.description, Matchers.not(Matchers.emptyString()))

        and: "the scenario includes the list of playable characters of that scenario"
        expect(scenario.characters, Matchers.any(List.class))

        and: "the scenario has a collection of games of that scenario"
        def gameIdentifiers = world.gameService.getGameIdentifiersOfScenario(scenarioId)
        expect(gameIdentifiers, Matchers.any(Collection.class))
        expect(gameIdentifiers, Matchers.hasItem(new NamedUUID(gameId, created.toString())))
    }
}