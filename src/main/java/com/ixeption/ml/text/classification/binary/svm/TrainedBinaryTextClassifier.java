package com.ixeption.ml.text.classification.binary.svm;

import com.ixeption.ml.text.classification.TextClassifier;
import com.ixeption.ml.text.classification.features.TextFeature;

public class TrainedBinaryTextClassifier implements TextClassifier {

    private final BinaryTextClassifierTrainer binaryTextClassifierTrainer;

    public TrainedBinaryTextClassifier(BinaryTextClassifierTrainer binaryTextClassifierTrainer) {
        this.binaryTextClassifierTrainer = binaryTextClassifierTrainer;
    }

    @Override
    public int predict(String text) {
        return binaryTextClassifierTrainer.predict(new TextFeature(text, null));

    }

    @Override
    public int predict(TextFeature textFeature) {
        return binaryTextClassifierTrainer.predict(textFeature);
    }

}
