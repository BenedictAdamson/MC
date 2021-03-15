# © Copyright Benedict Adamson 2021.
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
Feature: Current Game
  The system provides information about the game that a player is currently playing

  @integration
  Scenario: May examine current-game only if playing
    Given A scenario has games
    And user has the "player" role
    And logged in
    And user is not playing any games
    When examine home page
    Then The home-page does not indicate that the user has a current game

  @integration
  @back-end
  Scenario: Examine current game
    Given A scenario has games
    And user has the "player" role
    And logged in
    And user is playing a game
    When navigate to the current-game page
    Then MC provides a current-game page
    And the current-game page includes the scenario title
    And the current-game page includes the scenario description
    And the current-game page includes the date and time that the game was set up
    And the current-game page indicates which character the user is playing
 