# © Copyright Benedict Adamson 2020-21.
#
# This file is part of MC.
#
# MC is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# MC is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with MC.  If not, see <https://www.gnu.org/licenses/>.
#
Feature: Game
  The Mission Command game can have multiple games (plays) for each scenario

  @integration
  @back-end
  Scenario: Examine game as player
    Given a scenario has games
    And user has the "player" role
    And logged in
    And viewing the games of the scenario
    When navigate to one game
    Then it provides a game
    And the game includes the scenario title
    And the game includes the scenario description
    And the game includes the date and time that the game was set up
    And the game indicates whether the game has players
    And the game indicates whether the game is recruiting players
    And the game indicates whether the user may join the game
    And the game indicates whether the user is playing the game
    And the game indicates which character (if any) the user is playing
    And the game does not indicate which characters are played by which (other) users

  @integration
  @back-end
  Scenario: Examine game as game manager
    Given a scenario has games
    And user has the "manage games" role
    And logged in
    And viewing the games of the scenario
    When navigate to one game
    Then it provides a game
    And the game includes the scenario title
    And the game includes the scenario description
    And the game includes the date and time that the game was set up
    And the game indicates whether the game has players
    And the game indicates whether the game is recruiting players
    And the game indicates whether the user may join the game
    And the game indicates whether the user is playing the game
    And the game indicates which character (if any) the user is playing
    And the game indicates which characters are played by which users
    
  @integration
  @back-end
  Scenario: Add game
    Given user has the "manage games" role
    And logged in
    And examining scenario
    When creating a game
    Then MC accepts the creation of the game
    And it provides a game
    And the game indicates that the game is recruiting players
    And the game indicates that the game has no players
    And can get the list of games
    And the list of games includes the new game
    
  @integration
  @back-end
  Scenario: Only a game manager may add a game
    Given user does not have the "manage games" role
    And logged in
    And examining scenario
    Then MC does not allow creating a game
    
  @integration
  @back-end
  Scenario: End game recruitment
    Given user has the "manage games" role
    And logged in
    And viewing a game that is recruiting players
    When user ends recruitment for the game
    Then MC accepts ending recruitment for the game
    And it provides a game
    And the game indicates that the game is not recruiting players
    
  @integration
  @back-end
  Scenario: Only a game manager may end recruitment for a game
    Given user has the "player" role but not the "manage games" role
    And logged in
    And viewing a game that is recruiting players
    Then MC does not allow ending recruitment for the game
    
  @integration
  @back-end
  Scenario: Players may join a game
    Given user has the "player" role
    And logged in
    And user is not playing any games
    When examining a game recruiting players
    Then it provides a game
    And the game indicates that the user may join the game
    And the game indicates that the user is not playing the game
    
  @integration
  @back-end
  Scenario: Join a game
    Given user has the "player" role
    And logged in
    And user is not playing any games
    And examining a game recruiting players
    When the user joins the game
    Then MC accepts joining the game
    And it provides a game
    
  @integration
  @back-end
  Scenario: Only a player may join a game
    Given user has the "manage games" role but not the "player" role
    And logged in
    And user is not playing any games
    And examining a game recruiting players
    Then it provides a game
    And the game indicates that the user may not join the game