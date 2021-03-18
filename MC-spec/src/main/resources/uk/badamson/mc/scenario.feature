# © Copyright Benedict Adamson 2019-20.
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
Feature: Scenario
  The Mission Command game provides multiple scenarios that can be played.

  @integration
  @back-end
  Scenario: List scenarios
    When getting the scenarios
    Then MC serves the scenarios page
    And the response is a list of scenarios

  @integration
  @back-end
  Scenario: Examine scenario anonymously
    Given a scenario has games
    And not logged in
    When viewing the scenarios
    And navigate to a scenario with games
    Then MC serves the scenario page
    And the scenario page includes the scenario description
    And the scenario page includes the list of playable characters of that scenario
    And the scenario page includes the list of games of that scenario
    And the scenario page does not allow navigation to game pages

  @integration
  @back-end
  Scenario Outline: Examine scenario with authorization
    Given a scenario has games
    And user has the "<role>" role
    And logged in
    When viewing the scenarios
    And navigate to a scenario with games
    Then MC serves the scenario page
    And the scenario page includes the scenario description
    And the scenario page includes the list of playable characters of that scenario
    And the scenario page includes the list of games of that scenario
    And the scenario page allows navigation to game pages
    
    Examples:
      |role|
      |player|
      |manage games|
    
    