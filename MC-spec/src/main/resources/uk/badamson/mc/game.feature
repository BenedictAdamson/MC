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
    When Viewing the games of a scenario
    And Navigate to one game of the scenario
    Then MC serves the game page
    And The game page includes the scenario title
    And The game page includes the scenario description
    And The game page includes the date and time that the game was set up
    