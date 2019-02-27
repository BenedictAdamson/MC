# © Copyright Benedict Adamson 2019.
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
Feature: Player
  Mission Command is a multi-player game.
  To conserve resources, play on a server is restricted to only known (and presumably trusted) users.

  Scenario: Get players resource of fresh instance
    Given a fresh instance of MC
    When getting the players resource
    # The path of the players resource is /player
    Then MC serves the resource
    # And there is only one player, the administrator, with the default name
    And the response message is a list of players
    And the response message is a singleton list of players
    And the list of players in the response message includes the administrator
    And the list of players in the response message includes a player named "Administrator"

  Scenario: Get players of fresh instance
    Given a fresh MC service
    When getting the players
    Then the administrator is the only player
