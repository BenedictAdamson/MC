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

import java.util.Objects;

import org.opentest4j.AssertionFailedError;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * Auxiliary code for test JSON serialization and deserialization.
 * </p>
 */
public class JsonTest {

   private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
   static {
      OBJECT_MAPPER.findAndRegisterModules();
   }

   public static void assertCanSerialize(final Object object) {
      Objects.requireNonNull(object, "object");
      try {
         OBJECT_MAPPER.writeValueAsString(object);
      } catch (final JsonProcessingException e) {
         throw new AssertionFailedError("can serialize as JSON", e);
      }
   }

   public static void assertCanSerializeAndDeserialize(final Object object) {
      Objects.requireNonNull(object, "object");
      final String serialized;
      try {
         serialized = OBJECT_MAPPER.writeValueAsString(object);
      } catch (final JsonProcessingException e) {
         throw new AssertionFailedError("can serialize as JSON", e);
      }
      try {
         OBJECT_MAPPER.readValue(serialized, object.getClass());
      } catch (final JsonProcessingException e) {
         throw new AssertionFailedError("can not deserialize from JSON", e);
      }
   }
}
