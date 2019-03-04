package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2018-19.
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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * <p>
 * The Spring Boot configuration of the Mission Command game.
 * </p>
 */
@SpringBootConfiguration
@EnableWebFlux
@EnableWebFluxSecurity
@ComponentScan("uk.badamson.mc")
public class Application {

   /**
    * <p>
    * The entry point of the Mission Command program.
    * </p>
    *
    * @param args
    *           The command line arguments of the program.
    */
   public static void main(final String[] args) {
      SpringApplication.run(Application.class, args);
   }

   private void authorizeAdministration(final ServerHttpSecurity http) {
      http.authorizeExchange().pathMatchers(HttpMethod.POST, "/player")
               .permitAll();
   }

   private void authorizePublic(final ServerHttpSecurity http) {
      http.authorizeExchange().pathMatchers(HttpMethod.GET, "/", "/player")
               .permitAll();
   }

   @Bean
   public SecurityWebFilterChain securityWebFilterChain(
            final ServerHttpSecurity http) {
      http.formLogin();
      authorizeAdministration(http);
      authorizePublic(http);
      // TODO
      return http.build();
   }

}
