package uk.badamson.mc.auth;
/*
 * © Copyright Benedict Adamson 2019-20.
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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * <p>
 * Basic system test for the MC-auth Docker image, testing it operating alone,
 * using an in-memory database.
 * </p>
 * <p>
 * These tets are read-only, so do not need to set up fresh containers each
 * time.
 * </p>
 */
@TestMethodOrder(OrderAnnotation.class)
@Testcontainers
@Tag("IT")
public class SolitaryAuthServerIT {

   @Container
   private static final McAuthContainer container = new McAuthContainer()
            .withEnv("DB_VENDOR", "h2");

   private void assertThatNoErrorMessages(final String logs) {
      assertThat(logs, not(containsString("ERROR")));
   }

   @Test
   @Order(1)
   public void startUp() {
      assertThatNoErrorMessages(container.getLogs());
   }
}
