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

   private void requestHtml(final String path) {
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
      response = client.get().uri(requestUri.getPath())
               .accept(MediaType.TEXT_HTML).exchange();
   }

   @Given("the DNS name, example.com, of an MC server")
   public void the_DNS_name_of_an_MC_server() {
      dnsName = "example.com";
   }

   @Then("the MC server redirects to the home-page")
   public void the_MC_server_redirects_to_the_home_page() throws Throwable {
      response.expectStatus().isTemporaryRedirect();
   }

   @Then("the MC server serves the home-page")
   public void the_MC_server_serves_the_home_page() throws Throwable {
      response.expectStatus().isOk();
   }

   @When("the potential player gives the DNS name to a web browser")
   public void the_potential_player_gives_the_DNS_name_to_a_web_browser()
            throws Exception {
      final String path = null;
      requestHtml(path);
   }

   @When("the potential player gives the home-page URL to a web browser")
   public void the_potential_player_gives_the_home_page_URL_to_a_web_browser()
            throws Exception {
      requestHtml("/home");
   }

   @When("the potential player gives the obvious URL http://example.com/ to a web browser")
   public void the_potential_player_gives_the_obvious_URL_to_a_web_browser()
            throws Exception {
      requestHtml("/");
   }

}
