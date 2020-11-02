package uk.badamson.mc;
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

import java.util.Objects;

import javax.annotation.Nonnull;

import uk.badamson.mc.presentation.HomePage;
import uk.badamson.mc.presentation.Page;

/**
 * <p>
 * Definitions of BDD steps for the Cucumber-JVM BDD testing tool
 * </p>
 */
abstract class Steps {

   @Nonnull
   protected final WorldCore worldCore;

   protected Page expectedPage;

   protected Steps(@Nonnull final WorldCore worldCore) {
      this.worldCore = Objects.requireNonNull(worldCore, "worldCore");
   }

   protected final HomePage getHomePage() {
      final var homePage = new HomePage(worldCore.getWebDriver());
      homePage.get();
      expectedPage = homePage;
      return homePage;
   }
}
