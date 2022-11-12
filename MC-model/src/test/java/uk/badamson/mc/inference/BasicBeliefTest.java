package uk.badamson.mc.inference;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.badamson.dbc.assertions.ObjectVerifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class BasicBeliefTest {

    private static void constructor(double information, double nextInformation) {
        final var belief = new BasicBelief(information, nextInformation);

        assertInvariants(belief);
        assertThat(belief.getInformation(), is(information));
        assertThat(belief.getNextInformation(), is(nextInformation));
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

    public static void assertInvariants(final BasicBelief belief) {
        ObjectVerifier.assertInvariants(belief);
        BeliefTest.assertInvariants(belief);
        assertThat(belief.getNextInformation(), closeTo(belief.getNextInformation(), Belief.INFORMATION_PRECISION));
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
        public void justBigEnoughToChangeInformation() {
            final var belief = new BasicBelief(1, 1);
            addInformation(belief, Belief.INFORMATION_PRECISION);
        }

        @Test
        public void large() {
            final var belief = new BasicBelief(1, 1);
            addInformation(belief, Belief.INFORMATION_PRECISION * 10.0);
        }
    }
}
