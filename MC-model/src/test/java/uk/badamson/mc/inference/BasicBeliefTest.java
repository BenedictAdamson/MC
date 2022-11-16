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
import static org.junit.jupiter.api.Assertions.assertAll;

public class BasicBeliefTest {

    private static final Inference INFERENCE_A = premise -> {
        // do nothing
    };

    private static final Inference INFERENCE_B = premise -> {
        // do nothing
    };

    private static void constructor(double information, double nextInformation) {
        final var belief = new BasicBelief(information, nextInformation);

        assertInvariants(belief);
        assertThat(belief.getInformation(), is(information));
        assertThat(belief.getNextInformation(), is(nextInformation));
        assertThat(belief.getInferences(), empty());
    }

    private static void addInformation(final BasicBelief belief, final double change) {
        final var information0 = belief.getInformation();
        final var nextInformation0 = belief.getNextInformation();

        belief.addInformation(change);

        assertInvariants(belief);
        assertThat(
                belief.getNextInformation(),
                closeTo(nextInformation0 + change, Belief.INFORMATION_PRECISION * 0.01)
        );
        assertThat(
                "information unchanged iff change would be small",
                information0 == belief.getInformation(),
                is(Math.abs(information0 - belief.getNextInformation()) < Belief.INFORMATION_PRECISION)
        );
    }

    private static void addInference(final BasicBelief belief, Inference inference) {
        final var inferences0 = Set.copyOf(belief.getInferences());

        belief.addInference(inference);

        assertInvariants(belief);
        InferenceTest.assertInvariants(inference);
        final var inferences = Set.copyOf(belief.getInferences());
        assertAll(
                () -> assertThat(inferences, hasItem(inference)),
                () -> assertThat(inferences.size(), lessThanOrEqualTo(inferences0.size() + 1)),
                () -> CollectionVerifier.assertForAllElements(inferences, i -> assertThat(i, either(sameInstance(inference)).or(in(inferences0)))));
    }

    public static void assertInvariants(final BasicBelief belief) {
        ObjectVerifier.assertInvariants(belief);
        BeliefTest.assertInvariants(belief);
        final Set<Inference> inferences = belief.getInferences();
        assertThat(inferences, notNullValue());
        assertThat(belief.getInformation(), closeTo(belief.getNextInformation(), Belief.INFORMATION_PRECISION));
        CollectionVerifier.assertForAllElements(inferences, inference -> {
            assertThat(inference, notNullValue());
            InferenceTest.assertInvariants(inference);
        });
    }

    public static void assertInvariants(final BasicBelief beliefA, final BasicBelief beliefB) {
        ObjectVerifier.assertInvariants(beliefA, beliefB);
        assertThat(beliefA.equals(beliefB), is(beliefA == beliefB));
    }

    @Nested
    public class Construct {

        @Nested
        public class One {

            @Test
            public void a() {
                constructor(-1, -1);
            }

            @Test
            public void b() {
                constructor(1, 1.25);
            }

        }

        @Nested
        public class Two {

            @Test
            public void equalAttributes() {
                test(0, 0, 0, 0);
            }

            @Test
            public void differentAttributes() {
                test(1, 1.25, 2, 1.75);
            }

            private void test(double informationA, double nextInformationA, double informationB, double nextInformationB) {
                final var beliefA = new BasicBelief(informationA, nextInformationA);
                final var beliefB = new BasicBelief(informationB, nextInformationB);
                assertInvariants(beliefA, beliefB);
            }
        }
    }

    @Nested
    public class AddInformation {

        @Test
        public void noOp() {
            final var belief = new BasicBelief(1, 1);
            addInformation(belief, 0);
        }

        @Test
        public void smallA() {
            final var belief = new BasicBelief(1, 1);
            addInformation(belief, Belief.INFORMATION_PRECISION * 0.1);
        }

        @Test
        public void smallB() {
            final var belief = new BasicBelief(0.5, 0.75);
            addInformation(belief, Belief.INFORMATION_PRECISION * 0.1);
        }

        @Test
        public void justTooSmallToChangeInformation() {
            final var belief = new BasicBelief(1, 1);
            addInformation(belief, Math.nextDown(Belief.INFORMATION_PRECISION));
        }

        @Test
        public void justBigEnoughToChangeInformation() {
            final var belief = new BasicBelief(1, 1);
            addInformation(belief, Belief.INFORMATION_PRECISION);
        }

        @Test
        public void large() {
            final var belief = new BasicBelief(1, 1);
            addInformation(belief, Belief.INFORMATION_PRECISION * 10.0);
        }

        @Nested
        public class WithInference {

            @Nested
            public class TooSmallToChange {

                @Test
                public void zero() {
                    test(0);
                }

                @Test
                public void increase() {
                    test(Belief.INFORMATION_PRECISION * 0.5);
                }

                @Test
                public void decrease() {
                    test(Belief.INFORMATION_PRECISION * -0.5);
                }

                @Test
                public void justTooSmallIncreaseToChangeInformation() {
                    test(Math.nextDown(Belief.INFORMATION_PRECISION));
                }

                @Test
                public void justTooSmallDecreaseToChangeInformation() {
                    test(Math.nextUp(-Belief.INFORMATION_PRECISION));
                }

                private void test(double change) {
                    final var belief = new BasicBelief(0, 0);
                    final var spy = new InferenceTest.Spy();
                    belief.addInference(spy);

                    addInformation(belief, change);
                    assertThat(spy.nCalls, is(0));
                }
            }

            @Nested
            public class BigEnoughToChange {

                @Test
                public void increase() {
                    test(10);
                }

                @Test
                public void decrease() {
                    test(-10);
                }

                @Test
                public void justBigEnoughIncreaseToChangeInformation() {
                    test(Belief.INFORMATION_PRECISION);
                }

                @Test
                public void justBigEnoughDecreaseToChangeInformation() {
                    test(Belief.INFORMATION_PRECISION);
                }

                private void test(double change) {
                    final var belief = new BasicBelief(0, 0);
                    final var spy = new InferenceTest.Spy();
                    belief.addInference(spy);

                    addInformation(belief, change);

                    assertAll(
                            () -> assertThat("nCalls", spy.nCalls, is(1)),
                            () -> assertThat("premise", spy.premise, sameInstance(belief)));
                }
            }
        }
    }

    @Nested
    public class AddInference {

        @Test
        public void two() {
            final var belief = new BasicBelief(0, 0);
            belief.addInference(INFERENCE_A);

            addInference(belief, INFERENCE_B);

            assertThat(belief.getInferences(), containsInAnyOrder(INFERENCE_A, INFERENCE_B));
        }

        @Test
        public void twice() {
            final var belief = new BasicBelief(0, 0);
            belief.addInference(INFERENCE_A);

            addInference(belief, INFERENCE_A);

            assertThat(belief.getInferences(), contains(INFERENCE_A));
        }

        @Nested
        public class One {

            @Test
            public void a() {
                test(INFERENCE_A);
            }

            @Test
            public void b() {
                test(INFERENCE_B);
            }

            private void test(Inference inference) {
                final var belief = new BasicBelief(0, 0);

                addInference(belief, inference);

                assertThat(belief.getInferences(), contains(inference));
            }
        }
    }
}
