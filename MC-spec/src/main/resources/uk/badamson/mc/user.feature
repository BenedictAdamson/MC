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
@integration
Feature: User
  Mission Command is a multi-player game.
  To conserve resources, play on a server is restricted to only known (and presumably trusted) users.

  Scenario: List users
    Given user has the "player" role
    And logged in
    When getting the users
    Then MC serves the resource
    And the response is a list of users
    And the list of users has at least one user
    
  Scenario Outline: Login
    # Implicitly not logged in
    Given user has the "player" role
    When log in using correct password
    Then MC accepts the login
    
  Scenario Outline: Add user
    Given user has the "manage-users" role
    And logged in
    When adding a user named "<new-name>" with  password "<password>"
    Then MC accepts the addition
    And can get the list of users
    And the list of users includes a user named "<new-name>"
    
    Examples:
      |name|new-name|password|
      |Andrew|John|letmein|
      |Zoes|Jeff|password123|
    
  Scenario Outline: Only administrator may add user
    Given user does not have the "manage-users" role
    And logged in
    Then MC does not present adding a user as an option