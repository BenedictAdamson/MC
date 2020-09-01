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
Feature: Unknown Resource
  Attempts to access unknown or incorrectly named things should give useful error responses.

  Scenario Outline: Get unknown resource
    # Implicitly a fresh instance of MC
    # Implicitly not logged in
    When getting the unknown resource at "<path>"
    Then MC replies with Not Found

    Examples: 
      |path|
      |/xxxxx|
      |/players|

  Scenario Outline: Modify unknown resource
    # Implicitly a fresh instance of MC
    # Implicitly not logged in
    When modifying the unknown resource with a "<verb>" at "<path>"
    Then MC replies with Not Found or Forbidden or Method Not Allowed
    # The precise HTTP status code is determined by the ingress, rather than MC proper,
    # and does not matter in practice to a user using a browser.

    Examples: 
      |verb|path|
      |POST|/xxxxx|
      |PUT|/players|
      |DELETE|/players|
      