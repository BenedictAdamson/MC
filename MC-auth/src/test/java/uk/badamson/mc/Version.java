package uk.badamson.mc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>
 * The version of the SUT
 * </p>
 */
public final class Version {

   public static final String VERSION = getVersion();

   public static final String IMAGE = "index.docker.io/benedictadamson/mc-back-end:"
            + VERSION;

   private static Properties getApplicationProperties() throws IOException {
      final InputStream stream = Thread.currentThread().getContextClassLoader()
               .getResourceAsStream("application.properties");
      if (stream == null) {
         throw new FileNotFoundException(
                  "resource application.properties not found");
      }
      final Properties properties = new Properties();
      properties.load(stream);
      return properties;
   }

   private static String getVersion() {
      String version;
      try {
         version = getApplicationProperties().getProperty("build.version");
      } catch (final IOException e) {
         throw new IllegalStateException(
                  "unable to read application.properties resource", e);
      }
      if (version == null || version.isEmpty()) {
         throw new IllegalStateException(
                  "missing build.version property in application.properties resource");
      }
      return version;
   }

}
