package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2020.
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Unit tests for the class {@link NamedUUID}.
 * </p>
 */
public class NamedUUIDTest {

   /**
    * <p>
    * Unit tests for the {@link NamedUUID} class.
    * </p>
    */
   @Nested
   public class IdentifierTest {

      @Nested
      public class Construct2 {
         @Test
         public void differentIds() {
            final var identifierA = new NamedUUID(ID_A, TITLE_A);
            final var identifierB = new NamedUUID(ID_B, TITLE_A);
            assertInvariants(identifierA, identifierB);
            assertNotEquals(identifierA, identifierB);
         }

         @Test
         public void equalIds() {
            final var identifierA = new NamedUUID(ID_A, TITLE_A);
            final var identifierB = new NamedUUID(ID_A, TITLE_B);
            assertInvariants(identifierA, identifierB);
            assertEquals(identifierA, identifierB);
         }

         @Test
         public void equalTitles() {
            final var identifierA = new NamedUUID(ID_A, TITLE_A);
            final var identifierB = new NamedUUID(ID_B, TITLE_A);
            assertInvariants(identifierA, identifierB);
            assertNotEquals(identifierA, identifierB);
         }
      }// class

      @Nested
      public class Constructor {

         @Test
         public void a() {
            test(ID_A, TITLE_A);
         }

         @Test
         public void b() {
            test(ID_B, TITLE_B);
         }

         private void test(final UUID id, final String title) {
            final var identifier = new NamedUUID(id, title);

            assertInvariants(identifier);
            assertAll("Attributes have the given values",
                     () -> assertSame(id, identifier.getId(), "id"),
                     () -> assertSame(title, identifier.getTitle(), "title"));
         }
      }// class
   }// class

   private static final UUID ID_A = UUID.randomUUID();
   private static final UUID ID_B = UUID.randomUUID();
   private static final String TITLE_A = "Beach Assault";
   private static final String TITLE_B = "0123456789012345678901234567890123456789012345678901234567890123";// longest

   public static void assertInvariants(final NamedUUID identifier) {
      ObjectTest.assertInvariants(identifier);// inherited
      final var id = identifier.getId();
      final var title = identifier.getTitle();
      assertAll("Not null", () -> assertNotNull(id, "id"),
               () -> assertNotNull(title, "title"));// guard
      assertTrue(NamedUUID.isValidTitle(title), "title is valid");
   }

   public static void assertInvariants(final NamedUUID identifierA,
            final NamedUUID identifierB) {
      ObjectTest.assertInvariants(identifierA, identifierB);// inherited
      assertEquals(identifierA.equals(identifierB),
               identifierA.getId().equals(identifierB.getId()),
               "Entity semantics, with the ID serving as a unique identifier");
   }
}
