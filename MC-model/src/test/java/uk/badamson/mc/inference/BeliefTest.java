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

import javax.annotation.Nonnull;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BeliefTest {

    public static void assertInvariants(@Nonnull Belief b) {
        final var probability = b.getProbability();
        final var odds = b.getOdds();
        final var information = b.getInformation();
        assertThat("probability", probability, allOf(greaterThanOrEqualTo(0.0), lessThanOrEqualTo(1.0)));
        assertThat("odds", odds, greaterThanOrEqualTo(0.0));
        assertThat("information of probability",
                Belief.informationOfProbability(probability),
                closeTo(information, Belief.INFORMATION_PRECISION));
        assertThat("information of odds",
                Belief.informationOfOdds(odds),
                closeTo(information, Belief.INFORMATION_PRECISION));
    }

    @Nested
    public class InformationOfOdds {

        @Test
        public void one() {
            assertThat(Belief.informationOfOdds(1.0), closeTo(0.0, Belief.INFORMATION_PRECISION));
        }

        @Test
        public void two() {
            assertThat(Belief.informationOfOdds(2.0), closeTo(1.0, Belief.INFORMATION_PRECISION));
        }

        @Test
        public void half() {
            assertThat(Belief.informationOfOdds(0.5), closeTo(-1.0, Belief.INFORMATION_PRECISION));
        }
    }

    @Nested
    public class OddsOfProbability {
        @Test
        public void half() {
            assertThat(Belief.oddsOfProbability(0.5), closeTo(1.0, 0.1));
        }

        @Test
        public void twoThirds() {
            assertThat(Belief.oddsOfProbability(2.0 / 3.0), closeTo(2.0, 0.1));
        }

        @Test
        public void oneThird() {
            assertThat(Belief.oddsOfProbability(1.0 / 3.0), closeTo(0.5, 0.1));
        }
    }

    @Nested
    public class InformationOfProbability {
        @Test
        public void half() {
            assertThat(Belief.informationOfProbability(0.5), closeTo(0.0, Belief.INFORMATION_PRECISION));
        }

        @Test
        public void twoThirds() {
            assertThat(Belief.informationOfProbability(2.0 / 3.0), closeTo(1.0, Belief.INFORMATION_PRECISION));
        }

        @Test
        public void oneThird() {
            assertThat(Belief.informationOfProbability(1.0 / 3.0), closeTo(-1.0, Belief.INFORMATION_PRECISION));
        }

        @Test
        public void one() {
            final var information = Belief.informationOfProbability(1.0);
            assertThat("infinite", Double.isInfinite(information));
            assertThat("positive", 0.0 < information);
        }

        @Test
        public void zero() {
            final var information = Belief.informationOfProbability(0.0);
            assertThat("infinite", Double.isInfinite(information));
            assertThat("negative", information < 0.0);
        }
    }

    @Nested
    public class OddsOfInformation {
        @Test
        public void zero() {
            assertThat(Belief.oddsOfInformation(0.0), closeTo(1.0, 0.1));
        }

        @Test
        public void one() {
            assertThat(Belief.oddsOfInformation(1.0), closeTo(2.0, 0.1));
        }

        @Test
        public void minusOne() {
            assertThat(Belief.oddsOfInformation(-1.0), closeTo(0.5, 0.1));
        }
    }

    @Nested
    public class ProbabilityOfOdds {
        @Test
        public void one() {
            assertThat(Belief.probabilityOfOdds(1.0), closeTo(0.5, 0.1));
        }

        @Test
        public void three() {
            assertThat(Belief.probabilityOfOdds(3.0), closeTo(0.75, 0.1));
        }

        @Test
        public void oneThird() {
            assertThat(Belief.probabilityOfOdds(1.0 / 3.0), closeTo(0.25, 0.1));
        }
    }

    @Nested
    public class ProbabilityOfInformation {
        @Test
        public void zero() {
            assertThat(Belief.probabilityOfInformation(0.0), closeTo(0.5, 0.1));
        }

        @Test
        public void one() {
            assertThat(Belief.probabilityOfInformation(1.0), closeTo(2.0 / 3.0, 0.1));
        }

        @Test
        public void minusOne() {
            assertThat(Belief.probabilityOfInformation(-1.0), closeTo(1.0 / 3.0, 0.1));
        }
    }
}
