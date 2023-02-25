package uk.badamson.mc.emotion;
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

import uk.badamson.mc.inference.BasicBelief;

public final class Emotions {

    private final BasicBelief stressed;
    private final BasicBelief aggressive;
    private final BasicBelief fearful;

    public Emotions(
            double stressedInformation, double stressedNextInformation,
            double aggressiveInformation, double aggressiveNextInformation,
            double fearfulInformation, double fearfulNextInformation
    ) {
        stressed = new BasicBelief(stressedInformation, stressedNextInformation);
        aggressive = new BasicBelief(aggressiveInformation, aggressiveNextInformation);
        fearful = new BasicBelief(fearfulInformation, fearfulNextInformation);
    }

    public BasicBelief getStressed() {
        return stressed;
    }

    public BasicBelief getAggressive() {
        return aggressive;
    }

    public BasicBelief getFearful() {
        return fearful;
    }
}
