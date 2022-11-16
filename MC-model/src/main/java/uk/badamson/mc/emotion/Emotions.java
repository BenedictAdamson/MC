package uk.badamson.mc.emotion;

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
