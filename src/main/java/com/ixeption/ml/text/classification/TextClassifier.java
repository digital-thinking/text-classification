package com.ixeption.ml.text.classification;

import com.ixeption.ml.text.classification.features.TextFeature;

@FunctionalInterface
public interface TextClassifier {

    Prediction predict(TextFeature textFeature);
}
