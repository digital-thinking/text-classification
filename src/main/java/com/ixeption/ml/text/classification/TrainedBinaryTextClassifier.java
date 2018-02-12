package com.ixeption.ml.text.classification;

public class TrainedBinaryTextClassifier {

    private final BinaryTextClassifier binaryTextClassifier;

    public TrainedBinaryTextClassifier(BinaryTextClassifier binaryTextClassifier) {
        this.binaryTextClassifier = binaryTextClassifier;
    }

    public int predict(String text){
        return binaryTextClassifier.predict(new TextFeature(text, null));

    }
    public int predict(TextFeature textFeature){
        return binaryTextClassifier.predict(textFeature);
    }

}
