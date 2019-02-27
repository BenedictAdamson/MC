package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2019.
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

import java.util.Objects;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * <p>
 * Definitions of BDD steps for the Cucumber-JVM BDD testing tool, for features
 * about the basic operation of the MC service layer.
 * </p>
 */
public class ServiceSteps {

   private Service service;
   private Flux<Player> players;

   @Given("a fresh MC service")
   public void a_fresh_MC_service() {
      service = new ServiceImpl();
   }

   @When("getting the players")
   public void getting_the_players() {
      Objects.requireNonNull(service, "service");
      players = ServiceTest.getPlayers(service);
   }

   @Then("the administrator is the only player")
   public void the_administrator_is_the_only_player() {
      Objects.requireNonNull(players, "players");
      StepVerifier.create(players).expectNext(Player.DEFAULT_ADMINISTRATOR)
               .expectComplete().verify();
   }

}
