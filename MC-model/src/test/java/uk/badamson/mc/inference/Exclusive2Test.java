package uk.badamson.mc.inference;
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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.badamson.dbc.assertions.CollectionVerifier;
import uk.badamson.dbc.assertions.ObjectVerifier;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Exclusive2Test {
    private static final BasicBelief BASIC_BELIEF_A = new BasicBelief(0, 0);
    private static final BasicBelief BASIC_BELIEF_B = new BasicBelief(1, 1);
    private static final BasicBelief BASIC_BELIEF_C = new BasicBelief(2, 2);

    private static void constructor(BasicBelief beliefA, BasicBelief beliefB) {
        final var exclusive = new Exclusive2(beliefA, beliefB);

        assertInvariants(exclusive);
        assertThat(exclusive.getBeliefs(), containsInAnyOrder(beliefA, beliefB));
    }

    static void assertInvariants(Exclusive2 exclusive) {
        ObjectVerifier.assertInvariants(exclusive);
        final Set<BasicBelief> beliefs = exclusive.getBeliefs();
        assertThat(beliefs, notNullValue());
        CollectionVerifier.assertForAllElements(beliefs, b -> {
            assertThat(b, notNullValue());
            BasicBeliefTest.assertInvariants(b);
        });
        assertThat(beliefs, hasSize(2));
    }

    static void assertInvariants(Exclusive2 exclusiveA, Exclusive2 exclusiveB) {
        ObjectVerifier.assertInvariants(exclusiveA, exclusiveB);
    }

    @Nested
    public class Construct {

        @Test
        public void a() {
            constructor(BASIC_BELIEF_A, BASIC_BELIEF_B);
        }

        @Test
        public void b() {
            constructor(BASIC_BELIEF_B, BASIC_BELIEF_C);
        }
    }
}
