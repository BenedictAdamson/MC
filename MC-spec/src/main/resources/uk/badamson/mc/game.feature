# © Copyright Benedict Adamson 2020.
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
  Scenario: Examine game
    When A scenario has games
    And Viewing the games of the scenario
    And Navigate to one game of the scenario
    Then MC serves the game page
    And The game page includes the scenario title
    And The game page includes the scenario description
    And The game page includes the date and time that the game was set up
    And The game page indicates whether the game is recruiting players
    
  @integration
  @back-end
  Scenario: Add game
    Given user has the "MANAGE_GAMES" role
    And logged in
    When creating a game
    Then MC accepts the creation of the game
    And the game page indicates that the game is recruiting players
    And can get the list of games
    And the list of games includes the new game
    
  @integration
  @back-end
  Scenario: Only a game manager may add a game
    Given user does not have the "MANAGE_GAMES" role
    And logged in
    Then MC does not present creating a game as an option
    
  @integration
  @back-end
  Scenario: End game recruitment
    Given user has the "MANAGE_GAMES" role
    And viewing a game that is recruiting players
    When user ends recruitment for the game
    Then MC accepts ending recuitment for the game
    And the game page indicates that the game is not recruiting players
    
  @integration
  @back-end
  Scenario: Only a game manager may end recuitment for a game
    Given user does not have the "MANAGE_GAMES" role
    And logged in
    And viewing a game that is recruiting players
    Then MC does not present ending recuitment for the game as an option