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

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;

/**
 * <p>
 * Definitions of BDD steps for the Cucumber-JVM BDD testing tool, for features
 * about the basic operation of an MC server.
 * </p>
 */
public class BasicServerSteps {

   private final String scheme = "http";
   private String dnsName;
   private URI requestUri;

   @Given("A potential player has the DNS name of an MC server")
   public void givenDnsNameOfAnMcServer() {
      dnsName = "example.com";
   }

   @Given("the MC server at the DNS name is running")
   public void givenMcServerAtTheDnsNameIsRunning() {
      // TODO
   }

   @Given("the potential player gives the DNS name to a web browser")
   public void givenGivesTheDnsNameToAWebBrowser() {
      // Do nothing
   }

   @Given("the web browser gets the default HTML resource at the DNS name")
   public void givenWebBrowserGetsTheDefaultHtmlResourceAtTheDnsName()
            throws Exception {
      final String authority = dnsName;
      final String path = null;
      final String query = null;
      final String fragment = null;
      requestUri = new URI(scheme, authority, path, query, fragment);
   }

   @When("the MC server receives the request")
   public void whenMcServerReceivesTheRequest() {
      // TODO
   }

   @Then("the MC server serves a home-page")
   public void thenMcServerServesAHomePage() throws Throwable {
      // TODO
   }

}
