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
    Given A scenario has games
    And user has the "player" role
    And logged in
    And Viewing the games of the scenario
    When Navigate to one game page
    Then MC provides a game page
    And The game page includes the scenario title
    And The game page includes the scenario description
    And The game page includes the date and time that the game was set up
    And The game page indicates whether the game has players
    And The game page indicates whether the game is recruiting players
    And The game page indicates whether the user may join the game
    And The game page indicates whether the user is playing the game
    And The game page indicates which character (if any) the user is playing
    And The game page does not indicate which characters are played by which (other) users

  @integration
  @back-end
  Scenario: Examine game as game manager
    Given A scenario has games
    And user has the "manage games" role
    And logged in
    And Viewing the games of the scenario
    When Navigate to one game page
    Then MC provides a game page
    And The game page includes the scenario title
    And The game page includes the scenario description
    And The game page includes the date and time that the game was set up
    And The game page indicates whether the game has players
    And The game page indicates whether the game is recruiting players
    And The game page indicates whether the user may join the game
    And The game page indicates whether the user is playing the game
    And The game page indicates which character (if any) the user is playing
    And The game page indicates which characters are played by which users
    
  @integration
  @back-end
  Scenario: Add game
    Given user has the "manage games" role
    And logged in
    And examining scenario
    When creating a game
    Then MC accepts the creation of the game
    And MC provides a game page
    And the game page indicates that the game is recruiting players
    And The game page indicates that the game has no players
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
    And MC provides a game page
    And the game page indicates that the game is not recruiting players
    
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
    Then MC provides a game page
    And the game page indicates that the user may join the game
    And The game page indicates that the user is not playing the game
    
  @integration
  @back-end
  Scenario: Join a game
    Given user has the "player" role
    And logged in
    And user is not playing any games
    And examining a game recruiting players
    When the user joins the game
    Then MC accepts joining the game
    And MC provides a game page
    And The game page indicates that the current game is the game joined
    
  @integration
  @back-end
  Scenario: Only a player may join a game
    Given user has the "manage games" role but not the "player" role
    And logged in
    And user is not playing any games
    And examining a game recruiting players
    Then MC provides a game page
    And The game page indicates that the user may not join the game