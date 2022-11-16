package uk.badamson.mc.inference;

import javax.annotation.Nonnull;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class InferenceTest {

    public static void assertInvariants(Inference inference) {
    }

    public static void assertInvariants(Inference inferenceA, Inference inferenceB) {
    }


    public static void premiseChanged(Inference inference, BasicBelief premise) {
        inference.premiseChanged(premise);
        assertInvariants(inference);
    }

    public static class Spy implements Inference {
        public int nCalls;
        public BasicBelief premise;

        @Override
        public void premiseChanged(@Nonnull BasicBelief premise) {
            assertThat(premise, notNullValue());
            nCalls++;
            this.premise = premise;
        }
    }
}
