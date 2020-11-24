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
@front-end
@integration
Feature: Home-page
  It should be easy for users to access the home-page of an MC server, and know they have got the right page.

  Scenario: Potential user accesses an MC server using a simple URL with the root path
    Given the DNS name, example.com, of an MC server
    # Implicitly: And not logged in
    # Implicitly: And not resuming a session
    When the potential user gives the obvious URL http://example.com/ to a web browser
    Then MC serves the home page
    And the home page title includes the name of the game
    And the home page header includes the name of the game
    And MC presents logging in as an option
    And MC does not present logout as an option
    And MC does not present examining the current user as an option
