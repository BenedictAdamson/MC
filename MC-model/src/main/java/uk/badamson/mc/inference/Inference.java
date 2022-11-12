package uk.badamson.mc.inference;

import javax.annotation.Nonnull;

public interface Inference {

    /**
     * @throws IllegalArgumentException
     * If {@code premise} is not a premise of this inference
     */
    void premiseChanged(@Nonnull BasicBelief premise);
}
