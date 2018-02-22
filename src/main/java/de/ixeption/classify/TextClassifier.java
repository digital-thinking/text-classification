package de.ixeption.classify;

import de.ixeption.classify.features.TextFeature;

@FunctionalInterface
public interface TextClassifier {

    Prediction predict(TextFeature textFeature);
}
