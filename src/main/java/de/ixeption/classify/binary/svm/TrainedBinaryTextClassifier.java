package de.ixeption.classify.binary.svm;

import de.ixeption.classify.Prediction;
import de.ixeption.classify.TextClassifier;
import de.ixeption.classify.features.TextFeature;

public class TrainedBinaryTextClassifier implements TextClassifier {

    private final BinaryTextClassifierTrainer binaryTextClassifierTrainer;

    public TrainedBinaryTextClassifier(BinaryTextClassifierTrainer binaryTextClassifierTrainer) {
        this.binaryTextClassifierTrainer = binaryTextClassifierTrainer;
    }

    @Override
    public Prediction predict(TextFeature textFeature) {
        return binaryTextClassifierTrainer.predict(textFeature);
    }

}
