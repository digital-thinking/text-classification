package com.ixeption.ml.text.classification.preprocessing;

import com.ixeption.ml.text.classification.features.TextFeature;

@FunctionalInterface
public interface TextPreprocessor {

    String preprocess(TextFeature textFeature);
}
