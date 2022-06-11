package uk.badamson.mc.service;

import javax.annotation.Nonnull;

public class PasswordEncoderTest {

    public static final PasswordEncoder FAKE = new PasswordEncoder() {

        @Nonnull
        @Override
        public String encode(@Nonnull CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(@Nonnull CharSequence rawPassword, @Nonnull String encryptedPassword) {
            return encryptedPassword.contentEquals(rawPassword);
        }
    };
}
