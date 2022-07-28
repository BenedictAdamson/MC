package uk.badamson.mc;
/*
 * © Copyright Benedict Adamson 2022.
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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
public final class IdentifiedValue< IDENTIFIER, VALUE > {
    private final IDENTIFIER identifier;
    private final VALUE value;

    public IdentifiedValue(@Nonnull IDENTIFIER identifier, @Nonnull VALUE value) {
        this.identifier = Objects.requireNonNull(identifier);
        this.value = Objects.requireNonNull(value);
    }

    @Nonnull
    public IDENTIFIER getIdentifier() {
        return identifier;
    }

    @Nonnull
    public VALUE getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdentifiedValue<?, ?> that = (IdentifiedValue<?, ?>) o;
        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
