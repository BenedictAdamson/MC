package uk.badamson.mc.inference;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.badamson.dbc.assertions.EqualsSemanticsVerifier;
import uk.badamson.dbc.assertions.ObjectVerifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class DirectInferenceTest {
    private static final BasicBelief BASIC_BELIEF_A = new BasicBelief(0, 0);
    private static final BasicBelief BASIC_BELIEF_B = new BasicBelief(1, 1);
    private static final BasicBelief BASIC_BELIEF_C = new BasicBelief(2, 2);

    private static void constructor(
            BasicBelief premise, BasicBelief implication, double strength, double previousPremiseInformation) {
        final var inference = new DirectInference(premise, implication, strength, previousPremiseInformation);

        assertInvariants(inference);
        assertAll(
                () -> assertThat(inference.getPremise(), sameInstance(premise)),
                () -> assertThat(inference.getImplication(), sameInstance(implication)),
                () -> assertThat(inference.getBayesFactor(), is(strength)),
                () -> assertThat(inference.getPreviousPremiseInformation(), is(previousPremiseInformation))
        );
        assertThat(premise.getInferences(), hasItem(inference));
    }

    public static void assertInvariants(DirectInference inference) {
        ObjectVerifier.assertInvariants(inference);
        InferenceTest.assertInvariants(inference);
    }

    public static void assertInvariants(DirectInference inferenceA, DirectInference inferenceB) {
        ObjectVerifier.assertInvariants(inferenceA, inferenceB);
        InferenceTest.assertInvariants(inferenceA, inferenceB);
        assertAll(
                () -> EqualsSemanticsVerifier.assertValueSemantics(inferenceA, inferenceB, "bayesFactor", DirectInference::getBayesFactor),
                () -> EqualsSemanticsVerifier.assertValueSemantics(inferenceA, inferenceB, "implication", DirectInference::getImplication),
                () -> EqualsSemanticsVerifier.assertValueSemantics(inferenceA, inferenceB, "premise", DirectInference::getPremise)
        );
    }


    public static void premiseChanged(DirectInference inference, BasicBelief premise) {
        InferenceTest.premiseChanged(inference, premise);
        assertInvariants(inference);
    }

    @Nested
    public class Construct {

        @Nested
        public class One {

            @Test
            public void a() {
                constructor(BASIC_BELIEF_A, BASIC_BELIEF_B, 0.5, 0);
            }

            @Test
            public void b() {
                constructor(BASIC_BELIEF_B, BASIC_BELIEF_A, 0.25, 1.25);
            }

        }

        @Nested
        public class Two {

            @Test
            public void equivalent() {
                equivalent(
                        BASIC_BELIEF_A, BASIC_BELIEF_B,
                        0.5, 0, 0
                );
            }

            @Test
            public void equivalentDespiteDifferentPreviousPremiseInformation() {
                equivalent(
                        BASIC_BELIEF_B, BASIC_BELIEF_A,
                        0.25, 0.25, -0.25
                );
            }

            @Test
            public void differentPremises() {
                final double bayesFactor = 0.5;
                final double previousPremiseInformation = 0;
                different(
                        BASIC_BELIEF_C, BASIC_BELIEF_B,
                        bayesFactor, previousPremiseInformation,
                        BASIC_BELIEF_A, BASIC_BELIEF_B,
                        bayesFactor, previousPremiseInformation
                );
            }

            @Test
            public void differentImplication() {
                final double bayesFactor = 0.25;
                final double previousPremiseInformation = 0;
                different(
                        BASIC_BELIEF_A, BASIC_BELIEF_B,
                        bayesFactor, previousPremiseInformation,
                        BASIC_BELIEF_A, BASIC_BELIEF_C,
                        bayesFactor, previousPremiseInformation
                );
            }

            @Test
            public void differentBayesFactor() {
                final double previousPremiseInformation = 0;
                different(
                        BASIC_BELIEF_A, BASIC_BELIEF_C,
                        0.5, previousPremiseInformation,
                        BASIC_BELIEF_A, BASIC_BELIEF_C,
                        0.25, previousPremiseInformation
                );
            }

            private void different(
                    BasicBelief premiseA, BasicBelief implicationA,
                    double bayesFactorA, double previousPremiseInformationA,
                    BasicBelief premiseB, BasicBelief implicationB,
                    double bayesFactorB, double previousPremiseInformationB
            ) {
                final var inferenceA = new DirectInference(
                        premiseA, implicationA, bayesFactorA, previousPremiseInformationA);
                final var inferenceB = new DirectInference(
                        premiseB, implicationB, bayesFactorB, previousPremiseInformationB);
                assertInvariants(inferenceA, inferenceB);
                assertThat(inferenceA, not(is(inferenceB)));
            }

            private void equivalent(
                    BasicBelief premise, BasicBelief implication,
                    double bayesFactor,
                    double previousPremiseInformationA,
                    double previousPremiseInformationB
            ) {
                final var inferenceA = new DirectInference(
                        premise, implication, bayesFactor, previousPremiseInformationA);
                final var inferenceB = new DirectInference(
                        premise, implication, bayesFactor, previousPremiseInformationB);
                assertInvariants(inferenceA, inferenceB);
                assertThat(inferenceA, is(inferenceB));
            }
        }
    }

    @Nested
    public class PremiseChanged {

        @Test
        public void noActualChange() {
            final double premiseInformation = 1;
            test(
                    premiseInformation, premiseInformation,
                    2, 0.5,
                    premiseInformation);
        }

        @Test
        public void smallChange() {
            final double premiseInformation = 1;
            final double previousPremiseInformation = premiseInformation - 0.125;
            final double strength = 0.5;
            test(
                    premiseInformation, premiseInformation,
                    2, strength,
                    previousPremiseInformation);
        }

        @Test
        public void change() {
            final double premiseInformation = 2;
            final double previousPremiseInformation = premiseInformation + Belief.INFORMATION_PRECISION;
            final double strength = 0.5;
            test(
                    premiseInformation, premiseInformation,
                    3, strength,
                    previousPremiseInformation);
        }

        @Test
        public void bigChange() {
            final double premiseInformation = 1;
            final double previousPremiseInformation = premiseInformation + 5;
            final double strength = 0.5;
            test(
                    premiseInformation, premiseInformation,
                    2, strength,
                    previousPremiseInformation);
        }

        @Test
        public void weak() {
            final double premiseInformation = 1;
            final double previousPremiseInformation = premiseInformation + 1;
            final double strength = 0.125;
            test(
                    premiseInformation, premiseInformation,
                    2, strength,
                    previousPremiseInformation);
        }

        @Test
        public void differentInformationAndNextInformation() {
            final double premiseInformation = 1;
            final double premiseNextInformation = premiseInformation + Belief.INFORMATION_PRECISION * 0.5;
            final double previousPremiseInformation = premiseNextInformation + 1;
            final double strength = 0.5;
            test(
                    premiseInformation, premiseNextInformation,
                    2, strength,
                    previousPremiseInformation);
        }

        private void test(
                double premiseInformation, double premiseNextInformation,
                double implicationNextInformation0,
                double strength,
                double previousPremiseInformation
        ) {
            final var premise = new BasicBelief(premiseInformation, premiseNextInformation);
            final var implication = new BasicBelief(implicationNextInformation0, implicationNextInformation0);
            final var inference = new DirectInference(
                    premise, implication,
                    strength, previousPremiseInformation
            );
            final var expectedChange = strength * (premiseInformation - previousPremiseInformation);

            premiseChanged(inference, premise);

            assertAll(
                    () -> assertThat(premise.getInformation(), is(premiseInformation)),
                    () -> assertThat(premise.getNextInformation(), is(premiseNextInformation)));
            assertThat(
                    implication.getNextInformation(),
                    closeTo(implicationNextInformation0 + expectedChange, Belief.INFORMATION_PRECISION * 0.01));
        }
    }
}
