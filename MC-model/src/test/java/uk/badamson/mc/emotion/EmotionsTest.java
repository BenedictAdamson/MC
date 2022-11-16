package uk.badamson.mc.emotion;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.badamson.dbc.assertions.ObjectVerifier;
import uk.badamson.mc.inference.BasicBelief;
import uk.badamson.mc.inference.BasicBeliefTest;
import uk.badamson.mc.inference.Belief;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EmotionsTest {
    private static final double INFORMATION_A = 0;
    private static final double INFORMATION_B = 1;
    private static final double INFORMATION_C = -1;
    private static final double NEXT_INFORMATION_A = 0;
    private static final double NEXT_INFORMATION_B = 1.25;
    private static final double NEXT_INFORMATION_C = -0.75;

    public static void assertInvariants(Emotions emotions) {
        ObjectVerifier.assertInvariants(emotions);

        final BasicBelief stressed = emotions.getStressed();
        final BasicBelief aggressive = emotions.getAggressive();
        final BasicBelief fearful = emotions.getFearful();
        assertAll(
                () -> assertThat("stressed", stressed, notNullValue()),
                () -> assertThat("aggressive", aggressive, notNullValue()),
                () -> assertThat("fearful", fearful, notNullValue())
        );
        BasicBeliefTest.assertInvariants(stressed);
        BasicBeliefTest.assertInvariants(aggressive);
        BasicBeliefTest.assertInvariants(fearful);
    }

    public static void assertInvariants(Emotions emotionsA, Emotions emotionsB) {
        ObjectVerifier.assertInvariants(emotionsA, emotionsB);
    }

    private static void constructor(
            double stressedInformation, double stressedNextInformation,
            double aggressiveInformation, double aggressiveNextInformation,
            double fearfulInformation, double fearfulNextInformation
    ) {
        final var emotions = new Emotions(
                stressedInformation, stressedNextInformation,
                aggressiveInformation, aggressiveNextInformation,
                fearfulInformation, fearfulNextInformation
        );

        assertInvariants(emotions);
        final BasicBelief stressed = emotions.getStressed();
        final BasicBelief aggressive = emotions.getAggressive();
        final BasicBelief fearful = emotions.getFearful();
        assertAll(
                () -> assertAll("stressed",
                        () -> assertThat(
                                "information", stressed.getInformation(),
                                closeTo(stressedInformation, Belief.INFORMATION_PRECISION * 0.01)
                        ),
                        () -> assertThat(
                                "nextInformation", stressed.getNextInformation(),
                                closeTo(stressedNextInformation, Belief.INFORMATION_PRECISION * 0.01)
                        )
                ),
                () -> assertAll("aggressive",
                        () -> assertThat(
                                "information", aggressive.getInformation(),
                                closeTo(aggressiveInformation, Belief.INFORMATION_PRECISION * 0.01)
                        ),
                        () -> assertThat(
                                "nextInformation", aggressive.getNextInformation(),
                                closeTo(aggressiveNextInformation, Belief.INFORMATION_PRECISION * 0.01)
                        )
                ),
                () -> assertAll("fearful",
                        () -> assertThat(
                                "information", fearful.getInformation(),
                                closeTo(fearfulInformation, Belief.INFORMATION_PRECISION * 0.01)
                        ),
                        () -> assertThat(
                                "nextInformation", fearful.getNextInformation(),
                                closeTo(fearfulNextInformation, Belief.INFORMATION_PRECISION * 0.01)
                        )
                )
        );
    }

    @Nested
    public class Construct {

        @Test
        public void two() {
            final var emotionsA = new Emotions(
                    INFORMATION_A, NEXT_INFORMATION_A,
                    INFORMATION_B, NEXT_INFORMATION_B,
                    INFORMATION_C, NEXT_INFORMATION_C
            );
            final var emotionsB = new Emotions(
                    INFORMATION_B, NEXT_INFORMATION_B,
                    INFORMATION_C, NEXT_INFORMATION_C,
                    INFORMATION_A, NEXT_INFORMATION_A
            );

            assertInvariants(emotionsA, emotionsB);
        }

        @Nested
        public class One {

            @Test
            public void a() {
                constructor(
                        INFORMATION_A, NEXT_INFORMATION_A,
                        INFORMATION_B, NEXT_INFORMATION_B,
                        INFORMATION_C, NEXT_INFORMATION_C
                );
            }

            @Test
            public void b() {
                constructor(
                        INFORMATION_B, NEXT_INFORMATION_B,
                        INFORMATION_C, NEXT_INFORMATION_C,
                        INFORMATION_A, NEXT_INFORMATION_A
                );
            }
        }
    }
}
