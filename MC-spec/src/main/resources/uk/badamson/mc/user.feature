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
Feature: User
  Mission Command is a multi-player game.
  To conserve resources, play on a server is restricted to only known (and presumably trusted) users.

  @integration
  @back-end
  Scenario Outline: List users
    Given user has the "<role>" role
    And logged in
    When getting the users
    Then MC serves the users page
    And the response is a list of users
    And the list of users has at least one user
    
    Examples:
      |role|
      |player|
      |manage users|

  @integration
  @back-end
  Scenario: Examine user
    Given user has the "manage users" role
    And logged in
    And viewing the list of users
    When navigate to one user page
    Then MC serves the user page
    And the user page includes the user name
    And the user page lists the roles of the user
    
  @integration
  Scenario: Login as player
    Given not logged in
    And user has the "player" role
    When log in using correct password
    Then MC accepts the login
    And redirected to home-page
    And MC allows logout
    And MC allows examining the current user
    
  @integration
  @back-end
  Scenario: Logout
    Given user has any role
    And logged in
    When request logout
    Then MC accepts the logout
    
  @integration
  Scenario: Login as administrator
    Given not logged in
    And user is the administrator
    When log in using correct password
    Then MC accepts the login
    And redirected to home-page
    
  @integration
  Scenario: Login denied
    Given not logged in
    And unknown user
    When try to login
    Then MC rejects the login
    
  @integration
  @back-end
  Scenario Outline: Add user
    Given user has the "manage users" role
    And logged in
    When adding a user named "<new-name>" with  password "<password>"
    Then MC accepts the addition
    And can get the list of users
    And the list of users includes a user named "<new-name>"
    
    Examples:
      |name|new-name|password|
      |Andrew|John|letmein|
      |Zoes|Jeff|password123|
    
  @integration
  @back-end
  Scenario: Only administrator may add user
    Given user has the "player" role but not the "manage users" role
    And logged in
    Then MC does not allow adding a user
 
  @integration
  @back-end
  Scenario: Only administrator my examine user
    Given user has the "player" role but not the "manage users" role
    And logged in
    And viewing the list of users
    When trying to navigate to a user page
    Then MC does not allow navigating to a user page
    