package com.ixeption.ml.text.classification;

public class Prediction {
    private final double[] probabilities;
    private final int label;

    public Prediction(int label, double[] probabilities) {
        this.probabilities = probabilities;
        this.label = label;
    }

    int getLabel() {
        return this.label;
    }

    double[] getProbabilities() {
        return this.probabilities;
    }
}
