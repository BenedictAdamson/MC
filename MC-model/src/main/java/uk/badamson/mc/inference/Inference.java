package uk.badamson.mc.inference;

public interface Inference {

    /**
     * @throws IllegalArgumentException
     * If {@code premise} is not a premise of this inference
     */
    void premiseChanged(BasicBelief premise);
}
