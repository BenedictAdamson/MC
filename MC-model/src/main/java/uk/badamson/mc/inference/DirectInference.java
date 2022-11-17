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
    private final double bayesFactor;
    private double previousPremiseInformation;

    public DirectInference(
            @Nonnull BasicBelief premise,
            @Nonnull BasicBelief implication,
            double bayesFactor,
            double previousPremiseInformation
    ) {
        this.premise = premise;
        this.implication = implication;
        this.bayesFactor = bayesFactor;
        this.previousPremiseInformation = previousPremiseInformation;
        premise.addInference(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DirectInference that = (DirectInference) o;

        return Double.doubleToLongBits(bayesFactor) == Double.doubleToLongBits(that.bayesFactor) &&
                premise.equals(that.premise) &&
                implication.equals(that.implication);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = premise.hashCode();
        result = 31 * result + implication.hashCode();
        temp = Double.hashCode(bayesFactor);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "DirectInference{" +
                "premise=" + premise +
                ", implication=" + implication +
                ", bayesFactor=" + bayesFactor +
                ", previousPremiseInformation=" + previousPremiseInformation +
                '}';
    }

    @Override
    public void premiseChanged(@Nonnull BasicBelief premise) {
        Objects.requireNonNull(premise);
        if (premise != this.premise) {
            throw new IllegalArgumentException();
        }
        final double premiseInformation = premise.getInformation();
        final double change = bayesFactor * (premiseInformation - previousPremiseInformation);
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

    public double getBayesFactor() {
        return bayesFactor;
    }

    public double getPreviousPremiseInformation() {
        return previousPremiseInformation;
    }
}
