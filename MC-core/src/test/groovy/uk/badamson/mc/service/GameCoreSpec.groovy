package uk.badamson.mc.service

import org.hamcrest.Matchers
import uk.badamson.mc.Authority
import uk.badamson.mc.Game

import static org.hamcrest.MatcherAssert.assertThat
import static spock.util.matcher.HamcrestSupport.expect

/**
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

/**
 * The Mission Command game can have multiple games (plays) for each scenario
 */
class GameCoreSpec extends CoreSpecification {

    def "Examine game as player"() {
        given: "has a game"
        def scenarioId = getAScenarioId()
        def creationTime = world.clock.instant()
        def gameId = world.gameService.create(scenarioId).identifier

        and: "user has the player role"
        def userDetails = world.createBasicUserDetails(EnumSet.of(Authority.ROLE_PLAYER))
        def userId = world.userService.add(userDetails).getId()

        when: "examine the game"
        def gameOptional = world.gameService.getGameAsNonGameManager(gameId, userId)
        gameOptional.isPresent()
        def game = gameOptional.get()

        then: "the game indicates its scenario"
        game.scenario == scenarioId

        and: "the game includes the date and time that the game was set up"
        game.created == creationTime

        and: "the game indicates whether it has players"
        expect(game.getUsers(), Matchers.any(Map.class))

        and: "the game indicates whether it is recruiting players"
        expect(game.recruiting, Matchers.any(Boolean.class))

        and: "the game indicates whether the user may join the game"
        expect(world.gameService.mayUserJoinGame(userId, gameId), Matchers.any(Boolean.class))

        and: "the game indicates whether the user is playing the game"
        expect(game.users, Matchers.notNullValue())

        and: "the game indicates whether it is running"
        expect(game.runState, Matchers.notNullValue())

        and: "the game indicates which character (if any) the user is playing"
        expect(game.users, Matchers.any(Map.class))

        and: "the game does not indicate which characters are played by which (other) users"
        def users = game.users.values()
        users != null
        users.stream().map(u -> !(u == userId)).count() == 0
    }

    def "Examine game as game manager"() {
        given: "has a game"
        def scenarioId = getAScenarioId()
        def creationTime = world.clock.instant()
        def gameIdentifier = world.gameService.create(scenarioId).identifier

        and: "user has the manage games role"
        def userDetails = world.createBasicUserDetails(EnumSet.of(Authority.ROLE_MANAGE_GAMES))
        def user = world.userService.add(userDetails)

        when: "examine the game"
        def gameOptional = world.gameService.getGameAsGameManager(gameIdentifier)
        gameOptional.isPresent()
        def game = gameOptional.get()

        then: "the game indicates its scenario"
        game.scenario == scenarioId

        and: "the game includes the date and time that the game was set up"
        game.created == creationTime

        and: "the game indicates whether it has players"
        game.users != null

        and: "the game indicates whether it is recruiting players"
        expect(game.recruiting, Matchers.any(Boolean.class))

        and: "the game indicates whether the user may join the game"
        expect(world.gameService.mayUserJoinGame(user.id, gameIdentifier), Matchers.any(Boolean.class))

        and: "the game indicates whether it is running"
        expect(game.runState, Matchers.notNullValue())

        and: "the game indicates which character (if any) the user is playing"
        expect(game.users, Matchers.any(Map.class))

        and: "the game indicates which characters are played by which users"
        def users = game.users.values()
        expect(users, Matchers.notNullValue())
        expect(users, Matchers.any(Collection.class))
    }

    def "Add game"() {
        given: "has a scenario without any games"
        def scenarioId = getAScenarioId()

        when: "create a game for the scenario"
        def creationTime = world.clock.instant()
        def game = world.gameService.create(scenarioId).value

        then: "accepts the creation of the game"
        game != null

        and: "the game indicates that it is recruiting players"
        game.recruiting

        and: "the game indicates that it has no players"
        expect(game.users, Matchers.anEmptyMap())

        and: "the game indicates that it is not running"
        game.runState != Game.RunState.RUNNING

        and: "can get the list of games"
        def gameCreationTimes = world.gameService.getCreationTimesOfGamesOfScenario(scenarioId)
        gameCreationTimes != null

        and: "the list of games includes the new game"
        expect(gameCreationTimes, Matchers.hasItem(creationTime))
    }

