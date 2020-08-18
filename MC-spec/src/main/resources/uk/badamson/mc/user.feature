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

  Scenario: Get users of fresh instance
    # Implicitly a fresh instance of MC
    # Implicitly not logged in
    # Implicitly not presenting a CSRF token
    When getting the users
    # The path of the users resource is /api/user
    Then MC serves the resource
    # And there is only one user, the administrator, with the default name
    And the response message is a list of users
    And the list of users has one user
    And the list of users includes the administrator
    And the list of users includes a user named "Administrator"
    
  Scenario Outline: Login
    # Implicitly not logged in
    Given that user "<user>" exists with  password "<password>"
    And presenting a valid CSRF token
    When log in as "<user>" using password "<password>"
    Then MC accepts the login
    
    Examples:
      |user|password|
      |John|letmein|
      |Jeff|pasword123|
    
  Scenario Outline: Add user
    Given user authenticated as Administrator
    And presenting a valid CSRF token
    When adding a user named "<name>" with  password "<password>"
    Then MC accepts the addition
    And can get the list of users
    And the list of users includes a user named "<name>"
    
    Examples:
      |name|password|
      |John|letmein|
      |Jeff|password123|
    
  Scenario Outline: Only administrator may add user
    Given logged in as "<name>"
    And presenting a valid CSRF token
    When adding a user named "<new-name>" with  password "<password>"
    Then MC forbids the request
    
    Examples:
      |name|new-name|password|
      |John|Jeff|letmein|
      |Jeff|John|password123|