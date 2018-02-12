package com.ixeption.ml.text.classification;

import com.ixeption.ml.text.classification.features.TextFeature;

public interface TextClassifier {
    Prediction predict(String text);

    Prediction predict(TextFeature textFeature);
}
