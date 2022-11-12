package uk.badamson.mc.inference;

public class InferenceTest {

    public static void assertInvariants(Inference inference) {
    }

    public static void assertInvariants(Inference inferenceA, Inference inferenceB) {
    }


    public static void premiseChanged(Inference inference, BasicBelief premise) {
        inference.premiseChanged(premise);
        assertInvariants(inference);
    }
}
