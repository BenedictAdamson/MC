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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import reactor.core.publisher.Mono;
import uk.badamson.mc.service.Service;

/**
 * <p>
 * Unit tests for the {@link ReactiveAuthenticationManager} bean and its
 * collaborators.
 * </p>
 */
@SpringBootTest(classes = ApplicationTest.class,
         webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RunWith(JUnitPlatform.class)
public class ReactiveAuthenticationManagerTest {

   private static final String PASSWORD_A = "letmein";

   private static final String PASSWORD_B = "password123";

   private static final String PASSWORD_C = "secret";

   private static final String USERNAME_A = "John";

   private static final String USERNAME_B = "Jeff";

   public static void assertInvariants(
            final ReactiveAuthenticationManager authenticationManager) {
      // Do nothing
   }

   public static Mono<Authentication> authenticate(
            final ReactiveAuthenticationManager authenticationManager,
            final Authentication authentication) {
      final var publisher = authenticationManager.authenticate(authentication);

      assertNotNull(publisher, "Returns a (non null) publisher");
      assertInvariants(authenticationManager);

      return publisher;
   }

   @Autowired
   private Service service;

   @Autowired
   private ReactiveAuthenticationManager authenticationManager;

   private Authentication authenticate_usernameAndPassword(
            final String username, final String password) {
      final Authentication token = new UsernamePasswordAuthenticationToken(
               username, password);

      final var publisher = authenticate(authenticationManager, token);

      return publisher.block();
   }

   @Test
   public void authenticate_usernameAndPassword_unknonwUsername() {
      assertThrows(BadCredentialsException.class,
               () -> authenticate_usernameAndPassword(USERNAME_A, PASSWORD_A));
   }
}
