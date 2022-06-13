package uk.badamson.mc.service

import uk.badamson.mc.Authority
import uk.badamson.mc.Game

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.aMapWithSize
import static org.hamcrest.Matchers.contains
import static spock.util.matcher.HamcrestSupport.expect

/**
 * © Copyright Benedict Adamson 2021-22.
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
 * The system provides information about the game that a player is currently playing
 */
class CurrentGameCoreSpec extends CoreSpecification {

    def "May examine current-game only if playing"() {
        given: "has a game"
        createGame()

        and: "user with the player role"
        def user = world.userService.add(world.createBasicUserDetails(EnumSet.of(Authority.ROLE_PLAYER)))

        when: "examine current-game of the user"
        final Optional<Game.Identifier> currentGame = world.gamePlayersService.getCurrentGameOfUser(user.id)

        then: "does not indicate that the user has a current game"
        currentGame.isEmpty()
    }

    def "Examine current game"() {
        given: "has a game"
        def game = createGame()

        and: "has a user with the player role"
        def userDetails = world.createBasicUserDetails(EnumSet.of(Authority.ROLE_PLAYER))
        def user = world.userService.add(userDetails)

        and: "user is playing the game"
        world.gamePlayersService.userJoinsGame(user.id, game.identifier)

        when: "examine current game"
        def currentGame = world.gamePlayersService.getCurrentGameOfUser(user.id)

        then: "indicates that the user has a current game"
        currentGame.isPresent()
        currentGame.get() == game.identifier

        when: "examine the players of the game"
        def gamePlayersOptional = world.gamePlayersService.getGamePlayersAsNonGameManager(
                game.identifier, user.id)

        then: "the game indicates which character the user is playing"
        gamePlayersOptional.isPresent()
        def gamePlayers = gamePlayersOptional.get()
        def users = gamePlayers.users
        expect(users, aMapWithSize(1))
        expect(users.values(), contains(user.id))
    }

    private Game createGame() {
        def scenarioIdOptional = world.scenarioService.scenarioIdentifiers.findAny()
        assertThat('has a scenario', scenarioIdOptional.isPresent())
        def scenarioId = scenarioIdOptional.get()
        world.gameService.create(scenarioId)
    }


}
