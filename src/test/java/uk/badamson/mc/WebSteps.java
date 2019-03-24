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

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;
import org.springframework.web.reactive.function.BodyInserters;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import reactor.core.publisher.Hooks;
import uk.badamson.mc.service.Service;

/**
 * <p>
 * Definitions of BDD steps for the Cucumber-JVM BDD testing tool, for features
 * about the basic operation of an MC server.
 * </p>
 */
@SpringBootTest(classes = ApplicationTest.class,
         webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebTestClient
public class WebSteps {

   static {
      Hooks.onOperatorDebug();
   }

   @Autowired
   private ApplicationContext context;

   @Autowired
   private WebTestClient client;

   @Autowired
   private Service service;

   private final String scheme = "http";

   private String dnsName;
   private URI requestUri;
   private WebTestClient.ResponseSpec response;
   ListBodySpec<Player> responsePlayerList;

   @Given("a fresh instance of MC")
   public void a_fresh_instance_of_MC() {
      // Do nothing
   }

   @Given("a valid CSRF token")
   public void a_valid_CSRF_token() {
      client.mutateWith(csrf());
   }

   @When("adding a player named {string} with  password {string}")
   public void adding_a_player_named(final String name, final String password) {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(password, "password");
      postResource("/player", new Player(name, password, Set.of()));
   }

   @Then("can get the list of players")
   public void can_get_the_list_of_players() {
      requestJson("/player");
      responseIsOk();
      responsePlayerList = response.expectBodyList(Player.class);
   }

   private void getResource(final String path, final MediaType mediaType) {
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

   @When("getting the players")
   public void getting_the_players() {
      requestJson("/player");
   }

   @When("log in as {string} using password {string} with CSRF protection")
   public void log_in_as_using_password(final String player,
            final String password) {
      Objects.requireNonNull(player, "player");
      Objects.requireNonNull(password, "password");
      Objects.requireNonNull(context, "context");
      Objects.requireNonNull(client, "client");

      response = client.mutateWith(csrf()).post().uri("/login")
               .contentType(MediaType.APPLICATION_FORM_URLENCODED)
               .body(BodyInserters.fromFormData("username", player)
                        .with("password", password))
               .exchange();
   }

   @Given("logged in as {string}")
   public void logged_in_as(final String name) {
      client.mutateWith(mockUser(name));
   }

   @Then("MC accepts the addition")
   public void mc_accepts_the_addition() {
      response.expectStatus().isCreated();
   }

   @Then("MC accepts the login")
   public void mc_accepts_the_login() {
      response.expectStatus().isFound().expectHeader().valueEquals("Location",
               "/");
   }

   @Then("MC forbids the request")
   public void mc_forbids_the_request() {
      response.expectStatus().isForbidden();
   }

   @Then("MC serves the resource")
   public void mc_serves_the_players_resource() {
      responseIsOk();
   }

   @Then("MC serves the web page")
   public void mc_serves_the_web_page() {
      responseIsOk();
   }

   private void postResource(final String path, final Object body) {
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
      final var request = client.post().uri(requestUri.getPath())
               .contentType(MediaType.APPLICATION_JSON_UTF8).syncBody(body)
               .accept(MediaType.APPLICATION_JSON_UTF8);
      response = request.exchange();
   }

   private void requestHtml(final String path) {
      getResource(path, MediaType.TEXT_HTML);
   }

   private void requestJson(final String path) {
      getResource(path, MediaType.APPLICATION_JSON_UTF8);
   }

   private void responseIsOk() {
      response.expectStatus().isOk();
   }

   @Given("that player {string} exists with  password {string}")
   public void that_player_exists_with_password(final String player,
            final String password) {
      Objects.requireNonNull(player, "player");
      Objects.requireNonNull(password, "password");
      Objects.requireNonNull(service, "service");
      service.add(new Player(player, password, Set.of())).block();
   }

   @Given("the DNS name, example.com, of an MC server")
   public void the_DNS_name_of_an_MC_server() {
      dnsName = "example.com";
   }

   @Then("the list of players has one player")
   public void the_list_of_players_has_one_player() {
      responsePlayerList.hasSize(1);
   }

   @Then("the list of players includes a player named {string}")
   public void the_list_of_players_includes_a_player_named(final String name) {
      assertNotNull(responsePlayerList, "player list");
      responsePlayerList.contains(new Player(name, null, Set.of()));
   }

   @Then("the list of players includes the administrator")
   public void the_list_of_players_includes_the_administrator() {
      responsePlayerList.value(
               players -> players.stream()
                        .filter(player -> Player.ADMINISTRATOR_USERNAME
                                 .equals(player.getUsername()))
                        .count(),
               is(1L));
   }

   @When("the potential player gives the DNS name to a web browser")
   public void the_potential_player_gives_the_DNS_name_to_a_web_browser() {
      final String path = null;
      requestHtml(path);
   }

   @When("the potential player gives the obvious URL http://example.com/ to a web browser")
   public void the_potential_player_gives_the_obvious_URL_to_a_web_browser() {
      requestHtml("/");
   }

   @Then("the response message is a list of players")
   public void the_response_message_is_a_list_of_players() {
      responsePlayerList = response.expectBodyList(Player.class);
   }

   @Given("user authenticated as Administrator")
   public void user_authenticated_as_Administrator() {
      final UserDetails administrator = service
               .findByUsername(Player.ADMINISTRATOR_USERNAME).block();
      assert administrator != null;
      client.mutateWith(mockUser(administrator));
   }

}
