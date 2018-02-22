package de.ixeption.classify.preprocessing;

import de.ixeption.classify.features.TextFeature;

@FunctionalInterface
public interface TextPreprocessor {

    String preprocess(TextFeature textFeature);
}
