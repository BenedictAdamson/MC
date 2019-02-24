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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * End-points for the home-page
 * </p>
 */
@RestController
public class HomeController {

   @GetMapping("/home")
   public String get() {
      return "Hello";
   }

   @GetMapping("")
   public ResponseEntity<Void> getEmpty() {
      return redirect();
   }

   @GetMapping("/")
   public ResponseEntity<Void> getRoot() {
      return redirect();
   }

   private ResponseEntity<Void> redirect() {
      return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
               .header(HttpHeaders.LOCATION, "/home").build();
   }
}
