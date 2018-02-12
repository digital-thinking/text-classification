package com.ixeption.ml.text.classification;

import com.ixeption.ml.text.classification.features.TextFeature;

public interface TextClassifier {
    int predict(String text);

    int predict(TextFeature textFeature);
}
