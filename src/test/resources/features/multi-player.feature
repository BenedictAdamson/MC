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
    Given the DNS name, example.com, of an MC server
    When the potential player gives the DNS name to a web browser
    Then the MC server serves the home-page

  Scenario: Potential player accesses an MC server using a simple URL with root path
    Given the DNS name, example.com, of an MC server
    When the potential player gives the obvious URL http://example.com/ to a web browser
    Then the MC server serves the home-page