    def "End game recruitment"() {
        given: "a game that is initially recruiting players"
        def scenarioId = getAScenarioId()
        def gameIdentifier = world.gameService.create(scenarioId).identifier

        when: "user ends recruitment for the game"
        def gamePlayers = world.gameService.endRecruitment(gameIdentifier)

        then: "the game indicates that it is not recruiting players"
        !gamePlayers.recruiting
    }

    def "Players may join a game"() {
        given: "a game is recruiting players"
        def scenarioId = getAScenarioId()
        def gameIdentifier = world.gameService.create(scenarioId).identifier

        and: "user with the player role but is not playing any games"
        def userDetails = world.createBasicUserDetails(EnumSet.of(Authority.ROLE_PLAYER))
        def user = world.userService.add(userDetails)

        when: "examine the game"
        def gameOptional = world.gameService.getGameAsNonGameManager(gameIdentifier, user.id)
        gameOptional.isPresent()
        def game = gameOptional.get()

        then: "the game indicates that the user may join the game"
        world.gameService.mayUserJoinGame(user.id, gameIdentifier)

        and: "the game indicates that the user is not playing the game"
        expect(game.users.values(), Matchers.not(Matchers.hasItem(user.id)))
    }

    def "Join a game"() {
        given: "a game is recruiting players"
        def scenarioId = getAScenarioId()
        def gameIdentifier = world.gameService.create(scenarioId).identifier

        and: "user with the player role but is not playing any games"
        def userDetails = world.createBasicUserDetails(EnumSet.of(Authority.ROLE_PLAYER))
        def user = world.userService.add(userDetails)

        when: "the user joins the game"
        world.gameService.userJoinsGame(user.id, gameIdentifier)

        then: "the game indicates that the user is playing the game"
        def gameOptional = world.gameService.getGameAsNonGameManager(gameIdentifier, user.id)
        gameOptional.isPresent()
        def users = gameOptional.get().users
        expect(users.values(), Matchers.hasItem(user.id))

        and: "the game indicates which character the user is playing"
        expect(users, Matchers.any(Map.class))
    }

    def "Only a player may join a game"() {
        given: "a game is recruiting players"
        def scenarioId = getAScenarioId()
        def gameIdentifier = world.gameService.create(scenarioId).identifier

        and: "user with the manage games role but not the player role"
        def userDetails = world.createBasicUserDetails(EnumSet.of(Authority.ROLE_MANAGE_GAMES))
        def user = world.userService.add(userDetails)

        when: "examine the game"
        def mayJoin = world.gameService.mayUserJoinGame(user.id, gameIdentifier)

        then: "the game indicates that the user may not join the game"
        !mayJoin
    }

    def "Start game"() {
        given: "a game is waiting to start"
        def scenarioId = getAScenarioId()
        def gameIdentifier = world.gameService.create(scenarioId).identifier

        when: "user starts the game"
        def game = world.gameService.startGame(gameIdentifier)

        then: "the game indicates that it is running"
        game.runState == Game.RunState.RUNNING
    }

    def "Stop game"() {
        given: "a game is running"
        def scenarioId = getAScenarioId()
        def gameIdentifier = world.gameService.create(scenarioId).identifier
        world.gameService.startGame(gameIdentifier)

        when: "user stops the game"
        world.gameService.stopGame(gameIdentifier)

        then: "the game indicates that it is not running"
        def gameOptional = world.gameService.getGameAsGameManager(gameIdentifier)
        gameOptional.isPresent()
        def game = gameOptional.get()
        game.runState == Game.RunState.STOPPED
    }

    private UUID getAScenarioId() {
        def scenarioIdOptional = world.scenarioService.scenarioIdentifiers.findAny()
        assertThat('has a scenario', scenarioIdOptional.isPresent())
        def scenarioId = scenarioIdOptional.get()
        scenarioId
    }
}