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

import javax.annotation.Nonnegative;

public interface Belief {
    double INFORMATION_PRECISION = 0.3;

    @Nonnegative
    static double probabilityOfOdds(@Nonnegative double odds) {
        if (odds < 0.0) {
            throw new IllegalArgumentException();
        }
        return odds / (1.0 + odds);
    }

    @Nonnegative
    static double probabilityOfInformation(double information) {
        return probabilityOfOdds(oddsOfInformation(information));
    }

    @Nonnegative
    static double oddsOfProbability(@Nonnegative double probability) {
        if (probability < 0.0 || 1.0 < probability) {
            throw new IllegalArgumentException();
        }
        return probability / (1.0 - probability);
    }

    static double oddsOfInformation(double information) {
        return Math.pow(2.0, information);
    }

    static double informationOfOdds(@Nonnegative double odds) {
        if (odds < 0.0) {
            throw new IllegalArgumentException();
        }
        return Math.log(odds) * (1.0 / Math.log(2.0));
    }

    static double informationOfProbability(@Nonnegative double probability) {
        return informationOfOdds(oddsOfProbability(probability));
    }

    /**
     * <p>The number of bits of information that indicate this belief is likely to be true.</p>
     * <p>Also called the <i>entropy</i>, measured in <i>Shannons</i>.</p>
     */
    double getInformation();

    /**
     * <p>The likelihood of this belief being true, expressed as odds.</p>
     */
    @Nonnegative
    double getOdds();

    /**
     * <p>The likelihood of this belief being true, expressed as a probability.</p>
     * <p>In the range [0,1]</p>
     */
    @Nonnegative
    double getProbability();
}
