package uk.badamson.mc.repository;
/*
 * © Copyright Benedict Adamson 2019-20,22.
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

import uk.badamson.dbc.assertions.ObjectVerifier;
import uk.badamson.mc.UserGameAssociation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class CurrentUserGameRepositoryTest {

    public static void assertInvariants(
            final CurrentUserGameRepository repository) {
        ObjectVerifier.assertInvariants(repository);// inherited
        KeyValueRepositoryTest.assertInvariants(repository);// inherited
    }

    public static final class Fake
            extends KeyValueRepositoryTest.AbstractFake<UUID, UserGameAssociation>
            implements CurrentUserGameRepository {

        @Nonnull
        @Override
        protected UserGameAssociation copy(@Nonnull UserGameAssociation that) {
            return that;
        }
    }

}
