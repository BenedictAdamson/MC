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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NotThreadSafe
public final class BasicBelief implements Belief {
    private final Set<Inference> inferences = new HashSet<>();
    private double information;
    private double nextInformation;

    public BasicBelief(double information, double nextInformation) {
        if (Math.abs(information - nextInformation) > Belief.INFORMATION_PRECISION) {
            throw new IllegalArgumentException("information and nextInformation must be close");
        }
        this.information = information;
        this.nextInformation = nextInformation;
    }

    @Override
    public double getInformation() {
        return information;
    }

    @Override
    public double getOdds() {
        return Belief.oddsOfInformation(information);
    }

    @Override
    public double getProbability() {
        return Belief.probabilityOfInformation(information);
    }

    public double getNextInformation() {
        return nextInformation;
    }

    @Nonnull
    public Set<Inference> getInferences() {
        return Collections.unmodifiableSet(inferences);
    }

    public void addInformation(double change) {
        nextInformation += change;
        if (Math.abs(nextInformation - information) >= Belief.INFORMATION_PRECISION) {
            information = nextInformation;
            Set.copyOf(inferences).forEach(i -> i.premiseChanged(this));
        }
    }

    public void addInference(@Nonnull Inference inference) {
        Objects.requireNonNull(inference);
        inferences.add(inference);
    }

}
