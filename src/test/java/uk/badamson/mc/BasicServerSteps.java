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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * <p>
 * Definitions of BDD steps for the Cucumber-JVM BDD testing tool, for features
 * about the basic operation of an MC server.
 * </p>
 */
@SpringBootTest(classes = Application.class,
         webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
public class BasicServerSteps {

   @Autowired
   private ApplicationContext context;

   @Autowired
   private WebTestClient client;

   private final String scheme = "http";
   private String dnsName;
   private URI requestUri;
   private WebTestClient.ResponseSpec response;
   ListBodySpec<Player> responsePlayerList;

   @Given("a fresh instance of MC")
   public void a_fresh_instance_of_MC() {
      // Do nothing
   }

   @When("getting the players resource")
   public void getting_the_players_resource() {
      requestJson("/player");
   }

   @Then("MC serves the resource")
   public void mc_serves_the_players_resource() {
      responseIsOk();
   }

   @Then("MC serves the web page")
   public void mc_serves_the_web_page() {
      responseIsOk();
   }

   private void requestHtml(final String path) {
      requestResource(path, MediaType.TEXT_HTML);
   }

   private void requestJson(final String path) {
      requestResource(path, MediaType.APPLICATION_JSON_UTF8);
   }

   private void requestResource(final String path, final MediaType mediaType) {
      Objects.requireNonNull(context, "context");
      Objects.requireNonNull(client, "client");
      final String authority = dnsName;
      final String query = null;
      final String fragment = null;
      try {
         requestUri = new URI(scheme, authority, path, query, fragment);
      } catch (final URISyntaxException e) {
         throw new IllegalArgumentException(e);
      }
      response = client.get().uri(requestUri.getPath()).accept(mediaType)
               .exchange();
   }

   private void responseIsOk() {
      response.expectStatus().isOk();
   }

   @Given("the DNS name, example.com, of an MC server")
   public void the_DNS_name_of_an_MC_server() {
      dnsName = "example.com";
   }

   @Then("the list of players includes a player named {string}")
   public void the_list_of_players_includes_a_player_named(final String name) {
      responsePlayerList.contains(new Player(name));
   }

   @Then("the list of players includes the administrator")
   public void the_list_of_players_includes_the_administrator() {
      responsePlayerList.contains(Player.DEFAULT_ADMINISTRATOR);
   }

   @Then("the MC server redirects to the home-page")
   public void the_MC_server_redirects_to_the_home_page() throws Throwable {
      response.expectStatus().isTemporaryRedirect();
   }

   @When("the potential player gives the DNS name to a web browser")
   public void the_potential_player_gives_the_DNS_name_to_a_web_browser() {
      final String path = null;
      requestHtml(path);
   }

   @When("the potential player gives the home-page URL to a web browser")
   public void the_potential_player_gives_the_home_page_URL_to_a_web_browser() {
      requestHtml("/home");
   }

   @When("the potential player gives the obvious URL http://example.com/ to a web browser")
   public void the_potential_player_gives_the_obvious_URL_to_a_web_browser() {
      requestHtml("/");
   }

   @Then("the response is a list of players")
   public void the_response_is_a_list_of_players() {
      responsePlayerList = response.expectBodyList(Player.class);
   }

   @Then("the response is a singleton list of players")
   public void the_response_is_a_singleton_list_of_players() {
      responsePlayerList.hasSize(1);
   }

}
