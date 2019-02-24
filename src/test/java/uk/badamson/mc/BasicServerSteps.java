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

import static org.hamcrest.collection.IsIn.isOneOf;

import java.net.URI;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * <p>
 * Definitions of BDD steps for the Cucumber-JVM BDD testing tool, for features
 * about the basic operation of an MC server.
 * </p>
 */
@SpringBootTest(classes = TestConfig.class,
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

   @Given("the DNS name of an MC server")
   public void the_DNS_name_of_an_MC_server() {
      dnsName = "example.com";
   }

   @Given("the MC server is running")
   public void the_MC_server_is_running() {
      // Do nothing
   }

   @When("the MC server receives the request")
   public void the_MC_server_receives_the_request() {
      // Do nothing
   }

   @Then("the MC server serves a home-page")
   public void the_MC_server_serves_a_home_page() throws Throwable {
      response.expectStatus().value(isOneOf(200, 301, 308));
   }

   @Given("the potential player gives the DNS name to a web browser")
   public void the_potential_player_gives_the_DNS_name_to_a_web_browser() {
      // Do nothing
   }

   @Given("the web browser gets the default HTML resource at the DNS name")
   public void the_web_browser_gets_the_default_HTML_resource_at_the_DNS_name()
            throws Exception {
      Objects.requireNonNull(context, "context");
      Objects.requireNonNull(client, "client");
      final String authority = dnsName;
      final String path = null;
      final String query = null;
      final String fragment = null;
      requestUri = new URI(scheme, authority, path, query, fragment);
      response = client.get().uri(requestUri.getPath())
               .accept(MediaType.TEXT_HTML).exchange();
   }

}
