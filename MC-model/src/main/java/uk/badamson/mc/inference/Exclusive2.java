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

import java.util.Set;

final class Exclusive2 {
    private static final double BAYES_FACTOR = -Math.sqrt(0.5);
    private final Set<BasicBelief> beliefs;

    Exclusive2(BasicBelief beliefA, BasicBelief beliefB) {
        this.beliefs = Set.of(beliefA, beliefB);
        if (beliefA.equals(beliefB)) {
            throw new IllegalArgumentException();
        }
        new DirectInference(beliefA, beliefB, BAYES_FACTOR, beliefA.getInformation());
        new DirectInference(beliefB, beliefA, BAYES_FACTOR, beliefB.getInformation());
    }

    public Set<BasicBelief> getBeliefs() {
        return beliefs;
    }
}
