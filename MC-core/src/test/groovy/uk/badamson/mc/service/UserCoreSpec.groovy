package uk.badamson.mc.service

import org.hamcrest.Matchers
import uk.badamson.mc.Authority
import uk.badamson.mc.BasicUserDetails

import static spock.util.matcher.HamcrestSupport.expect
/**
 * © Copyright Benedict Adamson 2019-20,22.
 *
 * This file is part of MC.
 *
 * MC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with MC.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Mission Command is a multi-player game. To conserve resources, play on a server is restricted to only known (and presumably trusted) users.
 */
class UserCoreSpec extends CoreSpecification {
  def "List users"() {
    when: "getting the users"
    def users = world.userService.users.toList()

    then: "the list of users has at least one user"
    expect(users, Matchers.not(Matchers.empty()))
  }

  def "Examine user"() {
    given: "have the list of users"
    def users = world.userService.users

    when: "navigate to one user"
    def userIdOptional = users.map(u -> u.id).findAny()
    userIdOptional.isPresent()
    def userId = userIdOptional.get()
    def userOptional = world.userService.getUser(userId)

    then: "provides the user"
    userOptional.isPresent()
    def user = userOptional.get()

    and: "the user includes the user name"
    expect(user.username, Matchers.not(Matchers.emptyOrNullString()))

    and: "the user lists the roles of the user"
    expect(user.authorities, Matchers.any(Set.class))
  }

  def "Add user"() {
    when: "adding a user named ${userName} with password ${password}"
    def userDetails = new BasicUserDetails(userName, password, Set.of(Authority.ROLE_PLAYER),
            false, false, false, true)
    def user = world.userService.add(userDetails)

    then: "accepts the addition"
    user != null

    and: "can get the list of users"
    def users = world.userService.users

    and: "the list of users includes a user named ${userName}"
    users.anyMatch(u -> u.username == userName)

    where:
    userName | password
    'John' | 'secret'
    'Jeff' | 'password123'
  }
}