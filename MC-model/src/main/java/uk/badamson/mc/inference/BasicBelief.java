package uk.badamson.mc.inference;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class BasicBelief implements Belief {
    private double information;
    private double nextInformation;

    public BasicBelief(double information, double nextInformation) {
        if (Math.abs(information - nextInformation) > Belief.INFORMATION_PRECISION) {
            throw new IllegalArgumentException("information and nextInformation must be close");
        }
        this.information = information;
        this.nextInformation = nextInformation;
    }

    @Override
    public double getInformation() {
        return information;
    }

    @Override
    public double getOdds() {
        return Belief.oddsOfInformation(information);
    }

    @Override
    public double getProbability() {
        return Belief.probabilityOfInformation(information);
    }

    public double getNextInformation() {
        return nextInformation;
    }

    public void addInformation(double change) {
        nextInformation += change;
        if (Math.abs(nextInformation - information) >= Belief.INFORMATION_PRECISION) {
            information = nextInformation;
        }
    }

}
