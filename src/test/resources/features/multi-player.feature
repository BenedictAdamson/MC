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
Feature: Multi-player
  MC is a multi-player game. It should be easy for players to find each other using MC servers.

  Scenario: Potential player accesses an MC server using a web browser and a DNS name
    Given A potential player has the DNS name of an MC server
    And the MC server at the DNS name is running
    And the potential player gives the DNS name to a web browser
    And the web browser gets the default HTML resource at the DNS name
    When the MC server receives the request
    Then the MC server serves a home-page.
