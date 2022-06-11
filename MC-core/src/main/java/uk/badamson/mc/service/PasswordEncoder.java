package uk.badamson.mc.service;

import javax.annotation.Nonnull;

public interface PasswordEncoder {

    @Nonnull
    String encode(@Nonnull CharSequence rawPassword);

    boolean matches(@Nonnull CharSequence rawPassword, @Nonnull String encryptedPassword);
}
