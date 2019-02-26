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

  Scenario: Get players page
    Given the DNS name, example.com, of an MC server
    When someone uses a web browser to navigate to the players page
    # The path of the players page is /player
    Then MC serves the players page
