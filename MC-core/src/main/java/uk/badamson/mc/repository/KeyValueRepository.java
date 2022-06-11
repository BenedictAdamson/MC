package uk.badamson.mc.repository;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface KeyValueRepository<KEY, VALUE> {

    void save(@Nonnull KEY id, @Nonnull VALUE entity);

    @Nonnull
    Optional<VALUE> find(@Nonnull KEY id);

    boolean exists(@Nonnull KEY id);

    @Nonnull
    Iterable<VALUE> findAll();

    long count();

    void delete(@Nonnull KEY id);

    void deleteAll();
}