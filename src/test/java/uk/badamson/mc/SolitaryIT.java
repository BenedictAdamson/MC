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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * <p>
 * Basic system test for MC, testing it operating alone without any needed
 * servers.
 * </p>
 * <p>
 * MC expects and needs a database server to be present, so this tests that it
 * provides useful diagnostic messages if the database is missing or
 * misbehaving. The test builds the Docker image using the real Dockerfile, so
 * this also tests that Dockerfile.
 * </p>
 */
@Testcontainers
public class SolitaryIT {

   private static final Path TARGET_DIR = Paths.get("target");
   private static final Path DOCKERFILE = Paths.get("Dockerfile");

   private static final String SUT_VERSION;
   static {
      SUT_VERSION = System.getProperty("sutVersion", "");
      if (SUT_VERSION == null || SUT_VERSION.isEmpty()) {
         throw new IllegalStateException("setVersion property not set");
      }
   }
   private static final Path JAR = TARGET_DIR
            .resolve("MC-" + SUT_VERSION + ".jar");

   @Container
   private final GenericContainer<?> container = new GenericContainer<>(
            new ImageFromDockerfile().withFileFromPath("Dockerfile", DOCKERFILE)
                     .withFileFromPath("target/MC-.jar", JAR));

   @Test
   public void test() {
      assertTrue(true);
      // TODO
   }
}
