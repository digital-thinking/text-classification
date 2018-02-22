package de.ixeption.classify;

public class Prediction {
    private final double[] probabilities;
    private final int label;

    public Prediction(int label, double[] probabilities) {
        this.probabilities = probabilities;
        this.label = label;
    }

    public int getLabel() {
        return this.label;
    }

    public double[] getProbabilities() {
        return this.probabilities.clone();
    }
}
