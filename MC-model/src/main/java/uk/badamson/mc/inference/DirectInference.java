package uk.badamson.mc.inference;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Objects;

@NotThreadSafe
public final class DirectInference implements Inference {

    @Nonnull
    private final BasicBelief premise;
    @Nonnull
    private final BasicBelief implication;
    private final double strength;
    private double previousPremiseInformation;

    public DirectInference(
            @Nonnull BasicBelief premise,
            @Nonnull BasicBelief implication,
            double strength,
            double previousPremiseInformation
    ) {
        this.premise = premise;
        this.implication = implication;
        this.strength = strength;
        this.previousPremiseInformation = previousPremiseInformation;
    }

    @Override
    public void premiseChanged(@Nonnull BasicBelief premise) {
        Objects.requireNonNull(premise);
        if (premise != this.premise) {
            throw new IllegalArgumentException();
        }
        final double premiseInformation = premise.getInformation();
        final double change = strength * (premiseInformation - previousPremiseInformation);
        previousPremiseInformation = premiseInformation;
        implication.addInformation(change);
    }

    @Nonnull
    public BasicBelief getPremise() {
        return premise;
    }

    @Nonnull
    public BasicBelief getImplication() {
        return implication;
    }

    public double getStrength() {
        return strength;
    }

    public double getPreviousPremiseInformation() {
        return previousPremiseInformation;
    }
}
